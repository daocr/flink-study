package com.huilong.environnment;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

/**
 * bink 批处理
 *
 * @author daocr
 * @date 2020/11/18
 */
public class BlinkBatchQuery {

    public static void main(String[] args) {
        EnvironmentSettings bbSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inBatchMode().build();
        TableEnvironment bbTableEnv = TableEnvironment.create(bbSettings);
    }
}
