package com.huilong.functions;

import com.huilong.mock.MockOrderEvent;
import com.huilong.mock.source.MockEventSourceFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.time.Duration;
import java.util.Arrays;

/**
 * @author daocr
 * @date 2020/11/20
 */
public class MyReduceFunction {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);


        MockEventSourceFunction mockEventSourceFunction = new MockEventSourceFunction()
                .setUserIds(Arrays.asList(11, 22))
                .setSleepMax(Duration.ofSeconds(3))
                .setSleepMin(Duration.ofSeconds(1))
                .setShowGenData(true);

        DataStreamSource<MockOrderEvent> mockEventDataStreamSource = env.addSource(mockEventSourceFunction);

        /*
            1、用户id和 商品名称 进行分组
            2、每10s 统计一次
         */
        WindowedStream<MockOrderEvent, String, TimeWindow> mockOrderEventStringTimeWindowWindowedStream = mockEventDataStreamSource.
                keyBy(mock -> mock.getUserId() + mock.getGoodName())
                .timeWindow(Time.seconds(10));


        /*
            reduce 一般用来做增量更新，或者聚合计算
         */
        SingleOutputStreamOperator<MockOrderEvent> reduceOperator = mockOrderEventStringTimeWindowWindowedStream
                .reduce(new MockOrderEventReduceFunction());

        // 打印结果
        reduceOperator.print();

        env.execute();


    }

    /**
     * 特性： 每进来一个 event 事件，调用 reduce 一次
     */

    @Slf4j
    private static class MockOrderEventReduceFunction implements ReduceFunction<MockOrderEvent> {

        @Override
        public MockOrderEvent reduce(MockOrderEvent t1, MockOrderEvent t2) throws Exception {
            /**
             * 聚合统计总金额
             */
            MockOrderEvent mockOrderEvent = new MockOrderEvent();
            mockOrderEvent.setAmount(t1.getAmount() + t2.getAmount());
            mockOrderEvent.setGoodName(t1.getGoodName());
            mockOrderEvent.setUserId(t2.getUserId());

            return mockOrderEvent;
        }
    }
}
