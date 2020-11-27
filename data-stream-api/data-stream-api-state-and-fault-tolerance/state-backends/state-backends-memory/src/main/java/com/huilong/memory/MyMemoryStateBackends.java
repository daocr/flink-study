package com.huilong.memory;

import com.huilong.mock.dto.MockOrderEvent;
import com.huilong.mock.ProjectContext;
import com.huilong.mock.source.MockEventSourceFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.RichWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;

/**
 * MemoryStateBackends
 *
 * @author daocr
 * @date 2020/11/24
 */
@Slf4j
public class MyMemoryStateBackends {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 2000));

        StateBackend memoryStateBackend = new MemoryStateBackend(ProjectContext.getCheckPoint(MyMemoryStateBackends.class),
                ProjectContext.getSavePoint(MyMemoryStateBackends.class));

        env.setStateBackend(memoryStateBackend);

        // 添加 mock 数据源
        MockEventSourceFunction mockEventSourceFunction = new MockEventSourceFunction()
                .setSleepMin(Duration.ofMillis(50))
                .setSleepMax(Duration.ofMillis(200));

        DataStreamSource<MockOrderEvent> mockEventDataStreamSource = env.addSource(mockEventSourceFunction);

        /*
         *  1、商品名称 分组，
         *  2、30 秒统计一次数据
         */
        WindowedStream<MockOrderEvent, String, TimeWindow> windowedStream = mockEventDataStreamSource
                .keyBy(MockOrderEvent::getGoodName)
                .timeWindow(org.apache.flink.streaming.api.windowing.time.Time.seconds(30));


        windowedStream.apply(new MockOrderEventStringTimeWindowWindowFunction());

        env.execute();
    }


    private static class MockOrderEventStringTimeWindowWindowFunction extends RichWindowFunction<MockOrderEvent, Object, String, TimeWindow> {

        private ListState<MockOrderEvent> listState;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);

            //keyedState可以设置TTL过期时间
            StateTtlConfig config = StateTtlConfig
                    .newBuilder(Time.seconds(30))
                    .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                    .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                    .build();

            // 注册 state 到 context
            ListStateDescriptor<MockOrderEvent> valueStateDescriptor = new ListStateDescriptor<MockOrderEvent>("valueStateDesc", MockOrderEvent.class);

            valueStateDescriptor.enableTimeToLive(config);
            listState = getRuntimeContext().getListState(valueStateDescriptor);
        }

        @Override
        public void close() throws Exception {
            super.close();
            // 清除 state
            listState.clear();
        }

        @Override
        public void apply(String s, TimeWindow window, Iterable<MockOrderEvent> input, Collector<Object> out) throws Exception {

        }
    }
}
