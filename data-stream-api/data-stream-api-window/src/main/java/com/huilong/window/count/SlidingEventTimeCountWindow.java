package com.huilong.window.count;

import com.huilong.mock.dto.MockOrderEvent;
import com.huilong.mock.source.MockEventSourceFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 滑动计数窗口
 *
 * @author daocr
 * @date 2020/11/19
 */
@Slf4j
public class SlidingEventTimeCountWindow {

    public static void main(String[] args) throws Exception {

        /**
         * 例如我们每10事件计算一次，最近100的元素的聚合信息
         *
         * 在滑窗中，一个元素可以对应多个窗口
         */

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<MockOrderEvent> mockEventDataStreamSource = env.addSource(new MockEventSourceFunction());


        KeyedStream<MockOrderEvent, String> mockEventStringKeyedStream = mockEventDataStreamSource
                .keyBy(MockOrderEvent::getGoodName);


        // 每进入10事件，拉去最近的100的事件作为数据源，来进行计算
        WindowedStream<MockOrderEvent, String, GlobalWindow> mockEventStringGlobalWindowWindowedStream = mockEventStringKeyedStream.countWindow(100, 10);

        //apply是窗口的应用函数，即apply里的函数将应用在此窗口的数据上。
        mockEventStringGlobalWindowWindowedStream.apply(new WindowFunction<MockOrderEvent, Object, String, GlobalWindow>() {

            @Override
            public void apply(String eventName, GlobalWindow globalWindow, Iterable<MockOrderEvent> iterable, Collector<Object> collector) throws Exception {

                IntSummaryStatistics collect = StreamSupport.stream(iterable.spliterator(), false).map(e -> e.getAmount()).collect(Collectors.summarizingInt(Integer::intValue));

                log.info("每10秒，统计最近100个订单信息： eventName：{} 总额 amount:{}  最大金额：{} 最小金额 : {} 平均金额 :{} ", eventName, collect.getSum(), collect.getMax(), collect.getMin(), collect.getAverage());

            }
        });


        env.execute();


    }
}
