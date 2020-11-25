[TOC]

## api 介绍

**Using Keyed State**

- ValueState<T> getState(ValueStateDescriptor<T>)
- ReducingState<T> getReducingState(ReducingStateDescriptor<T>)
- ListState<T> getListState(ListStateDescriptor<T>)
- AggregatingState<IN, OUT> getAggregatingState(AggregatingStateDescriptor<IN, ACC, OUT>)
- MapState<UK, UV> getMapState(MapStateDescriptor<UK, UV>)



## 数据访问策略

- StateTtlConfig.UpdateType.OnCreateAndWrite - 只写权限
- StateTtlConfig.UpdateType.OnReadAndWrite -   读写权限
### 过期读取策略
- StateTtlConfig.StateVisibility.NeverReturnExpired - 过期不可访问
- StateTtlConfig.StateVisibility.ReturnExpiredIfNotCleanedUp - 如果过期了，但是还没有删除数据，则还可以访问

### 数据清除策略
#### 后台自动清除策略 
```java
import org.apache.flink.api.common.state.StateTtlConfig;
StateTtlConfig ttlConfig = StateTtlConfig
    .newBuilder(Time.seconds(1))                                
    // 禁用后台定时清除
    .disableCleanupInBackground()
    .build();
```

#### 建立完整快照，清除数据策略
```java
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.time.Time;

StateTtlConfig ttlConfig = StateTtlConfig
    .newBuilder(Time.seconds(1))
     // 建立完整快照后清除过期数据
    .cleanupFullSnapshot()
    .build();
```

#### 堆内存状态后端的增量清理
此方法只适用于堆内存状态后端（FsStateBackend和MemoryStateBackend）。
其基本思路是在存储后端的所有状态条目上维护一个全局的惰性迭代器。某些事件（例如状态访问）会触发增量清理，而每次触发增量清理时，迭代器都会向前遍历删除已遍历的过期数据。
以下代码示例展示了如何启用增量清理：
```java
StateTtlConfig ttlConfig = StateTtlConfig
    .newBuilder(Time.days(7))
    // check 10 keys for every state access
    .cleanupIncrementally(10, false)
    .build();
```
如果启用该功能，则每次状态访问都会触发清除。而每次清理时，都会检查一定数量的状态条目是否过期。其中有两个调整参数。第一个定义了每次清理时要检查的状态条目数。第二个参数是一个标志位，用于表示是否在每条记录处理（Record processed）之后（而不仅仅是访问状态，State accessed），都还额外触发清除逻辑。

关于这种方法有两个重要的注意事项：首先是增量清理所花费的时间会增加记录处理的延迟。其次，如果没有状态被访问（State accessed）或者没有记录被处理（Record processed），过期的状态也将不会被删除。

#### RocksDB 状态后端利用后台压缩来清理过期状态
如果使用 RocksDB 状态后端，则可以启用另一种清理策略，该策略基于 Flink 定制的 RocksDB 压缩过滤器（Compaction filter）。RocksDB 会定期运行异步的压缩流程以合并数据并减少相关存储的数据量，该定制的压缩过滤器使用生存时间检查状态条目的过期时间戳，并丢弃所有过期值。
使用此功能的第一步，需要设置以下配置选项：`state.backend.rocksdb.ttl.compaction.filter.enabled`。一旦配置使用 RocksDB 状态后端后，如以下代码示例将会启用压缩清理策略：
  
```java
// 查看日志：log4j.logger.org.rocksdb.FlinkCompactionFilter=DEBUG
StateTtlConfig ttlConfig = StateTtlConfig
    .newBuilder(Time.days(7))
    .cleanupInRocksdbCompactFilter()
    .build();
```

