package com.huilong.state;

import com.huilong.mock.MockOrderEvent;
import com.huilong.mock.source.MockEventSourceFunction;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author daocr
 * @date 2020/11/23
 */
public class UsingKeyedState {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
        DataStreamSource<MockOrderEvent> mockEventDataStreamSource = env.addSource(new MockEventSourceFunction());

        KeyedStream<MockOrderEvent, String> mockEventStringKeyedStream = mockEventDataStreamSource
                .keyBy(MockOrderEvent::getGoodName);


        mockEventStringKeyedStream.filter(new RichFilterFunction<MockOrderEvent>() {

            ValueState<MockOrderEvent> state;

            @Override
            public void open(Configuration parameters) throws Exception {

                /*
                 * 访问策略
                 * StateTtlConfig.UpdateType.OnCreateAndWrite - only on creation and write access
                 * StateTtlConfig.UpdateType.OnReadAndWrite - also on read access
                 *
                 * 过期访问策略
                 * StateTtlConfig.StateVisibility.NeverReturnExpired - expired value is never returned
                 * StateTtlConfig.StateVisibility.ReturnExpiredIfNotCleanedUp - returned if still available
                 */
                StateTtlConfig ttlConfig = StateTtlConfig
                        .newBuilder(Time.hours(1))
                        .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                        .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
//                        .disableCleanupInBackground()
                        .build();

                ValueStateDescriptor<MockOrderEvent> average = new ValueStateDescriptor<>("average", MockOrderEvent.class);

                average.enableTimeToLive(ttlConfig);

                // 注册到上下文
                state = getRuntimeContext().getState(average);

            }

            @Override
            public boolean filter(MockOrderEvent mockOrderEvent) throws Exception {
                return false;
            }
        });

    }
}
