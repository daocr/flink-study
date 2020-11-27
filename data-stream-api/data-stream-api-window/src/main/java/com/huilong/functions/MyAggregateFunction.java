package com.huilong.functions;

import com.huilong.mock.dto.MockOrderEvent;
import com.huilong.mock.source.MockEventSourceFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 集合计算
 *
 * @author daocr
 * @date 2020/11/20
 */
@Slf4j
public class MyAggregateFunction {


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
        WindowedStream<MockOrderEvent, Integer, TimeWindow> timeWindowStream = mockEventDataStreamSource.
                keyBy(MockOrderEvent::getUserId)
                .timeWindow(Time.seconds(10));


        // 开始集合计算
        SingleOutputStreamOperator<MockOrderEvent> aggregate = timeWindowStream.aggregate(new MockOrderEventMockOrderEventMockOrderEventAggregateFunction());
        aggregate.print();

        // 1、普通集合计算
//        SingleOutputStreamOperator<MockOrderEvent> aggregate1 = mockOrderEventIntegerTimeWindowWindowedStream.aggregate(new MockOrderEventMockOrderEventMockOrderEventAggregateFunction());
//


        // 2、带窗口的集合计算
        SingleOutputStreamOperator<Object> aggregate2 = timeWindowStream.aggregate(new MockOrderEventMockOrderEventMockOrderEventAggregateFunction(), new MockOrderEventIntegerTimeWindowWindowFunction());


    /*    //3、对聚合后的数据，进行整理
        mockOrderEventIntegerTimeWindowWindowedStream.aggregate(new MockOrderEventMockOrderEventMockOrderEventAggregateFunction(), new MockOrderEventIntegerTimeWindowProcessWindowFunction());
*/

        env.execute();

    }

    /**
     * 集合计算
     */
    private static class MockOrderEventMockOrderEventMockOrderEventAggregateFunction implements AggregateFunction<MockOrderEvent, MockOrderEvent, MockOrderEvent> {
        @Override
        public MockOrderEvent createAccumulator() {
            return new MockOrderEvent();
        }

        @Override
        public MockOrderEvent add(MockOrderEvent mockOrderEvent, MockOrderEvent mockOrderEvent2) {

            MockOrderEvent mockOrderEvent1 = new MockOrderEvent();

            mockOrderEvent1.setAmount(mockOrderEvent.getAmount() + Optional.ofNullable(mockOrderEvent2).map(MockOrderEvent::getAmount).orElse(0));
            mockOrderEvent1.setUserId(mockOrderEvent.getUserId());

            return mockOrderEvent1;
        }

        @Override
        public MockOrderEvent getResult(MockOrderEvent mockOrderEvent) {
            return mockOrderEvent;
        }

        @Override
        public MockOrderEvent merge(MockOrderEvent mockOrderEvent, MockOrderEvent acc1) {

            mockOrderEvent.setAmount(mockOrderEvent.getAmount() + acc1.getAmount());
            mockOrderEvent.setUserId(mockOrderEvent.getUserId());

            return mockOrderEvent;
        }
    }

    /**
     * 带窗口对聚合后的数据进行处理
     */
    private static class MockOrderEventIntegerTimeWindowWindowFunction implements WindowFunction<MockOrderEvent, Object, Integer, TimeWindow> {
        @Override
        public void apply(Integer integer, TimeWindow timeWindow, Iterable<MockOrderEvent> input, Collector<Object> out) throws Exception {

            String start = DateFormatUtils.format(new Date(timeWindow.getStart()), "yyyy-MM-dd HH:mm:ss");
            String end = DateFormatUtils.format(new Date(timeWindow.getEnd()), "yyyy-MM-dd HH:mm:ss");


            List<MockOrderEvent> collect = StreamSupport.stream(input.spliterator(), false).collect(Collectors.toList());

            log.info("start: {} end: {} size  : {}  content: {}", start, end, collect.size(), collect);

        }
    }

    /**
     * 不带窗口对聚合后的数据进行处理
     */
    private static class MockOrderEventIntegerTimeWindowProcessWindowFunction extends ProcessWindowFunction<MockOrderEvent, Object, Integer, TimeWindow> {
        @Override
        public void process(Integer integer, Context context, Iterable<MockOrderEvent> elements, Collector<Object> out) throws Exception {

            List<MockOrderEvent> collect = StreamSupport.stream(elements.spliterator(), false).collect(Collectors.toList());

            log.info("size  : {}  content: {}", collect.size(), collect);
        }
    }
}
