[toc]

# SpringCloud-09: Sentinel实现服务熔断与限流

[官网](https://sentinelguard.io/zh-cn/)

[Github](https://github.com/alibaba/Sentinel)

## 安装

Github下载jar包到本地。

![image-20220504170522278](../../../Documents/tmp/springcloud09_sentinel_01.png)

启动：

```bash
java -jar sentinel-dashboard-1.8.4.jar --server.port=8888
```

浏览器访问：

![image-20220504171720249](../../../Documents/tmp/springcloud09_sentinel_02.png)

默认账号密码都是`sentinel`。

![image-20220504171857456](../../../Documents/tmp/springcloud09_sentinel_03.png)

Maven依赖：

```xml
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-nacos</artifactId>
        </dependency>

        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>

        <!-- https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-nacos-discovery -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
```

