# SpringCloud-02：创建消费者模块

[项目Github地址](https://github.com/haiqiang0225/seckill)

## 创建cloud-consumer-order模块

- 新建子项目

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_02_consumer01.png" alt="image-20220412202455113" style="zoom:50%;" />

我这边创建过了，所以报红没啥毛病。

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_02_consumer02.png" alt="image-20220412202550172" style="zoom:50%;" />

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
  
      <artifactId>cloud-consumer-order80</artifactId>
  
      <properties>
          <maven.compiler.source>8</maven.compiler.source>
          <maven.compiler.target>8</maven.compiler.target>
      </properties>
  
      <dependencies>
  
          <!--  Eureka Client  -->
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
          </dependency>
  
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
    port: 80
  
  spring:
    application:
      name: cloud-order-service
  ```

- 创建主启动类

  ```java
  
     
  package cc.seckill.springcloud;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
  
  /**
   * description: OrderMain <br>
   * date: 2022/4/11 10:13 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @SpringBootApplication
  public class OrderMain {
      public static void main(String[] args) {
          SpringApplication.run(OrderMain.class, args);
      }
  }
  ```

- 添加配置类`cc.seckill.springcloud.config.ApplicationContextConfig.java`

  ```java
  package cc.seckill.springcloud.config;
  
  import org.springframework.cloud.client.loadbalancer.LoadBalanced;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.web.client.RestTemplate;
  
  /**
   * description: ApplicationContextConfig <br>
   * date: 2022/4/11 10:24 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @Configuration
  public class ApplicationContextConfig {
  
      @Bean
      public RestTemplate getRestTemplate() {
          return new RestTemplate();
      }
  }
  ```

- 添加实体类

  ```java
  package cc.seckill.springcloud.entities;
  
  import java.util.HashMap;
  import java.util.Map;
  
  /**
   * description: Result <br>
   * date: 2022/4/8 19:04 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  public class Result extends HashMap<String, Object> {
  
      private static final long serialVersionUID = 1L;
  
      public Result() {
          put("code", 200);
      }
  
      public static Result error() {
          return error(500, "未知异常，请联系管理员");
      }
  
      public static Result error(String msg) {
          return error(500, msg);
      }
  
      public static Result error(int code, String msg) {
          Result r = new Result();
          r.put("code", code);
          r.put("msg", msg);
          return r;
      }
  
      public static Result error(Object msg) {
          Result r = new Result();
          r.put("msg", msg);
          return r;
      }
  
      public static Result ok(Object msg) {
          Result r = new Result();
          r.put("msg", msg);
          return r;
      }
  
  
      public static Result ok(Map<String, Object> map) {
          Result r = new Result();
          r.putAll(map);
          return r;
      }
  
      public static Result ok() {
          return new Result();
      }
  
  
      @Override
      public Result put(String key, Object value) {
          super.put(key, value);
          return this;
      }
  }
  ```

- 添加Controller

  ```java
  package cc.seckill.springcloud.controller;
  
  import cc.seckill.springcloud.entities.Payment;
  import cc.seckill.springcloud.entities.Result;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.PathVariable;
  import org.springframework.web.bind.annotation.RestController;
  import org.springframework.web.client.RestTemplate;
  
  import javax.annotation.Resource;
  
  /**
   * description: OrderController <br>
   * date: 2022/4/11 10:15 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @RestController
  @Slf4j
  public class OrderController {
      public static final String PAYMENT_URL = "http://localhost:8001";
  
  
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

## 重构:提取公共模块

- `entities`包下的类是每个模块都通用的，因此我们抽出来形成公共模块供其它模块调用

- 新建`cloud-api-commons`工程

- 将`entities`包复制到工程目录下

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_02_consumer03.png" alt="image-20220412204625661" style="zoom:50%;" />

- 添加一个主启动类，防止一会maven构建报错

- 执行Maven install

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_02_consumer04.png" alt="image-20220412204748450" style="zoom:50%;" />

- 在其他子工程的`pom.xml`中添加依赖

  ```xml
  			  <!--  项目通用自定义api  -->
          <dependency>
              <groupId>cc.seckill</groupId>
              <artifactId>cloud-api-commons</artifactId>
              <version>${project.version}</version>
          </dependency>
  ```

- 启动测试。
