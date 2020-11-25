package com.huilong.state;

import com.huilong.mock.MockOrderEvent;
import com.huilong.mock.source.MockEventSourceFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author daocr
 * @date 2020/11/23
 */
public class UsingOperatorState {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
        // 重试 策略
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 2000));
        env.enableCheckpointing(1000, CheckpointingMode.AT_LEAST_ONCE);
        FsStateBackend fsStateBackend = new FsStateBackend(UsingOperatorState.class.getResource("/") + "resources/checkpoint", true);
//        fsStateBackend.resolveCheckpoint()
        env.setStateBackend(fsStateBackend);

        MockEventSourceFunction mockEventSourceFunction = new MockEventSourceFunction()
                .setSleepMin(Duration.ofMillis(50))
                .setSleepMax(Duration.ofMillis(200));

        DataStreamSource<MockOrderEvent> mockEventDataStreamSource = env.addSource(mockEventSourceFunction);

        mockEventDataStreamSource.addSink(new MockOrderEventSinkFunction(10));

        env.execute();
    }

    /**
     * sink 数据，和快照 恢复数据
     */
    @Slf4j
    private static class MockOrderEventSinkFunction implements SinkFunction<MockOrderEvent>, CheckpointedFunction {

        private final int count;

        private transient ListState<MockOrderEvent> checkpointedState;

        private int saveCut = 0;

        private List<MockOrderEvent> bufferedElements;


        public MockOrderEventSinkFunction(int count) {
            this.count = count;
            this.bufferedElements = new ArrayList<>();
        }

        @Override
        public void invoke(MockOrderEvent value, Context context) throws Exception {

            bufferedElements.add(value);

            // 每10条数据出发一次保存
            if (bufferedElements.size() == count) {
                for (MockOrderEvent element : bufferedElements) {
                    log.info("保存 element {}", element);

                    throw new RuntimeException("模拟错误");
                }

                bufferedElements.clear();
            }
        }


        /**
         * 建立新快照
         *
         * @param context
         * @throws Exception
         */
        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            // 清楚老快照数据
            checkpointedState.clear();
            for (MockOrderEvent element : bufferedElements) {
                checkpointedState.add(element);
                log.info("添加数据到快照：{}", element);
            }
        }

        /**
         * 初始化数据，从快照中恢复数据
         *
         * @param context
         * @throws Exception
         */
        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            ListStateDescriptor<MockOrderEvent> descriptor =
                    new ListStateDescriptor<MockOrderEvent>(
                            "buffered-elements",
                            MockOrderEvent.class);

            checkpointedState = context.getOperatorStateStore().getListState(descriptor);

            log.info("初始化 State isRestored：{} result: {}", context.isRestored(), checkpointedState.get());
            if (context.isRestored()) {
                for (MockOrderEvent element : checkpointedState.get()) {
                    bufferedElements.add(element);
                    log.info("从快照中恢复：{}", element);
                }
            }
        }
    }
}
