# flink-study
flink  study demo


## 快速创建项目

```
mvn archetype:generate                               \
  -DarchetypeGroupId=org.apache.flink              \
  -DarchetypeArtifactId=flink-quickstart-java      \
  -DarchetypeVersion=1.11.2
```


```

./flink-study
├── flink-common
│   └── flink-common-kafka
└── flink-study-table-sql-connectors
    ├── format-connector
    │   ├── format-apache-avro
    │   ├── format-apache-orc
    │   ├── format-apache-parquet
    │   ├── format-canal-cdc
    │   ├── format-csv
    │   ├── format-debezium-cdc
    │   └── format-json
    ├── jdbc-connectors
    └── kafka-connectors

```