[toc]

# SpringCloud-10: Seata处理分布式事务

[官方文档](http://seata.io/zh-cn/docs/overview/what-is-seata.html)

分布式事务：事务的参与者位于分布式系统的不同节点之上，一次大的操作可能由许多个小的操作组成，而每个小的操作可能落到不同的节点上，分布式事务就需要保证这些小操作要么全部成功，要么全部失败。

[七种常见分布式事务详解（2PC、3PC、TCC、Saga、本地事务表、MQ事务消息、最大努力通知）](https://blog.csdn.net/a745233700/article/details/122402303?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-1.pc_relevant_paycolumn_v3&spm=1001.2101.3001.4242.2&utm_relevant_index=4)

## Seata

术语：

- Transaction ID XID

    全局唯一的事务ID，根据XID区分不同的分布式事务

- TC (Transaction Coordinator) - 事务协调者

维护全局和分支事务的状态，驱动全局事务提交或回滚。

- TM (Transaction Manager) - 事务管理器

定义全局事务的范围：开始全局事务、提交或回滚全局事务。

- RM (Resource Manager) - 资源管理器

管理分支事务处理的资源，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务提交或回滚。

![image](https://user-images.githubusercontent.com/68344696/145942191-7a2d469f-94c8-4cd2-8c7e-46ad75683636.png)

处理过程：

- TM向TC申请开启一个全局事务，全局事务创建成功返回一个全局唯一的XID；
- XID在微服务调用链路的上下文中传播；
- RM向TC注册分支事务，将其纳入XID对应全局事务的管辖；
- TM向TC发起针对XID的全局提交或回滚决议；
- TC调度XID下管辖的全部分支事务完成提交或回滚请求。

## Seata安装

[Github下载地址 v1.4.2](https://github.com/seata/seata/releases/download/v1.4.2/seata-server-1.4.2.zip)

下载后解压，修改`registry.conf`文件，配置注册中心。这里还是使用`Nacos`。

> v1.4.2只需要修改`registry.conf`文件

```bash
registry {
  # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
  type = "nacos"

  nacos {
    application = "seata-server"
    serverAddr = "seckill.cc:1111"
    group = "SEATA_GROUP"
    namespace = "seata"
    cluster = "default"
    username = ""
    password = ""
  }
  ...
}

config {
  # file、nacos 、apollo、zk、consul、etcd3
  type = "nacos"

  nacos {
    serverAddr = "seckill.cc:1111"
    namespace = "seata"
    group = "SEATA_GROUP"
    username = ""
    password = ""
    dataId = "seataServer.properties"
  }
 	...
}
```

切换到Nacos，创建一个新的命名空间，记住命名空间ID。

![image-20220506203508602](../../../Documents/tmp/springcloud10_seata_01.png)

添加配置文件：

![image-20220506203634080](../../../Documents/tmp/springcloud10_seata_02.png)

内容如下：

> 参考这个文件写：https://github.com/seata/seata/blob/develop/script/config-center/config.txt

```bash
transport.type=TCP
transport.server=NIO
transport.heartbeat=true
transport.enableClientBatchSendRequest=true
transport.threadFactory.bossThreadPrefix=NettyBoss
transport.threadFactory.workerThreadPrefix=NettyServerNIOWorker
transport.threadFactory.serverExecutorThreadPrefix=NettyServerBizHandler
transport.threadFactory.shareBossWorker=false
transport.threadFactory.clientSelectorThreadPrefix=NettyClientSelector
transport.threadFactory.clientSelectorThreadSize=1
transport.threadFactory.clientWorkerThreadPrefix=NettyClientWorkerThread
transport.threadFactory.bossThreadSize=1
transport.threadFactory.workerThreadSize=default
transport.shutdown.wait=3
transport.serialization=seata
transport.compressor=none
# server
server.recovery.committingRetryPeriod=1000
server.recovery.asynCommittingRetryPeriod=1000
server.recovery.rollbackingRetryPeriod=1000
server.recovery.timeoutRetryPeriod=1000
server.undo.logSaveDays=7
server.undo.logDeletePeriod=86400000
server.maxCommitRetryTimeout=-1
server.maxRollbackRetryTimeout=-1
server.rollbackRetryTimeoutUnlockEnable=false
server.distributedLockExpireTime=10000
# store
#model改为db
store.mode=db
store.lock.mode=file
store.session.mode=file
# store.publicKey=""
store.file.dir=file_store/data
store.file.maxBranchSessionSize=16384
store.file.maxGlobalSessionSize=512
store.file.fileWriteBufferCacheSize=16384
store.file.flushDiskMode=async
store.file.sessionReloadReadSize=100
store.db.datasource=druid
store.db.dbType=mysql
store.db.driverClassName=com.mysql.jdbc.Driver
# 改为上面创建的seata服务数据库
store.db.url=jdbc:mysql://ip:3306/seata?useUnicode=true&rewriteBatchedStatements=true
# 改为自己的数据库用户名
store.db.user=root
# 改为自己的数据库密码
store.db.password=
store.db.minConn=5
store.db.maxConn=30
store.db.globalTable=global_table
store.db.branchTable=branch_table
store.db.distributedLockTable=distributed_lock
store.db.queryLimit=100
store.db.lockTable=lock_table
store.db.maxWait=5000
store.redis.mode=single
store.redis.single.host=127.0.0.1
store.redis.single.port=6379
# store.redis.sentinel.masterName=""
# store.redis.sentinel.sentinelHosts=""
store.redis.maxConn=10
store.redis.minConn=1
store.redis.maxTotal=100
store.redis.database=0
# store.redis.password=""
store.redis.queryLimit=100
# log
log.exceptionRate=100
# metrics
metrics.enabled=false
metrics.registryType=compact
metrics.exporterList=prometheus
metrics.exporterPrometheusPort=9898
# service
# 自己命名一个vgroupMapping
service.vgroupMapping.test-tx-group=default
service.default.grouplist=127.0.0.1:8091
service.enableDegrade=false
service.disableGlobalTransaction=false

# client
client.rm.asyncCommitBufferLimit=10000
client.rm.lock.retryInterval=10
client.rm.lock.retryTimes=30
client.rm.lock.retryPolicyBranchRollbackOnConflict=true
client.rm.reportRetryCount=5
client.rm.tableMetaCheckEnable=false
client.rm.tableMetaCheckerInterval=60000
client.rm.sqlParserType=druid
client.rm.reportSuccessEnable=false
client.rm.sagaBranchRegisterEnable=false
client.rm.tccActionInterceptorOrder=-2147482648
client.tm.commitRetryCount=5
client.tm.rollbackRetryCount=5
client.tm.defaultGlobalTransactionTimeout=60000
client.tm.degradeCheck=false
client.tm.degradeCheckAllowTimes=10
client.tm.degradeCheckPeriod=2000
client.tm.interceptorOrder=-2147482648
client.undo.dataValidation=true
client.undo.logSerialization=jackson
client.undo.onlyCareUpdateColumns=true
client.undo.logTable=undo_log
client.undo.compress.enable=true
client.undo.compress.type=zip
client.undo.compress.threshold=64k
```

创建数据库seata，并建表：

> 建表脚本：https://github.com/seata/seata/tree/develop/script/server/db

MySQL：

```sql
-- -------------------------------- The script used when storeMode is 'db' --------------------------------
-- the table to store GlobalSession data
CREATE TABLE IF NOT EXISTS `global_table`
(
    `xid`                       VARCHAR(128) NOT NULL,
    `transaction_id`            BIGINT,
    `status`                    TINYINT      NOT NULL,
    `application_id`            VARCHAR(32),
    `transaction_service_group` VARCHAR(32),
    `transaction_name`          VARCHAR(128),
    `timeout`                   INT,
    `begin_time`                BIGINT,
    `application_data`          VARCHAR(2000),
    `gmt_create`                DATETIME,
    `gmt_modified`              DATETIME,
    PRIMARY KEY (`xid`),
    KEY `idx_status_gmt_modified` (`status` , `gmt_modified`),
    KEY `idx_transaction_id` (`transaction_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- the table to store BranchSession data
CREATE TABLE IF NOT EXISTS `branch_table`
(
    `branch_id`         BIGINT       NOT NULL,
    `xid`               VARCHAR(128) NOT NULL,
    `transaction_id`    BIGINT,
    `resource_group_id` VARCHAR(32),
    `resource_id`       VARCHAR(256),
    `branch_type`       VARCHAR(8),
    `status`            TINYINT,
    `client_id`         VARCHAR(64),
    `application_data`  VARCHAR(2000),
    `gmt_create`        DATETIME(6),
    `gmt_modified`      DATETIME(6),
    PRIMARY KEY (`branch_id`),
    KEY `idx_xid` (`xid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- the table to store lock data
CREATE TABLE IF NOT EXISTS `lock_table`
(
    `row_key`        VARCHAR(128) NOT NULL,
    `xid`            VARCHAR(128),
    `transaction_id` BIGINT,
    `branch_id`      BIGINT       NOT NULL,
    `resource_id`    VARCHAR(256),
    `table_name`     VARCHAR(32),
    `pk`             VARCHAR(36),
    `status`         TINYINT      NOT NULL DEFAULT '0' COMMENT '0:locked ,1:rollbacking',
    `gmt_create`     DATETIME,
    `gmt_modified`   DATETIME,
    PRIMARY KEY (`row_key`),
    KEY `idx_status` (`status`),
    KEY `idx_branch_id` (`branch_id`),
    KEY `idx_xid_and_branch_id` (`xid` , `branch_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `distributed_lock`
(
    `lock_key`       CHAR(20) NOT NULL,
    `lock_value`     VARCHAR(20) NOT NULL,
    `expire`         BIGINT,
    primary key (`lock_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('AsyncCommitting', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('RetryCommitting', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('RetryRollbacking', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('TxTimeoutCheck', ' ', 0);
```

启动seata

```bash
nohup bash bin/seata-server.sh -p 8091 -h 可以ping通的IP &
```

![image-20220506204144984](../../../Documents/tmp/springcloud10_seata_03.png)

Nacos中如果能看到对应的服务，说明安装启动成功。

## example

业务需求：`下订单` -> `减库存` ->`扣余额`->`改变订单状态`

### 创建数据库

创建三个数据库：

```sql
CREATE DATABASE seata_order;
CREATE DATABASE seata_storage;
CREATE DATABASE seata_account;
```

在三个数据库下分别建表：

- seata_order

```sql
CREATE TABLE t_oder (
	`id` BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`user_id` BIGINT(11) DEFAULT NULL COMMENT 'user id',
	`product_id` BIGINT(11) DEFAULT NULL COMMENT '产品id',
	`count` INT(11) DEFAULT NULL,
	`money` DECIMAL(11, 0) DEFAULT NULL,
	`status` INT(1) DEFAULT NULL COMMENT '订单状态， 0：创建中， 1：已完结'
) ENGINE=INNODB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
```

- seata_storage

```sql
CREATE TABLE t_storage (
`id` BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, 
`product_id` BIGINT(11) DEFAULT NULL COMMENT '产品id', 
`total` INT(11) DEFAULT NULL COMMENT '总库存', 
`used` INT(11) DEFAULT NULL COMMENT '已用库存', 
`residue` INT(11) DEFAULT NULL COMMENT '剩余库存' 
) ENGINE=INNODB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
INSERT INTO seata_storage.t_storage(`id`, `product_id`, `total`, `used`, `residue`) VALUES ('1', '1', '100', '0', '100'); 
```

- seata_account

```sql
CREATE TABLE t_account (
`id` BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
`user_id` BIGINT(11) DEFAULT NULL COMMENT '用户id',
`total` DECIMAL(10,0) DEFAULT NULL COMMENT '总额度',
`used` DECIMAL(10,0) DEFAULT NULL COMMENT '已用余额',
`residue` DECIMAL(10,0) DEFAULT '0' COMMENT '剩余可用额度'
) ENGINE=INNODB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8; 
INSERT INTO seata_account.t_account(`id`, `user_id`, `total`, `used`, `residue`) VALUES ('1', '1', '1000', '0', '1000'); 
```

为每个数据库建立回滚表：

```sql
-- 注意此处0.7.0+ 增加字段 context
CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8; COMMENT ='AT transaction mode undo table';
```

### 新建Maven项目:seata-order-service

- `pom.xml`：

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <parent>
            <artifactId>seckill</artifactId>
            <groupId>cc.seckill</groupId>
            <version>1.0-SNAPSHOT</version>
        </parent>
        <modelVersion>4.0.0</modelVersion>
    
        <artifactId>cloud-seata-order-service2001</artifactId>
    
        <properties>
            <maven.compiler.source>8</maven.compiler.source>
            <maven.compiler.target>8</maven.compiler.target>
        </properties>
    
        <dependencies>
    
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-context-support</artifactId>
            </dependency>
            
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
                <exclusions>
                    <exclusion>
                        <groupId>io.seata</groupId>
                        <artifactId>seata-spring-boot-starter</artifactId>
                    </exclusion>
                </exclusions>
            </dependency>
    
            <dependency>
                <groupId>io.seata</groupId>
                <artifactId>seata-spring-boot-starter</artifactId>
                <version>1.4.2</version>
            </dependency>
    
            <!--  项目通用自定义api  -->
            <dependency>
                <groupId>cc.seckill</groupId>
                <artifactId>cloud-api-commons</artifactId>
                <version>${project.version}</version>
            </dependency>
    
            <dependency>
                <groupId>com.alibaba.csp</groupId>
                <artifactId>sentinel-datasource-nacos</artifactId>
            </dependency>
    
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-openfeign</artifactId>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-loadbalancer</artifactId>
            </dependency>
    
    
    
            <!-- https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-nacos-discovery -->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            </dependency>
    
    
            <!--    WEB    -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
    
            <!--  数据库 -->
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
            </dependency>
    
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid-spring-boot-starter</artifactId>
            </dependency>
            <!--mysql-connector-java-->
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
            </dependency>
            <!--jdbc-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-jdbc</artifactId>
            </dependency>
    
            <!--  mybatis-plus  -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-boot-starter</artifactId>
            </dependency>
    
            <!--    通用    -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
                <scope>runtime</scope>
                <optional>true</optional>
            </dependency>
    
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <optional>true</optional>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>
    
            <!--  加密工具包  -->
            <!-- https://mvnrepository.com/artifact/com.github.ulisesbocchio/jasypt-spring-boot-starter -->
            <dependency>
                <groupId>com.github.ulisesbocchio</groupId>
                <artifactId>jasypt-spring-boot-starter</artifactId>
            </dependency>
        </dependencies>
    
    </project>
    
    
    ```

- `yaml`:

    ```yaml
    server:
      port: 2001
    
    spring:
      application:
        name: seata-order-service
      cloud:
        nacos:
          discovery:
            server-addr: seckill.cc:1111
        loadbalancer:
          cache:
            enabled: true
            caffeine:
              spec: initialCapacity=500,expireAfterWrite=5s
    
      datasource:
        type: com.alibaba.druid.pool.DruidDataSource        # 数据源操作类型
        driver-class-name: org.gjt.mm.mysql.Driver          # mysql 驱动
        url: jdbc:mysql://mysql_server:3306/db_seckill?useUnicode=true&characterEncoding=utf-8&useSSL=false
        username: root
        password: ENC(JQaDTMkm+6SfkR02THGL4ir9FQ+CdlT+Q1c1i1beugd3VVULMR19YBfiksl7+xoP)
        druid:
          test-while-idle: false
    
    
    seata:
      enabled: true
      enable-auto-data-source-proxy: true #是否开启数据源自动代理,默认为true
      tx-service-group: test-tx-group  #要与配置文件中的vgroupMapping一致
      registry: #registry根据seata服务端的registry配置
        type: nacos #默认为file
        nacos:
          application: seata-server #配置自己的seata服务
          server-addr: seckill.cc:1111 #根据自己的seata服务配置
          username: nacos
          password: ENC(JQaDTMkm+6SfkR02THGL4ir9FQ+CdlT+Q1c1i1beugd3VVULMR19YBfiksl7+xoP)
          namespace: 2fa7bcca-4687-4b7d-9434-1c8a0df249df  # seata-server在nacos的命名空间ID
          cluster: default # 配置自己的seata服务cluster, 默认为 default
          group: SEATA_GROUP    # seata-server在nacos的分组
      config:
        type: nacos #默认file,如果使用file不配置下面的nacos,直接配置seata.service
        nacos:
          server-addr: seckill.cc:1111 #配置自己的nacos地址
          group: SEATA_GROUP #配置自己的dev
          username: nacos
          password: ENC(JQaDTMkm+6SfkR02THGL4ir9FQ+CdlT+Q1c1i1beugd3VVULMR19YBfiksl7+xoP)
          namespace: 2fa7bcca-4687-4b7d-9434-1c8a0df249df
    
          #配置自己的dataId,由于搭建服务端时把客户端的配置也写在了seataServer.properties,
          # 所以这里用了和服务端一样的配置文件,实际客户端和服务端的配置文件分离出来更好
          dataId: seataServer.properties
    
    
    
    mybatis:
      mapper-locations: classpath:mapper/*.xml
      type-aliases-package: cc.seckill.srpingcloud.entities
    
    
    mybatis-plus:
      configuration:
        log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
      mapper-locations: classpath:mapper/*.xml
    
    jasypt:
      encryptor:
        #    password:
        algorithm: PBEWITHHMACSHA512ANDAES_256
    
    
    feign:
      client:
        config:
          default:
            connectTimeout: 1000
            readTimeout: 1000
            loggerLevel: basic
    ```

- `domain`:

    ```java
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @TableName(value = "t_order")
    public class Order {
        private Long id;
    
        private Long userId;
    
        private Long productId;
    
        private Integer count;
    
        private BigDecimal money;
    
        //订单状态 0 创建中, 1 已完成
        private Integer status;
    }
    ```

- `DAO`:

    ```java
    @Mapper
    public interface OrderMapper extends BaseMapper<Order> {
    }
    ```

- `service`：

    ![image-20220507153202783](../../../Documents/tmp/springcloud10_seata_04.png)

    ```java
    public interface OrderService {
        void create(Order order);
    }
    
    // **************************************************************;
    
    @FeignClient(value = "seata-account-service")
    @Component
    public interface AccountService {
    
        @PostMapping("/account/decrease")
        Result decrease(@RequestParam("userId") Long userId,
                        @RequestParam("money") BigDecimal money);
    }
    
    // **************************************************************;
    
    @FeignClient(value = "seata-storage-service")
    @Component
    public interface StorageService {
    
        @PostMapping("/storage/decrease")
        Result decrease(@RequestParam("productId") Long productId,
                        @RequestParam("count") Integer count);
    }
    
    // **************************************************************;
    
    @Service
    @Slf4j
    public class OrderServiceImpl implements OrderService {
    
        @Resource
        private AccountService accountService;
    
        @Resource
        private StorageService storageService;
    
        @Resource
        private OrderMapper orderMapper;
    
        @Override
        public void create(Order order) {
            log.info("新建订单: {}", order.getId());
            orderMapper.insert(order);
            log.info("订单微服务开始调用库存服务, 开始扣减库存");
            storageService.decrease(order.getProductId(), order.getCount());
            log.info("订单微服务, 扣减库存完成");
    
            log.info("订单微服务开始调用账号服务, 开始减余额");
            accountService.decrease(order.getUserId(), order.getMoney());
            log.info("订单微服务调用账号服务, 减余额完成");
    
            // 修改订单状态
            log.info("修改订单状态: {}", order.getId());
            order.setStatus(1);
            orderMapper.updateById(order);
            log.info("修改订单状态完成 status=: {}", order.getStatus());
        }
    }
    
    
    
    ```

- `controller`:

    ```java
    @RestController
    public class OderController {
        @Resource
        private OrderService orderService;
    
    
        @GetMapping("/order/create")
        public Result create(Order order) {
            orderService.create(order);
            return new Result()
                    .msg("订单创建成功")
                    .code(200);
        }
    }
    ```

- 主启动类

    ```java
    @SpringBootApplication
    @EnableAutoDataSourceProxy
    @EnableDiscoveryClient
    @EnableFeignClients
    public class SeataOrderMain {
    
        public static void main(String[] args) {
            EnvironmentVariableInit.init();
            SpringApplication.run(SeataOrderMain.class, args);
        }
    }
    ```

    

### 新建Maven项目：seata-storage-service

- `pom.xml`:

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <parent>
            <artifactId>seckill</artifactId>
            <groupId>cc.seckill</groupId>
            <version>1.0-SNAPSHOT</version>
        </parent>
        <modelVersion>4.0.0</modelVersion>
    
        <artifactId>cloud-seata-storage-service2002</artifactId>
    
        <properties>
            <maven.compiler.source>8</maven.compiler.source>
            <maven.compiler.target>8</maven.compiler.target>
        </properties>
        <dependencies>
    
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-context-support</artifactId>
            </dependency>
    
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
                <exclusions>
                    <exclusion>
                        <groupId>io.seata</groupId>
                        <artifactId>seata-spring-boot-starter</artifactId>
                    </exclusion>
                </exclusions>
            </dependency>
    
            <dependency>
                <groupId>io.seata</groupId>
                <artifactId>seata-spring-boot-starter</artifactId>
                <version>1.4.2</version>
            </dependency>
    
            <!--  项目通用自定义api  -->
            <dependency>
                <groupId>cc.seckill</groupId>
                <artifactId>cloud-api-commons</artifactId>
                <version>${project.version}</version>
            </dependency>
    
            <dependency>
                <groupId>com.alibaba.csp</groupId>
                <artifactId>sentinel-datasource-nacos</artifactId>
            </dependency>
    
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-openfeign</artifactId>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-loadbalancer</artifactId>
            </dependency>
    
    
    
            <!-- https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-nacos-discovery -->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            </dependency>
    
    
            <!--    WEB    -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
    
            <!--  数据库 -->
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
            </dependency>
    
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid-spring-boot-starter</artifactId>
            </dependency>
            <!--mysql-connector-java-->
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
            </dependency>
            <!--jdbc-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-jdbc</artifactId>
            </dependency>
    
            <!--  mybatis-plus  -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-boot-starter</artifactId>
            </dependency>
    
            <!--    通用    -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
                <scope>runtime</scope>
                <optional>true</optional>
            </dependency>
    
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <optional>true</optional>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>
    
            <!--  加密工具包  -->
            <!-- https://mvnrepository.com/artifact/com.github.ulisesbocchio/jasypt-spring-boot-starter -->
            <dependency>
                <groupId>com.github.ulisesbocchio</groupId>
                <artifactId>jasypt-spring-boot-starter</artifactId>
            </dependency>
    
    
        </dependencies>
    
    </project>
    ```

- `yaml`:

    ```yaml
    server:
      port: 2002
    
    spring:
      application:
        name: seata-storage-service
    
      cloud:
        nacos:
          discovery:
            server-addr: seckill.cc:1111
    
      datasource:
        type: com.alibaba.druid.pool.DruidDataSource        # 数据源操作类型
        driver-class-name: org.gjt.mm.mysql.Driver          # mysql 驱动
        url: jdbc:mysql://tx_cloud:3306/seata_storage?useUnicode=true&characterEncoding=utf-8&useSSL=false
        username: root
        password: ENC(JQaDTMkm+6SfkR02THGL4ir9FQ+CdlT+Q1c1i1beugd3VVULMR19YBfiksl7+xoP)
        druid:
          test-while-idle: false
    
    
    mybatis:
      mapper-locations: classpath:mapper/*.xml
      type-aliases-package: cc.seckill.srpingcloud.entities
    
    
    mybatis-plus:
      configuration:
        log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
      mapper-locations: classpath:mapper/*.xml
    
    jasypt:
      encryptor:
        #    password:
        algorithm: PBEWITHHMACSHA512ANDAES_256
    
    feign:
      client:
        config:
          default:
            connectTimeout: 1000
            readTimeout: 1000
            loggerLevel: basic
    
    seata:
      enabled: true
      enable-auto-data-source-proxy: true #是否开启数据源自动代理,默认为true
      tx-service-group: test-tx-group  #要与配置文件中的vgroupMapping一致
      registry: #registry根据seata服务端的registry配置
        type: nacos #默认为file
        nacos:
          application: seata-server #配置自己的seata服务
          server-addr: seckill.cc:1111 #根据自己的seata服务配置
          username: nacos
          password: ENC(JQaDTMkm+6SfkR02THGL4ir9FQ+CdlT+Q1c1i1beugd3VVULMR19YBfiksl7+xoP)
          namespace: 2fa7bcca-4687-4b7d-9434-1c8a0df249df  # seata-server在nacos的命名空间ID
          cluster: default # 配置自己的seata服务cluster, 默认为 default
          group: SEATA_GROUP    # seata-server在nacos的分组
      config:
        type: nacos #默认file,如果使用file不配置下面的nacos,直接配置seata.service
        nacos:
          server-addr: seckill.cc:1111 #配置自己的nacos地址
          group: SEATA_GROUP #配置自己的dev
          username: nacos
          password: ENC(JQaDTMkm+6SfkR02THGL4ir9FQ+CdlT+Q1c1i1beugd3VVULMR19YBfiksl7+xoP)
          namespace: 2fa7bcca-4687-4b7d-9434-1c8a0df249df
    
          #配置自己的dataId,由于搭建服务端时把客户端的配置也写在了seataServer.properties,
          # 所以这里用了和服务端一样的配置文件,实际客户端和服务端的配置文件分离出来更好
          dataId: seataServer.properties
    
    ```

- `domain`:

    ```java
    @Data
    @TableName(value = "t_storage")
    public class Storage {
        private Long id;
    
        private Long productId;
    
        private Integer total;
    
        private Integer used;
    
        private Integer residue;
    }
    ```

- `dao`:

    ```java
    @Mapper
    public interface StorageMapper extends BaseMapper<Storage> {
    }
    ```

- `service`:

    ```java
    public interface StorageService {
        void decrease(Long productId, Integer count);
    }
    
    // ******************************************************************;
    @Service
    @Slf4j
    public class StorageServiceImpl implements StorageService {
    
        @Resource
        private StorageMapper storageMapper;
    
        @Override
        public void decrease(Long productId, Integer count) {
            log.info("------->storage-service 中扣减库存开始 ");
            Storage storage = storageMapper.selectById(productId);
            storage.setUsed(storage.getUsed() + count);
            storage.setResidue(storage.getResidue() - count);
            storageMapper.updateById(storage);
            log.info("------->storage-service 中扣减库存结束 ");
        }
    }
    ```

- `controller`:

    ```java
    @RestController
    public class StorageController {
        @Resource
        private StorageService storageService;
    
    
        @RequestMapping("/storage/decrease")
        public Result decrease(Long productId, Integer count) {
            storageService.decrease(productId, count);
            return new Result()
                    .code(200)
                    .msg("扣减库存成功");
        }
    }
    
    
    ```

- 主启动类：

    ```java
    @SpringBootApplication
    @EnableFeignClients
    @EnableDiscoveryClient
    @EnableAutoDataSourceProxy
    public class StorageMain {
        public static void main(String[] args) {
            EnvironmentVariableInit.init();
            SpringApplication.run(StorageMain.class, args);
        }
    }
    ```

### 新建项目：seata-account-service

- `pom.xml`:

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <parent>
            <artifactId>seckill</artifactId>
            <groupId>cc.seckill</groupId>
            <version>1.0-SNAPSHOT</version>
        </parent>
        <modelVersion>4.0.0</modelVersion>
    
        <artifactId>cloud-seata-account-service2003</artifactId>
    
        <properties>
            <maven.compiler.source>8</maven.compiler.source>
            <maven.compiler.target>8</maven.compiler.target>
        </properties>
    
        <dependencies>
    
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-context-support</artifactId>
            </dependency>
    
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
                <exclusions>
                    <exclusion>
                        <groupId>io.seata</groupId>
                        <artifactId>seata-spring-boot-starter</artifactId>
                    </exclusion>
                </exclusions>
            </dependency>
    
            <dependency>
                <groupId>io.seata</groupId>
                <artifactId>seata-spring-boot-starter</artifactId>
                <version>1.4.2</version>
            </dependency>
    
            <!--  项目通用自定义api  -->
            <dependency>
                <groupId>cc.seckill</groupId>
                <artifactId>cloud-api-commons</artifactId>
                <version>${project.version}</version>
            </dependency>
    
            <dependency>
                <groupId>com.alibaba.csp</groupId>
                <artifactId>sentinel-datasource-nacos</artifactId>
            </dependency>
    
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-openfeign</artifactId>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-loadbalancer</artifactId>
            </dependency>
    
    
    
            <!-- https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-nacos-discovery -->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            </dependency>
    
    
            <!--    WEB    -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
    
            <!--  数据库 -->
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
            </dependency>
    
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid-spring-boot-starter</artifactId>
            </dependency>
            <!--mysql-connector-java-->
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
            </dependency>
            <!--jdbc-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-jdbc</artifactId>
            </dependency>
    
            <!--  mybatis-plus  -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-boot-starter</artifactId>
            </dependency>
    
            <!--    通用    -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
                <scope>runtime</scope>
                <optional>true</optional>
            </dependency>
    
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <optional>true</optional>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>
    
            <!--  加密工具包  -->
            <!-- https://mvnrepository.com/artifact/com.github.ulisesbocchio/jasypt-spring-boot-starter -->
            <dependency>
                <groupId>com.github.ulisesbocchio</groupId>
                <artifactId>jasypt-spring-boot-starter</artifactId>
            </dependency>
        </dependencies>
    
    </project>
    ```

- `yaml`:

    ```yaml
    server:
      port: 2003
    
    
    spring:
      application:
        name: seata-account-service
      cloud:
        nacos:
          discovery:
            server-addr: seckill.cc:1111
      datasource:
        type: com.alibaba.druid.pool.DruidDataSource        # 数据源操作类型
        driver-class-name: org.gjt.mm.mysql.Driver          # mysql 驱动
        url: jdbc:mysql://tx_cloud:3306/seata_account?useUnicode=true&characterEncoding=utf-8&useSSL=false
        username: root
        password: ENC(JQaDTMkm+6SfkR02THGL4ir9FQ+CdlT+Q1c1i1beugd3VVULMR19YBfiksl7+xoP)
        druid:
          test-while-idle: false
    
    mybatis:
      mapper-locations: classpath:mapper/*.xml
      type-aliases-package: cc.seckill.srpingcloud.entities
    
    
    mybatis-plus:
      configuration:
        log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
      mapper-locations: classpath:mapper/*.xml
      
    
    feign:
      client:
        config:
          default:
            connectTimeout: 1000
            readTimeout: 1000
            loggerLevel: basic
    
    seata:
      enabled: true
      enable-auto-data-source-proxy: true #是否开启数据源自动代理,默认为true
      tx-service-group: test-tx-group  #要与配置文件中的vgroupMapping一致
      registry: #registry根据seata服务端的registry配置
        type: nacos #默认为file
        nacos:
          application: seata-server #配置自己的seata服务
          server-addr: seckill.cc:1111 #根据自己的seata服务配置
          username: nacos
          password: ENC(JQaDTMkm+6SfkR02THGL4ir9FQ+CdlT+Q1c1i1beugd3VVULMR19YBfiksl7+xoP)
          namespace: 2fa7bcca-4687-4b7d-9434-1c8a0df249df  # seata-server在nacos的命名空间ID
          cluster: default # 配置自己的seata服务cluster, 默认为 default
          group: SEATA_GROUP    # seata-server在nacos的分组
      config:
        type: nacos #默认file,如果使用file不配置下面的nacos,直接配置seata.service
        nacos:
          server-addr: seckill.cc:1111 #配置自己的nacos地址
          group: SEATA_GROUP #配置自己的dev
          username: nacos
          password: ENC(JQaDTMkm+6SfkR02THGL4ir9FQ+CdlT+Q1c1i1beugd3VVULMR19YBfiksl7+xoP)
          namespace: 2fa7bcca-4687-4b7d-9434-1c8a0df249df
    
          #配置自己的dataId,由于搭建服务端时把客户端的配置也写在了seataServer.properties,
          # 所以这里用了和服务端一样的配置文件,实际客户端和服务端的配置文件分离出来更好
          dataId: seataServer.properties
    ```

- `domain`:

    ```java
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @TableName(value = "t_account")
    public class Account {
        private Long id;
    
        private Long userId;
    
        private BigDecimal total;
    
        private BigDecimal used;
    
        private BigDecimal residue;
    }
    ```

- `dao`:

    ```java
    @Mapper
    public interface AccountMapper extends BaseMapper<Account> {
    }
    ```

- `service`:

    ```java
    public interface AccountService {
        void decrease(@RequestParam("userId") Long userId,
                      @RequestParam("money") BigDecimal money);
    }
    
    // ******************************************************************;
    @Service
    @Slf4j
    public class AccountServiceImpl implements AccountService {
    
        @Resource
        private AccountMapper accountMapper;
    
        @Override
        public void decrease(Long userId, BigDecimal money) {
            log.info("------->account-service 中扣减账户余额开始 ");
            QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(Account::getUserId, userId);
            Account account = accountMapper.selectOne(queryWrapper);
            account.setUsed(account.getUsed().add(money));
            account.setResidue(account.getResidue().subtract(money));
    
            accountMapper.updateById(account);
    
            log.info("------->account-service 中扣减账户余额结束 ");
        }
    }
    ```

- `controller`:

    ```java
    @RestController
    public class AccountController {
        @Resource
        private AccountService accountService;
    
    
        @RequestMapping(value = "/account/decrease")
        public Result decrease(@RequestParam("userId") Long userId,
                               @RequestParam("money") BigDecimal money) {
            accountService.decrease(userId, money);
            return new Result()
                    .code(200)
                    .msg("扣减余额成功");
        }
    }
    ```

- 主启动类

    ```java
    @SpringBootApplication
    @EnableAutoDataSourceProxy
    @EnableFeignClients
    @EnableDiscoveryClient
    public class AccountMain {
        public static void main(String[] args) {
            EnvironmentVariableInit.init();
            SpringApplication.run(AccountMain.class, args);
        }
    }
    ```

    

### 测试

现在还没有开启分布式事务，会出问题。

浏览器输入：`http://localhost:2001/order/create?userId=1&productId=1&count=10&money=100`

![image-20220507172606405](../../../Documents/tmp/springcloud10_seata_05.png)

查看对应表变化：

- order表多了一条记录：

![image-20220507172646661](../../../Documents/tmp/springcloud10_seata_06.png)

- storage表记录变化正常

    ![image-20220507172721002](../../../Documents/tmp/springcloud10_seata_07.png)

- account表记录变化正常

    ![image-20220507172750718](../../../Documents/tmp/springcloud10_seata_08.png)

因为我们这个调用是单线程的，所以在每个微服务都不出异常的情况下，是没问题的。

现在模拟某个微服务出错。

修改 `AccountServiceImpl`，添加一个超时的方法。因为我们在调用方2001的OpenFeign中设置的默认超时时间是1s，所以对这里的调用会报超时异常。

```java
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountMapper accountMapper;

    @Override
    public void decrease(Long userId, BigDecimal money) {
        log.info("------->account-service 中扣减账户余额开始 ");
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Account::getUserId, userId);

        try {
            // 模拟超时异常 调用方Feign超时时间设置的是1s所以一定会报错
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Account account = accountMapper.selectOne(queryWrapper);
        account.setUsed(account.getUsed().add(money));
        account.setResidue(account.getResidue().subtract(money));

        accountMapper.updateById(account);

        log.info("------->account-service 中扣减账户余额结束 ");
    }
}
```

再次调用发现报错：

![image-20220507173337535](../../../Documents/tmp/springcloud10_seata_09.png)

查看订单：

![image-20220507185103312](../../../Documents/tmp/springcloud10_seata_10.png)

可以看到新插入的订单状态为`NULL`，说明`accountService.decrease(order.getUserId(), order.getMoney());`这句之后的语句没有执行（因为这句报错了，超时）

```java
@Override
    public void create(Order order) {
        log.info("订单信息: {}", order);
        orderMapper.insert(order);
        log.info("订单微服务开始调用库存服务, 开始扣减库存");
        storageService.decrease(order.getProductId(), order.getCount());
        log.info("订单微服务, 扣减库存完成");

        log.info("订单微服务开始调用账号服务, 开始减余额");
        accountService.decrease(order.getUserId(), order.getMoney());
        log.info("订单微服务调用账号服务, 减余额完成");

        // 修改订单状态
        log.info("修改订单状态: {}", order.getId());
        order.setStatus(1);
        orderMapper.updateById(order);
        log.info("修改订单状态完成 status=: {}", order.getStatus());
    }
```

从控制栏也能看出确实报错了。

![image-20220507185233336](../../../Documents/tmp/springcloud10_seata_11.png)

但是storage表和account表是正常的（account表也有可能不正常，如果Feign配置了超时重试的话）

![image-20220507185340783](../../../Documents/tmp/springcloud10_seata_12.png)

在业务方法上添加`@GlobalTransactional`注解，即可开启全局事务。

```java
    @Override
    @GlobalTransactional(name = "test_global_xid", rollbackFor = Exception.class)
    public void create(Order order) {
        log.info("订单信息: {}", order);
        orderMapper.insert(order);
        log.info("订单微服务开始调用库存服务, 开始扣减库存");
        storageService.decrease(order.getProductId(), order.getCount());
        log.info("订单微服务, 扣减库存完成");

        log.info("订单微服务开始调用账号服务, 开始减余额");
        accountService.decrease(order.getUserId(), order.getMoney());
        log.info("订单微服务调用账号服务, 减余额完成");

        // 修改订单状态
        log.info("修改订单状态: {}", order.getId());
        order.setStatus(1);
        orderMapper.updateById(order);
        log.info("修改订单状态完成 status=: {}", order.getStatus());
    }
```

重启后，再次执行

![image-20220507190314481](../../../Documents/tmp/springcloud10_seata_13.png)

可以看到仍然是报错了，但是这个时候再查看数据库

![image-20220507190344135](../../../Documents/tmp/springcloud10_seata_14.png)

![image-20220507190400230](../../../Documents/tmp/springcloud10_seata_15.png)

![image-20220507190418489](../../../Documents/tmp/springcloud10_seata_16.png)

三个表都是正常的，没有再发生不一致现象。

查看控制台，可以看到事务回滚了。

```bash
Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@43d2c217]
2022-05-07 19:01:17.424  INFO 26399 --- [nio-2002-exec-7] c.s.s.service.impl.StorageServiceImpl    : ------->storage-service 中扣减库存结束 
2022-05-07 19:01:17.800  INFO 26399 --- [h_RMROLE_1_1_16] i.s.r.d.undo.AbstractUndoLogManager      : xid 202.199.6.118:8091:6152177009216172033 branch 6152177009216172039, undo_log deleted with GlobalFinished
2022-05-07 19:01:17.844  INFO 26399 --- [h_RMROLE_1_1_16] io.seata.rm.AbstractRMHandler            : Branch Rollbacked result: PhaseTwo_Rollbacked
```

```bash
Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6701e822]
2022-05-07 19:01:16.224  INFO 45763 --- [nio-2001-exec-1] c.s.s.service.impl.OrderServiceImpl      : 订单微服务开始调用库存服务, 开始扣减库存
2022-05-07 19:01:17.875  INFO 45763 --- [h_RMROLE_1_1_16] i.s.c.r.p.c.RmBranchRollbackProcessor    : rm handle branch rollback process:xid=202.199.6.118:8091:6152177009216172033,branchId=6152177009216172035,branchType=AT,resourceId=jdbc:mysql://tx_cloud:3306/seata_order,applicationData=null
2022-05-07 19:01:17.877  INFO 45763 --- [h_RMROLE_1_1_16] io.seata.rm.AbstractRMHandler            : Branch Rollbacking: 202.199.6.118:8091:6152177009216172033 6152177009216172035 jdbc:mysql://tx_cloud:3306/seata_order
2022-05-07 19:01:18.365  INFO 45763 --- [h_RMROLE_1_1_16] i.s.r.d.undo.AbstractUndoLogManager      : xid 202.199.6.118:8091:6152177009216172033 branch 6152177009216172035, undo_log deleted with GlobalFinished
2022-05-07 19:01:18.410  INFO 45763 --- [h_RMROLE_1_1_16] io.seata.rm.AbstractRMHandler            : Branch Rollbacked result: PhaseTwo_Rollbacked
2022-05-07 19:01:18.471  INFO 45763 --- [nio-2001-exec-1] i.seata.tm.api.DefaultGlobalTransaction  : Suspending current transaction, xid = 202.199.6.118:8091:6152177009216172033
2022-05-07 19:01:18.472  INFO 45763 --- [nio-2001-exec-1] i.seata.tm.api.DefaultGlobalTransaction  : [202.199.6.118:8091:6152177009216172033] rollback status: Rollbacked
2022-05-07 19:01:18.498 ERROR 45763 --- [nio-2001-exec-1] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is feign.RetryableException: Read timed out executing POST http://seata-storage-service/storage/decrease?productId=1&count=10] with root cause
```

