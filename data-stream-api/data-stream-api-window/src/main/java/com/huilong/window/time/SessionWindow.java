package com.huilong.window.time;

import com.huilong.mock.dto.MockOrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 会话窗口
 *
 * @author daocr
 * @date 2020/11/18
 */
@Slf4j
public class SessionWindow {

    public static void main(String[] args) throws Exception {


        /**
         *  例如我们需要在用户会话结束后，触发数据计算
         *
         * 在滑窗中，一个元素可以对应多个窗口
         */

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);

        DataStreamSource<MockOrderEvent> mockEventDataStreamSource = env.addSource(new MockOrderEventSourceFunction());

        KeyedStream<MockOrderEvent, Integer> mockEventStringKeyedStream = mockEventDataStreamSource
                .keyBy(MockOrderEvent::getUserId);


        WindowedStream<MockOrderEvent, Integer, TimeWindow> sessionWindow = mockEventStringKeyedStream.window(EventTimeSessionWindows.withGap(Time.seconds(5)));

        //apply是窗口的应用函数，即apply里的函数将应用在此窗口的数据上。
        sessionWindow.apply(new WindowFunction<MockOrderEvent, Object, Integer, TimeWindow>() {
            @Override
            public void apply(Integer userId, TimeWindow timeWindow, Iterable<MockOrderEvent> iterable, Collector<Object> collector) throws Exception {

                IntSummaryStatistics collect = StreamSupport.stream(iterable.spliterator(), false).map(MockOrderEvent::getAmount).collect(Collectors.summarizingInt(Integer::intValue));

                log.info("用户 userId：{} 总额 amount:{}  最大金额：{} 最小金额 : {} 平均金额 :{} ", userId, collect.getSum(), collect.getMax(), collect.getMin(), collect.getAverage());

            }
        });


        env.execute();
    }


    @Slf4j
    private static class MockOrderEventSourceFunction implements SourceFunction<MockOrderEvent> {
        @Override
        public void run(SourceContext<MockOrderEvent> sourceContext) throws Exception {

            ThreadLocalRandom current = ThreadLocalRandom.current();
            AtomicLong eventId = new AtomicLong();
            List<String> eventName = Arrays.asList("登录", "打开首页", "打开详情页", "点击菜单");
            CopyOnWriteArrayList<Integer> userIds = new CopyOnWriteArrayList<>(Arrays.asList(11, 22, 33, 44, 55, 66, 77));

            while (true) {

                MockOrderEvent mockEvent = new MockOrderEvent();
                mockEvent.setEventId(eventId.incrementAndGet());
                mockEvent.setPayTime(new Date());
                int goodIndex = current.nextInt(1, eventName.size() + 1) - 1;
                mockEvent.setGoodName(eventName.get(goodIndex));
                mockEvent.setAmount(current.nextInt(1, 1000));
                int userIdIndex = current.nextInt(1, userIds.size() + 1) - 1;
                mockEvent.setUserId(userIds.get(userIdIndex));
                sourceContext.collect(mockEvent);


                long sleep = current.nextLong(5, 50);

                Thread.sleep(sleep);

                if (eventId.get() == 300) {

                    // 当数据了达到 300 的时候，模拟用户11 下线
                    userIds.remove(new Integer(11));

                    log.info("用户 下线 成功 ,还剩 userIds {}", userIds);

                }

            }

        }

        @Override
        public void cancel() {

        }
    }
}
