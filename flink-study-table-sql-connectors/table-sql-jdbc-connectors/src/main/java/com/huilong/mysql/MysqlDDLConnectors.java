package com.huilong.mysql;

import org.apache.commons.io.FileUtils;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

import java.io.File;

/**
 * mysql 数据库 Connectors
 *
 * @author daocr
 * @date 2020/11/14
 */
public class MysqlDDLConnectors {

    // https://iteblog.blog.csdn.net/article/details/108656414


    public static void main(String[] args) throws Exception {

        String ddl = FileUtils.readFileToString(new File(MysqlDDLConnectors.class.getResource("/").getPath() + "createMysqlTable.sql"));

        EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance()
                .build();
        TableEnvironment tableEnvironment = TableEnvironment.create(environmentSettings);

        // 注册 表
        tableEnvironment.executeSql(ddl);

//        tableEnvironment.executeSql("insert into MyUserTable values (3,'李四','888@qq.com','123password',2)");

        // 查询计划
        Table result = tableEnvironment.sqlQuery(
                "SELECT name,email  FROM MyUserTable ");

        // 执行查询
        TableResult execute = result.execute();

        // 打印输出
        execute.print();

    }
}
