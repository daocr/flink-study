package com.huilong.window.time;

import com.huilong.mock.dto.MockOrderEvent;
import com.huilong.mock.source.MockEventSourceFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Date;
import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 滑动 event-time 窗口
 * <p>
 * 滑动窗口和翻滚窗口类似，区别在于：滑动窗口可以有重叠的部分。
 *
 *
 * </pre>
 *
 * @author daocr
 * @date 2020/11/18
 */
@Slf4j
public class SlidingEventTimeWindow {


    public static void main(String[] args) throws Exception {

        /**
         * 应用场景
         *
         * 但是对于某些应用，它们需要的窗口是不间断的，需要平滑地进行窗口聚合。
         * 比如，我们可以10秒计算一次最近一分钟用户购买的商品总数。
         * 这种窗口我们称为滑动时间窗口（Sliding Time Window）。在滑窗中，一个元素可以对应多个窗口
         */
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        DataStreamSource<MockOrderEvent> mockEventDataStreamSource = env.addSource(new MockEventSourceFunction());


        KeyedStream<MockOrderEvent, String> mockEventStringKeyedStream = mockEventDataStreamSource
                .keyBy(MockOrderEvent::getGoodName);


        // 每10秒，统计过去1分钟的数据
        WindowedStream<MockOrderEvent, String, TimeWindow> mockEventStringTimeWindowWindowedStream = mockEventStringKeyedStream.timeWindow(Time.minutes(1), Time.seconds(10));

        //apply是窗口的应用函数，即apply里的函数将应用在此窗口的数据上。
        SingleOutputStreamOperator<Object> apply = mockEventStringTimeWindowWindowedStream.apply(new WindowFunction<MockOrderEvent, Object, String, TimeWindow>() {
            @Override
            public void apply(String eventName, TimeWindow timeWindow, Iterable<MockOrderEvent> iterable, Collector<Object> collector) throws Exception {

                String start = DateFormatUtils.format(new Date(timeWindow.getStart()), "yyyy-MM-dd HH:mm:ss");
                String end = DateFormatUtils.format(new Date(timeWindow.getEnd()), "yyyy-MM-dd HH:mm:ss");

                IntSummaryStatistics collect = StreamSupport.stream(iterable.spliterator(), false).map(MockOrderEvent::getAmount).collect(Collectors.summarizingInt(Integer::intValue));

                log.info("每10秒 统计过去一分钟交易信息 eventName：{}  size:{}  start：{} end：{} 总额 amount:{}  最大金额：{} 最小金额 : {} 平均金额 :{} ", eventName, collect.getCount(), start, end, collect.getSum(), collect.getMax(), collect.getMin(), collect.getAverage());

            }
        });


        env.execute();

    }
}
