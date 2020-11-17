CREATE TABLE MyUserTable
(
    id     BIGINT,
    name STRING,
    email STRING,
    password    STRING,
    user_info_ext_id INT,
    PRIMARY KEY (id) NOT ENFORCED
)
WITH (
    'connector' = 'filesystem',
    'path' = '/Users/daocr/IdeaProjects/study/flink-study/flink-study-table-sql-connectors/format-connector/format-csv/src/main/resources/data.csv',
    'format' = 'csv',
    'csv.field-delimiter'=','
)