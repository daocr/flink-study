package com.huilong.mock.source;

import com.huilong.mock.dto.MockOrderEvent;
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

    public MockEventSourceFunction() {

    }

    public MockEventSourceFunction(Duration sleepMin, Duration sleepMax) {
        this.sleepMin = sleepMin;
        this.sleepMax = sleepMax;
    }

    private Duration sleepMin = Duration.ofMillis(5);
    private Duration sleepMax = Duration.ofMillis(50);


    /**
     * 是否显示生产的数据
     */
    private boolean showGenData = false;

    private List<String> eventName = Arrays.asList("电视", "手机", "电脑", "相机");
    private List<Integer> userIds = Arrays.asList(11, 22, 33, 44, 55, 66, 77);


    @Override
    public void run(SourceFunction.SourceContext<MockOrderEvent> sourceContext) throws Exception {


        ThreadLocalRandom current = ThreadLocalRandom.current();

        AtomicLong eventId = new AtomicLong();


        while (true) {

            MockOrderEvent mockEvent = new MockOrderEvent();
            mockEvent.setEventId(eventId.incrementAndGet());
            mockEvent.setPayTime(new Date());
            int goodIndex = current.nextInt(1, eventName.size() + 1) - 1;
            mockEvent.setGoodName(eventName.get(goodIndex));
            mockEvent.setAmount(current.nextInt(1, 1000));
            int userIdIndex = current.nextInt(1, userIds.size() + 1) - 1;
            mockEvent.setUserId(userIds.get(userIdIndex));

            if (showGenData) {
                log.info("gen data : {}", mockEvent);
            }

            sourceContext.collect(mockEvent);

            long sleep = current.nextLong(sleepMin.toMillis(), sleepMax.toMillis());

            Thread.sleep(sleep);

        }


    }

    @Override
    public void cancel() {

    }

    public boolean isShowGenData() {
        return showGenData;
    }

    public MockEventSourceFunction setShowGenData(boolean showGenData) {
        this.showGenData = showGenData;
        return this;
    }

    public Duration getSleepMin() {
        return sleepMin;
    }

    public MockEventSourceFunction setSleepMin(Duration sleepMin) {
        this.sleepMin = sleepMin;
        return this;
    }

    public Duration getSleepMax() {
        return sleepMax;
    }

    public MockEventSourceFunction setSleepMax(Duration sleepMax) {
        this.sleepMax = sleepMax;
        return this;
    }

    public List<String> getEventName() {
        return eventName;
    }

    public MockEventSourceFunction setEventName(List<String> eventName) {
        this.eventName = eventName;
        return this;
    }

    public List<Integer> getUserIds() {
        return userIds;
    }

    public MockEventSourceFunction setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
        return this;
    }


}