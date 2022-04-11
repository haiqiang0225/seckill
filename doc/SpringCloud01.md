[toc]

# SpringCloud-01：项目搭建

[项目Github地址](https://github.com/haiqiang0225/seckill)

## 父项目搭建

- 新建工程，选择JDK版本和项目骨架，我这里是用maven构建的：

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_father_prj_create.png" alt="image-20220411135923209" style="zoom:50%;" />

- 选择创建位置（我这里已经创建过了所以下面报红）

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_father_prj_create02.png" alt="image-20220411140408747" style="zoom:50%;" />

- 选择`maven`，我这里因为IDEA自带的`maven`用着没啥问题所以选的是自带的，用的全局的`settings.xml`

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_father_prj_create03.png" alt="image-20220411140439287" style="zoom:50%;" />

- 创建完成，如下。删除src目录，因为我们父项目用不到它

  ![image-20220411140734028](https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_father_prj_create04.png)

- 修改`pom.xml`，自己需要哪些组件可以手动的去添加。Maven仓库：[mvnrepository.com/](https://mvnrepository.com/)，版本依赖：[spring cloud版本依赖关系](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)。

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  
  <project xmlns="http://maven.apache.org/POM/4.0.0"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
  
      <groupId>cc.seckill</groupId>
      <artifactId>seckill</artifactId>
      <version>1.0-SNAPSHOT</version>
      <packaging>pom</packaging>
  
  
      <!--  统一管理Jar包版本  -->
      <properties>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <maven.compiler.source>1.8</maven.compiler.source>
          <maven.compiler.target>1.8</maven.compiler.target>
          <junit.version>4.13.2</junit.version>
          <log4j.version>1.2.17</log4j.version>
          <lombok.version>1.18.22</lombok.version>
          <mysql.version>5.1.49</mysql.version>
          <druid.version>1.2.8</druid.version>
          <mybatis.spring.boot.version>2.2.2</mybatis.spring.boot.version>
          <mybatis.plus.spring.boot.version>3.5.1</mybatis.plus.spring.boot.version>
  
      </properties>
  
      <!--  供子模块继承,作用:锁定版本+子module不需要写groupId和version,不会引入依赖  -->
      <!--  组件依赖关系: https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E  -->
      <dependencyManagement>
          <dependencies>
  
              <!--  boot  -->
              <dependency>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-dependencies</artifactId>
                  <version>2.6.3</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
  
              <!--  cloud  -->
              <dependency>
                  <groupId>org.springframework.cloud</groupId>
                  <artifactId>spring-cloud-dependencies</artifactId>
                  <version>2021.0.1</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
  
              <!--  cloud alibaba -->
              <dependency>
                  <groupId>com.alibaba.cloud</groupId>
                  <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                  <version>2021.0.1.0</version>
              </dependency>
  
              <!--  mysql  -->
              <dependency>
                  <groupId>mysql</groupId>
                  <artifactId>mysql-connector-java</artifactId>
                  <version>${mysql.version}</version>
              </dependency>
  
              <!--  数据库连接池  -->
              <dependency>
                  <groupId>com.alibaba</groupId>
                  <artifactId>druid</artifactId>
                  <version>${druid.version}</version>
              </dependency>
  
              <dependency>
                  <groupId>com.alibaba</groupId>
                  <artifactId>druid-spring-boot-starter</artifactId>
                  <version>${druid.version}</version>
              </dependency>
  
              <!--  MyBatis-boot  -->
              <dependency>
                  <groupId>org.mybatis.spring.boot</groupId>
                  <artifactId>mybatis-spring-boot-starter</artifactId>
                  <version>${mybatis.spring.boot.version}</version>
              </dependency>
  
              <!--  junit  -->
              <dependency>
                  <groupId>junit</groupId>
                  <artifactId>junit</artifactId>
                  <version>${junit.version}</version>
              </dependency>
  
              <!--  log4j  -->
              <dependency>
                  <groupId>log4j</groupId>
                  <artifactId>log4j</artifactId>
                  <version>${log4j.version}</version>
              </dependency>
              <!--  lombok  -->
              <dependency>
                  <groupId>org.projectlombok</groupId>
                  <artifactId>lombok</artifactId>
                  <version>${lombok.version}</version>
                  <optional>true</optional>
              </dependency>
  
              <dependency>
                  <groupId>com.baomidou</groupId>
                  <artifactId>mybatis-plus-boot-starter</artifactId>
                  <version>${mybatis.plus.spring.boot.version}</version>
              </dependency>
  
              <!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-devtools -->
              <dependency>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-devtools</artifactId>
                  <version>2.6.6</version>
              </dependency>
  
              <!-- https://mvnrepository.com/artifact/com.github.ulisesbocchio/jasypt-spring-boot-starter -->
              <dependency>
                  <groupId>com.github.ulisesbocchio</groupId>
                  <artifactId>jasypt-spring-boot-starter</artifactId>
                  <version>3.0.4</version>
              </dependency>
  
          </dependencies>
      </dependencyManagement>
  
      <build>
          <plugins>
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
                  <version>2.6.4</version>
                  <executions>
                      <execution>
                          <goals>
                              <goal>repackage</goal>
                          </goals>
                      </execution>
                  </executions>
                  <configuration>
                      <fork>true</fork>
                      <addResources>true</addResources>
                  </configuration>
              </plugin>
          </plugins>
      </build>
  
  </project>
  
  ```

- 如果要用版本管理工具如git的话，创建一个`.gitignore`文件和`README.md`、`LICENSE`文件。创建`.gitignore`文件的插件叫做`.ignore`，在IDEA的marketplace里一搜就有了。

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_father_prj_create05.png" alt="image-20220411141924628" style="zoom:50%;" />

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_father_prj_create06.png" alt="image-20220411142423691" style="zoom:50%;" />

- 父项目搭建完成。

## 子项目搭建

 ### 创建第一个模块`cloud-provider-payment8001`

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create01.png" alt="image-20220411142621479" style="zoom:50%;" />

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create02.png" alt="image-20220411142657049" style="zoom:50%;" />

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create03.png" alt="image-20220411142828385" style="zoom:50%;" />

- 创建完毕

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create04.png" alt="image-20220411142917747" style="zoom:50%;" />

- 添加pom依赖：

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
  
      <artifactId>cloud-provider-payment8001</artifactId>
  
      <properties>
          <maven.compiler.source>8</maven.compiler.source>
          <maven.compiler.target>8</maven.compiler.target>
      </properties>
  
      <dependencies>
  
          <dependency>
              <groupId>com.baomidou</groupId>
              <artifactId>mybatis-plus-boot-starter</artifactId>
          </dependency>
  
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-actuator</artifactId>
          </dependency>
          <dependency>
              <groupId>org.mybatis.spring.boot</groupId>
              <artifactId>mybatis-spring-boot-starter</artifactId>
          </dependency>
  
          <dependency>
              <groupId>com.alibaba</groupId>
              <artifactId>druid-spring-boot-starter</artifactId>
          </dependency>
          <!--mysql-connector-java-->
          <dependency>
              <groupId>mysql</groupId>
              <artifactId>mysql-connector-java</artifactId>
          </dependency>
          <!--jdbc-->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-jdbc</artifactId>
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
  
  
          <!-- https://mvnrepository.com/artifact/com.github.ulisesbocchio/jasypt-spring-boot-starter -->
          <dependency>
              <groupId>com.github.ulisesbocchio</groupId>
              <artifactId>jasypt-spring-boot-starter</artifactId>
          </dependency>
      </dependencies>
  
  
  </project>
  
  ```

- 在`src/main/resources`下添加配置文件`application.yml`

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create05.png" alt="image-20220411143134230" style="zoom:50%;" />

  配置信息如下，注意**将数据库配置等信息换成自己的**：

  需要替换的：

  - `url: jdbc:mysql://mysql_server:3306/db_seckill?useUnicode=true&characterEncoding=utf-8&useSSL=false`
  - `username: root`
  - `password: ENC()`

  ```yaml
  server:
    port: 8001
  
  
  spring:
    application:
      name: cloud-payment-service
  
    datasource:
      type: com.alibaba.druid.pool.DruidDataSource
      driver-class-name: org.gjt.mm.mysql.Driver
      url: jdbc:mysql://mysql_server:3306/db_seckill?useUnicode=true&characterEncoding=utf-8&useSSL=false
      username: root
      password: ENC()
  
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

  这里在配置文件里对敏感信息进行了加密。需要注意的是**`jasypt`的密钥理论上也是不能放在配置文件里，否则和脱了裤子放屁有啥区别呢。**这一步`password`可以先写上明文，后面测试接口都没问题后，再使用加密工具加密一下。

- 创建`payment`表。

  ```sql
  SET NAMES utf8mb4;
  SET FOREIGN_KEY_CHECKS = 0;
  
  -- ----------------------------
  -- Table structure for payment
  -- ----------------------------
  DROP TABLE IF EXISTS `payment`;
  CREATE TABLE `payment` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `serial` varchar(200) DEFAULT '',
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=1513346825690599426 DEFAULT CHARSET=utf8;
  
  SET FOREIGN_KEY_CHECKS = 1;
  ```

  ### 创建启动类

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create06.png" alt="image-20220411144657688" style="zoom:50%;" />

- 创建实体类

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create07.png" alt="image-20220411144813553" style="zoom:50%;" />

  ### 创建dao接口，这里为了开发快捷使用的是MyBatis-Plus，因为我们的重点是学习相关知识而不是去写增删查改

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create08.png" alt="image-20220411145016285" style="zoom:50%;" />

- 测试接口：创建单元测试

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create09.png" alt="image-20220411145820290" style="zoom:50%;" />

  

  测试通过没有问题：<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create10.png" alt="image-20220411145913857" style="zoom:50%;" />

  ### 创建`service`接口以及对应的实现类，因为我们暂时没有业务代码放到这里，所以这里就只是简单的调用`dao`接口

  <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create11.png" alt="image-20220411150242470" style="zoom:50%;" />

  - `PaymentService.java`

  ```java
  package cc.seckill.springcloud.service;
  
  import cc.seckill.springcloud.entities.Payment;
  
  /**
   * description: PaymentService <br>
   * date: 2022/4/8 18:34 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  public interface PaymentService {
      int create(Payment payment);
  
      Payment getPaymentById(Long id);
  }
  
  ```

  - `PaymentServiceImpl.java`

  ```java
  package cc.seckill.springcloud.service.impl;
  
  import cc.seckill.springcloud.dao.PaymentMapper;
  import cc.seckill.springcloud.entities.Payment;
  import cc.seckill.springcloud.service.PaymentService;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Service;
  
  /**
   * description: PaymentServiceImpl <br>
   * date: 2022/4/8 18:35 <br>
   * author: hq <br>
   * version: 1.0 <br>
   */
  @Service
  public class PaymentServiceImpl implements PaymentService {
  
      private PaymentMapper paymentMapper;
  
  
      @Override
      public int create(Payment payment) {
          return paymentMapper.insert(payment);
      }
  
      @Override
      public Payment getPaymentById(Long id) {
          return paymentMapper.selectById(id);
      }
  
  
      /***************       setters       ***************/
      @Autowired
      public void setPaymentMapper(PaymentMapper paymentMapper) {
          this.paymentMapper = paymentMapper;
      }
  }
  
  ```

  ### 编写`Controller`

    <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create12.png" alt="image-20220411150845486" style="zoom:50%;" />

  - 定义统一返回结果

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

  - 编写`PaymentController`，这里先不管url是否是`REST`风格，后续再改。

    ```java
    package cc.seckill.springcloud.controller;
    
    import cc.seckill.springcloud.entities.Payment;
    import cc.seckill.springcloud.entities.Result;
    import cc.seckill.springcloud.service.PaymentService;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;
    
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
            Payment payment = paymentService.getPaymentById(id);
            log.info("查询支付信息: {}", payment);
            Result result = new Result();
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
    
    
        @Autowired
        public void setPaymentService(PaymentService paymentService) {
            this.paymentService = paymentService;
        }
    }
    ```

  - 启动项目，测试

    <img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create13.png" alt="image-20220411151043614" style="zoom:50%;" />

    没啥问题。

## 使用jasypt对配置文件加密

到现在为止，我们的密码等信息是以明文的形式放在配置文件里的，这种裸奔的方式肯定是不合适的。不管是线上项目还是说写这种要放到github上分享的项目。

### 在子项目下引入jasypt包

```xml
<!-- https://mvnrepository.com/artifact/com.github.ulisesbocchio/jasypt-spring-boot-starter -->
        <dependency>
            <groupId>com.github.ulisesbocchio</groupId>
            <artifactId>jasypt-spring-boot-starter</artifactId>
        </dependency>
```

### 添加工具类

CV大法好啊

```java
package cc.seckill.springcloud.utils;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * description: JasyptUtil <br>
 * date: 2022/4/8 15:38 <br>
 * author: hq <br>
 * version: 1.0 <br>
 *
 * @author 杨帅帅
 * @time 2021/12/5 - 1:00
 * <p>
 *      \\\ ///
 * aka  cv大法得来
 */
public class JasyptUtil {

    private static final String PBEWITHHMACSHA512ANDAES_256 =
            "PBEWITHHMACSHA512ANDAES_256";

    /**
     * @param plainText 待加密的原文
     * @param factor    加密秘钥
     * @return java.lang.String
     * @Description: Jasyp 加密（PBEWITHHMACSHA512ANDAES_256）
     * @Author: Rambo
     * @CreateDate: 2020/7/25 14:34
     * @UpdateUser: Rambo
     * @UpdateDate: 2020/7/25 14:34
     * @Version: 1.0.0
     */
    public static String encryptWithSHA512(String plainText, String factor) {
        // 1. 创建加解密工具实例
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        // 2. 加解密配置
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(factor);
        config.setAlgorithm(PBEWITHHMACSHA512ANDAES_256);
        // 为减少配置文件的书写，以下都是 Jasyp 3.x 版本，配置文件默认配置
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        // 3. 加密
        return encryptor.encrypt(plainText);
    }

    /**
     * @param encryptedText 待解密密文
     * @param factor        解密秘钥
     * @return java.lang.String
     * @Description: Jaspy解密（PBEWITHHMACSHA512ANDAES_256）
     * @Author: Rambo
     * @CreateDate: 2020/7/25 14:40
     * @UpdateUser: Rambo
     * @UpdateDate: 2020/7/25 14:40
     * @Version: 1.0.0
     */
    public static String decryptWithSHA512(String encryptedText, String factor) {
        // 1. 创建加解密工具实例
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        // 2. 加解密配置
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(factor);
        config.setAlgorithm(PBEWITHHMACSHA512ANDAES_256);
        // 为减少配置文件的书写，以下都是 Jasyp 3.x 版本，配置文件默认配置
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        // 3. 解密
        return encryptor.decrypt(encryptedText);
    }

    public static void main(String[] args) {
        String factor = "miyao";
        String plainText = "mima";

        String encryptWithSHA512Str = encryptWithSHA512(plainText, factor);
        String decryptWithSHA512Str = decryptWithSHA512(encryptWithSHA512Str, factor);
        System.out.println("采用AES256加密前原文密文：" + encryptWithSHA512Str);
        System.out.println("采用AES256解密后密文原文:" + decryptWithSHA512Str);
        System.out.println("密钥加密" + encryptWithSHA512(factor, factor));
    }
}
```

`String factor = "";`这个是我们加密的密钥，是不能共享给别人的。

`String plainText = "";`这个是我们要加密的明文信息，同样是不能共享给别人的。

运行这个工具类加密自己的密码，得到密文信息。

例如：

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create14.png" alt="image-20220411151620996" style="zoom:50%;" />

### 修改`application.yml`

需要加密的信息用`ENC()`包裹。

```java
server:
  port: 8001 # 绑定端口号


spring:
  application:
    name: cloud-payment-service

  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: org.gjt.mm.mysql.Driver
    url: jdbc:mysql://mysql_server:3306/db_seckill?useUnicode=true&characterEncoding=utf-8&useSSL=false
    username: root
    password: ENC(i7RsueBmDAJEWGuqtMXfmzBZQY58gq3kmGIrRvd7qoKHNyWpSsW/GG0c/J2mnrOd)

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: cc.seckill.srpingcloud.entities

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl


jasypt:
  encryptor:
    password: miyao
    algorithm: PBEWITHHMACSHA512ANDAES_256

```

### 配置环境变量加载密钥

虽然现在我们的密码是加密成密文的了，但是我们看到密钥还是在配置文件里裸奔，这肯定是不行的，因此我们采取其它形式加载密钥。

这里我选择的是从系统环境变量加载。

```bash
echo "JASYPT_PASS=miyao" >> /etc/profile
echo "export JASYPT_PASS" >> /etc/profile
```

**注意这里千万不要写成一个   >    了，一定是  >> ！**建议还是vim然后手打。

记得将`miyao`换成你自己的。

最后将`application.yml`中的密钥信息删除

```yaml
jasypt:
  encryptor:
    # password:
    algorithm: PBEWITHHMACSHA512ANDAES_256
```

### 测试

再次运行刚才的`PaymentMapperTest`，发现报错了.可以看到是因为我们没有加载密钥，这时候我们在手动加载一下密钥就可以了。

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create15.png" alt="image-20220411152326335" style="zoom:50%;" />

添加`init()`方法，再次执行，注意`init()`要添加`@BeforeAll`注解。

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create16.png" alt="image-20220411152506520" style="zoom:50%;" />

测试通过，没有问题：

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create17.png" alt="image-20220411152550382" style="zoom:50%;" />

记得在主启动类也加载密钥！

```java
@SpringBootApplication
@MapperScan("cc.seckill.springcloud.dao")
public class PaymentMain {
    public static void main(String[] args) {
        System.setProperty("jasypt.encryptor.password", System.getenv("JASYPT_PASS"));
        SpringApplication.run(PaymentMain.class, args);
    }
}
```

但是这样的话，我们没写一个测试类就要写一个`init()`这也太麻烦了吧？因此我们抽象出一个`TestBase`类作为公共类让有需要的子测试类继承。

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create18.png" alt="image-20220411152816169" style="zoom:50%;" />

<img src="https://haiqiang-picture.oss-cn-beijing.aliyuncs.com/blog/spring_cloud_start_01_son_prj1_create19.png" alt="image-20220411152839548" style="zoom:50%;" />

运行一下，正常是没有问题的。



____

有疑问欢迎评论私聊。

参考：

- [1.尚硅谷SpringCloud框架开发教程(SpringCloudAlibaba微服务分布式架构丨Spring Cloud)](https://www.bilibili.com/video/BV18E411x7eT)
- [2.小柒2012 / spring-boot-seckill](https://gitee.com/52itstyle/spring-boot-seckill?_from=gitee_search)
- [3.黑马程序员RocketMQ系统精讲，电商分布式消息中间件，硬核揭秘双十一](https://www.bilibili.com/video/BV1L4411y7mn)

