[TOC]

## api 介绍

**Keyed Windows**

```
stream
       .keyBy(...)               <-  keyed versus non-keyed windows
       .window(...)              <-  required: "assigner"
      [.trigger(...)]            <-  optional: "trigger" (else default trigger)
      [.evictor(...)]            <-  optional: "evictor" (else no evictor)
      [.allowedLateness(...)]    <-  optional: "lateness" (else zero)
      [.sideOutputLateData(...)] <-  optional: "output tag" (else no side output for late data)
       .reduce/aggregate/fold/apply()      <-  required: "function"
      [.getSideOutput(...)]      <-  optional: "output tag"
```

**Non-Keyed Windows**

```
stream
       .windowAll(...)           <-  required: "assigner"
      [.trigger(...)]            <-  optional: "trigger" (else default trigger)
      [.evictor(...)]            <-  optional: "evictor" (else no evictor)
      [.allowedLateness(...)]    <-  optional: "lateness" (else zero)
      [.sideOutputLateData(...)] <-  optional: "output tag" (else no side output for late data)
       .reduce/aggregate/fold/apply()      <-  required: "function"
      [.getSideOutput(...)]      <-  optional: "output tag"
```



## 窗口分类

### Time Windows

time window是根据时间对数据流进行分组和处理的。其中时间的概念有3中类型，分别是

- EventTime
  - 1.事件生成时的时间，在进入Flink之前就已经存在，可以从event的字段中抽取。
  - 2.必须指定watermarks（水位线）的生成方式。
  - 3.优势：确定性，乱序、延时、或者数据重放等情况，都能给出正确的结果
  - 4.弱点：处理无序事件时性能和延迟受到影响
- IngestionTime
  - 1.事件进入flink的时间，即在source里获取的当前系统的时间，后续操作统一使用该时间。
  - 2.不需要指定watermarks的生成方式(自动生成)
  - 3.弱点：不能处理无序事件和延迟数据
- ProcessingTime
  - 1.执行操作的机器的当前系统时间(每个算子都不一样)
  - 2.不需要流和机器之间的协调
  - 3.优势：最佳的性能和最低的延迟
  - 4.弱点：不确定性 ，容易受到各种因素影像(event产生的速度、到达flink的速度、在算子之间传输速度等)，压根就不管顺序和延迟
- 三种时间的综合比较
  - 性能： ProcessingTime> IngestTime> EventTime
  - 延迟： ProcessingTime< IngestTime< EventTime
  - 确定性： EventTime> IngestTime> ProcessingTime

#### Tumbling Windows 翻滚窗口

​		我们需要统计每一分钟中用户购买的商品的总数，需要将用户的行为事件按每一分钟进行切分，这种切分被成为翻滚时间窗口（Tumbling Time Window）。翻滚窗口能将数据流切分成不重叠的窗口，每一个事件只能属于一个窗口。



![img](https://cdn.jsdelivr.net/gh/daocr/img@master/uPic/20201118155736fBzSiN.svg)

#### Sliding Windows 滑动窗口

​		滑动窗口和翻滚窗口类似，区别在于：滑动窗口可以有重叠的部分。对于某些应用，它们需要的窗口是不间断的，需要平滑地进行窗口聚合。比如，我们可以每30秒计算一次最近一分钟用户购买的商品总数。这种窗口我们称为滑动时间窗口（Sliding Time Window）。在滑窗中，一个元素可以对应多个窗口



![img](https://cdn.jsdelivr.net/gh/daocr/img@master/uPic/20201118155724ezIXxc.svg)



#### Session Windows

![img](https://cdn.jsdelivr.net/gh/daocr/img@master/uPic/20201119095916tw15w3.svg)

#### Global Windows



![img](https://cdn.jsdelivr.net/gh/daocr/img@master/uPic/20201118155712GB5Jxi.svg)

### Count Window

count window 是根据数据条数对流进行分组的

#### Tumbling Count Window 翻滚数量统计窗口





#### Sliding Count Windows 滑动数量统计窗口



### Window Functions

