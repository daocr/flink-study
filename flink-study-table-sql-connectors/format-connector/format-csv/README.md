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
<!-- https://mvnrepository.com/artifact/org.apache.flink/flink-csv -->
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-csv</artifactId>
    <version>1.11.2</version>
</dependency>
```
 
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
</dependencies>
```   
## 功能说明
 

### 参数介绍
 ## Format Options
 
 | Option                      | Required | Default |  Type   |                         Description                          |
 | :-------------------------- | :------: | :-----: | :-----: | :----------------------------------------------------------: |
 | format                      | required | (none)  | String  |     Specify what format to use, here should be `'csv'`.      |
 | csv.field-delimiter         | optional |   `,`   | String  |        Field delimiter character (`','` by default).         |
 | csv.line-delimiter          | optional |  `\n`   | String  | Line delimiter, `\n` by default. Note the `\n` and `\r` are invisible special characters, you have to use unicode to specify them in plain SQL.e.g. `'csv.line-delimiter' = U&'\000D'` specifies the to use carriage return `\r` as line delimiter.e.g. `'csv.line-delimiter' = U&'\000A'` specifies the to use line feed `\n` as line delimiter. |
 | csv.disable-quote-character | optional |  false  | Boolean | Disabled quote character for enclosing field values (false by default). If true, option `'csv.quote-character'` can not be set. |
 | csv.quote-character         | optional |   `"`   | String  | Quote character for enclosing field values (`"` by default). |
 | csv.allow-comments          | optional |  false  | Boolean | Ignore comment lines that start with `'#'` (disabled by default). If enabled, make sure to also ignore parse errors to allow empty rows. |
 | csv.ignore-parse-errors     | optional |  false  | Boolean | Skip fields and rows with parse errors instead of failing. Fields are set to null in case of errors. |
 | csv.array-element-delimiter | optional |   `;`   | String  | Array element delimiter string for separating array and row element values (`';'` by default). |
 | csv.escape-character        | optional | (none)  | String  | Escape character for escaping values (disabled by default).  |
 | csv.null-literal            | optional | (none)  | String  | Null literal string that is interpreted as a null value (disabled by default). |
 
 
## Data Type Mapping

Currently, the CSV schema is always derived from table schema. Explicitly defining an CSV schema is not supported yet.

Flink CSV format uses [jackson databind API](https://github.com/FasterXML/jackson-databind) to parse and generate CSV string.

The following table lists the type mapping from Flink type to CSV type.

| Flink SQL type            | CSV type                        |
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
| `ROW`                     | `object`                        |
 
 
## 示例

