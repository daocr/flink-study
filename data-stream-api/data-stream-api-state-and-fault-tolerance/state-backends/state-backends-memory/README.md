[TOC]


### state 分类

#### 普通 state 
- ValueState<T> getState(ValueStateDescriptor<T>)
- ReducingState<T> getReducingState(ReducingStateDescriptor<T>)
- ListState<T> getListState(ListStateDescriptor<T>)
- AggregatingState<IN, OUT> getAggregatingState(AggregatingStateDescriptor<IN, ACC, OUT>)
- MapState<UK, UV> getMapState(MapStateDescriptor<UK, UV>)

#### checkpoint 快照 `CheckpointedFunction`


## MemoryStateBackend 介绍 和 使用
MemoryStateBackend 是将 state 存储在内存中，本地开发或调试时建议使用 MemoryStateBackend，因为这种场景的状态大小的是有限的。
MemoryStateBackend 最适合小状态的应用场景。例如 Kafka consumer，或者一次仅一记录的函数 （Map, FlatMap，或 Filter）。


## 注意事项

- 默认情况下，每一个状态的大小限制为 5 MB。可以通过 MemoryStateBackend 的构造函数增加这个大小。
- 状态大小受到 akka 帧大小的限制，所以无论怎么调整状态大小配置，都不能大于 akka 的帧大小。也可以通过 akka.framesize 调整 akka 帧大小（通过配置文档了解更多）。
- 状态的总大小不能超过 JobManager 的内存。



## 使用例子

### maven 依赖
```xml
<dependencies>
    <!--        基础依赖-->
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-java</artifactId>
        <version>${flink.version}</version>
    </dependency>

    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-streaming-java_${scala.binary.version}</artifactId>
        <version>${flink.version}</version>
    </dependency>

    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-clients_${scala.binary.version}</artifactId>
        <version>${flink.version}</version>
        <!--            <scope>provided</scope>-->
    </dependency>

    <dependency>
        <groupId>com.huilong</groupId>
        <artifactId>flink-common-data</artifactId>
        <version>${project.version}</version>
    </dependency>
</dependencies>
```

### 

## 参考
http://wuchong.me/blog/2018/11/21/flink-tips-how-to-choose-state-backends/

