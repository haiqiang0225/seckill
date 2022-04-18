[toc]

# SpringCloud-07: 引入微服务网关SpringCloud Gateway

[项目Github地址](https://github.com/haiqiang0225/seckill)

[SpringCloud Gateway 官方文档](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)

## 微服务网关SpringCloud Gateway

本项目（SpringCloud Gateway）提供了一个构建在 Spring 生态之上的 API Gateway，包括：Spring 5、Spring Boot 2 和 Project Reactor。Spring Cloud Gateway 旨在提供一种简单而有效的方式来路由到 API，并为它们提供横切关注点（AOP），例如：安全性、监控/指标和弹性。

____

1.术语：

- **Route（路由）：**
- **Predicate（断言）：**
- **Filter（过滤器）：**



> 来源：官网翻译

## Route-静态路由

- 新建项目

  ![image-20220417153354454](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_07_gateway_01.png)

- `pom.xml`

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
  
      <artifactId>cloud-gateway9527</artifactId>
  
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
  
  
          <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-gateway -->
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-gateway</artifactId>
          </dependency>
          <dependency>
              <groupId>io.netty</groupId>
              <artifactId>netty-all</artifactId>
          </dependency>
  
          <!--  Eureka Client  -->
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
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

- `application.yml`

  ```yaml
  server:
    port: 9527
  
  
  spring:
    main:
      web-application-type: reactive
    application:
      name: cloud-gateway
    cloud:
      gateway:
        routes:
          - id: payment_route            # 路由的id,要求唯一
            uri: http://localhost:8001   # 转发地址
            predicates:                  # 断言,匹配后才进行转发
              - Path=/payment/get/**
  
  #        - id: payment_route2
  #            uri: http://localhost:8001
  #            predicates:
  #              - Path=/payment/get/**
  
  eureka:
    instance:
      hostname: cloud-gateway-service
      instance-id: cloud-gateway-service
      prefer-ip-address: true
    client:
      service-url:
        defaultZone: http://eureka7001.com:7001/eureka,
  
  ```

- 主启动类

  ```java
  package cc.seckill.springcloud;
  
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
  
  /**
   * description: GatewayMain <br>
   * date: 2022/4/17 15:42 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @SpringBootApplication
  @EnableEurekaClient
  public class GatewayMain {
      public static void main(String[] args) {
          SpringApplication.run(GatewayMain.class, args);
      }
  }
  ```

- 启动测试

  ![image-20220417160005399](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_07_gateway_02.png)

  ![image-20220417160017224](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_07_gateway_03.png)

## Route-动态路由

- `application.yml`，将写死的地址改为服务名

  ```yaml
  server:
    port: 9527
  
  
  spring:
    main:
      web-application-type: reactive
    application:
      name: cloud-gateway
    cloud:
      gateway:
        discovery:
          locator:
            enabled: true                   # 使用微服务名进行动态路由
        routes:
          - id: payment_route               # 路由的id,要求唯一
  #          uri: http://localhost:8001     # 转发地址
            uri: lb://cloud-payment-service # lb 代表开启负载均衡，ReactiveLoadBalancerClientFilter
            predicates:                     # 断言,匹配后才进行转发
              - Path=/payment/**
  
  #        - id: payment_route2
  #            uri: http://localhost:8001
  #            predicates:
  #              - Path=/payment/get/**
  
  eureka:
    instance:
      hostname: cloud-gateway-service
      instance-id: cloud-gateway-service
      prefer-ip-address: true
    client:
      service-url:
        defaultZone: http://eureka7001.com:7001/eureka,
  
  ```

- 启动测试

  ![image-20220417211359996](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_07_gateway_04.png)

- 调用成功且每次返回不一样的端口，代表负载均衡同样生效。

## Predicate断言

请求到了路由后，需要满足所有配置的`Predicate`才能真正的进行转发，只要有一个断言没有匹配成功，那么就不会进行请求的转发，会直接返回`404`。

常用的`Predicate`

- `After Route Predicate`：    请求的真实时间需要满足在配置的时间之后，`ZonedDateTime`格式，`yml`配置：`-After:ZONED_TIME`
- `Before Route Predicate`：  请求的真实时间需要满足在配置的时间之前，`ZonedDateTime`格式，`yml`配置：`-Before:ZONED_TIME`
- `Between Route Predicate`：请求的真实时间需要满足在配置的时间之间，`ZonedDateTime`格式，`yml`配置：`-Between:TS, TE`
- `Cookie Route Predicate`：  请求所携带的`Cookie`需要满足指定的名称和正则表达式（匹配V），`yml`配置：`-Cookie:name,value_rgx`
- `Header Route Predicate`：  请求的请求头需要满足包含指定的属性并且其值与正则表达式匹配，`yml`配置：`-Header:name,value_rgx`
- `Host Route Predicate`：      请求需要满足来自指定的主机地址,`yml`配置：`-Host:URL_RGX`
- `Method Route Predicate`：  请求需要满足是指定的Http方法，比如`GET`,`yml`配置：`-Method:GET`
- `Path Route Predicate`：      请求的路径需要满足配置的路径正则,`yml`配置：`-Path:PATH_RGX`
- `Query Route Predicate`：    请求需要满足携带请求参数`name`，参数的值要满足正则表达式，,`yml`配置：`-Query:name,value_rgx`

> TS=TimeStart,  TE=TimeEnd,  rgx/RGX = Regex=正则表达式



## Filter过滤器

### 自带过滤器以及配置

使用过滤器，可以在请求被路由转发之前或之后进行修改。因此`Filter`的生命周期分为`pre`和`post`两种。`pre`类型的可以做参数校验、权限验证等功能，而`post`类型的可以对响应进行改变，比如改变响应内容、修改响应头。

Route filters are scoped to a particular route. 自带的这些过滤器，都是route级别的，**也就是说在当前route下配置的Filter不会影响其他route**

具体的实现很多，能实现的功能也很多。具体要使用的建议直接查[官方文档](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#gatewayfilter-factories)，这里只简单介绍几个（都是从官网扒的，建议直接官网看）。

- 添加请求头`AddRequestHeader GatewayFilter Factory`的`yml`配置

  ```yaml
  spring:
    cloud:
      gateway:
        routes:
        - id: add_request_header_route
          uri: https://example.org
          filters:
          - AddRequestHeader=X-Request-red, blue
  ```

  没有什么难理解的，就是在请求头添加`X-Request-red`属性，值是`blue`。此外还可以拼接请求路径所带的变量，即下面的{segment}

  ```yaml
  spring:
    cloud:
      gateway:
        routes:
        - id: add_request_header_route
          uri: https://example.org
          predicates:
          - Path=/red/{segment}
          filters:
          - AddRequestHeader=X-Request-Red, Blue-{segment}
  ```

- 添加请求参数`AddRequestParameter` `GatewayFilter`的yml

  ```yaml
  spring:
    cloud:
      gateway:
        routes:
        - id: add_request_parameter_route
          uri: https://example.org
          filters:
          - AddRequestParameter=red, blue
  ```

  给这次请求添加上参数，相当于`https://example.org?red=blue`。同样有类似上面的用法。

  ```yaml
  spring:
    cloud:
      gateway:
        routes:
        - id: add_request_parameter_route
          uri: https://example.org
          predicates:
          - Host: {segment}.myhost.org
          filters:
          - AddRequestParameter=foo, bar-{segment}
  ```

### 全局Filter

The `GlobalFilter` interface has the same signature as `GatewayFilter`. These are special filters that are conditionally applied to all routes.

`GlobalFilter`和`GatewayFilter`是Spring自带的两个接口，实现了`GlobalFilter`接口的类会被视为全局的Filter，应用到所有的路由之上，而`GatewayFilter`则只会应用到特定的路由或者路由分组之上。

除了上面两个接口还要实现`org.springframework.core.Ordered`，这个接口主要是定义全局Filter之间顺序的，一般返回的数字越小，优先级越高。

- `ReactiveLoadBalancerClientFilter`就是一个全局的客户端负载均衡过滤器，它实现了`GlobalFilter`和`Ordered`接口。

  ```yaml
  spring:
    cloud:
      gateway:
        routes:
        - id: myRoute
          uri: lb://service
          predicates:
          - Path=/service/**
  ```

  上面配置的`uri: lb://service`就会应用`ReactiveLoadBalancerClientFilter`，从而实现客户端的负载均衡。

### 自定义Filter

- 新建类`cc.seckill.springcloud.filter.LogGatewayFilter`，代码如下

  ```java
  package cc.seckill.springcloud.filter;
  
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.cloud.gateway.filter.GatewayFilterChain;
  import org.springframework.cloud.gateway.filter.GlobalFilter;
  import org.springframework.core.Ordered;
  import org.springframework.http.HttpStatus;
  import org.springframework.stereotype.Component;
  import org.springframework.web.server.ServerWebExchange;
  import reactor.core.publisher.Mono;
  
  /**
   * description: LogGatewayFilter <br>
   * date: 2022/4/18 21:10 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @Component
  @Slf4j
  public class LogGatewayFilter implements GlobalFilter, Ordered {
  
      @Override
      public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
          log.info("执行全局过滤器");
          String token = exchange.getRequest().getQueryParams().getFirst("token");
          if (token == null) {
              log.warn("未携带token");
              exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
              return exchange.getResponse().setComplete();
          }
          return chain.filter(exchange);
      }
  
      @Override
      public int getOrder() {
          return 0;
      }
  }
  ```

- 启动测试

  ![image-20220418212714691](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_07_gateway_05.png)

  ![image-20220418212727911](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_07_gateway_06.png)

  发现如果请求不带`token`则会报错，说明配置生效。



____

有疑问❓欢迎评论私聊。
