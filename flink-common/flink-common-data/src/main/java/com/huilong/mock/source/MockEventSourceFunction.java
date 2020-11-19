package com.huilong.mock.source;

import com.huilong.mock.MockOrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class MockEventSourceFunction implements SourceFunction<MockOrderEvent> {

    private Duration sleepMin = Duration.ofMillis(5);
    private Duration sleepMax = Duration.ofMillis(50);

    public MockEventSourceFunction() {

    }

    public MockEventSourceFunction(Duration sleepMin, Duration sleepMax) {
        this.sleepMin = sleepMin;
        this.sleepMax = sleepMax;
    }

    @Override
    public void run(SourceFunction.SourceContext<MockOrderEvent> sourceContext) throws Exception {


        ThreadLocalRandom current = ThreadLocalRandom.current();

        AtomicLong eventId = new AtomicLong();

        List<String> eventName = Arrays.asList("电视", "手机", "电脑", "相机");
        List<Integer> userIds = Arrays.asList(11, 22, 33, 44, 55, 66, 77);

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

            long sleep = current.nextLong(sleepMin.toMillis(), sleepMax.toMillis());

            Thread.sleep(sleep);

        }


    }

    @Override
    public void cancel() {

    }
}