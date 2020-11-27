package com.huilong.broadcast;

import com.huilong.mock.dto.MockOrderEvent;
import com.huilong.mock.source.MockEventSourceFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;

/**
 * 广播
 *
 * @author daocr
 * @date 2020/11/23
 */
public class UsingBroadcastState {

    public static void main(String[] args) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);

        MockEventSourceFunction mockEventSourceFunction = new MockEventSourceFunction()
                .setSleepMin(Duration.ofMillis(50))
                .setSleepMax(Duration.ofMillis(200));

        DataStreamSource<MockOrderEvent> mockEventDataStreamSource = env.addSource(mockEventSourceFunction);

    }
}
