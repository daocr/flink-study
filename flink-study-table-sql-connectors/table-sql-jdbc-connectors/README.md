[TOC]

## 介绍



## 准备工作

### 驱动依赖

| Driver     | Group Id           | Artifact Id            | JAR                                                          |
| :--------- | :----------------- | :--------------------- | :----------------------------------------------------------- |
| MySQL      | `mysql`            | `mysql-connector-java` | [Download](https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/) |
| PostgreSQL | `org.postgresql`   | `postgresql`           | [Download](https://jdbc.postgresql.org/download.html)        |
| Derby      | `org.apache.derby` | `derby`                | [Download](http://db.apache.org/derby/derby_downloads.html)  |

### jar 依赖

```xml
    <dependencies>

        <!--       核心依赖     -->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-java</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-streaming-java_${scala.binary.version}</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-clients_${scala.binary.version}</artifactId>
        </dependency>

        <!--        flink table 依赖-->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-table-api-java-bridge_${scala.binary.version}</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.flink</groupId>
             <artifactId>flink-table-api-java-bridge_${scala.binary.version}</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-table-planner_${scala.binary.version}</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-table-planner-blink_${scala.binary.version}</artifactId>
        </dependency>

        <!--     jdbc connector    -->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-jdbc_2.11</artifactId>
            <version>1.11.2</version>
        </dependency>


        <!--    mysql  驱动-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.46</version>
        </dependency>

    </dependencies>
```
## 功能说明

### 参数介绍

| Option                     | Required | Default | Type     | Description                                                  |
| :------------------------- | :------- | :------ | :------- | :----------------------------------------------------------- |
| connector                  | required | (none)  | String   | Specify what connector to use, here should be `'jdbc'`.      |
| url                        | required | (none)  | String   | 连接字符串                                                   |
| table-name                 | required | (none)  | String   | 数据库的表名称                                               |
| driver                     | optional | (none)  | String   | 驱动 class                                                   |
| username                   | optional | (none)  | String   | 用户名                                                       |
| password                   | optional | (none)  | String   | 密码                                                         |
| scan.partition.column      | optional | (none)  | String   | 对于列名称进行分区                                           |
| scan.partition.num         | optional | (none)  | Integer  | 数量进行分区                                                 |
| scan.partition.lower-bound | optional | (none)  | Integer  | The smallest value of the first partition.                   |
| scan.partition.upper-bound | optional | (none)  | Integer  | The largest value of the last partition.                     |
| scan.fetch-size            | optional | 0       | Integer  | The number of rows that should be fetched from the database when reading per round trip. If the value specified is zero, then the hint is ignored. |
| lookup.cache.max-rows      | optional | (none)  | Integer  | 最大缓存条数                                                 |
| lookup.cache.ttl           | optional | (none)  | Integer  | 查询缓存过期时间                                             |
| lookup.max-retries         | optional | 3       | Integer  | 最多重试次数                                                 |
| sink.buffer-flush.max-rows | optional | 100     | Integer  | The max size of buffered records before flush. Can be set to zero to disable it. |
| sink.buffer-flush.interval | optional | 1s      | Duration | The flush interval mills, over this time, asynchronous threads will flush data. Can be set to `'0'` to disable it. Note, `'sink.buffer-flush.max-rows'` can be set to `'0'` with the flush interval set allowing for complete async processing of buffered actions. |
| sink.max-retries           | optional | 3       | Integer  | The max retry times if writing records to database failed.   |

### 数据类型映射

Flink supports connect to several databases which uses dialect like MySQL, PostgresSQL, Derby. The Derby dialect usually used for testing purpose. The field data type mappings from relational databases data types to Flink SQL data types are listed in the following table, the mapping table can help define JDBC table in Flink easily.

| MySQL type                            | PostgreSQL type                                              | Flink SQL type                       |
| :------------------------------------ | :----------------------------------------------------------- | :----------------------------------- |
| `TINYINT`                             |                                                              | `TINYINT`                            |
| `SMALLINT` `TINYINT UNSIGNED`         | `SMALLINT` `INT2` `SMALLSERIAL` `SERIAL2`                    | `SMALLINT`                           |
| `INT` `MEDIUMINT` `SMALLINT UNSIGNED` | `INTEGER` `SERIAL`                                           | `INT`                                |
| `BIGINT` `INT UNSIGNED`               | `BIGINT` `BIGSERIAL`                                         | `BIGINT`                             |
| `BIGINT UNSIGNED`                     |                                                              | `DECIMAL(20, 0)`                     |
| `BIGINT`                              | `BIGINT`                                                     | `BIGINT`                             |
| `FLOAT`                               | `REAL` `FLOAT4`                                              | `FLOAT`                              |
| `DOUBLE` `DOUBLE PRECISION`           | `FLOAT8` `DOUBLE PRECISION`                                  | `DOUBLE`                             |
| `NUMERIC(p, s)` `DECIMAL(p, s)`       | `NUMERIC(p, s)` `DECIMAL(p, s)`                              | `DECIMAL(p, s)`                      |
| `BOOLEAN` `TINYINT(1)`                | `BOOLEAN`                                                    | `BOOLEAN`                            |
| `DATE`                                | `DATE`                                                       | `DATE`                               |
| `TIME [(p)]`                          | `TIME [(p)] [WITHOUT TIMEZONE]`                              | `TIME [(p)] [WITHOUT TIMEZONE]`      |
| `DATETIME [(p)]`                      | `TIMESTAMP [(p)] [WITHOUT TIMEZONE]`                         | `TIMESTAMP [(p)] [WITHOUT TIMEZONE]` |
| `CHAR(n)` `VARCHAR(n)` `TEXT`         | `CHAR(n)` `CHARACTER(n)` `VARCHAR(n)` `CHARACTER VARYING(n)` `TEXT` | `STRING`                             |
| `BINARY` `VARBINARY` `BLOB`           | `BYTEA`                                                      | `BYTES`                              |
|                                       | `ARRAY`                                                      | `ARRAY`                              |

## 示例

