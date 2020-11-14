
## 介绍

## 准备工作

### 依赖


## 功能说明

## Connector Options

| Option                     | Required | Default | Type     | Description                                                  |
| :------------------------- | :------- | :------ | :------- | :----------------------------------------------------------- |
| connector                  | required | (none)  | String   | Specify what connector to use, here should be `'jdbc'`.      |
| url                        | required | (none)  | String   | The JDBC database url.                                       |
| table-name                 | required | (none)  | String   | The name of JDBC table to connect.                           |
| driver                     | optional | (none)  | String   | The class name of the JDBC driver to use to connect to this URL, if not set, it will automatically be derived from the URL. |
| username                   | optional | (none)  | String   | The JDBC user name. `'username'` and `'password'` must both be specified if any of them is specified. |
| password                   | optional | (none)  | String   | The JDBC password.                                           |
| scan.partition.column      | optional | (none)  | String   | The column name used for partitioning the input. See the following [Partitioned Scan](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/jdbc.html#partitioned-scan) section for more details. |
| scan.partition.num         | optional | (none)  | Integer  | The number of partitions.                                    |
| scan.partition.lower-bound | optional | (none)  | Integer  | The smallest value of the first partition.                   |
| scan.partition.upper-bound | optional | (none)  | Integer  | The largest value of the last partition.                     |
| scan.fetch-size            | optional | 0       | Integer  | The number of rows that should be fetched from the database when reading per round trip. If the value specified is zero, then the hint is ignored. |
| lookup.cache.max-rows      | optional | (none)  | Integer  | The max number of rows of lookup cache, over this value, the oldest rows will be expired. Lookup cache is disabled by default. See the following [Lookup Cache](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/jdbc.html#lookup-cache) section for more details. |
| lookup.cache.ttl           | optional | (none)  | Integer  | The max time to live for each rows in lookup cache, over this time, the oldest rows will be expired. Lookup cache is disabled by default. See the following [Lookup Cache](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/jdbc.html#lookup-cache) section for more details. |
| lookup.max-retries         | optional | 3       | Integer  | The max retry times if lookup database failed.               |
| sink.buffer-flush.max-rows | optional | 100     | Integer  | The max size of buffered records before flush. Can be set to zero to disable it. |
| sink.buffer-flush.interval | optional | 1s      | Duration | The flush interval mills, over this time, asynchronous threads will flush data. Can be set to `'0'` to disable it. Note, `'sink.buffer-flush.max-rows'` can be set to `'0'` with the flush interval set allowing for complete async processing of buffered actions. |
| sink.max-retries           | optional | 3       | Integer  | The max retry times if writing records to database failed.   |


## 示例

