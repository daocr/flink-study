[TOC]

### checkpint 介绍



### 局部快照设置



### 全局state参数说明



```

```





### Related Config Options

Some more parameters and/or defaults may be set via `conf/flink-conf.yaml` (see [configuration](https://ci.apache.org/projects/flink/flink-docs-release-1.11/ops/config.html) for a full guide):

| Key                                | Default | Type       | Description                                                  |
| :--------------------------------- | :------ | :--------- | :----------------------------------------------------------- |
| state.backend                      | (none)  | String     | The state backend to be used to store and checkpoint state.  |
| state.backend.async                | true    | Boolean    | Option whether the state backend should use an asynchronous snapshot method where possible and configurable. Some state backends may not support asynchronous snapshots, or only support asynchronous snapshots, and ignore this option. |
| state.backend.fs.memory-threshold  | 20 kb   | MemorySize | The minimum size of state data files. All state chunks smaller than that are stored inline in the root checkpoint metadata file. The max memory threshold for this configuration is 1MB. |
| state.backend.fs.write-buffer-size | 4096    | Integer    | The default size of the write buffer for the checkpoint streams that write to file systems. The actual write buffer size is determined to be the maximum of the value of this option and option 'state.backend.fs.memory-threshold'. |
| state.backend.incremental          | false   | Boolean    | Option whether the state backend should create incremental checkpoints, if possible. For an incremental checkpoint, only a diff from the previous checkpoint is stored, rather than the complete checkpoint state. Once enabled, the state size shown in web UI or fetched from rest API only represents the delta checkpoint size instead of full checkpoint size. Some state backends may not support incremental checkpoints and ignore this option. |
| state.backend.local-recovery       | false   | Boolean    | This option configures local recovery for this state backend. By default, local recovery is deactivated. Local recovery currently only covers keyed state backends. Currently, MemoryStateBackend does not support local recovery and ignore this option. |
| state.checkpoints.dir              | (none)  | String     | The default directory used for storing the data files and meta data of checkpoints in a Flink supported filesystem. The storage path must be accessible from all participating processes/nodes(i.e. all TaskManagers and JobManagers). |
| state.checkpoints.num-retained     | 1       | Integer    | The maximum number of completed checkpoints to retain.       |
| state.savepoints.dir               | (none)  | String     | The default directory for savepoints. Used by the state backends that write savepoints to file systems (MemoryStateBackend, FsStateBackend, RocksDBStateBackend). |
| taskmanager.state.local.root-dirs  | (none)  | String     | The config parameter defining the root directories for storing file-based state for local recovery. Local recovery currently only covers keyed state backends. Currently, MemoryStateBackend does not support local recovery and ignore this optionRelated Config Options |


### 使用实例



### 参考来源：