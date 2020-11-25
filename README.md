# flink-study
flink  study demo


## 快速创建项目

```
mvn archetype:generate                               \
  -DarchetypeGroupId=org.apache.flink              \
  -DarchetypeArtifactId=flink-quickstart-java      \
  -DarchetypeVersion=1.11.2
```


### 项目结构

### [data set api](https://github.com/daocr/flink-study/tree/feature/1.11/data-set-api)



- └── [data set api](https://github.com/daocr/flink-study/tree/feature/1.11/data-set-api)
  - ├── data-set-api-execution
  - ├── data-set-api-operators
  - ├── data-set-api-sinks
  - └── data-set-api-sources

```
└── data-set-api
   ├── data-set-api-execution
   ├── data-set-api-operators
   ├── data-set-api-sinks
   └── data-set-api-sources
```

#### data-set-connectors
```
└── flink-study
   ├── data-set-connectors
```

#### flink-cep
```
└── flink-study
   ├── data-set-connectors
```
#### serialization
```
└── flink-study
   ├── data-types-and-serialization
```

#### data-stream-api
```
└── data-stream-api
   ├── data-stream-ap-sources
   ├── data-stream-api-event-time
   │   └── data-stream-api-event-time-watermark
   ├── data-stream-api-side-outputs
   ├── data-stream-api-sinks
   ├── data-stream-api-state-and-fault-tolerance
   │   ├── broadcast-state-pattern
   │   ├── checkpoint
   │   ├── queryable-stated-beta
   │   ├── state-backends
   │   │   └── state-backends-memory
   │   └── working-with-state
   ├── data-stream-api-window
   └── data-stream-operators
```

#### data-stream-connectors
```
└── data-stream-connectors
   ├── data-stream-apache-cassandra-connector
   ├── data-stream-elasticsearch-connector
   ├── data-stream-hadoop-fileSystem-connector
   ├── data-stream-jdbc-connector
   ├── data-stream-kafka-connector
   ├── data-stream-rabbitMQ-connector
   └── data-stream-streaming-file-sink
```

#### 
```
└── flink-study-table-sql-connectors
   ├── table-sql-data-gen-connector
   ├── table-sql-elasticsearch-connector
   ├── table-sql-file-system-connector
   ├── table-sql-format-connector
   │   ├── table-sql-format-apache-avro
   │   ├── table-sql-format-apache-orc
   │   ├── table-sql-format-apache-parquet
   │   ├── table-sql-format-canal-cdc
   │   ├── table-sql-format-csv
   │   ├── table-sql-format-debezium-cdc
   │   └── table-sql-format-json
   ├── table-sql-hbase-connector
   ├── table-sql-jdbc-connectors
   └── table-sql-kafka-connectors
```

#### table-api-and-sql
```
└── table-api-and-sql
    ├── table-api
    ├── table-api-and-sql-catalog
    ├── table-api-and-sql-detecting-patterns
    ├── table-api-and-sql-dynamic-table
    ├── table-api-and-sql-environment
    │   ├── blink-environment
    │   └── flink-environment
    ├── table-api-and-sql-query
    ├── table-api-and-sql-temporal-table
    ├── table-api-and-sql-view
    └── table-sql
```