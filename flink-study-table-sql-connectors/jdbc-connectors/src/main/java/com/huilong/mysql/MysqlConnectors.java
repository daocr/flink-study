package com.huilong.mysql;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

/**
 * mysql 数据库 Connectors
 *
 * @author daocr
 * @date 2020/11/14
 */
public class MysqlConnectors {

    // https://iteblog.blog.csdn.net/article/details/108656414

    private static String ddl = "CREATE TABLE MyUserTable\n" +
            "(\n" +
            "    id     int,\n" +
            "    name STRING,\n" +
            "    email    STRING,\n" +
            "    password STRING,\n" +
            "    PRIMARY KEY (id) NOT ENFORCED\n" +
            ")\n" +
            "WITH (\n" +
            "    'connector' = 'jdbc',\n" +
            "    'username' = 'yhl',\n" +
            "    'password' = '123456',\n" +
            "    'url' = 'jdbc:mysql://localhost:3306/demo',\n" +
            "    'table-name' = 'user_info'\n" +
            ")";

    public static void main(String[] args) throws Exception {

        EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance()
                .build();
        TableEnvironment tableEnvironment = TableEnvironment.create(environmentSettings);

        // 注册 表
        tableEnvironment.executeSql(ddl);

        // 查询计划
        Table result = tableEnvironment.sqlQuery(
                "SELECT name,email  FROM MyUserTable ");

        // 执行查询
        TableResult execute = result.execute();

        // 打印输出
        execute.print();

    }


}
