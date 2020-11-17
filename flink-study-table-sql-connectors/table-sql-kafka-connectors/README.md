[TOC]

## 介绍



## 准备工作

### connector 依赖

| Kafka Version | Maven dependency                  | SQL Client JAR                                               |
| :------------ | :-------------------------------- | :----------------------------------------------------------- |
| universal     | `flink-connector-kafka_2.11`      | [Download](https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-kafka_2.11/1.11.2/flink-sql-connector-kafka_2.11-1.11.2.jar) |
| 0.11.x        | `flink-connector-kafka-0.11_2.11` | [Download](https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-kafka-0.11_2.11/1.11.2/flink-sql-connector-kafka-0.11_2.11-1.11.2.jar) |
| 0.10.x        | `flink-connector-kafka-0.10_2.11` | [Download](https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-kafka-0.10_2.11/1.11.2/flink-sql-connector-kafka-0.10_2.11-1.11.2.jar) |


### jar 依赖

```xml 


```
## 功能说明

### 参数介绍

| Option                        |      Required      |    Default    |  Type  |                         Description                          |
| :---------------------------- | :----------------: | :-----------: | :----: | :----------------------------------------------------------: |
| connector                     |      required      |    (none)     | String | Specify what connector to use, for Kafka the options are: `'kafka'`, `'kafka-0.11'`, `'kafka-0.10'`. |
| topic                         |      required      |    (none)     | String |           Topic name from which the table is read.           |
| properties.bootstrap.servers  |      required      |    (none)     | String |            Comma separated list of Kafka brokers.            |
| properties.group.id           | required by source |    (none)     | String | The id of the consumer group for Kafka source, optional for Kafka sink. |
| format                        |      required      |    (none)     | String | The format used to deserialize and serialize Kafka messages. The supported formats are `'csv'`, `'json'`, `'avro'`, `'debezium-json'` and `'canal-json'`. Please refer to [Formats](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/formats/) page for more details and more format options. |
| scan.startup.mode             |      optional      | group-offsets | String | Startup mode for Kafka consumer, valid values are `'earliest-offset'`, `'latest-offset'`, `'group-offsets'`, `'timestamp'` and `'specific-offsets'`. See the following [Start Reading Position](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/kafka.html#start-reading-position) for more details. |
| scan.startup.specific-offsets |      optional      |    (none)     | String | Specify offsets for each partition in case of `'specific-offsets'` startup mode, e.g. `'partition:0,offset:42;partition:1,offset:300'`. |
| scan.startup.timestamp-millis |      optional      |    (none)     |  Long  | Start from the specified epoch timestamp (milliseconds) used in case of `'timestamp'` startup mode. |
| sink.partitioner              |      optional      |    (none)     | String | Output partitioning from Flink's partitions into Kafka's partitions. Valid values are`fixed`: each Flink partition ends up in at most one Kafka partition.`round-robin`: a Flink partition is distributed to Kafka partitions round-robin.Custom `FlinkKafkaPartitioner` subclass: e.g. `'org.mycompany.MyPartitioner'`. |



  
## 示例

