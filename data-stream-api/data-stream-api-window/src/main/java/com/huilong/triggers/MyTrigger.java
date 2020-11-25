package com.huilong.triggers;

import com.huilong.mock.MockOrderEvent;
import com.huilong.mock.source.MockEventSourceFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 触发器
 *
 * @author daocr
 * @date 2020/11/20
 */
@Slf4j
public class MyTrigger {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);

        MockEventSourceFunction mockEventSourceFunction = new MockEventSourceFunction()
                .setUserIds(Arrays.asList(11))
                .setSleepMin(Duration.ofMillis(50))
                .setSleepMax(Duration.ofMillis(200));

        DataStreamSource<MockOrderEvent> mockEventDataStreamSource = env.addSource(mockEventSourceFunction);

   /*
            1、用户id和 商品名称 进行分组
            2、每10s 统计一次
         */
        WindowedStream<MockOrderEvent, Integer, TimeWindow> timeWindowStream = mockEventDataStreamSource.
                keyBy(MockOrderEvent::getUserId)
                .timeWindow(Time.seconds(20));

//        timeWindowStream.apply(new WindowFunction<MockOrderEvent, Object, Integer, TimeWindow>() {
//            @Override
//
//            public void apply(Integer integer, TimeWindow timeWindow, Iterable<MockOrderEvent> iterable, Collector<Object> out) throws Exception {
//                String start = DateFormatUtils.format(new Date(timeWindow.getStart()), "yyyy-MM-dd HH:mm:ss");
//                String end = DateFormatUtils.format(new Date(timeWindow.getEnd()), "yyyy-MM-dd HH:mm:ss");
//
//                IntSummaryStatistics collect = StreamSupport.stream(iterable.spliterator(), false).map(MockOrderEvent::getAmount).collect(Collectors.summarizingInt(Integer::intValue));
//
//                log.info("开始计算  size:{}  start：{} end：{} 总额 amount:{}  最大金额：{} 最小金额 : {} 平均金额 :{} ", collect.getCount(), start, end, collect.getSum(), collect.getMax(), collect.getMin(), collect.getAverage());
//
//            }
//        });



        timeWindowStream.trigger(new MockOrderEventTimeWindowTrigger())

                .apply(new WindowFunction<MockOrderEvent, Object, Integer, TimeWindow>() {
                    @Override
                    public void apply(Integer integer, TimeWindow timeWindow, Iterable<MockOrderEvent> iterable, Collector<Object> out) throws Exception {

                        String start = DateFormatUtils.format(new Date(timeWindow.getStart()), "yyyy-MM-dd HH:mm:ss");
                        String end = DateFormatUtils.format(new Date(timeWindow.getEnd()), "yyyy-MM-dd HH:mm:ss");

                        IntSummaryStatistics collect = StreamSupport.stream(iterable.spliterator(), false).map(MockOrderEvent::getAmount).collect(Collectors.summarizingInt(Integer::intValue));

                        log.info("开始计算  size:{}  start：{} end：{} 总额 amount:{}  最大金额：{} 最小金额 : {} 平均金额 :{} ", collect.getCount(), start, end, collect.getSum(), collect.getMax(), collect.getMin(), collect.getAverage());


                    }
                });


        env.execute();

    }

    /**
     * 触发器
     *
     * <pre>
     *
     *          TriggerResult
     *                  .CONTINUE       #继续
     *                  .FIRE           #触发计算，但是不调用 clear 方法
     *                  .FIRE_AND_PURGE #触发计算，并且调用 clear 方法
     *                  .PURGE          #调用clear 方法，但是不触发计算
     * </pre>
     */
    @Slf4j
    private static class MockOrderEventTimeWindowTrigger extends Trigger<MockOrderEvent, TimeWindow> {


        ValueStateDescriptor<Integer> stateDescriptor = new ValueStateDescriptor<>("total", Integer.class);

        @Override
        public TriggerResult onElement(MockOrderEvent element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {

            ValueState<Integer> sumState = ctx.getPartitionedState(stateDescriptor);
            if (null == sumState.value()) {
                sumState.update(0);
            }

            log.info("cut : {}", sumState.value());
            // 数量达到255 的时候，结束窗口
            sumState.update(sumState.value() + 1);
            if (sumState.value() >= 255) {
                //这里可以选择手动处理状态
                sumState.clear();
                log.info(" 已达标 cut : {}", sumState.value());
                return TriggerResult.PURGE;
            }

            return TriggerResult.PURGE;

//            // 注册定时器，当系统时间到达window end timestamp时会回调该trigger的onProcessingTime方法
//            ctx.registerProcessingTimeTimer(window.getEnd());
//            // 注册定时器，当系统时间到达window end timestamp时会回调该trigger的onEventTime方法
//            ctx.registerEventTimeTimer(window.getEnd());


        }

        @Override
        public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {

            return TriggerResult.CONTINUE;
        }

        @Override
        public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
            return TriggerResult.CONTINUE;
        }

        @Override
        public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
            Integer value = ctx.getPartitionedState(stateDescriptor).value();
            log.info("清空 state : {}", value);
            ctx.getPartitionedState(stateDescriptor).clear();
        }
    }
}
