package com.huilong.environnment;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;

/**
 * flink 批处理 查询
 *
 * @author daocr
 * @date 2020/11/18
 */
public class FlinkBatchTableQuery {

    public static void main(String[] args) {
        ExecutionEnvironment fbEnv = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment fbTableEnv = BatchTableEnvironment.create(fbEnv);


    }
}
