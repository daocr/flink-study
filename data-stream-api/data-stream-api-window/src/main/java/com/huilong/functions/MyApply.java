package com.huilong.functions;

import com.huilong.mock.MockOrderEvent;
import com.huilong.mock.source.MockEventSourceFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 窗口函数 处理器
 *
 * @author daocr
 * @date 2020/11/20
 */
public class MyApply {

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

        timeWindowStream.apply(new MockOrderEventIntegerTimeWindowWindowFunction());

        env.execute();
    }

    /**
     * 对窗口内的数据进行处理
     */
    @Slf4j
    private static class MockOrderEventIntegerTimeWindowWindowFunction implements WindowFunction<MockOrderEvent, Object, Integer, TimeWindow> {
        @Override
        public void apply(Integer userId, TimeWindow timeWindow, Iterable<MockOrderEvent> iterable, Collector<Object> out) throws Exception {


            String start = DateFormatUtils.format(new Date(timeWindow.getStart()), "yyyy-MM-dd HH:mm:ss");
            String end = DateFormatUtils.format(new Date(timeWindow.getEnd()), "yyyy-MM-dd HH:mm:ss");


            IntSummaryStatistics collect = StreamSupport.stream(iterable.spliterator(), false).map(MockOrderEvent::getAmount).collect(Collectors.summarizingInt(Integer::intValue));

            log.info("start: {} end: {} 用户 userId：{} 总额 amount:{}  最大金额：{} 最小金额 : {} 平均金额 :{} ", start, end, userId, collect.getSum(), collect.getMax(), collect.getMin(), collect.getAverage());

        }
    }
}
