# SpringCloud-04: 实现客户端负载均衡

## 负载均衡

- 概念

  负载+均衡，就是字面意思：将负载均衡到所有的服务器上，让总的压力平均到每个服务器上，防止某个服务器承受不住压力直接挂掉了。

- 实现

  - 客户端负载均衡

    客户端知道每个后端，发送请求时尽量均衡的去发送。

  - 服务端负载均衡

    最常见的就是Nginx负载均衡，客户端是不知道服务端具体是谁的，它只知道Nginx，因此它将自己的请求交给Nginx让Nginx转发请求到真正的服务端，这也是Nginx反向代理的实现方式。因为Nginx知道所有的服务端服务器，因此Nginx在转发时可以做负载均衡，让请求平均（可以带权）的转发到每个后端。

## 集成Ribbon

因为我用的版本比较新，Eureka Client包里是没有集成Ribbon的，因此手动添加Ribbon依赖，结果运行报错了，删了吧，LoadBalancer一样用的。

```java
				<!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-ribbon -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
            <version>2.2.10.RELEASE</version>
        </dependency>
```

既然新的已经将Ribbon抛弃了，那我们这里也采用最新的SpringCloud LoadBalancer做客户端负载均衡。

## 集成SpringCloud LoadBalancer

[Spring Cloud LoadBalancer官方文档](https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#spring-cloud-loadbalancer)

- LoadBalancer默认实现的负载均衡算法提供类是`RoundRobinLoadBalancer`，即轮询的策略。
- 默认除了提供轮询外还提供`RandomLoadBalancer`类，提供随机访问的策略。

### 切换轮询策略为随机访问

- 添加`CustomLoadBalancerConfiguration.java`需要注意的是该类不能添加`@Configuration`注解也不能在`@ComponentScan`之外，即只能放在主启动类的扫描目录之下。

  <img src="../../../Documents/md_image/spring_cloud_start_04_lb_01.png" alt="image-20220412194951392" style="zoom:50%;" />

  ```java
  public class CustomLoadBalancerConfiguration {
  
      @Bean
      ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment environment,
                                                              LoadBalancerClientFactory loadBalancerClientFactory) {
          String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
          return new RandomLoadBalancer(loadBalancerClientFactory
                  .getLazyProvider(name, ServiceInstanceListSupplier.class),
                  name);
      }
  }
  ```

- 在`RestTemplate`的配置类上添加`@LoadBalancerClient`注解

  ```java
  @Configuration
  @LoadBalancerClient(value = "CLOUD-PAYMENT-SERVICE", configuration =
          CustomLoadBalancerConfiguration.class)
  public class ApplicationContextConfig {
  
      @Bean
      @LoadBalanced
      public RestTemplate getRestTemplate() {
          return new RestTemplate();
      }
  }
  ```

  其中`value`的值为要负载均衡的服务名，`configuration`为具体的负载均衡实现类，这里我们选择是`RandomLoadBalancer`

- 启动测试，刷新发现每次调用的服务具体提供者确实是随机的。

## 自定义负载均衡算法

[Spring Cloud LoadBalancer原理讲解及自定义负载均衡器 ](https://www.cnblogs.com/linchenguang/p/15656603.html)

