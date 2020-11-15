[TOC]

## avro 介绍

Avro（读音类似于[ævrə]）是Hadoop的一个子项目，由Hadoop的 创始人Doug Cutting（也是Lucene，Nutch等项目的创始人）牵头开发。
Avro是一个数据序列化系统，设计用于支持大 批量数据交换的应用。
它的主要特点有：支持二进制序列化方式，可以便捷，快速地处理大量数据；动态语言友好，Avro提供的机制使动态语言可以方便地处理 Avro数据。

## 准备工作

### 驱动依赖
 
### jar 依赖
   
## 功能说明

### 参数介绍

## Format Options

| Option     | Required | Default |  Type  |                         Description                          |
| :--------- | :------: | :-----: | :----: | :----------------------------------------------------------: |
| format     | required | (none)  | String |     Specify what format to use, here should be `'avro'`.     |
| avro.codec | optional | (none)  | String | For [Filesystem](https://ci.apache.org/projects/flink/flink-docs-release-1.11/dev/table/connectors/filesystem.html) only, the compression codec for avro. No compression as default. The valid enumerations are: deflate, snappy, bzip2, xz. |
 
## 类型映射
## Data Type Mapping

Currently, the Avro schema is always derived from table schema. Explicitly defining an Avro schema is not supported yet. So the following table lists the type mapping from Flink type to Avro type.

| Flink SQL type                                        | Avro type | Avro logical type  |
| :---------------------------------------------------- | :-------- | :----------------- |
| CHAR / VARCHAR / STRING                               | string    |                    |
| `BOOLEAN`                                             | `boolean` |                    |
| `BINARY / VARBINARY`                                  | `bytes`   |                    |
| `DECIMAL`                                             | `fixed`   | `decimal`          |
| `TINYINT`                                             | `int`     |                    |
| `SMALLINT`                                            | `int`     |                    |
| `INT`                                                 | `int`     |                    |
| `BIGINT`                                              | `long`    |                    |
| `FLOAT`                                               | `float`   |                    |
| `DOUBLE`                                              | `double`  |                    |
| `DATE`                                                | `int`     | `date`             |
| `TIME`                                                | `int`     | `time-millis`      |
| `TIMESTAMP`                                           | `long`    | `timestamp-millis` |
| `ARRAY`                                               | `array`   |                    |
| `MAP` (key must be string/char/varchar type)          | `map`     |                    |
| `MULTISET` (element must be string/char/varchar type) | `map`     |                    |
| `ROW`                                                 | `record`  |                    |


 
## 示例

