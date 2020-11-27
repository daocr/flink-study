package com.huilong.window.time;

import com.huilong.mock.dto.MockOrderEvent;
import com.huilong.mock.source.MockEventSourceFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.streaming.api.TimeCharacteristic;
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
 * 翻滚 窗口
 * <p>
 * <p>
 * 翻滚窗口能将数据流切分成不重叠的窗口，每一个事件只能属于一个窗
 * 翻滚窗具有固定的尺寸，不重叠。
 *
 *
 *
 * <pre>
 *  Keyed Windows
 *      stream
 *          .keyBy(...)               <-  keyed versus non-keyed windows
 *          .window(...)              <-  required: "assigner"
 *          [.trigger(...)]            <-  optional: "trigger" (else default trigger)
 *          [.evictor(...)]            <-  optional: "evictor" (else no evictor)
 *          [.allowedLateness(...)]    <-  optional: "lateness" (else zero)
 *          [.sideOutputLateData(...)] <-  optional: "output tag" (else no side output for late data)
 *          .reduce/aggregate/fold/apply()      <-  required: "function"
 *          [.getSideOutput(...)]      <-  optional: "output tag"
 *
 *
 * Non-Keyed Windows
 *      stream
 *          .windowAll(...)           <-  required: "assigner"
 *          [.trigger(...)]            <-  optional: "trigger" (else default trigger)
 *          [.evictor(...)]            <-  optional: "evictor" (else no evictor)
 *          [.allowedLateness(...)]    <-  optional: "lateness" (else zero)
 *          [.sideOutputLateData(...)] <-  optional: "output tag" (else no side output for late data)
 *          .reduce/aggregate/fold/apply()      <-  required: "function"
 *          [.getSideOutput(...)]      <-  optional: "output tag"
 *
 * </pre>
 *
 * @author daocr
 * @date 2020/11/18
 */
@Slf4j
public class TumblingWindow {


    public static void main(String[] args) throws Exception {


        /**
         * 我们需要统计10s中用户购买的商品的总数，需要将用户的行为事件按每一分钟进行切分，
         * 这种切分被成为翻滚时间窗口（Tumbling Time Window）。
         * 翻滚窗口能将数据流切分成不重叠的窗口，每一个事件只能属于一个窗口。
         */

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        DataStreamSource<MockOrderEvent> mockEventDataStreamSource = env.addSource(new MockEventSourceFunction());

        KeyedStream<MockOrderEvent, String> mockEventStringKeyedStream = mockEventDataStreamSource
                .keyBy(MockOrderEvent::getGoodName);


        // 10s 一个窗口
        WindowedStream<MockOrderEvent, String, TimeWindow> mockEventStringTimeWindowWindowedStream = mockEventStringKeyedStream.timeWindow(Time.seconds(60));

        //apply是窗口的应用函数，即apply里的函数将应用在此窗口的数据上。
        SingleOutputStreamOperator<Object> apply = mockEventStringTimeWindowWindowedStream.apply(new WindowFunction<MockOrderEvent, Object, String, TimeWindow>() {
            @Override
            public void apply(String eventName, TimeWindow timeWindow, Iterable<MockOrderEvent> iterable, Collector<Object> collector) throws Exception {

                String start = DateFormatUtils.format(new Date(timeWindow.getStart()), "yyyy-MM-dd HH:mm:ss");
                String end = DateFormatUtils.format(new Date(timeWindow.getEnd()), "yyyy-MM-dd HH:mm:ss");

                IntSummaryStatistics collect = StreamSupport.stream(iterable.spliterator(), false).map(MockOrderEvent::getAmount).collect(Collectors.summarizingInt(Integer::intValue));

                log.info(" 过去一分钟交易信息  eventName：{}  size:{}  start：{} end：{} 总额 amount:{}  最大金额：{} 最小金额 : {} 平均金额 :{} ", eventName, collect.getCount(), start, end, collect.getSum(), collect.getMax(), collect.getMin(), collect.getAverage());

            }
        });


        env.execute();

    }
}
