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
    'connector' = 'jdbc',
    'username' = 'yhl',
    'password' = '123456',
    'url' = 'jdbc:mysql://localhost:3306/demo',
    'table-name' = 'user_info'
)