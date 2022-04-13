[toc]
# SpringCloud-03：Eureka服务注册与发现

[项目Github地址](https://github.com/haiqiang0225/seckill)

## 服务注册中心

- 什么是服务注册中心？

  不难理解，就是所有的服务都在服务注册中心注册，消费者可以通过主动查询或者被动通知的方式来获取服务的具体信息。

- 为什么要使用服务注册中心？

  因为每个微服务可能有多个服务提供者，如果通过硬编码的方式编码每个服务提供者的话，那么工作量将非常巨大而且不易维护。所以为了能更好的支持弹性扩容特性，通过服务注册中心解耦了服务提供者和消费者，不再需要硬编码的方式去编码具体的服务提供者，只需要服务提供者自己向服务注册中心注册，然后服务消费者去服务注册中心查询服务即可。

[Eureka服务注册中心（笔记+实操）](https://zhuanlan.zhihu.com/p/369416226)



[Netflex官方文档](https://github.com/Netflix/eureka/wiki/Eureka-at-a-glance)

> tips:现在Eureka已经停止更新了，后续会修改成阿里的Nacos做注册中心。

![Eureka High level Architecture](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/eureka_architecture.png)

可以看到，`Eureka`作为注册中心分为两个部分：`Eureka Server`和`Eureka Client`。Eureka Client会发起Register请求将自身注册到注册中心，这样其他Eureka client通过Get Registry请求就能获取到新注册应用的相关信息。

## Eureka组件

### Eureka Server

注册中心。

### Eureka Client

服务提供者或者服务消费者。

## Eureka单机部署

### 创建Eureka子项目

- 新建项目

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_01.png" alt="image-20220411160459824" style="zoom:50%;" />

- 修改`pom.xml`

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
  
      <artifactId>cloud-eureka-server7001</artifactId>
  
      <properties>
          <maven.compiler.source>8</maven.compiler.source>
          <maven.compiler.target>8</maven.compiler.target>
      </properties>
  
      <dependencies>
        
          <!--  Eureka Server  -->
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
          </dependency>
  
          <!--  项目通用自定义api  -->
          <dependency>
              <groupId>cc.seckill</groupId>
              <artifactId>cloud-api-commons</artifactId>
              <version>${project.version}</version>
          </dependency>
  
          <!--  web 依赖 -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-actuator</artifactId>
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
  
      </dependencies>
  
  </project>
  ```

- 添加`application.yml`

  ```yaml
  server:
    port: 7001
  
  
  eureka:
    instance:
      hostname: localhost  # eureka服务端实例名
    client:
      register-with-eureka: false  # 表示不向注册中心注册自己
      fetch-registry: false  # 不向注册中心检索服务,因为自己就是注册中心
      service-url:  # 设置与 Eureka Server 交互的地址查询服务和注册服务都需要依赖这个地址
        defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  
  ```

- 创建主启动类

  ```java
  package cc.seckill.springcloud;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  
  /**
   * description: EurekaMain <br>
   * date: 2022/4/11 16:19 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @SpringBootApplication
  @EnableEurekaServer
  public class EurekaMain {
      public static void main(String[] args) {
          SpringApplication.run(EurekaMain.class, args);
      }
  }
  ```

- 启动

- 验证

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_02.png" alt="image-20220411162730034" style="zoom:50%;" />

### 修改`provider-payment`模块，让它注册为服务提供者

- 引入Eureka Client依赖

  ```xml
  				<!--  Eureka Client  -->
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
          </dependency>
  ```

- 主启动类添加`EurekaClient`注解

  ```java
  @SpringBootApplication
  @EnableEurekaClient
  @MapperScan("cc.seckill.springcloud.dao")
  public class PaymentMain {
      public static void main(String[] args) {
          System.setProperty("jasypt.encryptor.password", System.getenv("JASYPT_PASS"));
          SpringApplication.run(PaymentMain.class, args);
      }
  }
  ```

- 修改`application.yml`

  ```yaml
  server:
    port: 8001 # 绑定端口号
  
  
  spring:
    application:
      name: cloud-payment-service
  
    datasource:
      type: com.alibaba.druid.pool.DruidDataSource        # 数据源操作类型
      driver-class-name: org.gjt.mm.mysql.Driver          # mysql 驱动
      url: jdbc:mysql://mysql_server:3306/db_seckill?useUnicode=true&characterEncoding=utf-8&useSSL=false
      username: root
      password: ENC()
  
  eureka:
    client:
      register-with-eureka: true   # 注册到Eureka Server
      fetch-registry: true         # 是否从 Eureka Server 拉取注册信息, 集群必须设置为true才能配合ribbon使用负载均衡
      service-url:
        defaultZone: http://localhost:7001/eureka
  
  mybatis:
    mapper-locations: classpath:mapper/*.xml
    type-aliases-package: cc.seckill.srpingcloud.entities
  
  mybatis-plus:
    configuration:
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  
  
  jasypt:
    encryptor:
      #    password:
      algorithm: PBEWITHHMACSHA512ANDAES_256
  
  ```

- 启动,看到服务注册成功

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_03.png" alt="image-20220411183240218" style="zoom:50%;" />

  ### 修改`consumer-order`模块

  - `pom.xml`添加：

    ```xml
    				<!--  Eureka Client  -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            </dependency>
    ```

  - `application.yml`修改：

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
          defaultZone: http://localhost:7001/eureka
    
    ```

  - 启动类添加注解

    ```java
    @SpringBootApplication
    @EnableEurekaClient
    public class OrderMain {
        public static void main(String[] args) {
            SpringApplication.run(OrderMain.class, args);
        }
    }
    ```

  - 启动主启动类

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_04.png" alt="image-20220411183644975" style="zoom:50%;" />

## Eureka集群部署

### 工作原理

![Eureka High level Architecture](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/eureka_architecture.png)

可以看到，`Eureka`集群是通过Replicate来同步数据的，因此不存在主节点和从节点，所有节点都是平等的。节点通过彼此互相注册来提高可用性，每个节点需要添加一个或多个有效的 serviceUrl 指向其他节点。

### 集群部署

我们使用单节点部署伪集群，后续如果要使用多台机器部署的话，只需要修改hosts映射，然后在不同机器启动对应实例即可。

- 在`/ect/hosts`添加主机映射记录（Windows下为`C：\Windows\System32\drivers\etc\hosts`）

  ```shell
  127.0.0.1       eureka7001.com
  127.0.0.1       eureka7002.com
  127.0.0.1       eureka7003.com
  ```

- 修改`cloud-eureka-server7001`目录下的配置文件，复制三份。

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_05.png" alt="image-20220411212023254" style="zoom:50%;" />

  - eureka1:

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
          defaultZone: http://eureka7002.com:7002/eureka, http://eureka7003.com:7003/eureka
    ```

  - eureka2:

    ```yaml
    server:
      port: 7002
    
    eureka:
      instance:
        hostname: eureka7002.com  # eureka服务端实例名
      client:
        register-with-eureka: false  # 表示不向注册中心注册自己
        fetch-registry: false  # 不向注册中心检索服务,因为自己就是注册中心
        service-url: # 设置与 Eureka Server 交互的地址查询服务和注册服务都需要依赖这个地址
          defaultZone: http://eureka7001.com:7001/eureka, http://eureka7003.com:7003/eureka
    
    ```

  - eureka3:

    ```yaml
    server:
      port: 7003
    
    
    eureka:
      instance:
        hostname: eureka7003.com  # eureka服务端实例名
      client:
        register-with-eureka: false  # 表示不向注册中心注册自己
        fetch-registry: false  # 不向注册中心检索服务,因为自己就是注册中心
        service-url: # 设置与 Eureka Server 交互的地址查询服务和注册服务都需要依赖这个地址
          defaultZone: http://eureka7001.com:7001/eureka, http://eureka7002.com:7002/eureka
    ```

  - 修改启动配置，指定SpringBoot的启动配置文件

    <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_06.png" alt="image-20220411212416217" style="zoom:50%;" />

  - 再复制两个

    <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_07.png" alt="image-20220411212505950" style="zoom:50%;" />

    <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_08.png" alt="image-20220411212548091" style="zoom:50%;" />

    第三个同理不放图了。

  - 分别启动

    <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_09.png" alt="image-20220411212823860" style="zoom:50%;" />

  - 查看

    <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_10.png" alt="image-20220411212752759" style="zoom:50%;" />

  集群部署完毕。

## 修改每个子模块，向集群中所有的注册中心注册

### cloud-provider-payment8001模块

- 修改`application.yml`文件中`eureka`部分：

  ```yaml
  eureka:
    client:
      register-with-eureka: true   # 注册到Eureka Server
      fetch-registry: true         # 是否从 Eureka Server 拉取注册信息, 集群必须设置为true才能配合ribbon使用负载均衡
      service-url:
        defaultZone: http://eureka7001.com:7001/eureka,
          http://eureka7002.com:7002/eureka,
          http://eureka7003.com:7003/eureka,
  #      defaultZone: http://localhost:7001/eureka
  
  ```

- 启动模块

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_11.png" alt="image-20220411213654999" style="zoom:50%;" />

- 完成。其它模块同样改动。

- 测试模块：

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_12.png" alt="image-20220411213856329" style="zoom:50%;" />

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_13.png" alt="image-20220411213934127" style="zoom:50%;" />

  没有问题。

## 服务提供者配置集群

- 同上，直接添加多个`yml`文件。

  ![image-20220411214903640](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_14.png)

- 复制多个IDEA启动器

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_15.png" alt="image-20220411215203662" style="zoom:50%;" />

- 启动

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_16.png" alt="image-20220411220104698" style="zoom:50%;" />

- 测试

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_17.png" alt="image-20220411220052171" style="zoom:50%;" />

## 修改服务消费者，走注册中心

- 修改Controller，将url中的硬编码地址修改为服务名称。

  ```java
  @RestController
  @Slf4j
  public class OrderController {
  //    public static final String PAYMENT_URL = "http://localhost:8001";
      public static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";
  
      @Resource
      private RestTemplate restTemplate;
  
      @GetMapping("/consumer/payment/create")
      public Result create(Payment payment) {
          return restTemplate.postForObject(PAYMENT_URL + "/payment/create", payment,
                  Result.class);
      }
  
      @GetMapping("/consumer/payment/get/{id}")
      public Result getPayment(@PathVariable("id") Long id) {
          return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id, Result.class);
      }
  }
  ```

  此时还不能访问，很明显，RestTemplate没有为我们做负载均衡，或者说并没有权利决定调用服务背后的哪个真正服务提供者。

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_18.png" alt="image-20220411220714195" style="zoom:50%;" />

- 开启`LoadBalance`负载均衡，默认ip轮询，每次调用会发现是不一样的端口号即不一样的主机提供的服务（虽然我们还是单机但是可以这么理解）。

  修改配置类：

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_21.png" alt="image-20220411221148170" style="zoom:50%;" />

  ```java
  @Configuration
  public class ApplicationContextConfig {
  
      @Bean
      @LoadBalanced
      public RestTemplate getRestTemplate() {
          return new RestTemplate();
      }
  }
  ```

  

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_19.png" alt="image-20220411220936444" style="zoom:50%;" />

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_20.png" alt="image-20220411221009543" style="zoom:50%;" />



## 修改Eureka服务实例名

这个主要为了后续调试方便，如果是在真实场景下的话。

- 修改配置：

  ```yaml
  eureka:
    client:
      register-with-eureka: true   # 注册到Eureka Server
      fetch-registry: true         # 是否从 Eureka Server 拉取注册信息, 集群必须设置为true才能配合ribbon使用负载均衡
      service-url:
        defaultZone: http://eureka7001.com:7001/eureka,
          http://eureka7002.com:7002/eureka,
          http://eureka7003.com:7003/eureka,
    instance:
      instance-id: payment8001
      prefer-ip-address: true
  ```

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_22.png" alt="image-20220411222001612" style="zoom:50%;" />



## 服务发现

- 修改服务提供者的Controller

  ```java
  package cc.seckill.springcloud.controller;
  
  import cc.seckill.springcloud.entities.Payment;
  import cc.seckill.springcloud.entities.Result;
  import cc.seckill.springcloud.service.PaymentService;
  import com.netflix.appinfo.InstanceInfo;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.cloud.client.ServiceInstance;
  import org.springframework.cloud.client.discovery.DiscoveryClient;
  import org.springframework.web.bind.annotation.*;
  
  import javax.annotation.Resource;
  import java.util.List;
  
  /**
   * description: PaymentController <br>
   * date: 2022/4/8 18:45 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @RestController
  @Slf4j
  public class PaymentController {
      private PaymentService paymentService;
  
      @Value("${server.port}")
      private String serverPort;
  
      @Resource
      private DiscoveryClient discoveryClient;
  
  
      @PostMapping(value = "payment/create")
      public Result create(@RequestBody Payment payment) {
          if (payment == null || payment.getSerial() == null) {
              // 通知...
              log.error("未接收到第三方支付订单信息");
              return Result.error();
          }
          int res = paymentService.create(payment);
          log.info("支付信息: {}", payment);
          Result result = Result.ok();
          if (res > 0) {
              result.put("msg", "支付成功");
          } else {
              result.put("code", 444);
              result.put("msg", "支付失败");
          }
          return result;
      }
  
      @GetMapping(value = "payment/get/{id}")
      public Result getPayment(@PathVariable("id") Long id) {
          log.info("当前端口 : {}", serverPort);
          Payment payment = paymentService.getPaymentById(id);
          log.info("查询支付信息: {}", payment);
          Result result = new Result();
          result.put("port", serverPort);
          if (payment != null) {
              result.put("code", 200);
              result.put("msg", "查询成功");
              result.put("data", payment);
          } else {
              result.put("code", 445);
              result.put("msg", "查询失败");
          }
          return result;
      }
  
      @GetMapping(value = "/payment/discovery")
      public Object discovery() {
          List<String> services = discoveryClient.getServices();
          for (String service : services) {
              log.info("element : {}", service);
          }
          List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT" +
                  "-SERVICE");
          for (ServiceInstance instance : instances) {
              log.info("id:{}, host:{}, port: {}, uri:{}", instance.getInstanceId(),
                      instance.getHost(), instance.getPort(), instance.getUri());
          }
          return this.discoveryClient;
      }
  
  
      @Autowired
      public void setPaymentService(PaymentService paymentService) {
          this.paymentService = paymentService;
      }
  }
  ```

- 主启动类添加注解：

  ```java
  @SpringBootApplication
  @EnableEurekaClient
  @MapperScan("cc.seckill.springcloud.dao")
  @EnableDiscoveryClient
  public class PaymentMain {
      public static void main(String[] args) {
          System.setProperty("jasypt.encryptor.password", System.getenv("JASYPT_PASS"));
          SpringApplication.run(PaymentMain.class, args);
      }
  }
  ```

- 测试：

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_03_eur_23.png" alt="image-20220411223734960" style="zoom:50%;" />



## Eureka自我保护

### 理论

- 某时刻Eureka中的微服务不可用了，Eureka不会立即清理，依旧会对该微服务的信息进行保存。
- 属于CAP中的AP。Consistency(一致性)、Availability(可用性)、Partition Tolerance(分区容错性)。

- 防止EurekaClient可以正常运行，但是与EurekaServer网络不同的情况下，EurekaServer立即将EurekaClient删除导致服务不可用。也就是说服务有可能是健康的，但只是因为一段时间没有心跳包而导致Client无法与Server通信。

### 关闭自我保护



___

