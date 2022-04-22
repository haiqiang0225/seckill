[toc]

# SpringCloud-08: 引入Nacos服务注册中心与配置中心

[项目Github地址](https://github.com/haiqiang0225/seckill)

[Nacos官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)

[SpringCloud Alibaba 官方文档](https://spring-cloud-alibaba-group.github.io/github-pages/2021/en-us/index.html#_introduction)

[SpringCloud Config 官方文档](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/)

## 注册中心

### 服务提供者

- 新建子工程，该工程使用nacos作为服务配置中心

  ![image-20220419194117996](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/image-20220419194117996.png)

- 引入`pom.xml`依赖

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
  
      <artifactId>cloud-config-nacos-client3377</artifactId>
  
      <properties>
          <maven.compiler.source>8</maven.compiler.source>
          <maven.compiler.target>8</maven.compiler.target>
      </properties>
  
  
      <dependencies>
          <!--  项目通用自定义api  -->
          <dependency>
              <groupId>cc.seckill</groupId>
              <artifactId>cloud-api-commons</artifactId>
              <version>${project.version}</version>
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

  **！！！我这里父项目没注意依赖整错了，记得修改父项目的依赖！！！**

  ```xml
              <!--  cloud alibaba -->
              <dependency>
                  <groupId>com.alibaba.cloud</groupId>
                  <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                  <version>2021.0.1.0</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
  ```

- `application.yml`

  ```yaml
  server:
    port: 9001
  
  
  spring:
    application:
      name: nacos-payment-provider
  
    cloud:
      nacos:
        discovery:
          server-addr: localnacos:8848
  
  
  management:
    endpoints:
      web:
        exposure:
          include: '*'
  ```

- 主启动类

  ```java
  package cc.seckill.springcloud;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
  
  /**
   * description: PaymentMain <br>
   * date: 2022/4/19 19:47 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @SpringBootApplication
  @EnableDiscoveryClient
  public class PaymentMain {
      public static void main(String[] args) {
          SpringApplication.run(PaymentMain.class, args);
      }
  }
  
  ```

- `Controller`

  ```java
  package cc.seckill.springcloud.controller;
  
  import cc.seckill.springcloud.entities.Result;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.PathVariable;
  import org.springframework.web.bind.annotation.RestController;
  
  
  /**
   * description: PaymentController <br>
   * date: 2022/4/19 19:49 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @RestController
  @Slf4j
  public class PaymentController {
      @Value("${server.port}")
      private String serverPort;
  
      @GetMapping("/payment/nacos/get/{id}")
      public Result getPayment(@PathVariable("id") Long id) {
          Result result = new Result();
          result.put("code", "200");
          result.put("data", id);
          result.put("port", serverPort);
          return result;
      }
  
  }
  
  ```

- 启动测试

  ![image-20220419195644684](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_01.png)

- nacos管理后台

  ![image-20220419195707871](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_02.png)

- 再添加一个yml，以启动两个服务

  ![image-20220419200225927](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_03.png)

  ```yaml
  server:
    port: 9002
  
  
  spring:
    application:
      name: nacos-payment-provider
  
    cloud:
      nacos:
        discovery:
          server-addr: localnacos:8848
  
  
  management:
    endpoints:
      web:
        exposure:
          include: '*'
  
  ```

- 再次启动查看，说明配置成功

  ![image-20220419200413728](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_04.png)

  ![image-20220419200401307](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_05.png)

### 服务消费者（客户端）

- 新建项目，不再过多阐述，只放配置代码

- pom

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
  
      <artifactId>cloud-consumer-nacos-order80</artifactId>
  
      <properties>
          <maven.compiler.source>8</maven.compiler.source>
          <maven.compiler.target>8</maven.compiler.target>
      </properties>
  
      <dependencies>
  
          <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-loadbalancer -->
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-loadbalancer</artifactId>
          </dependency>
  
  
  
          <!--  项目通用自定义api  -->
          <dependency>
              <groupId>cc.seckill</groupId>
              <artifactId>cloud-api-commons</artifactId>
              <version>${project.version}</version>
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

- yml

  ```yaml
  server:
    port: 80
  
  
  spring:
    application:
      name: nacos-order-consumer
  
    cloud:
      nacos:
        discovery:
          server-addr: localnacos:8848
  
  ```

- Main

  ```java
  package cc.seckill.springcloud;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
  
  /**
   * description: OrderMain <br>
   * date: 2022/4/19 20:15 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @SpringBootApplication
  @EnableDiscoveryClient
  public class OrderMain {
      public static void main(String[] args) {
          SpringApplication.run(OrderMain.class, args);
      }
  }
  
  ```

- config

  ```java
  package cc.seckill.springcloud.config;
  
  import org.springframework.cloud.client.loadbalancer.LoadBalanced;
  import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
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
      @LoadBalanced
      public RestTemplate getRestTemplate() {
          return new RestTemplate();
      }
  }
  ```

- Controller

  ```java
  package cc.seckill.springcloud.controller;
  
  import cc.seckill.springcloud.entities.Result;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.PathVariable;
  import org.springframework.web.bind.annotation.RestController;
  import org.springframework.web.client.RestTemplate;
  
  import javax.annotation.Resource;
  
  /**
   * description: OrderNacosController <br>
   * date: 2022/4/19 20:19 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @RestController
  @Slf4j
  public class OrderNacosController {
  
      @Resource
      private RestTemplate restTemplate;
  
      @Value("${service-url.nacos-user}")
      private String serverURL;
  
  
      @GetMapping("/consumer/payment/nacos/get/{id}")
      public Result paymentInfo(@PathVariable("id") Long id) {
          return restTemplate.getForObject(serverURL + "/payment/nacos/get/" + id,
                  Result.class);
      }
  
  }
  ```

- 启动![image-20220419203517798](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_06.png)

  

- 测试

  ![image-20220419204227630](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_07.png)

  ![image-20220419204237633](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_08.png)

## 微服务配置中心

### 基础配置

- 创建项目

  ![image-20220419192514701](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_09.png)

- 引入`pom.xml`依赖

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
  
      <artifactId>cloud-config-nacos-client3377</artifactId>
  
      <properties>
          <maven.compiler.source>8</maven.compiler.source>
          <maven.compiler.target>8</maven.compiler.target>
      </properties>
  
  
      <dependencies>
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-bootstrap</artifactId>
          </dependency>
  
          <!--  项目通用自定义api  -->
          <dependency>
              <groupId>cc.seckill</groupId>
              <artifactId>cloud-api-commons</artifactId>
              <version>${project.version}</version>
          </dependency>
  
          <!-- https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-nacos-discovery -->
          <dependency>
              <groupId>com.alibaba.cloud</groupId>
              <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
          </dependency>
  
  
          <!-- https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-alibaba-nacos-config -->
          <dependency>
              <groupId>com.alibaba.cloud</groupId>
              <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
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

- 新建`bootstrap.yml`和`application.yml`

  `bootstrap.yml`加载优先级要高于`application.yml`

  ```yaml
  server:
    port: 3377
  
  spring:
    application:
      name: nacos-config-client
  
    cloud:
      nacos:
        discovery:
          server-addr: localnacos:8848
  
        config:
          server-addr: localnacos:8848
          file-extension: yaml
  
  ```

  `application.yml`

  ```yaml
  spring:
    profiles:
      active: dev
  
  ```

- Controller

  ```java
  package cc.seckill.springcloud.controller;
  
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.cloud.context.config.annotation.RefreshScope;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RestController;
  
  /**
   * description: ConfigClientController <br>
   * date: 2022/4/19 21:04 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @RestController
  @RefreshScope
  public class ConfigClientController {
      @Value("${config.info}")
      private String configInfo;
  
      @GetMapping("/config/info")
      public String getConfigInfo() {
          return configInfo;
      }
  }
  ```

- 配置文件名称规则

  在 Nacos Spring Cloud 中，`dataId` 的完整格式如下：

  ```yaml
  ${prefix}-${spring.profiles.active}.${file-extension}
  ```

  - `prefix` 默认为 `spring.application.name` 的值，也可以通过配置项 `spring.cloud.nacos.config.prefix`来配置。
  - `spring.profiles.active` 即为当前环境对应的 profile，详情可以参考 [Spring Boot文档](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html#boot-features-profiles)。 **注意：当 `spring.profiles.active` 为空时，对应的连接符 `-` 也将不存在，dataId 的拼接格式变成 `${prefix}.${file-extension}`**
  - `file-exetension` 为配置内容的数据格式，可以通过配置项 `spring.cloud.nacos.config.file-extension` 来配置。目前只支持 `properties` 和 `yaml` 类型。

  > 来源： Nacos官网

  因此当前配置文件的名称应该为：`nacos-config-client-dev.yaml`

- 在nacos管理界面新建配置 （配图里的文件名是错的，注意！）

  ![image-20220419211557527](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_10.png)

  ![image-20220419214212282](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_11.png)

- 测试

  ![image-20220419212700170](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_12.png)

### 分类配置

- NameSpace + Group + Data ID来区分配置，类似java类的包名+类名来提供全限定名。

  NameSpace区分部署环境，Group、Data ID进行逻辑区分

  比如我们有三个环境：开发、测试、成产环境，我们就可以创建三个NameSpace，不同的NameSpace之间是相互隔离的。Group可以将不同的微服务划分到同一个Group里，Data ID可以用来区分微服务。

#### 根据DataID分类配置

- 修改配置文件`application.yml`中`spring.profiles.active`的值进行分类配置

  ```yaml
  spring:
    profiles:
      active: test
  #    active: dev
  ```

  ![image-20220419214348550](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_13.png)

  ![image-20220419214410013](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_14.png)

#### 根据Group分类配置

- 添加配置

  ![image-20220419214722449](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_15.png)

  ![image-20220419214828617](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_16.png)

- 修改`bootstrap.yaml`（yaml和yml作用一样的），在config下添加group信息

  ```yaml
  server:
    port: 3377
  
  spring:
    application:
      name: nacos-config-client
  
    cloud:
      nacos:
        discovery:
          server-addr: localnacos:8848
  
        config:
          server-addr: localnacos:8848
          file-extension: yaml
          group: TEST_GROUP
  ```

  同样修改`application.yaml`

  ```yaml
  spring:
    profiles:
      active: info
  #    active: test
  #    active: dev
  ```

- 测试

  ![image-20220419215235005](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_17.png)

#### 根据NameSpace分类配置

- 去命名空间新建NameSpace，然后在配置管理界面切换到对应的命名空间

  ![image-20220420092703371](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_18.png)

  ![image-20220420092624638](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_19.png)

- 修改`bootstrap.yaml`

  ```yaml
  server:
    port: 3377
  
  spring:
    application:
      name: nacos-config-client
  
    cloud:
      nacos:
        discovery:
          server-addr: localnacos:8848
  
        config:
          server-addr: localnacos:8848
          file-extension: yaml
  #        group: TEST_GROUP
          namespace: 9fdb67b6-7d0d-4684-a1a2-bd2af588190d
  
  ```

  `group`可以不注释掉，但是需要和配置文件的`group`一致，因为我上面选的是DefaultGroup，所以直接注释掉了。

- 测试

  ![image-20220420092933498](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_08_nacos_20.png)
