[toc]

# SpringCloud-09: Sentinel实现服务熔断与限流

[官网](https://sentinelguard.io/zh-cn/)

[Github](https://github.com/alibaba/Sentinel)

## 安装

Github下载jar包到本地。

![image-20220504170522278](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220504170522278.png)

启动：

```bash
java -jar sentinel-dashboard-1.8.4.jar --server.port=8888
```

浏览器访问：

![image-20220504171720249](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220504171720249.png)

默认账号密码都是`sentinel`。

![image-20220504171857456](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220504171857456.png)

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

yml配置：

```yaml
server:
  port: 9001


spring:
  application:
    name: nacos-payment-provider

  cloud:
    nacos:
      discovery:
        #        server-addr: localnacos:8848
        server-addr: localnacos:1111  # nacos 集群
    sentinel:
      transport:
        dashboard: seckill.cc:8888
        port: 8719



management:
  endpoints:
    web:
      exposure:
        include: '*'

```

Controller：

```java
@RestController
public class FlowLimitController {
    @GetMapping("/testA")
    public String testA() {
        return "--------testA";
    }

    @GetMapping("/testB")
    public String testB() {
        return "----------testB";
    }
}
```

访问一下：

![image-20220504192719151](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/springcloud09_sentinel_04.png)

![image-20220504193352125](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220504193352125.png)

## 相关功能

[官方文档](https://sentinelguard.io/zh-cn/docs/dashboard.html)

### 簇点链路

所有访问过的链路（懒加载）都会添加

![image-20220504193753868](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220504193753868.png)

### 流控规则

![image-20220504193736890](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/springcloud09_sentinel_07.png)

- 资源名：默认请求路径，要求唯一
- 针对来源：Sentinel可以针对调用者进行限流，填写微服务名称，默认default代表不区分来源
- 阈值类型/单机阈值
    - QPS：每秒请求数，当QPS达到设定值时，进行限流
    - 并发线程数：当并发调用的线程数达到设定值时，进行限流
- 是否集群：
- 流控模式：
    - 直接：api达到限流条件时，直接限流
    - 关联：当关联的资源达到阈值时，就限流自己
    - 链路：只记录指定链路上的流量（指定资源从入口资源进来的流量，如果达到阈值就进行限流）
- 流控效果：
    - 快速失败：被限流后直接抛异常
    - Warm Up：根据codeFactor（冷加载因子，默认3）的值，api的实际阈值从初始阈值（设置阈值/codeFactor），经过预热时长（单位s），才达到设置的QPS阈值。
    - 排队等待：匀速排队，让请求以匀速的速度通过，对应的算法是漏桶算法。阈值类型必须设置为QPS，否则无效。

现在限制testA的调用QPS上限为1，快速失败，效果如下：

![image-20220504195952957](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220504195952957.png)

### 熔断降级规则

[官方wiki](https://github.com/alibaba/Sentinel/wiki/%E7%86%94%E6%96%AD%E9%99%8D%E7%BA%A7)

![image-20220504212659432](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/springcloud09_sentinel_09.png)

![image-20220504213902717](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/springcloud09_sentinel_10.png)

![image-20220504213926973](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220504213926973.png)

- 慢调用比例 (`SLOW_REQUEST_RATIO`)

    选择以慢调用比例作为阈值，需要设置允许的慢调用 RT（即最大的响应时间），请求的响应时间大于该值则统计为慢调用。当单位统计时长（`statIntervalMs`）内请求数目大于设置的最小请求数目，并且慢调用的比例大于阈值，则接下来的熔断时长内请求会自动被熔断。经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求响应时间小于设置的慢调用 RT 则结束熔断，若大于设置的慢调用 RT 则会再次被熔断。

- 异常比例 (`ERROR_RATIO`)

    当单位统计时长（`statIntervalMs`）内请求数目大于设置的最小请求数目，并且异常的比例大于阈值，则接下来的熔断时长内请求会自动被熔断。经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求成功完成（没有错误）则结束熔断，否则会再次被熔断。异常比率的阈值范围是 `[0.0, 1.0]`，代表 0% - 100%。

- 异常数 (`ERROR_COUNT`)

    当单位统计时长内的异常数目超过阈值之后会自动进行熔断。经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求成功完成（没有错误）则结束熔断，否则会再次被熔断。

### 热点规则

[官方wiki](https://github.com/alibaba/Sentinel/wiki/%E7%83%AD%E7%82%B9%E5%8F%82%E6%95%B0%E9%99%90%E6%B5%81)

![image-20220504214247340](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220504214247340.png)

热点即经常访问的数据。很多时候我们希望统计某个热点数据中访问频次最高的 Top K 数据，并对其访问进行限制。比如：

- 商品 ID 为参数，统计一段时间内最常购买的商品 ID 并进行限制
- 用户 ID 为参数，针对一段时间内频繁访问的用户 ID 进行限制

热点参数限流会统计传入参数中的热点参数，并根据配置的限流阈值与模式，对包含热点参数的资源调用进行限流。热点参数限流可以看做是一种特殊的流量控制，仅对包含热点参数的资源调用生效。

Controller添加对应方法注解：

```java
@RestController
public class FlowLimitController {
    @GetMapping("/testA")
    public String testA() {
        return "--------testA";
    }

    @GetMapping("/testB")
    public String testB() {
        return "----------testB";
    }

    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey", blockHandler = "testHotKeyHandler")
    public String testHotKey(@RequestParam(value = "p1", required = false) String p1,
                             @RequestParam(value = "p1", required = false) String p2) {
        return "----------testHotKey";
    }

    public String testHotKeyHandler(String p1, String p2, BlockException e) {
        return "----------testHotKeyHandler";
    }
}
```

Sentinel配置：

![image-20220505105601635](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220505105601635.png)

![image-20220505105635597](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220505105635597.png)

将第一个参数`p1`删除后，继续访问，可以发现规则不再生效。

![image-20220505105742615](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/springcloud09_sentinel_15.png)

参数例外项：当被监控的参数是某个特定的值时，应用这里配置的规则而不是通用的规则。

![image-20220505110422467](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220505110422467.png)

可以发现对于p1=10086时，每秒点击两三下不会触发block方法。而其他参数值仍然会触发block方法。

![image-20220505110509116](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/springcloud09_sentinel_17.png)

### 系统规则/系统自适应限流

[官方wiki](https://github.com/alibaba/Sentinel/wiki/%E7%B3%BB%E7%BB%9F%E8%87%AA%E9%80%82%E5%BA%94%E9%99%90%E6%B5%81)

>  Sentinel 系统自适应限流从整体维度对应用入口流量进行控制，结合应用的 Load、CPU 使用率、总体平均 RT、入口 QPS 和并发线程数等几个维度的监控指标，通过自适应的流控策略，让系统的入口流量和系统的负载达到一个平衡，让系统尽可能跑在最大吞吐量的同时保证系统整体的稳定性。

![image-20220505144110689](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220505144110689.png)

- **LOAD自适应（仅对 Linux/Unix-like 机器生效）**

    系统的 load1 作为启发指标，进行自适应系统保护。当系统 load1 超过设定的启发值，且系统当前的并发线程数超过估算的系统容量时才会触发系统保护（BBR 阶段）。系统容量由系统的 `maxQps * minRt` 估算得出。设定参考值一般是 `CPU cores * 2.5`。

- **平均RT**

    当单台机器上所有入口流量的平均 RT 达到阈值即触发系统保护，单位是毫秒。

- **并发线程数**

    当单台机器上所有入口流量的并发线程数达到阈值即触发系统保护。

- **入口QPS**

    当单台机器上所有入口流量的 QPS 达到阈值即触发系统保护。

- **CPU使用率**

    当系统 CPU 使用率超过阈值即触发系统保护（取值范围 0.0-1.0），比较灵敏。



### 授权规则

黑名单/白名单。

![image-20220505153449028](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220505153449028.png)



### 规则持久化

[Sentinel 基于Nacos规则持久化-推模式](https://blog.csdn.net/weixin_40816738/article/details/119086578)
