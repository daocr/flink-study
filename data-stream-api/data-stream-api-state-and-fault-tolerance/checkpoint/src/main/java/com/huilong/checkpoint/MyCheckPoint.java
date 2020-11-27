package com.huilong.checkpoint;

import com.huilong.mock.dto.MockOrderEvent;
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
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author daocr
 * @date 2020/11/24
 */
public class MyCheckPoint {

    public static void main(String[] args) throws Exception {


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
        // 重试 策略
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 2000));

        // 快照建立间隔 ，默认不建立快照 (毫秒)
        env.enableCheckpointing(1000);

        // set mode to exactly-once (this is the default)
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);

        // make sure 500 ms of progress happen between checkpoints    (毫秒)
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);

        // 最长建立快照时间，如果超过就终止掉这个快照 (毫秒)
        env.getCheckpointConfig().setCheckpointTimeout(60000);

        // 同时允许几个 checkpoint
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);

        // Checkpoint 的初衷是进行失败恢复，因此，当一个 Flink 应用程序停止时（比如，失败终止、人为取消等），它的 Checkpoint 就会被清除。
        // 但是，你可以通过开启外化 Checkpoint 功能，在应用程序停止后，保存 Checkpoint。

        //DELETE_ON_CANCELLATION：当应用程序完全失败或者明确地取消时，保存 Checkpoint。
        //RETAIN_ON_CANCELLATION：当应用程序完全失败时，保存 Checkpoint。如果应用程序是明确地取消时，Checkpoint 被删除。
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);

        // allow job recovery fallback to checkpoint when there is a more recent savepoint
        env.getCheckpointConfig().setPreferCheckpointForRecovery(true);

        // enables the experimental unaligned checkpoints
        env.getCheckpointConfig().enableUnalignedCheckpoints();

        FsStateBackend fsStateBackend = new FsStateBackend(MyCheckPoint.class.getResource("/") + "resources/checkpoint", true);

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
