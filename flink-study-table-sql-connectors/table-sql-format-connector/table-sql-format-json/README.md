[TOC]

## 介绍

| ormats                                                       | Supported Connectors                                         |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| [CSV](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/formats/csv.html) | [Apache Kafka](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/kafka.html), [Filesystem](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/filesystem.html) |
| [JSON](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/formats/json.html) | [Apache Kafka](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/kafka.html), [Filesystem](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/filesystem.html), [Elasticsearch](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/elasticsearch.html) |
| [Apache Avro](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/formats/avro.html) | [Apache Kafka](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/kafka.html), [Filesystem](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/filesystem.html) |
| [Debezium CDC](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/formats/debezium.html) | [Apache Kafka](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/kafka.html) |
| [Canal CDC](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/formats/canal.html) | [Apache Kafka](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/kafka.html) |
| [Apache Parquet](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/formats/parquet.html) | [Filesystem](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/filesystem.html) |
| [Apache ORC](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/formats/orc.html) | [Filesystem](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/filesystem.html) |

## 准备工作

### 驱动依赖

```xml
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-json</artifactId>
    <version>1.11.2</version>
</dependency>
```
 
### jar 依赖
```xml
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
    <artifactId>flink-table-api-scala-bridge_${scala.binary.version}</artifactId>
</dependency>

<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-table-planner_${scala.binary.version}</artifactId>
</dependency>

<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-table-planner-blink_${scala.binary.version}</artifactId>
</dependency>

```   
## 功能说明
 

### 参数介绍

## Format Options

| Option                         | Required | Default |  Type   |                         Description                          |
| :----------------------------- | :------: | :-----: | :-----: | :----------------------------------------------------------: |
| format                         | required | (none)  | String  |     Specify what format to use, here should be `'json'`.     |
| json.fail-on-missing-field     | optional |  false  | Boolean |        Whether to fail if a field is missing or not.         |
| json.ignore-parse-errors       | optional |  false  | Boolean | Skip fields and rows with parse errors instead of failing. Fields are set to null in case of errors. |
| json.timestamp-format.standard | optional | `'SQL'` | String  | Specify the input and output timestamp format. Currently supported values are `'SQL'` and `'ISO-8601'`:Option `'SQL'` will parse input timestamp in "yyyy-MM-dd HH:mm:ss.s{precision}" format, e.g '2020-12-30 12:13:14.123' and output timestamp in the same format.Option `'ISO-8601'`will parse input timestamp in "yyyy-MM-ddTHH:mm:ss.s{precision}" format, e.g '2020-12-30T12:13:14.123' and output timestamp in the same format. |
 

## Data Type Mapping

Currently, the JSON schema is always derived from table schema. Explicitly defining an JSON schema is not supported yet.

Flink JSON format uses [jackson databind API](https://github.com/FasterXML/jackson-databind) to parse and generate JSON string.

The following table lists the type mapping from Flink type to JSON type.

| Flink SQL type            | JSON type                       |
| :------------------------ | :------------------------------ |
| `CHAR / VARCHAR / STRING` | `string`                        |
| `BOOLEAN`                 | `boolean`                       |
| `BINARY / VARBINARY`      | `string with encoding: base64`  |
| `DECIMAL`                 | `number`                        |
| `TINYINT`                 | `number`                        |
| `SMALLINT`                | `number`                        |
| `INT`                     | `number`                        |
| `BIGINT`                  | `number`                        |
| `FLOAT`                   | `number`                        |
| `DOUBLE`                  | `number`                        |
| `DATE`                    | `string with format: date`      |
| `TIME`                    | `string with format: time`      |
| `TIMESTAMP`               | `string with format: date-time` |
| `INTERVAL`                | `number`                        |
| `ARRAY`                   | `array`                         |
| `MAP / MULTISET`          | `object`                        |
| `ROW`                     | `object`                        |


## 示例

