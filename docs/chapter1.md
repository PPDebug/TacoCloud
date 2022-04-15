# 第一章 Spring起步

## 1 什么是Spring

Spring的核心是提供一个容器（Container）,通常称为Spring应用上下文（Spring application context），它们会管理和创建应用组件。这些应用组件也可以称为bean，会在Spring上下文中装配在一起，从而形成一个完整的应用程序。

将bean装配在一起的行为是通过一种依赖注入（Dependency injection,DI）的模式实现的。此外，组件不会再去创建它所依赖的组件并管理他们的生命周期，使用依赖注入的应用依赖于单独的实体（容器）来创建和维护所有的组件，并将其注入到需要他们的bean中。通常，这是通过构造器参数和属性的访问方法来实现的。

在核心容器上，Spring及其一系列的相关库提供了Web框架，各种持久化可选方案，安全框架，与其他系统集成，运行时监控，微服务支持，反应式编程以及众多的现代化应用开发所需的特性。

- 通过XML来配置Bean

  ```xml
  <bean id="invertoryService" class="com.example.IvertoryService"/>
  <bean id="productService" class="com.example.ProductService">
  	<constructor-arg ref="invertoryService"/>
  </bean>
  ```

- 基于java的配置

  ```java
  @Configuration
  public class ServiceConfiguration {
  	@Bean
  	public InvertoryService invertoryService(){
  		return new InventoryService();
  	}
  
  	@Bean
  	public ProductService productService(){
  		return new ProductService(inventoryService());
  	}
  }
  ```

  [Configuration](https://www.notion.so/Configuration-36b0232ac110455eb40437fc62fcb9f9) 注解会告知Spring这是一个配置类，会为Spring应用上下文提供bean，配置类的方法使用 [Bean](https://www.notion.so/Bean-37f5b779a2e54ccd8c61a6c8d3ac8348)   注解进行标注，表明这些方法所返回的对象会以bean的形式添加到spring大的引用上下文中（默认情况下，这些bean所对应的beanID与定义它们的方法名称是相同的）。

相对于基于XML的配置⽅式，基于Java的配置会带来多项额外的收益，包括 更强的类型安全性以及更好的重构能⼒。即便如此，不管是使⽤Java还是使⽤XML的显式配置，只有当Spring不能进⾏⾃动配置的时候才是必要的。

在Spring技术中，自动装配起源于所谓的自动装配(autowiring)和组件扫描(component scanning)。借助组件扫描技术，Spring能够自动发现应用类路径下的组件，并将他们创建成Spring应用上下文中的bean。借助自动装配技术，spring能够自动为组件注入它们所依赖的其它bean。

springboot的自动配置的能力已经远远超过了组件扫描和自动装配，spring是spring框架的扩展，提供了很多增强生产效率的方法，比如自动配置(autoconfiguation),springboot能够基于类路径中的条目，环境变量和其他因素猜测需要配置的组件并将它们装配在一起。

## 2 初始化Spring应用

🚩目标：创建一个Taco Cloud在线应用

- 1️⃣直接使用Spring Initializer初始化应用

  几种方式：

    1. 通过地址为https://start.spring.io/的Web应⽤；
    2. 在命令⾏中使⽤curl命令；
    3. 在命令⾏中使⽤Spring Boot命令⾏接⼝；
    4. 在Spring Tool Suite中创建新项⽬；
    5. 在IntelliJ IDEA中创建新项⽬；
    6. 在NetBeans中创建新项⽬

- 2️⃣检查Spring项目的结构

  ![Untitled](%E7%AC%AC%E4%B8%80%E7%AB%A0%20Spring%204f80e/Untitled.png)

  这是一个典型的Maven的项目结构

    - 源码放在src/main/java
    - 测试代码放到src/test/java
    - 非java的资源放到src/main/resources中
    - mvnw和mvnw.cmd是Maven包装器(Wrapper)脚本，借助这些脚本，即使本机上没有安装Maven，也可以构建项目
    - pom.xml这是Maven的构建规范
    - TacoCouldApplicaiton.java：这是Spring Boot主类，他会启动该项目文件。
    - applicaiton.properties：提供指定配置属性的地方
    - static：可以存放任意为浏览器提供服务的静态内容（图片，样式表，JavaScript等）
    - templates：这个文件用来存放渲染到浏览器的模板文件，我们一般会往里面存放Thymeleaf模板
    - TacoCloodApplicationTests.java：这是一个简单的测试类，它能确保Spring应用上下文可以加载成功。

  一般spring项目的打包方式都会配置为jar，而不是传统的war包

- 3️⃣pom.xml

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>2.2.6.RELEASE</version>
          <relativePath/> <!-- lookup parent from repository -->
      </parent>
      <groupId>online.pengpeng</groupId>
      <artifactId>Taco-Cloud</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <name>Taco-Cloud</name>
      <description>Taco-Cloud</description>
      <properties>
          <java.version>1.8</java.version>
      </properties>
      <dependencies>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-thymeleaf</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-devtools</artifactId>
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
          <!-- https://mvnrepository.com/artifact/org.seleniumhq.selenium/htmlunit-driver -->
  <!--        <dependency>-->
  <!--            <groupId>org.seleniumhq.selenium</groupId>-->
  <!--            <artifactId>htmlunit-driver</artifactId>-->
  <!--            <scope>test</scope>-->
  <!--        </dependency>-->
      </dependencies>
  
      <build>
          <plugins>
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
                  <configuration>
                      <excludes>
                          <exclude>
                              <groupId>org.projectlombok</groupId>
                              <artifactId>lombok</artifactId>
                          </exclude>
                      </excludes>
                  </configuration>
              </plugin>
          </plugins>
      </build>
  
  </project>
  ```

  <parent>元素，更具体来说是它的<version>⼦元素。这 表明我们的项⽬要以spring-boot-starter-parent作为其⽗POM。除了其他的⼀些 功能之外，这个⽗POM为Spring项⽬常⽤的⼀些库提供了依赖管理，现在你不需 要指定它们的版本，因为这是通过⽗POM来管理的。

  Spring Boot starter的依赖的特别之处在于他们本身并不包含库代码，而是传递性的拉取其他的库，这种starter的依赖主要有三个好处：

    1. 构建文件会显著减小并且更易与管理，因为这样不必为每个所需的依赖库都明白依赖
    2. 我们可以根据它们所提供的功能来思考依赖，而不是根据库的名称。如果开发web项目，那么只需要添加web starter就可以了，而不必添加一堆单独的库再编写web应用
    3. 我们不必担心版本的问题，可以直接像相信给定版本的Spring Boot，传递性导入的库的版本是兼容的。

  SpringBoot插件提供一些重要的功能

    1. 它提供一个Maven goal，允许我们使用aven来运行应用
    2. 它会确保所依赖的库都会包含在可执行Jar文件中，并且能够保证他们在运行时类路径下是可用的
    3. 它会在JAR中生成一个manifest文件，将引导类声明为可执行JAR的主类

- 4️⃣**引导应用**

  因为我们将会通过可执行JAR文件的形式来运行应用，所以很重要的一点就是要有一个主类，他会在JAR运行时被执行。我们还需要一个最小化的Spring配置，以引导该应用，这就是TacoCloudApplicaition类所做的事情

    - TacoCloudApplicaiton.java

      ```java
      package online.pengpeng.tacocloud;
      
      import org.springframework.boot.SpringApplication;
      import org.springframework.boot.autoconfigure.SpringBootApplication;
      
      @SpringBootApplication
      public class TacoCloudApplication {
      
          public static void main(String[] args) {
              SpringApplication.run(TacoCloudApplication.class, args);
          }
      
      }
      ```

  [SpringBootApplication](https://www.notion.so/SpringBootApplication-a66b3cc800094a0f82cfa2e80a5444b4) 是一个组合注解，它组合了3个其他的注解

    1. [SpringBootConfiguration](https://www.notion.so/SpringBootConfiguration-4d0372b0184240a58e48c91a83120975) 该类声明为配置类。这个注解实际上是 [Configuration](https://www.notion.so/Configuration-36b0232ac110455eb40437fc62fcb9f9) 注解的特殊形式，这意味着我们在引导类中添加基于Java的Spring框架配置。
    2. [EnableAutoConfiguration](https://www.notion.so/EnableAutoConfiguration-9028f528f4974aebb20b3b72a63582c2) 启动Spring Boot的自动配置，这个注解会告诉SpringBoot自动类他认为我们会用到的组件
    3. [ComponentScan](https://www.notion.so/ComponentScan-6adcec51419e4a689ade62229f3d0642) 启动组件扫描，这样这样我们能够通过想像 [Component](https://www.notion.so/Component-325c9ac2de854b19be69fc1cd6a5c416) [Controller](https://www.notion.so/Controller-e0618ee2a4114a7b93a0660af36170cf) [Service](https://www.notion.so/Service-cb83901426234fd686a99765211b6c80) 这样的注解来声明其他类，Spring会自动发现他们并将他们注册为Spring应用上下文中的组件

  TacoCloudApplication另外一个很重要的方法使他的main()方法，这是JAR文件执行的时候要运行的方法。在大多数情况下，这个 方法都是样板代码，我们编写的诶个SpringBoot应用都会有一个类似或完全相同的方法（里面的类名肯定会变）

  这个main()方法会调用SpringApplication中的静态的run()方法，后者会真正执行应用的引导过程，也就是创建Spring的应用上下文。在传递给run()方法的两个参数中，一个是配置类，另一个是命令行参数。尽管传递给run()的配置类不一定要和引导类相同，但这是最便利和最典型的做法。

  大多数情况下不需要修改引导类中的任何内容，对于简单的应用程序来说，可以在引导类中配置一两个组件。但在大多数应用来说，最好是为没有实现自动配置的功能创建以恶单独的配置类。

- 5️⃣**测试应用**

  测试是软件开发的重要组成部分，鉴于此，Spring Initializer一般会提供一个测试类做作为起步

    - TacoCloudApllicationTests.java

      ```java
      package online.pengpeng.tacocloud;
      
      import org.junit.jupiter.api.Test;
      import org.springframework.boot.test.context.SpringBootTest;
      
      @SpringBootTest
      class TacoCloudApplicationTests {
      
          @Test
          void contextLoads() {
          }
      
      }
      ```

  这个测试类中只有一个空的测试方法，这个测试类会执行必要的检查，确保Spring应用上下文能够成功加载。

  另外，注意这个类带有 [RunWith(SpringRunner.class)](https://www.notion.so/RunWith-SpringRunner-class-3b87b5b1d6c64320aecec992bcc7f9f8)注解，这是Junit的注解，它会提供一个测试运行器（runner）来指导JUnit如何运行测试。相当于给JUnit应用一个插件，以提供自定义的行为。常见的是SpringRunner，这是Spring提供的测试运行器，他会创建测试时所需的Spring应用上下文。

  SpringRunner是SpringUnitClassRunner的别名

  [`SpringBootTest`](https://www.notion.so/SpringBootTest-042bff436090446ab3c5762588421d4d) 会告诉Junit在启动时要添加上SpringBoot的功能，可以理解为同在main方法中调用SpringApplication.run()

## 3 编写Spring应用

为Taco Cloud应用添加一个主页，在添加主页是，会创建两个代码构件

1. 控制器类，用来处理业务相关的请求
2. 视图模板，用来定义主页看起来是什么样子

同时还会编写一个简单的测试来测试主页

- 1️⃣处理Web请求

  Spring自带了一个强大的Web框架，Spring MVC。Spring MVC的核心是**控制器**(Controller)的理念。控制器是处理请求并以某种方式进行信息响应的类。在面向浏览器的应用中，控制器会填充可选的数据模型并将请求传递给一个试图，以便于生成返回给浏览器的HTML

    - HomeController.java

      ```java
      package online.pengpeng.tacocloud.controller;
      
      import org.springframework.stereotype.Controller;
      import org.springframework.web.bind.annotation.GetMapping;
      
      @Controller     // <-- 控制器
      public class HomeController {
          @GetMapping("/")
          public String home(){
              return "home"; // <--返回视图名
          }
      }Controller
      ```

  可以看到，这个带有 [Controller](https://www.notion.so/Controller-e0618ee2a4114a7b93a0660af36170cf)，就其本身而言，Controller注解并没有做什么太多的事情。它的主要目的是让组件扫描这个类识别为一个组件。因为HomeController带有Controller所以spring的组件扫描器会自动发现它，并创建一个带有HomeController实例作为Spring应用上下文中的Bean

  实际上 [Component](https://www.notion.so/Component-325c9ac2de854b19be69fc1cd6a5c416) [Service](https://www.notion.so/Service-cb83901426234fd686a99765211b6c80) [Repository](https://www.notion.so/Repository-7d8b28ac9acd4af48cf4380b8fef1d2c) 的作用是完全相同的。

  home()是一个简单的控制器方法，它带有 [GetMapping](https://www.notion.so/GetMapping-367ae3b0db9d49f980562144fe06fdd9) 注解，表明如果针对“/”发送Http Get请求,那么这个方法会处理请求。该方法所做的只是返回String类型的home值。这个值会被解析为视图的逻辑名。视图如何实现取决于多个因素，但是因为Thymeleaf位于类路径中，所以我们可以使用Thymeleaf来定义模板

- 2️⃣定义视图

  模板名称是由逻辑视图名派生而来，再加上“/templates”前缀和“.html”后缀，最终形成的模板路径将是”/templates/home.html”目录中。

    - home.html

      ```html
      <!DOCTYPE html>
      <html lang="en"
              xmlns="http://www.w3.org/1999/xhtml"
              xmlns:th="http://www.thymeleaf.org">
      <head>
          <meta charset="UTF-8">
          <title>Taco Cloud</title>
      </head>
      <body>
          <h1>Welcome to...</h1>
          <img th:src="@{/images/TacoCloud.png}"/>
      </body>
      </html>
      ```

  注意这里展现TacoCloudLogo图片使用的是thymeleaf的th:src标签和@{...}表达式，一遍引用相对上下文路径的图片

  logo的图片位于”/src/main/resources/static/images/TacoCloud.png”

  现在已经有了以恶处理主页请求的控制器并且有了渲染主页的模板，现在基本可以启动来查看效果，不过在此之前可以先为控制器编写测试

- 3️⃣测试控制器

  在测试Web应用时，对HTML页面的内容进行断言是比较困难的。可以直接使用Spring对测试提供的支持来进行测试

  对于主页来说我们所编写的测试在复杂性上与主页本身差不多。测试需要针对根“/”路径发送HTTP GET请求并期望得到成功结果，其中视图名称为home并且结果包含“Welcome to...”

    - HomeControllerTest

      ```java
      package online.pengpeng.tacocloud.controller;
      
      import static org.hamcrest.Matchers.containsString;
      import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
      import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
      import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
      import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
      
      import org.junit.Test;
      import org.junit.runner.RunWith;
      import org.springframework.beans.factory.annotation.Autowired;
      import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
      import org.springframework.test.context.junit4.SpringRunner;
      import org.springframework.test.web.servlet.MockMvc;
      
      /**
       * @author pengpeng
       * @date 2022/4/15
       */
      @RunWith(SpringRunner.class)
      @WebMvcTest(HomeController.class) // <--针对HomeController的Web测试
      public class HomeControllerTest {
          @Autowired
          private MockMvc mockMvc; // <-- 注入MockMvc
      
          @Test
          public void testHomePage() throws Exception{
              mockMvc.perform(get("/")) // <-- 发起对“/”的GET
                      .andExpect(status().isOk()) // <-- 期望得到HTTP 200
                      .andExpect(view().name("home")) // <-- 期望得到home试视图
                      .andExpect(content().string(containsString("Welcome to..."))); // <-- 期望包含"Welcome to..."
          }
      
      }
      ```

  没有使用SpringBootTest注解，而是使用 [WebMvcTest](https://www.notion.so/WebMvcTest-8ee08972d2b548e0a33109609772c01d) 注解，这是SpringBoot所提供的一个特殊注解，它会让这个测试在SpringMVC应用的上下文中执行，即我们可以向它发送请求。

  通过testHomePage方法，我们定义了针对主页想要执行的测试，他首先使用MockMvc对象对”/”路径发起HTTP GET请求，对于这个请求，设置了如下的测试

    - 响应应该具备HTTP 200 (OK)状态；
    - 视图的逻辑名称应该是home；
    - 渲染后的视图应该包含⽂本“Welcome to...”

- 4️⃣构建和运行应用

  由于是使用的idea，可以直接点击启动引导类旁边的启动按钮启动，在启动过程中，会在控制台看到一些Spring ASCII码，随后是描述启动各个步骤的日志条目，在控制台输出的最后，会看到一条Tomcat（spring自动集成的）已经在port(s):8080启动的日志，这意味着此时你可以打开Web浏览器并导航至主页[http://localhost:8080](http://localhost:8080)

- 5️⃣了解Spring Boot devTools

  DevTools为开发人员提供一些便利的开发期工具，包括：

    1. 代码变更后会自动重启
    2. 当面向浏览器的资源（模板、javaScript、样式表）发生改变时，会自动刷新浏览器
    3. 自动禁用模板缓存
    4. 如果使用H2数据库的话，内置了H2控制台

  同时这是个java包，不是IDE插件，不需要使用特定的IDE，同时这个的作用只是为了开发，所以在生产环境中会自动把自己禁用掉。

    - 应用自动重启

      如果将DevTools作为项⽬的⼀部分，那么你可以看到，当对项⽬中的Java代 码和属性⽂件做出修改后，这些变更稍后就能发挥作⽤。DevTools会监控变更， 当它看到有变化的时候，将会⾃动重启应⽤。

      更准确地说，当DevTools运⾏的时候，应⽤程序会被加载到Java虚拟机 （Java virtual Machine，JVM）两个独⽴的类加载器中。其中⼀个类加载器会加 载你的Java代码、属性⽂件以及项⽬中“src/main/”路径下⼏乎所有的内容。这些 条⽬很可能会经常发⽣变化。另外⼀个类加载器会加载依赖的库，这些库不太可能 经常发⽣变化。

      当探测到变更的时候，DevTools只会重新加载包含项⽬代码的类加载器，并 重启Spring的应⽤上下⽂，在这个过程中另外⼀个类加载器和JVM会原封不动。 这个策略⾮常精细，但是它能减少应⽤启动的时间。

      这种策略的⼀个不⾜之处就是⾃动重启⽆法反映依赖项的变化。这是因为包含 依赖库的类加载器不会⾃动重新加载。这意味着每当我们在构建规范中添加、变更 或移除依赖的时候，为了让变更⽣效，我们需要重新启动应⽤。

    - 浏览器自动刷新和禁用模板缓存

      默认情况下，像Thymeleaf和FreeMarker这样的模板⽅案在配置时会缓存模 板解析的结果。这样的话，在为每个请求提供服务的时候，模板就不⽤重新解析 了。在⽣产环境中，这是⼀种很好的⽅式，因为它会带来⼀定的性能收益。

      但是在开发阶段，如果缓存模板，那么我们刷新浏览器就无法立即看到模板变更后的效果。

      DevTools通过禁⽤所有模板缓存解决了这个问题。你可以对模板进⾏任意数 量的修改，只需要刷新⼀下浏览器就能看到结果。

      DevTools在运⾏的时候，它会和你的应⽤程序⼀起，同时⾃动启动⼀个
      LiveReload服务器。LiveReload服务器本⾝并没有太⼤的⽤处。但是，当它与
      LiveReload浏览器插件结合起来的时候，就能够在模板、图⽚、样式表、
      JavaScript等（实际上，⼏乎涵盖为浏览器提供服务的所有内容）发⽣变化的时候 ⾃动刷新浏览器。

    - 内置的H2控制台

      当使用H2数据库进⾏开发时，DevTools将会⾃动启⽤H2。 通过Web浏览器进⾏访问http://localhost:8080/h2-console，就能看到应⽤所使⽤的数据。

- 6️⃣回顾一下

  之前做的工作一共包括

  1️⃣使用Spring Initializer创建初始的项目结构

  2️⃣编写控制器类处理针对主页的请求

  3️⃣定义一个试图模板来渲染主页

  4️⃣编写一个简单的测试类来验证工作符合预期

  使用过spring分开的一个重要收益是可以只关注应用需求的代码，无需考虑如何满足框架的需求。

  spring利用pom.xml中的依赖来满足需求，其中包括：

    1. Spring的MVC框架
    2. 嵌入式的Tomcat
    3. Thymeleaf和Thymeleaf布局标签
    4. SpringBoot自动配置库
    5. 在Spring应用上下文中配置bean以启动SpringMVC
    6. 在Spring应用上下文中配置嵌入式的Tomcat服务器
    7. 配置Thymeleaf视图解析器，以便使用Thyleaf模板渲染MVC视图

## 4 俯瞰Spring风景线

了解Spring整体生态的最快方法是查看完整版本的Spring Initialzer Web表单上的那一堆框选列表。

- 1️⃣Spring核心框架

  Spring核⼼框架是Spring领域中⼀切的基础。它提供了核⼼容器 和依赖注⼊框架，另外还提供了⼀些其他重要的特性。

  包括SpringMVC，也就是Spring的Web框架，使用SpringMVC编写控制器以处理Web请求，Spring MVC还可以用来创建REST API,以生成非HTML的输出

  Spring核心框架还提供一些对数据持久化的基础支持，尤其是计划于模板的JDBC支持，包括jdbcTemplate

  以及反应式（Reactive）风格编程的支持，其中包括名为Spring WebFlux的新反应式Web框架

- 2️⃣SpringBoot

  SpringBoo会带来很多收益，包括Starter依赖和自动配置。一般来说，会尽可能的使用Spring Boot，并避免任何形式的显示配置，除非显示配置是绝对必要的。除了starter依赖和自动配置，spring boot还提供了大量其他有用的特性：

    1. Actutor能够洞察应用运行运行是的内部状态，包括指标，线程dump信息，应用的健康状况以及应用可用的环境属性
    2. 灵活的环境属性规范
    3. 在核心框架的测试辅助功能之上提供了对测试的额外支持

  除此之外，Spring Boot还提供了⼀个基于Groovy脚本的编程模型，称为
  Spring Boot 命令⾏接⼝（Command-Line Interface，CLI）。使⽤Spring
  Boot CLI，可以将整个应⽤程序编写为Groovy脚本的集合，并通过命令⾏运 ⾏它们。
  Spring Boot已经成为Spring开发中不可或缺的⼀部分，很难想象如果没有它该如何开发Spring应⽤程序。因此使用spring这个词的时候默认是包括了SpringBoot的。

- 3️⃣SpringData

  Spring核⼼框架提供了基本的数据持久化⽀持

  Spring Data提供了⾮常令⼈惊叹的功能：将应⽤程序的数据repository定义为简单的Java接⼝， 在定义驱动存储和检索数据的⽅法时使⽤⼀种命名约定即可。

  Spring Data能够处理多种不同类型的数据库，包括关系型数据库 （JPA）、⽂档数据库（Mongo）、图数据库（Neo4j）

- 4️⃣Spring Security

  Spring Security解决了应⽤程序通⽤的安全性需求，包括⾝份验证、授权和API安全性。

- 5️⃣Spring Integration和Spring Batch

  ⼤多数应⽤程序都需要与其他应⽤甚⾄本应⽤中的其他组件进⾏集成。

  Spring Integration解决了实时集成问题。在实时集成中，数据在可⽤时⻢ 上就会得到处理。

  Spring Batch解决的则是批处理集成的问题，在此过程 中，数据可以收集⼀段时间，直到某个触发器（可能是⼀个时间触发器）发出信 号，表⽰该处理批量数据了才会对数据进⾏批处理。

- 6️⃣Spring Cloud

  应⽤程序开发领域正在进⼊⼀个新的时代，不再将应⽤程序作为单个部署单元来开发，⽽是使⽤由微服务组成的多个独⽴部署单元来组合形成应⽤程序，微服务是一个热门话题，解决了开发期和运行期 的一些实际的问题，但也带来也一些挑战，Spring Cloud可以解决这些挑战，Spring Cloud是使⽤Spring开发云原⽣应⽤程序的⼀组项⽬

## 5 小结

- Spring旨在简化开发⼈员所⾯临的挑战，⽐如创建Web应⽤程序、处理数据 库、保护应⽤程序以及实现微服务。
- Spring Boot构建在Spring之上，通过简化依赖管理、⾃动配置和运⾏时洞 察，使Spring更加易⽤。
- Spring应⽤程序可以使⽤Spring Initializr进⾏初始化。Spring Initializr是 基于Web的应⽤，并且为⼤多数Java开发环境提供了原⽣⽀持。
- 在Spring应⽤上下⽂中，组件（通常称为bean）既可以使⽤Java或XML显式 声明，也可以通过组件扫描发现，还可以使⽤Spring Boot⾃动配置功能实现 ⾃动化配置。