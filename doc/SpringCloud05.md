[toc]

# SpringCloud-05: OpenFeign服务接口调用

[项目Github地址](https://github.com/haiqiang0225/seckill)

[Spring Cloud OpenFeign 官方文档](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)

## 微服务服务接口调用

- 之前的调用方式
  - `restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id, Result.class);`
- 引入Feign/OpenFeign后的调用方式
  - `paymentFeignService.getPayment(id);`

引入Feign后，调用远程微服务方法就跟调用本地方法一样，相当于由Feign包装了一层。简化了微服务的调用。

## Feign与OpenFeign

简单理解：OpenFeign = 加强版 Feign，且Feign已停止维护，故选择OpenFeign。

## 集成OpenFeign

- 添加依赖

  ```xml
  				<!--   OpenFeign   -->
          <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign -->
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-openfeign</artifactId>
          </dependency>
  ```

- 创建`@FeignClient`接口

  ```java
  package cc.seckill.springcloud.service;
  
  import cc.seckill.springcloud.entities.Result;
  import org.springframework.cloud.openfeign.FeignClient;
  import org.springframework.stereotype.Component;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.PathVariable;
  
  /**
   * description: PaymentFeignService <br>
   * date: 2022/4/13 10:53 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @Component
  @FeignClient(value = "CLOUD-PAYMENT-SERVICE")
  public interface PaymentFeignService {
  
      @GetMapping(value = "/payment/get/{id}")
      public Result getPayment(@PathVariable("id") Long id);
  }
  ```

  接口里这个方法就是服务方方法的签名，这样的话OpenFeign就会根据我们配置的服务名（@FeignClient(value = "CLOUD-PAYMENT-SERVICE")）以及对应的方法，帮我们去调用

  对比原来直接调用`RestTemplate`实例来访问的方式，这种面向接口编程，相当于直接调用对面方法的这种形式比原来拼接url来调用的形式确实要更`优雅`，可重用性更好，也方便更好的维护。

  ```java
  @GetMapping("/consumer/payment/get/{id}")
      public Result getPayment(@PathVariable("id") Long id) {
          return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id,
                  Result.class);
      }
  ```

- 创建Controller，直接调用本地FeignClient接口。

  ```java
  @RestController
  @Slf4j
  public class OrderFeignController {
      @Resource
      private PaymentFeignService paymentFeignService;
  
      @GetMapping(value = "/consumer/feign/payment/get/{id}")
      public Result getPaymentById(@PathVariable("id") Long id) {
          return paymentFeignService.getPayment(id);
      }
  }
  ```

- 主启动类开启FeignClient

  ```java
  @SpringBootApplication
  @EnableEurekaClient
  @EnableFeignClients
  public class OrderMain {
      public static void main(String[] args) {
          SpringApplication.run(OrderMain.class, args);
      }
  }
  ```

- 修改自定义负载均衡算法，主要是进行了日志的打印，打印每次选择了哪个服务实例

  ```java
  @Slf4j
  public class CustomRandomLoadBalancerClient implements ReactorServiceInstanceLoadBalancer {
  
      // 服务列表
      private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
  
      public CustomRandomLoadBalancerClient(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
          this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
      }
  
      @Override
      public Mono<Response<ServiceInstance>> choose(Request request) {
          ServiceInstanceListSupplier supplier =
                  serviceInstanceListSupplierProvider.getIfAvailable();
          return supplier.get().next().map(this::getInstanceResponse);
      }
  
      /**
       * 使用随机数获取服务
       *
       * @param instances
       * @return
       */
      private Response<ServiceInstance> getInstanceResponse(
              List<ServiceInstance> instances) {
  //        System.out.println("进来了");
          log.info("调用自定义负载均衡算法");
          if (instances.isEmpty()) {
              return new EmptyResponse();
          }
  
          // 随机算法
          int size = instances.size();
          Random random = new Random();
          ServiceInstance instance = instances.get(random.nextInt(size));
          log.info("随机选取的服务实例为 :{}", instance);
  
          return new DefaultResponse(instance);
      }
  }
  ```

- 启动项目

- 测试，多次刷新。

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_05_openfeign_01.png" alt="image-20220413123559502" style="zoom:50%;" />

- 查看日志，发现我们自定义的客户端负载均衡算法同样生效。

  ![image-20220413123710318](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_05_openfeign_02.png)

## OpenFeign超时控制

- 模仿业务超时

  在服务提供者`payment8001`的Controller添加

  ```java
  		@GetMapping(value = "/payment/feign/timeout")
      public String paymentFeignTimeout() {
          try {
              // 模仿超时
              TimeUnit.SECONDS.sleep(3);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
          return serverPort;
      }
  ```

- 在消费者`order80`的FeignClient添加Feign方法调用

  ```java
  @Component
  @FeignClient(value = "CLOUD-PAYMENT-SERVICE")
  public interface PaymentFeignService {
  
      @GetMapping(value = "/payment/get/{id}")
      public Result getPayment(@PathVariable("id") Long id);
  
      @GetMapping(value = "/payment/feign/timeout")
      public String paymentFeignTimeout();
  }
  ```

- 配置OpenFeign超时时间，这里选择的是yml配置方式，配置类方式请查看官方文档。

  在服务消费者的`application.yml`添加OpenFeign配置，下面的配置是全局的默认配置。

  ```yaml
  feign:
    client:
      config:
        default: 
          connectTimeout: 1000
          readTimeout: 1000
          loggerLevel: basic
  ```

  单独为某个FeignClient配置

  ```yaml
  feign:
      client:
          config:
              feignName:
                  connectTimeout: 5000
                  readTimeout: 5000
                  loggerLevel: full
                  errorDecoder: com.example.SimpleErrorDecoder
                  retryer: com.example.SimpleRetryer
                  defaultQueryParameters:
                      query: queryValue
                  defaultRequestHeaders:
                      header: headerValue
                  requestInterceptors:
                      - com.example.FooRequestInterceptor
                      - com.example.BarRequestInterceptor
                  decode404: false
                  encoder: com.example.SimpleEncoder
                  decoder: com.example.SimpleDecoder
                  contract: com.example.SimpleContract
                  capabilities:
                      - com.example.FooCapability
                      - com.example.BarCapability
                  queryMapEncoder: com.example.SimpleQueryMapEncoder
                  metrics.enabled: false
  ```

  其中feignName为`@FeignClient(value = "CLOUD-PAYMENT-SERVICE")`注解中的值。或者`value`改为`name`是一样的，下面是`@FeignClient`注解的源码，可以看到`value`其实是`name`的别名。

  ```java
  		@AliasFor("name")
      String value() default "";
  		@AliasFor("value")
      String name() default "";
  ```

- 这里我们先只按默认的配置来做，启动主启动类后，调用，可以看到报超时错误了，因为我们默认设置的调用超时时间是1s

  ![image-20220413134136780](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220413134136780.png)

  ![image-20220413134330438](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220413134330438.png)

- 继续修改`application.yml`配置超时时间

  ```yml
  feign:
    client:
      config:
        default: # OpenFeign默认配置
          connectTimeout: 1000  # 默认建立连接的超时时间
          readTimeout: 1000     # 默认方法调用超时时间
          loggerLevel: basic    # 日志打印级别
        CLOUD-PAYMENT-SERVICE:
          connectTimeout: 1000  # 默认建立连接的超时时间
          readTimeout: 5000     # 默认方法调用超时时间
          loggerLevel: basic
  ```

- 等热加载完成后，继续调用，等待几秒后，返回了结果，说明我们的配置生效了。

  ![image-20220413134408096](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_05_openfeign_05.png)

## OpenFeign日志增强

通过调整OpenFeign的日志打印级别，可以修改OpenFeign打印Http请求的细节。

OpenFeign日志级别有：

- NONE：默认的，不显示任何日志
- BASIC：仅记录请求方法、URL、响应状态码及执行时间
- HEADERS：除了BASIC中定义的信息之外，还有请求和相应的头信息
- FULL：除了HEADERS中定义的信息外，还有请求和响应的正文及元数据

同样可以使用配置类或者yml来配置，因为SpringBoot默认的日志级别是`info`，因此我们还需要配置日志级别。

```yaml
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

日志框架日志级别有（级别由低到高）

- TRACE：最低级别的日志，一般不会使用
- DEBUG：打印调试信息
- INFO：粒度主要强调程序的运行过程中的一些信息，可以用于生产环境中输出程序运行的一些重要信息。
- WARN：打印警告⚠️日志，表示潜在的可能出问题的地方
- ERROR：出错啦，但是又没完全错，程序还能跑
- FATAL：程序寄了，很高级别的日志了，发生这种日志的打印说明出现重大错误了，直接停止程序纠错吧。
- OFF：最高等级的了，就是关闭所有日志。😄？

选择日志打印级别后，只会打印级别大于等于配置级别的日志，比如设置的级别是INFO，那么就不会打印DEBUG信息和TRACE信息。
