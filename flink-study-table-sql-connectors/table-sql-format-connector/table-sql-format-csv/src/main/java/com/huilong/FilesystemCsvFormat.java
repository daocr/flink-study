package com.huilong;

import org.apache.commons.io.FileUtils;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

import java.io.File;
import java.io.IOException;

/**
 * 文件系统 加载 csv
 */
public class FilesystemCsvFormat {

    public static void main(String[] args) throws IOException {

        String ddl = FileUtils.readFileToString(new File(FilesystemCsvFormat.class.getResource("/").getPath() + "FilesystemCreateTable.sql"));

        EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance()
                .build();

        TableEnvironment tableEnvironment = TableEnvironment.create(environmentSettings);

        // 注册 表
        tableEnvironment.executeSql(ddl);

        // 查询计划
        Table result = tableEnvironment.sqlQuery(
                "SELECT *  FROM MyUserTable ");
        // 执行查询
        TableResult execute = result.execute();

        // 打印输出
        execute.print();
    }
}
