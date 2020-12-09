create table if not exists dept
(
    deptno integer not null
        constraint dept_pkey
        primary key,
    dname  varchar(50),
    loc    varchar(50)
);

comment on table dept is '部门表';

comment on column dept.deptno is '部门编号';

comment on column dept.dname is '部门名称';

comment on column dept.loc is '部门地址';

alter table dept owner to huilong;

create table if not exists emp
(
    empno    integer not null
        constraint emp_pkey
        primary key,
    ename    varchar(50),
    job      varchar(50),
    mgr      integer
        constraint fk_emp
        references emp,
    hiredate date,
    sal      numeric(7, 2),
    comm     numeric(7, 2),
    deptno   integer
);

comment on table emp is '员工表';

comment on column emp.empno is '员工编号';

comment on column emp.ename is '员工名称';

comment on column emp.job is '职务';

comment on column emp.sal is '销售金额';

comment on column emp.deptno is '部门id';

alter table emp owner to huilong;

create table if not exists salgrade
(
    grade integer not null
        constraint salgrade_pkey
        primary key,
    losal integer,
    hisal integer
);

alter table salgrade owner to huilong;

create table if not exists student
(
    sid      integer not null
        constraint stu_pkey
        primary key,
    sname    varchar(50),
    age      integer,
    gander   varchar(10),
    province varchar(50),
    tuition  integer
);

comment on table student is '学生表';

comment on column student.sname is '学生名称';

comment on column student.age is '年龄';

comment on column student.gander is '性别';

comment on column student.province is '省份';

comment on column student.tuition is '学费';

alter table student owner to huilong;



/*插入dept表数据*/
INSERT INTO dept
VALUES (10, '教研部', '北京');
INSERT INTO dept
VALUES (20, '学工部', '上海');
INSERT INTO dept
VALUES (30, '销售部', '广州');
INSERT INTO dept
VALUES (40, '财务部', '武汉');

/*插入emp表数据*/
INSERT INTO emp
VALUES (1009, '曾阿牛', '董事长', NULL, '2001-11-17', 50000, NULL, 10);
INSERT INTO emp
VALUES (1004, '刘备', '经理', 1009, '2001-04-02', 29750, NULL, 20);
INSERT INTO emp
VALUES (1006, '关羽', '经理', 1009, '2001-05-01', 28500, NULL, 30);
INSERT INTO emp
VALUES (1007, '张飞', '经理', 1009, '2001-09-01', 24500, NULL, 10);
INSERT INTO emp
VALUES (1008, '诸葛亮', '分析师', 1004, '2007-04-19', 30000, NULL, 20);
INSERT INTO emp
VALUES (1013, '庞统', '分析师', 1004, '2001-12-03', 30000, NULL, 20);
INSERT INTO emp
VALUES (1002, '黛绮丝', '销售员', 1006, '2001-02-20', 16000, 3000, 30);
INSERT INTO emp
VALUES (1003, '殷天正', '销售员', 1006, '2001-02-22', 12500, 5000, 30);
INSERT INTO emp
VALUES (1005, '谢逊', '销售员', 1006, '2001-09-28', 12500, 14000, 30);
INSERT INTO emp
VALUES (1010, '韦一笑', '销售员', 1006, '2001-09-08', 15000, 0, 30);
INSERT INTO emp
VALUES (1012, '程普', '文员', 1006, '2001-12-03', 9500, NULL, 30);
INSERT INTO emp
VALUES (1014, '黄盖', '文员', 1007, '2002-01-23', 13000, NULL, 10);
INSERT INTO emp
VALUES (1011, '周泰', '文员', 1008, '2007-05-23', 11000, NULL, 20);


INSERT INTO emp
VALUES (1001, '甘宁', '文员', 1013, '2000-12-17', 8000, NULL, 20);


/*插入salgrade表数据*/
INSERT INTO salgrade
VALUES (1, 7000, 12000);
INSERT INTO salgrade
VALUES (2, 12010, 14000);
INSERT INTO salgrade
VALUES (3, 14010, 20000);
INSERT INTO salgrade
VALUES (4, 20010, 30000);
INSERT INTO salgrade
VALUES (5, 30010, 99990);

