package com.huilong.window.all;

import com.huilong.mock.MockOrderEvent;
import com.huilong.mock.source.MockEventSourceFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Date;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 全局窗口
 *
 * @author daocr
 * @date 2020/11/18
 */
@Slf4j
public class SlidingGlobalWindow {

    public static void main(String[] args) throws Exception {

        /**
         *1、WindowAll 算子：并行度始终为1，并且不能设置并行度
         *2、Window 算子：是可以设置并行度的
         *3、WindowAll 是应用在没有分组的数据上
         *
         */
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);

        DataStreamSource<MockOrderEvent> mockEventDataStreamSource = env.addSource(new MockEventSourceFunction());

        // 每10秒触发一次，统计过去60秒的数据
        AllWindowedStream<MockOrderEvent, TimeWindow> mockOrderEventTimeWindowAllWindowedStream = mockEventDataStreamSource.windowAll(SlidingEventTimeWindows.of(Time.seconds(60), Time.seconds(10)));

        mockOrderEventTimeWindowAllWindowedStream.apply(new AllWindowFunction<MockOrderEvent, Object, TimeWindow>() {
            @Override
            public void apply(TimeWindow timeWindow, Iterable<MockOrderEvent> iterable, Collector<Object> out) throws Exception {

                String start = DateFormatUtils.format(new Date(timeWindow.getStart()), "yyyy-MM-dd HH:mm:ss");

                String end = DateFormatUtils.format(new Date(timeWindow.getEnd()), "yyyy-MM-dd HH:mm:ss");

                List<MockOrderEvent> mockOrderEvents = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());



                IntSummaryStatistics collect = mockOrderEvents.stream().map(MockOrderEvent::getAmount).collect(Collectors.summarizingInt(Integer::intValue));

                log.info("每10秒 统计过去一分钟交易信息  size:{}  start：{} end：{} 总额 amount:{}  最大金额：{} 最小金额 : {} 平均金额 :{} ", collect.getCount(), start, end, collect.getSum(), collect.getMax(), collect.getMin(), collect.getAverage());

            }
        });

        env.execute();
    }

}
