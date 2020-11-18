package com.huilong.environnment;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * flink 流 查询
 *
 * @author daocr
 * @date 2020/11/18
 */
public class FlinkStreamQuery {

    public static void main(String[] args) {

        EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
        StreamExecutionEnvironment fsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment fsTableEnv = StreamTableEnvironment.create(fsEnv, fsSettings);

        // or TableEnvironment fsTableEnv = TableEnvironment.create(fsSettings);


    }
}
