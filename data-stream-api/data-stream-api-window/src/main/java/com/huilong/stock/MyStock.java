package com.huilong.stock;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.huilong.mock.dto.StockTransaction;
import com.huilong.mock.source.LoadCsvSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.io.File;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author daocr
 * @date 2020/11/27
 */
public class MyStock {

    public static void main(String[] args) throws Exception {

        String yyyyMMdd = "2020-11-26 ";

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);
        DataStream<StockTransaction> stockTransactionDataStream = LoadCsvSource.loadStockTransaction(env, "/Users/daocr/Desktop/0601600.csv");

        SingleOutputStreamOperator<StockTransaction> stockTransactionSingleOutputStreamOperator = stockTransactionDataStream
                .assignTimestampsAndWatermarks(WatermarkStrategy.<StockTransaction>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                        .withTimestampAssigner(new SerializableTimestampAssigner<StockTransaction>() {
                            @Override
                            public long extractTimestamp(StockTransaction element, long recordTimestamp) {
                                try {
                                    Date date = DateUtils.parseDate(yyyyMMdd + element.getTransactionTime(), "yyyy-MM-dd HH:mm:ss");
                                    return date.getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            }
                        }));

        stockTransactionSingleOutputStreamOperator.keyBy(StockTransaction::getTransactionType)

                .timeWindow(Time.minutes(5), Time.seconds(5))

                .process(new StockTransactionStringTimeWindowProcessWindowFunction())

                .addSink(new SinkFunction<Result>() {
                    @Override
                    public void invoke(Result value, Context context) throws Exception {
                        FileUtil.appendString(JSONUtil.toJsonStr(value) + ",\n", new File("./文件.json"), "utf-8");
                    }
                });


        env.execute();

    }

    @Slf4j
    private static class StockTransactionStringTimeWindowProcessWindowFunction extends ProcessWindowFunction<StockTransaction, Result, String, TimeWindow> {
        @Override
        public void process(String transactionType, Context context, Iterable<StockTransaction> elements, Collector<Result> out) throws Exception {

            long end = context.window().getEnd();

            List<StockTransaction> collect = StreamSupport.stream(elements.spliterator(), false).collect(Collectors.toList());

            LongSummaryStatistics summaryAmountStatistics = collect.stream().collect(Collectors.summarizingLong(StockTransaction::getTransactionAmount));

            LongSummaryStatistics summaryCutStatistics = collect.stream().collect(Collectors.summarizingLong(StockTransaction::getTransactionCut));

            log.info("transactionType: {} Cut : {} Amount : {}  ", transactionType, summaryCutStatistics, summaryAmountStatistics);

            Result result = new Result();

            result.setDate(DateFormatUtils.format(new Date(end), "yyyy-MM-dd HH:mm:ss"));
            result.setCount(summaryAmountStatistics.getCount());
            result.setMax(summaryAmountStatistics.getMax());
            result.setMin(summaryAmountStatistics.getMin());
            result.setSum(summaryAmountStatistics.getSum());
            out.collect(result);
        }
    }

    @Data
    public static class Result {
        private String date;
        private long count;
        private long sum;
        private long min = Long.MAX_VALUE;
        private long max = Long.MIN_VALUE;
    }

}
