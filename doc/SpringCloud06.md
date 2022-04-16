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

  ![image-20220413160157467](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_01.png)

  ![image-20220413160228461](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_02.png)

  调用均可以正常调用

## JMeter压力测试

开启JMeter添加测试计划

- 添加线程组

  ![image-20220413160526643](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_03.png)

  ![image-20220413160708200](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_06.png)

- 线程组下添加http请求

  ![image-20220413160604507](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_04.png)

  ![image-20220413171909628](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_07.png)

- 启动JMeter

  ![image-20220413171939897](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_08.png)

- 发现正常的调用（不带sleep的）响应变得比较慢，会有转圈的情况，设置线程数越多越明显。关闭JMeter后会发现这个是立即返回的。

  ![image-20220413172009092](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_09.png)

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

![image-20220413185534387](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_10.png)

![image-20220413185554862](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_11.png)

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

  ![image-20220413194201919](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_12.png)

  正常方法调用没问题。

  ![image-20220413194226282](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_13.png)

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

- Controller配置

  ```java
      @HystrixCommand(fallbackMethod = "paymentInfoTimeoutHyxHandler", commandProperties = {
              @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                      value = "2000")
      })
      @GetMapping(value = "/consumer/payment/hyx/timeout/get/{id}")
      String paymentInfoTimeout(@PathVariable("id") Long id) {
          return paymentFeignService.paymentInfoTimeout(id);
      }
  
  
      public String paymentInfoTimeoutHyxHandler(@PathVariable("id") Long id) {
          return "80: 支付系统繁忙,请稍后调用";
      }
  ```

- 设置payment8001Hystrix超时时间大于服务调用时间

  ```java
      @Override
      @HystrixCommand(fallbackMethod = "paymentInfoTimeoutHyxHandler", commandProperties = {
              @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                      value = "6000")
      })
      public String paymentInfoTimeout(Long id) {
          // 模拟异常
  //        if (true) {
  //            throw new RuntimeException();
  //        }
          try {
              TimeUnit.SECONDS.sleep(5);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
          return "线程: " + Thread.currentThread().getName() + " payment id" + id;
      }
  
  ```

- 启动测试

  ![image-20220416160802063](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_14.png)

  客户端服务降级成功。

### 配置全局默认fallback

首先测试一下全局默认fallback。

前面的方式每一个方法都要配置一个专属的fallback方法，这显然是不合适的。我们修改80端口。

- Cotroller添加全局方法

  ```java
  public String paymentGlobalFallbackHyxHandler() {
          return "80: 出错啦!请稍后再试.";
      }
  ```

- Controller添加注解及配置

  ```java
  @DefaultProperties(defaultFallback = "paymentGlobalFallbackHyxHandler")
  public class OrderFeignController {
    
    
    
  //    @HystrixCommand(fallbackMethod = "paymentInfoTimeoutHyxHandler", commandProperties = {
  //            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
  //                    value = "2000")
  //    })
      @HystrixCommand
      @GetMapping(value = "/consumer/payment/hyx/timeout/get/{id}")
      String paymentInfoTimeout(@PathVariable("id") Long id) {
          return paymentFeignService.paymentInfoTimeout(id);
      }
  ```

- 测试

  ![image-20220416162929169](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_15.png)

  >  tips:如果OpenFeign配置了超时时间且调用超时，也会触发Hystrix服务降级

#### 在FeignClient配置

- 添加fallback服务类`PaymentHystrixService`

  ```java
  package cc.seckill.springcloud.service.impl;
  
  import cc.seckill.springcloud.entities.Result;
  import cc.seckill.springcloud.service.PaymentFeignService;
  import org.springframework.stereotype.Component;
  
  /**
   * description: PaymentHystrixService <br>
   * date: 2022/4/16 19:04 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @Component
  public class PaymentHystrixService implements PaymentFeignService {
      @Override
      public Result getPayment(Long id) {
          return Result.error();
      }
  
      @Override
      public String paymentFeignTimeout() {
          return "PaymentHystrixService: time out, wait !";
      }
  
      @Override
      public String paymentInfoOK(Long id) {
          return "PaymentHystrixService: ok";
      }
  
      @Override
      public String paymentInfoTimeout(Long id) {
          return "PaymentHystrixService: time out, wait !";
      }
  }
  
  ```

- `application.yml`配置OpenFeign开启服务熔断功能

  ```yaml
  feign:
    circuitbreaker:
      enabled: true
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
  ```

- 启动服务测试：

  ![image-20220416192710068](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_16.png)

  ![image-20220416192737252](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_17.png)

  停掉`Payment8001`，模拟对方故障。

  ![image-20220416192817690](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_18.png)

  发现调用了对应的服务降级方法。

  ![image-20220416192837315](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_19.png)

至此服务降级配置完成。

## 服务熔断

### 理论

熔断机制：应对服务雪崩效应的微服务链路保护机制，当扇出链路的某个微服务出错不可用或者响应时间太长时，会进行服务的降级，进而熔断该节点微服务的调用，快速返回错误的响应信息。**当检测到该微服务调用响应正常后，恢复调用链路。**

Hystrix会监控微服务间调用的状况，当失败的调用达到一定阈值（缺省是5秒20次调用失败），就会启动熔断机制。对应的注解是`@HystrixCommand`。

其实就是一个断路器，如果服务不可用，那么Hystrix就会将链路断开，现在断路器就是Open的状态，就是下面图片的状态。

<img src="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.defanli.com%2Fi3%2F1975444143%2FO1CN01vDHomD1gTXTR0nPK5_%21%211975444143.jpg_q90.jpg&refer=http%3A%2F%2Fimg.defanli.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1652701176&t=efcffe47be653044dd8405350fd390b8" alt="img" style="zoom:25%;" />

断开以后，在reset这个时间内不会去调用下游服务，如果过了这段时间，会先尝试去调用一下试试，如果调用成功了，那么就将断路器合上，也就是下面的状态。

<img src="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg2.tbcdn.cn%2Ftfscom%2Fi1%2F810331470%2FTB2IlahfFXXXXXYXpXXXXXXXXXX_%21%21810331470.jpg&refer=http%3A%2F%2Fimg2.tbcdn.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1652701225&t=ef7a311592d1a593ad877fc5b744a046" alt="img" style="zoom:25%;" />

理论图是下面这样的，Half Open就对应上面尝试的这个状态。



![img](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_20.png)

### 实现

- `payment8001 PaymentServiceImpl.java`添加方法

  ```java
      @Override
      @HystrixCommand(fallbackMethod = "paymentCircuitBreakerHandler", commandProperties = {
              @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),  // 开启断路器
              @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value =
                      "10"), // 请求次数
              @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value =
                      "10000"), // 时间窗口
              @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value =
                      "60"),  // 失败率到达多少后断开
      })
      public String paymentCircuitBreaker(Long id) {
          if (id < 0) {
              throw new RuntimeException("id negative");
          }
          String serialNumber = IdUtil.simpleUUID();
          return Thread.currentThread().getName() + "\t" + "调用成功, id = " + 1;
      }
  
      public String paymentCircuitBreakerHandler() {
          return "break!";
      }
  ```

- Controller添加调用

  ```java
      @GetMapping("payment/circuit/{id}")
      public String paymentCircuitBreaker(@PathVariable("id") Long id) {
          String result = paymentService.paymentCircuitBreaker(id);
          log.info("result : {}", result);
          return result;
      }
  ```

- 启动测试

  正常情况

  ![image-20220416200935505](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_23.png)

  错误情况（id为负）：

  ![image-20220416201030743](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_24.png)

  使用JMeter一直进行错误的调用

  ![image-20220416201337488](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_25.png)

  ![image-20220416201356477](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_26.png)

  再次调用正常方法，发现也被断开了，说明断路器处于Open状态了，服务已经被熔断了。

  ![image-20220416201431634](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_06_hystrix_27.png)


