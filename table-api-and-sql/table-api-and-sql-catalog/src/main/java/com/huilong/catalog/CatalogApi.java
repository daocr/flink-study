package com.huilong.catalog;

import org.apache.flink.connector.jdbc.catalog.JdbcCatalogUtils;
import org.apache.flink.connector.jdbc.catalog.PostgresCatalog;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.CatalogBaseTable;
import org.apache.flink.table.catalog.ObjectPath;
import org.apache.flink.table.catalog.exceptions.TableNotExistException;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

import java.util.stream.Stream;

/**
 * java 创建 catelog
 *
 * @author daocr
 * @date 2020/12/9
 */
public class CatalogApi {

    public static void main(String[] args) throws TableNotExistException {

        EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
        StreamExecutionEnvironment fsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(fsEnv, fsSettings);

        String catalogName = "mycatalog";
        String defaultDatabase = "postgres";
        String username = "huilong";
        String pwd = "123456";
        String baseUrl = "jdbc:postgresql://localhost:5432/";


//        JdbcCatalog postgresCatalog = new JdbcCatalog(catalogName, defaultDatabase, username, pwd, baseUrl);

//
        PostgresCatalog postgresCatalog = (PostgresCatalog) JdbcCatalogUtils.createCatalog(
                catalogName,
                defaultDatabase,
                username,
                pwd,
                baseUrl);

        tEnv.registerCatalog(postgresCatalog.getName(), postgresCatalog);
        tEnv.useCatalog(postgresCatalog.getName());

        System.out.println("show databases :");
        String[] databases = tEnv.listDatabases();
        Stream.of(databases).forEach(e -> System.out.println("\t" + e));
        System.out.println("--------------------- :");
//
        tEnv.useDatabase(defaultDatabase);
        System.out.println("show tables :");
        String[] tables = tEnv.listTables(); // 也可以使用  postgresCatalog.listTables(defaultDatabase);
        Stream.of(tables).forEach(e -> System.out.println("\t" + e));

        System.out.println("show functions :");
        String[] functions = tEnv.listFunctions();
        Stream.of(functions).forEach(e -> System.out.println("\t" + e));

        CatalogBaseTable catalogBaseTable = postgresCatalog.getTable(new ObjectPath(
                defaultDatabase,
                "emp"));

        TableSchema tableSchema = catalogBaseTable.getSchema();

        System.out.println("tableSchema --------------------- :");
        System.out.println(tableSchema);


        CloseableIterator<Row> collect = tEnv.sqlQuery("select * from dept")
                .execute()
                .collect();

        collect.forEachRemaining(e -> System.out.println("\t" + e));

//
//        tEnv.executeSql("insert into table1 values (3,'c')");
//
//        System.out.println("after insert  --------------------- :");
//        List<Row> results1 = Lists.newArrayList(tEnv.sqlQuery("select * from table1")
//                .execute()
//                .collect());
//        results1.stream().forEach(System.out::println);
//
//

    }
}
