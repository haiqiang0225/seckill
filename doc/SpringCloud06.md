[toc]

# SpringCloud-06: 引入Hystrix断路器

[项目Github地址](https://github.com/haiqiang0225/seckill)

[Spring Cloud OpenFeign 官方文档](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)

## 服务雪崩、服务降级、服务熔断、服务限流的概念

假设有一条服务调用链路，A->B->C

- 服务雪崩

  假设下游服务C出现了问题而导致请求响应非常慢或者说干脆宕机了，那么B就会有大量请求调用“堵”在这里，最终可能导致B不可用，而同理B不可用就又可能导致A不可用，这样套娃🪆下去，可能导致系统里几乎所有服务都无法正常提供服务，就像雪崩一样。最终导致整个系统挂掉。

- 服务降级（fallback）

  服务降级是从系统整体上来说的，如果整个系统的负载比较高，可能会导致系统挂掉，这个时候就需要🕒**暂时停掉**一些不那么重要的服务，只保留核心服务，从而让出资源确保核心服务的可用性。

  触发服务降级的原因有：

  - 程序运行异常
  - 超时
  - 服务熔断触发服务降级
  - 线程池/信号量满载

- 服务熔断（break）

  服务熔断是面向单个服务的，当某个服务不可用或者出现响应超时的话，会先暂停对该服务的调用。

- 服务限流（flowlimit）

  限制同时的请求数量，防止过高的请求到达某个服务，导致服务提供者扛不住而挂掉



## 集成Hystrix

### 关闭Eureka集群

为了给我们的电脑降低压力，将前面配置的集群暂时关闭掉。

- Eureka Server 7001:

  ```yaml
  server:
    port: 7001
  
  
  eureka:
    instance:
      hostname: eureka7001.com  # eureka服务端实例名
    client:
      register-with-eureka: false  # 表示不向注册中心注册自己
      fetch-registry: false  # 不向注册中心检索服务,因为自己就是注册中心
      service-url: # 设置与 Eureka Server 交互的地址查询服务和注册服务都需要依赖这个地址
        defaultZone: http://eureka7001.com:7001/eureka
  #      defaultZone: http://eureka7002.com:7002/eureka, http://eureka7003.com:7003/eureka
  #  server:
  #    enable-self-preservation: false
  #    eviction-interval-timer-in-ms: 2000
  ```

- Eureka Client 8001:

  ```yaml
  eureka:
    client:
      register-with-eureka: true   # 注册到Eureka Server
      fetch-registry: true         # 是否从 Eureka Server 拉取注册信息, 集群必须设置为true才能配合ribbon使用负载均衡
      service-url:
        defaultZone: http://eureka7001.com:7001/eureka,
  #        http://eureka7002.com:7002/eureka,
  #        http://eureka7003.com:7003/eureka,
    instance:
      instance-id: payment8001
      prefer-ip-address: true
    #      defaultZone: http://localhost:7001/eureka
  ```

### 引入Hystrix依赖

```xml
				<!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-hystrix -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
            <version>2.2.10.RELEASE</version>
        </dependency>
```

### 添加服务方法模拟正常异常情况

- 在`payment8001`模块的Service接口以及对应实现类添加方法声明和具体实现：

  ```java
  @Override
      public String paymentInfoOk(Long id) {
          return "线程: " + Thread.currentThread().getName() + " payment id" + id;
      }
  
      @Override
      public String paymentInfoTimeout(Long id) {
          try {
              TimeUnit.SECONDS.sleep(3);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
          return "线程: " + Thread.currentThread().getName() + " payment id" + id;
      }
  ```

- 添加Controller逻辑:

  ```java
   @GetMapping(value = "/payment/hyx/get/{id}")
      public String paymentInfoOK(@PathVariable("id") Long id) {
          String result = paymentService.paymentInfoOk(id);
          log.info("*****result : {}", result);
          return result;
      }
  
      @GetMapping(value = "/payment/hyx/timeout/get/{id}")
      public String paymentInfoTimeout(@PathVariable("id") Long id) {
          String result = paymentService.paymentInfoTimeout(id);
          log.info("*****result : {}", result);
          return result;
      }
  ```

- 启动主启动类

- 测试

  ![image-20220413160157467](../../../Documents/md_image/spring_cloud_start_06_hystrix_01.png)

  ![image-20220413160228461](../../../Documents/md_image/spring_cloud_start_06_hystrix_02.png)

  调用均可以正常调用

## JMeter压力测试

开启JMeter添加测试计划

- 添加线程组

  ![image-20220413160526643](../../../Documents/md_image/spring_cloud_start_06_hystrix_03.png)

  ![image-20220413160708200](../../../Documents/md_image/spring_cloud_start_06_hystrix_06.png)

- 线程组下添加http请求

  ![image-20220413160604507](../../../Documents/md_image/spring_cloud_start_06_hystrix_04.png)

  ![image-20220413171909628](../../../Documents/md_image/spring_cloud_start_06_hystrix_07.png)

- 启动JMeter

  ![image-20220413171939897](../../../Documents/md_image/spring_cloud_start_06_hystrix_08.png)

- 发现正常的调用（不带sleep的）响应变得比较慢，会有转圈的情况，设置线程数越多越明显。关闭JMeter后会发现这个是立即返回的。

  ![image-20220413172009092](../../../Documents/md_image/spring_cloud_start_06_hystrix_09.png)

- 上面的测试说明对前一个服务的调用拖慢了整个系统，如果来更多的并发请求，可能直接导致整个系统宕机。

## 服务调用方集成Hystrix

- 依赖：

```xml
        <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-hystrix -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
            <version>2.2.10.RELEASE</version>
        </dependency>
```

- 修改`application.yml`

  ```yaml
  server:
    port: 80
  
  spring:
    application:
      name: cloud-order-service
  
  eureka:
    client:
      register-with-eureka: true   # 注册到Eureka Server
      fetch-registry: true         # 是否从 Eureka Server 拉取注册信息, 集群必须设置为true才能配合ribbon使用负载均衡
      service-url:
        defaultZone: http://eureka7001.com:7001/eureka,
  #        http://eureka7002.com:7002/eureka,
  #        http://eureka7003.com:7003/eureka,
  
  feign:
    client:
      config:
        default: # OpenFeign默认配置
          connectTimeout: 1000  # 默认建立连接的超时时间
          readTimeout: 1000     # 默认方法调用超时时间
          loggerLevel: BASIC    # 日志打印级别
        CLOUD-PAYMENT-SERVICE:
          connectTimeout: 1000  # 默认建立连接的超时时间
          readTimeout: 5000     # 默认方法调用超时时间
          loggerLevel: BASIC
  
  logging:
    level:
      root: info
      cc.seckill.springcloud.service.PaymentFeignService: debug
  
  ```

- `@FeignClient`接口添加对应方法

  ```java
  @Component
  @FeignClient(name = "CLOUD-PAYMENT-SERVICE")
  public interface PaymentFeignService {
  
      @GetMapping(value = "/payment/get/{id}")
      Result getPayment(@PathVariable("id") Long id);
  
      @GetMapping(value = "/payment/feign/timeout")
      String paymentFeignTimeout();
  
      @GetMapping(value = "/payment/hyx/get/{id}")
      String paymentInfoOK(@PathVariable("id") Long id);
  
      @GetMapping(value = "/payment/hyx/timeout/get/{id}")
      String paymentInfoTimeout(@PathVariable("id") Long id);
  }
  ```

- Controller同理

  ```java
      @GetMapping(value = "/consumer/payment/hyx/get/{id}")
      String paymentInfoOK(@PathVariable("id") Long id) {
          return paymentFeignService.paymentInfoOK(id);
      };
  
      @GetMapping(value = "/consumer/payment/hyx/timeout/get/{id}")
      String paymentInfoTimeout(@PathVariable("id") Long id) {
          return paymentFeignService.paymentInfoTimeout(id);
      };
  ```

- 启动测试

![image-20220413185534387](../../../Documents/md_image/spring_cloud_start_06_hystrix_10.png)

![image-20220413185554862](../../../Documents/md_image/spring_cloud_start_06_hystrix_11.png)

## 服务降级

一般放在客户端，但放在服务端也是可以的。这里为了熟悉流程，都配置了。

### 服务提供者（provider-payment8001）配置服务降级fallback
- 主启动类添加`@EnableCircuitBreaker`或者`@SpringCloudApplication`注解

  ```java
  @SpringBootApplication
  @EnableEurekaClient
  @MapperScan("cc.seckill.springcloud.dao")
  @EnableDiscoveryClient
  @EnableCircuitBreaker
  public class PaymentMain {
      public static void main(String[] args) {
          System.setProperty("jasypt.encryptor.password", System.getenv("JASYPT_PASS"));
          SpringApplication.run(PaymentMain.class, args);
      }
  }
  
  ```

- Service实现类添加fallback方法配置

  ```java
      @Override
      @HystrixCommand(fallbackMethod = "paymentInfoTimeoutHyxHandler", commandProperties = {
              @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                      value = "1000")
      })
      public String paymentInfoTimeout(Long id) {
          try {
              TimeUnit.SECONDS.sleep(5);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
          return "线程: " + Thread.currentThread().getName() + " payment id" + id;
      }
  
  
      public String paymentInfoTimeoutHyxHandler(Long id) {
          return "线程: " + Thread.currentThread().getName() + " payment id" + id + "," +
                  "出错🌶!等待超时!等会再来访问.o(╥﹏╥)o";
      }
  ```

- 启动测试

  ![image-20220413194201919](../../../Documents/md_image/spring_cloud_start_06_hystrix_12.png)

  正常方法调用没问题。

  ![image-20220413194226282](../../../Documents/md_image/spring_cloud_start_06_hystrix_13.png)

### 服务消费者配置服务降级fallback

- 主启动类添加注解`@EnableCircuitBreaker`注解

  ```java
  @SpringBootApplication
  @EnableEurekaClient
  @EnableFeignClients
  @EnableCircuitBreaker
  public class OrderMain {
      public static void main(String[] args) {
          SpringApplication.run(OrderMain.class, args);
      }
  }
  ```

- 

## 服务熔断

## 服务限流

