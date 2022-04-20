# 第四章 保护Spring

> 自动配置Spring Security
> 设置自定义的用户存储
> 自定义登录页
> 防范CSRF攻击
> 知道用户是谁

## 1️⃣启动Spring Security

添加依赖Spring Boot starter security

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

当添加了这项依赖之后，自动配置会检测到SpringSecurity出现在类路径中，因此会初始化一些基本的安全配置。

应用会弹出一个HTTP basic认证对话框并提示进行认证。用户默认是user, 密码会在终端随机生成

比如

```
Using generated security password: b8a48105-8dd6-4a2a-b614-811c64cff455
```

自动配置提供安全的特点：

- 所有的HTTP请求都需要认证
- 不需要特定的角色和权限
- 没有登陆页面
- 认证过程是通过HTTP basic认证对话框实现的
- 系统只有一个用户，用户名为user

实际中使用至少需要配置Spring Security以下功能：

- 通过登录页面提示用户进行认证，为不是使用HTTP basic对话框
- 提供多个用户，并提供一个注册页面，这样Taco Cloud用户的型用户能够注册进来。
- 对不同的请求路径，执行不同的安全规则，主页和注册页面不需要认证。

我们可以自定义一些显示的配置来覆盖自动配置提供的功能。这就是继承 [Adapter](https://www.notion.so/Adaper-68a13b5a679642de812055dd182a2a54) 而不是继承接口的好处，我们只用修改我们需要的功能，不需要的直接使用默认设置就好.

## 2️⃣配置Spring Security

- 基于内存的用户存储

  Spring Security支持基于MXL的配置和基于Java的配置

  ```java
  import org.springframework.context.annotation.Configuration;
  import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
  import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
  
  @Configuration
  @EnableWebSecurity
  public class SecurityConfig extends WebSecurityConfigurerAdapter {
  }
  ```

  [Configuration](https://www.notion.so/Configuration-36b0232ac110455eb40437fc62fcb9f9) [EnableWebSecurity](https://www.notion.so/EnableWebSecurity-2ec74f0636344c20885f9d31d5078be5)

  这个基础的安全配置，会将提示登录的HTTP basic认证的对话框变成登录表单

  Spring Security为配置用户存储提供了多个可选方案，包括：基于内存的用户存储，基于JDBC的用户存储，以LDAP单位后端的用户存储、自定义用户详情。配置都可以通过覆盖WebSecurityConfigurerAdapter基础配置基础类中定义的configure方法来进行配置

  假设只有数量有限的几个用户，并且这些用户几乎不会发生变化，那么用户信息可以存储来内存中。

  在内存中配置两个用户

  ```java
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.inMemoryAuthentication()
              .withUser("buzz")
                  .password("infinity")
                  .authorities("ROLE_USER")
              .and()
              .withUser("woody")
                  .authorities("bullseye")
                  .authorities("ROLE_USER");
  }
  ```

  AuthenticationManagerBuilder使用构造者 [Builder](https://www.notion.so/Builder-53f02bb2e7624197be2211cb61aa17f4) 风格来建造认证细节.我们使用inMemoryAuthentication方法来指定用户信息.

  每次调用withUser()用户都会配置一个用户,需要给行方法名,密码和授权信息.一般来说,基于内存的用户存储在测试时很方便,但是不能很方便的编辑用户,这就需要数据库后台作为用户存储.

- 基于JDBC的用户存储

  用户信息通常会在关系型数据库中进行维护,基于JDBC的用户关系存储会更加合理一些.

  使用JDBC存储关系型数据库中的用户信息进行认证所需的Spring Security配置:

  ```java
  @Autowired
  DataSource dataSource;
  
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.jdbcAuthentication()
              .dataSource(dataSource);
  }
  ```

  在这里的configure()中,调用了AuthenticationManagerBuilder的jdbcAuthentication()方法,这个方法需要DataSource数据源的支持,这是通过自动装配来实现的.

  **重写默认的用户查询功能:**

  自动配置能使一切运转起来,但是对我们的数据模式有一些要求,预期某些存储用户数据表已经存在.

  Spring Security查找用户时使用的SQL查询语句如下:

  ```sql
  public static final String DEF_USERS_BY_USERNAME_QUERY =
   "select username,password,enabled " +
   "from users " +
   "where username = ?";
  public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY =
   "select username,authority " +
   "from authorities " +
   "where username = ?";
  public static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY =
   "select g.id, g.group_name, ga.authority " +
   "from groups g, group_members gm, group_authorities ga " +
   "where gm.username = ? " +
   "and g.id = ga.group_id " +
   "and g.id = gm.group_id";
  ```

  第一个查询用来获取用户的用户名,密码以及是否启用的信息,用来 进行用户认证.

  接下来查询用户所授予的权限,用来进行授权.

  最后查询用户作为群组的成员所授予的权限.

  最简单的方式就是在数据库中定义和填充这些查询的表,就可以使用”自动配置”的好处了.

  当然,有时候需要在查询上有更多的控制权,那么可以配置自己的查询:

  ```java
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.jdbcAuthentication()
              .dataSource(dataSource)
              .usersByUsernameQuery(
                      "select username, password, enabled form Users " +
                              "where username=?"
              )
              .authoritiesByUsernameQuery(
                      "select username, authority form UserAuthorities " +
                              "where username=?"
              );
  }
  ```

  所有查询都将用户名作为唯一参数:

    - 认证查询会选取用户名,密码和启用状态信息.
    - 权限查询会去选取零行或多行包含该用户名及其权限信息的数据.
    - 群组查询会包含群组ID,群组名以及权限.

  **使用转码后的密码:**

  上⾯的认证查询，它预期⽤户密码存储在了数据库之中。这⾥唯⼀的问题在于如果密码明⽂存储，就很容易受到⿊客的窃取。但是，如果数据库中的密码进⾏了转码，那么认证就会失败，因为它与⽤ 户提交的明⽂密码并不匹配.可以使用一个passwordEncoder()

  方法来指定一个密码转换器(encoder)

  ```java
  auth.jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery(
                    "select username, password, enabled form Users " +
                            "where username=?"
            )
            .authoritiesByUsernameQuery(
                    "select username, authority form UserAuthorities " +
                            "where username=?"
            )
            .passwordEncoder(new StandardPasswordEncoder("salt"));
  ```

  或者自定义一个类来实现PasswordEncoder接口

  ```java
  public interface PasswordEncoder {
  
  	String encode(CharSequence rawPassword);
  
  	boolean matches(CharSequence rawPassword, String encodedPassword);
  
  	default boolean upgradeEncoding(String encodedPassword) {
  		return false;
  	}
  }
  ```

  数据库中的密 码是永远不会解码的,⽤户在登录时所采取的策略与之相反，输⼊的密 码会按照相同的算法进⾏转码，然后与数据库中已经转码过的密码进⾏ 对⽐。这个对⽐是在PasswordEncoder的matches()⽅法中进⾏的。

- 基于LDAP作为后端的用户存储

  需要依赖(这个太麻烦了，就先不学了)

  ```xml
  <dependency>
      <groupId>org.springframework.ldap</groupId>
      <artifactId>spring-ldap-core</artifactId>
  </dependency>
  <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-ldap</artifactId>
  </dependency>
  <dependency>
      <groupId>com.unboundid</groupId>
      <artifactId>unboundid-ldapsdk</artifactId>
  </dependency>
  ```

  Lightweight Directory Access Protocol轻量级目录访问协议

  配置Spring Security基于LDAP和jdbc的配置差不多,不过使用的是ldapAuthentication方法罢了.

  ```java
  auth.ldapAuthentication()
      .userSearchFilter("(uid={0})")
      .groupSearchFilter("member={0}");
  ```

  方法userSearchFilter()和groupSearchFilter()用来为基础LDAP查询提供过滤条件,分别用于搜索用户和组.

  默认情况下,对于用户和组的基础查询都是空的,即搜索会在LDAP层级目录的根目录开始.

  我们可以设置指定查询根目录,声明⽤户应该在 名为people的组织单元下搜索⽽不是从根开始，⽽组应该在名为groups的组织单元下搜索:

  ```java
  auth.ldapAuthentication()
    .userSearchBase("ou=people")
    .userSearchFilter("(uid={0})")
    .groupSearchBase("ou=groups")
    .groupSearchFilter("member={0}");
  ```

  **配置密码比对**

  基于LDAP认证的默认策略是进⾏绑定操作，直接通过LDAP服务 器认证⽤户。另⼀种可选的⽅式是进⾏⽐对操作。这涉及将输⼊的密码 发送到LDAP⽬录上，并要求服务器将这个密码和⽤户的密码进⾏⽐ 对。因为⽐对是在LDAP服务器内完成的，实际的密码能保持私密。

  ```java
  auth.ldapAuthentication()
    .userSearchBase("ou=people")
    .userSearchFilter("(uid={0})")
    .groupSearchBase("ou=groups")
    .groupSearchFilter("member={0}")
  	.passwordCompare();
  ```

  默认情况下，在登录表单中提供的密码将会与⽤户的LDAP条⽬中 的userPassword属性进⾏⽐对。如果密码被保存在不同的属性中，可 以通过passwordAttribute()⽅法来声明密码属性的名称,同时我们也可以对密码编码

  ```java
  auth.ldapAuthentication()
    .userSearchBase("ou=people")
    .userSearchFilter("(uid={0})")
    .groupSearchBase("ou=groups")
    .groupSearchFilter("member={0}")
    .passwordCompare()
    .passwordAttribute("password")
    .passwordEncoder(new BCryptPasswordEncoder());
  ```

  当然,这里采取的编码方式必须和LDAP服务器一致

  **引用远程的LDAP服务器**

  Spring Security的LDAP认证假设LDAP服务器监听本机的33389端⼝,可以使用contextSource()方法来配置这个地址:contextSource()⽅法会返回⼀个ContextSourceBuilder对象，提供了url()⽅法来指定LDAP服务器的地址.

  ```java
  auth.ldapAuthentication()
    .userSearchBase("ou=people")
    .userSearchFilter("(uid={0})")
    .groupSearchBase("ou=groups")
    .groupSearchFilter("member={0}")
    .passwordCompare()
    .passwordAttribute("password")
    .passwordEncoder(new BCryptPasswordEncoder())
  	.and()
  		.contextSource()
  			.url("ldap://127.0.0.1.com:33389/dc=tacocloud, dc=com");
  ```

  **配置嵌⼊式的LDAP服务器**

  如果没有现成的LDAP服务器,可以使用嵌入式的LDAP服务器,不需要设置远程服务器的URL,只需要通过root()方法指定嵌入式的根前缀就可以了.

  ```java
  .and()
  		.contextSource()
  		.root("dc=taocloud, dc=com");
  ```

  当LDAP服务器启动时，它会尝试在类路径下寻找LDIF⽂件来加载 数据。LDIF（LDAP Data Interchange Format，LDAP数据交换格 式）是以⽂本⽂件展现LDAP数据的标准⽅式。每条记录可以有⼀⾏或多⾏，每项包含⼀个name:value配对信息。记录之间通过空⾏进⾏分割

  不想让Spring从整个根路径下搜索LDIF⽂件，那么可以通 过调⽤ldif()⽅法来明确指定加载哪个LDIF⽂件

  ```java
  .and()
  		.contextSource()
  		.root("dc=tacocloud, dc=com");
  		.ldif("classpath:users.ldif");
  ```

  是⼀个包含⽤户 数据的LDIF⽂件

  ```
  dn: ou=groups,dc=tacocloud,dc=com
  objectclass: top
  objectclass: organizationalUnit
  ou: groups
  dn: ou=people,dc=tacocloud,dc=com
  objectclass: top
  objectclass: organizationalUnit
  ou: people
  dn: uid=buzz,ou=people,dc=tacocloud,dc=com
  objectclass: top
  objectclass: person
  objectclass: organizationalPerson
  objectclass: inetOrgPerson
  cn: Buzz Lightyear
  sn: Lightyear
  uid: buzz
  userPassword: password
  dn: cn=tacocloud,ou=groups,dc=tacocloud,dc=com
  objectclass: top
  objectclass: groupOfNames
  cn: tacocloud
  member: uid=buzz,ou=people,dc=tacocloud,dc=com
  ```

- 自定义用户认证

  用户数据应该位于关系型数据库之中，所以使用JDBC的方法，同时使用Spring Data repository来存储用户 。

    - 定义用户领域对象

        - User.java

          ```java
          package online.pengpeng.tacocloud.entity;
          
          import lombok.Data;
          import lombok.NoArgsConstructor;
          import lombok.RequiredArgsConstructor;
          import org.springframework.security.core.GrantedAuthority;
          import org.springframework.security.core.authority.SimpleGrantedAuthority;
          import org.springframework.security.core.userdetails.UserDetails;
          
          import javax.persistence.Entity;
          import javax.persistence.GeneratedValue;
          import javax.persistence.GenerationType;
          import javax.persistence.Id;
          import java.util.Arrays;
          import java.util.Collection;
          
          @Entity
          @Data
          @NoArgsConstructor(force=true)
          @RequiredArgsConstructor
          public class User implements UserDetails {
              private static final long serialVersionUID = 1L;
          
              @Id
              @GeneratedValue(strategy = GenerationType.AUTO)
              private Long id;
          
              private final String username;
              private final String password;
              private final String fullName;
              private final String street;
              private final String city;
              private final String state;
              private final String zip;
              private final String phoneNumber;
          
              @Override
              public Collection<? extends GrantedAuthority> getAuthorities() {
                  return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
              }
          
              @Override
              public boolean isAccountNonExpired() {
                  return true;
              }
          
              @Override
              public boolean isAccountNonLocked() {
                  return true;
              }
          
              @Override
              public boolean isCredentialsNonExpired() {
                  return false;
              }
          
              @Override
              public boolean isEnabled() {
                  return false;
              }
          }
          ```

      User类实现了Spring Security的
      UserDetails, 通过实现UserDetails接口，我们能够提供更多的信息个框架，如用户被授予了哪些权限，用户账号是否可用等

      getAuthorities()方法因该返回用户被授予权限的一个集合，各种is...Expired()方法要返回一个boolean值，表明账户是否可用或过期。

    - 持久化

      直接继承自CrudRepository,使用JPA

      ```java
      import online.pengpeng.tacocloud.entity.User;
      import org.springframework.data.repository.CrudRepository;
      
      public interface UserRepository extends CrudRepository<User, Long> {
          User findByUsername(String username);
      }
      ```

    - 创建用户详情服务

      ```java
      public interface UserDetailsService {
          UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
      }
      ```

      我们需要实现这个这个接口要么返回查找到的UserDetails对象，要么根据用户名无法得到任何结果的情况下抛出Username NotFoundException异常。

      ```java
      @Service
      public class UserRepositoryUserDetailsService implements UserDetailsService{
      
          private final UserRepository userRepo;
      
          @Autowired
          public UserRepositoryUserDetailsService(UserRepository userRepo){
              this.userRepo = userRepo;
          }
      
          @Override
          public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
              User user = userRepo.findByUsername(username);
              if (user != null) {
                  return user;
              }
              throw new UsernameNotFoundException("User '" + username + "' not found");
          }
      }
      ```

      [Service](https://www.notion.so/Service-cb83901426234fd686a99765211b6c80) 注解也是一个构造性stereotype注解，它表明这个类要包含到Spring的组件扫描中去，不需要在显示的声明这个类为bean了

    - 将这个用户详情服务与Spring Security配置到一起

      通过 [Autowired](https://www.notion.so/Autowired-b1907e552c0647f09e6922e42040022b) 来注入userDetailsService

      ```java
      auth.userDetailsService(userDetailsService);
      ```

      当然，也可以配置一个密码转码器，这样在数据库中二密码是转码过的，首先声明一个PasswordEncoder类型的bean。然后通过调用passwordEncoder（）方法将它注入到用户详情服务中：

      ```java
      @Bean
      public PasswordEncoder encoder() {
       return new BCryptPasswordEncoder()；
      }
      @Override
      protected void configure(AuthenticationManagerBuilder auth)
       throws Exception {
       auth
       .userDetailsService(userDetailsService)
       .passwordEncoder(encoder());
      }
      ```

      encoder方法带有 [Bean](https://www.notion.so/Bean-37f5b779a2e54ccd8c61a6c8d3ac8348) 注解，它将用来在Spring应用上下文中声明PasswordEncoder bean，对于encoder()的任何调用都会被拦截，并且返回上下文中的bean实例。

- 注册用户

    - 注册时需要提交的用户表单实体类

      ```java
      @Data
      public class RegistrationForm {
          @NotNull
          @Size(min=5, message = "user name length at least 5")
          private String username;
          @NotNull(message = "this field can't be empty")
          private String password;
          @NotNull(message = "this field can't be empty")
          private String fullName;
          @NotNull(message = "this field can't be empty")
      
          private String street;
          @NotNull(message = "this field can't be empty")
          private String city;
          @NotNull(message = "this field can't be empty")
          private String state;
          @NotNull(message = "this field can't be empty")
          private String zip;
          @NotNull(message = "this field can't be empty")
          private String phone;
      
          public User toUser(PasswordEncoder passwordEncoder) {
              return new User(
                      username,
                      passwordEncoder.encode(password),
                      fullName,
                      street,
                      city,
                      state,
                      zip,
                      phone
              );
          }
      }
      ```

    - RegistrationController

      ```java
      @Controller
      @RequestMapping("/register")
      public class RegistrationController {
          private UserRepository userRepo;
          private PasswordEncoder passwordEncoder;
      
          public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
              this.userRepo = userRepo;
              this.passwordEncoder = passwordEncoder;
          }
      
          @GetMapping
          public String registerFrom() {
              return "registration";
          }
      
          @PostMapping
          public String processingRegistration(RegistrationForm form) {
              userRepo.save(form.toUser(passwordEncoder));
              return "redirect:/login";
          }
      }
      ```

    - 注册的逻辑视图的模板

      ```html
      <!DOCTYPE html>
      <html lang="en">
      <head>
          <meta charset="UTF-8">
          <title>Taco Cloud</title>
      </head>
      <body>
          <h1>Register</h1>
          <img th:src="@{/images/TacoCloud.png}"/>
      
          <form method="POST" th:action="@{/register}" id="registerFrom">
              <label for="username">Username: </label>
              <input type="text" name="username"/><br/>
              <label for="password">Password: </label>
              <input type="password" name="password"/><br/>
              <label for="confirm">Confirm password: </label>
              <input type="password" name="confirm"/><br/>
              <label for="fullName">Full name: </label>
              <input type="text" name="fullName"/><br/>
              <label for="street">Street: </label>
              <input type="text" name="street"/><br/>
              <label for="city">City: </label>
              <input type="text" name="city"/><br/>
              <label for="state">State: </label>
              <input type="text" name="state"/><br/>
              <label for="zip">Zip: </label>
              <input type="text" name="zip"/><br/>
              <label for="phone">Phone: </label>
              <input type="text" name="phone"/><br/>
              <input type="submit" value="Register"/>
          </form>
      </body>
      </html>
      ```

目前，Taco Cloud应用已经有了完整的用户注册和认证功能，但是如果现在启动应用我们什么也做不了，因为在自动配置security的情况下，所有的请求都需要认证，甚至连首页和注册页面都需要验证。所以还需要自定义安全保护策略。

## 3️⃣保护web请求

为了配置不同的安全策略我们需要重写WebSecurityConfigureAdapter的其它configure方法：

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
}
```

我们可以使用HttpSecurity配置的功能包括：

配置HttpSecurity常见的需求就是拦截请求以确保用户具备适当的权限。

- 保护请求

  我们确保只有认证过的用户才能发起对“/design”和“/orders”的请求。

  ```java
  @Override
      protected void configure(HttpSecurity http) throws Exception {
          http
                  .authorizeRequests()
                  .antMatchers("/design", "orders")
                      .hasRole("ROLE_USER")
                  .antMatchers("/", "/**").permitAll()
          ;
      }
  ```

  这些规则的顺序很重要，声明在前面的规则并声明在后面的规则有更高的优先级

  所有可用的方法：

  | 方法                         | 作用                                             |
    | ---------------------------- | ------------------------------------------------ |
  | access(String)               | 如果给定的SpEL表达式计算结果为true，就允许访问   |
  | anonymous()                  | 允许匿名用户访问                                 |
  | authenticated()              | 允许认证过的用户访问                             |
  | denyAll()                    | ⽆条件拒绝所有访问                               |
  | fullyAythenticationed()      | 如果⽤户是完整认证的（不是通过Remember-me        |
  | 功能认证的），就允许访问     |                                                  |
  | hasAnyAuthenority(String...) | 如果⽤户具备给定权限中的某⼀个，就允许访问       |
  | hasAnyRole(String...)        | 如果⽤户具备给定⾓⾊中的某⼀个，就允许访问       |
  | hasAuthority(String)         | 如果⽤户具备给定权限，就允许访问                 |
  | hasIpAddress(String)         | 如果请求来⾃给定IP地址，就允许访问               |
  | HasRole(String)              | 如果⽤户具备给定⾓⾊，就允许访问                 |
  | not()                        | 对其他访问⽅法的结果求反                         |
  | permitAll()                  | ⽆条件允许访问                                   |
  | rememberMe()                 | 如果⽤户是通过Remember-me功能认证的，就允 许访问 |

- 使用access()方法重写的例子

  ```java
  @Override
  protected void configure(HttpSecurity http) throws Exception {
   http
   .authorizeRequests()
   .antMatchers("/design", "/orders")
   .access("hasRole('ROLE_USER')")
   .antMatchers(“/”, "/**").access("permitAll")
   ;
  }
  ```

  只允许具备ROLLE_USER权限的用户在星期er创建taco

  ```java
  @Override
  protected void configure(HttpSecurity http) throws Exception {
   http
   .authorizeRequests()
   .antMatchers("/design", "/orders")
   .access("hasRole('ROLE_USER') && " +
   "T(java.util.Calendar).getInstance().get("+
   "T(java.util.Calendar).DAY_OF_WEEK) == " +
   "T(java.util.Calendar).TUESDAY")
   .antMatchers(“/”, "/**").access("permitAll")
   ;
  }
  ```

- 替换内置的登录页面

  需要调用httpSecurity对象的formLogin()方法来实现

  ```java
  protected void configure(HttpSecurity http) throws Exception {
          http
              .authorizeRequests()
              .antMatchers("/design", "orders")
                  .hasRole("ROLE_USER")
              .antMatchers("/", "/**").permitAll()
  
              .and()
              .formLogin()
                  .loginPage("/login")
          ;
      }
  ```

  使用and()方法将不同的配置连接在一起，and()方法表示我们已经完成了授权相关的配置，并且要添加一些其它的HTTP配置，在开始新的配置区域之前，可以多次调用and()

  其实就是返回一个 [Builder](https://www.notion.so/Builder-53f02bb2e7624197be2211cb61aa17f4) ，同时使用了泛型

  ```java
  public B and() {
  		return getBuilder();
  	}
  ```

- 添加一个视图控制器

  由于登录页面十分简单，不需要model来传递额外的参数，所以可以直接在webConfig中声明为一个视图控制器

  ```java
  @Override
      public void addViewControllers(ViewControllerRegistry registry) {
          registry.addViewController("/").setViewName("home");
          registry.addViewController("/login").setViewName("login");
      }
  ```

- 自定义登录页的视图

  ```html
  <!DOCTYPE html>
  <html lang="en">
  <head>
      <meta charset="UTF-8">
      <title>Taco Cloud</title>
  </head>
  <body>
      <h1>Login</h1>
      <img th:src="@{/images/TacoCloud.png}"/>
      <div th:if="${error}">
          Unable to login. Check your username and password.
      </div>
      <p>New here? Click
      <a th:href="@{/register}">here</a> to register.</p>
      <!-- tag::thAction[] -->
      <form method="POST" th:action="@{/login}" id="loginForm">
          <!-- end::thAction[] -->
          <label for="username">Username: </label>
          <input type="text" name="username" id="username" /><br/>
          <label for="password">Password: </label>
          <input type="password" name="password" id="password" /><br/>
          <input type="submit" value="Login"/>
      </form>
  </body>
  </html>
  ```

- 配置表单中的username和password

  下面是默认属性，但是写出来之后方便修改

  ```java
  .and()
   .formLogin()
   .loginPage("/login")
   .loginProcessingUrl("/login")
   .usernameParameter("username")
   .passwordParameter("password")
   .defaultSuccessUrl("/design"， true)
  ```

  loginProcessingUrl是处理登录请求的接口

  defaultSuccessUrl表明登录成功后会被重定向到那一个页面，第二个参数为true时表明如果登陆之前用户访问的是一个其他的页面（非登录页面），那么还是会强制重定向到default页面，如果不设置为true，那会回到之前请求的页面

- 退出

  ```java
  .and()
   .logout()
   .logoutSuccessUrl("/")
  ```

  过滤器会拦截对“/logout”的请求

  添加一个退出表单

  ```java
  <form method="POST" th:action="@{/logout}">
   <input type="submit" value="Logout"/>
  </form>
  ```

  当⽤户点击按钮的时候，他们的session将会被清理，这样他们就 退出应⽤了。

- 防止跨站请求伪造

  跨站请求伪造（Cross-Site Request Forgery，CSRF）是⼀种 常⻅的安全攻击。它会让⽤户在⼀个恶意的Web⻚⾯上填写信息，然 后⾃动（通常是秘密的）将表单以攻击受害者的⾝份提交到另外⼀个应 ⽤上。

  为了防⽌这种类型的攻击，应⽤可以在展现表单的时候⽣成⼀个CSRF token，并放到隐藏域中，然后将其临时存储起来，以便后续在服务器上使⽤。

  在Thymeleaf模板中，我们可以按照如下的⽅式在隐 藏域中渲染CSRF token：

  （在design.html中的form表单中添加了这一项）

  ```html
  <input type="hidden" name="_csrf" th:value="${_csrf.token}"/>
  ```

  ```html
  <form method="POST" th:action="@{/login}" id="loginForm">
  ```

  如果需要关闭spring security对CSRF的支持，在configure中可以很轻松的关闭（但完全没有必要关闭）

  ```java
  .and()
   .csrf()
   .disable()
  ```

## 4️⃣了解用户是谁

在OrderController中，在最初创建Order的时候会绑定⼀ 个订单的表单，如果我们能够预先将⽤户的姓名和地址填充到Order中 就好了，这样⽤户就不需要为每个订单都重新输⼊这些信息了。也许更 重要的是，在保存订单的时候应该将Order实体与创建该订单的⽤户关 联起来。

为了在Order实体和User实体之间实现所需的关联，我们需要为Order类添加⼀个新的属性

```java
@Data
@Entity
@Table(name = "Taco_Order")
public class Order implements Serializable {
// ...
    @ManyToOne
    private User user;
//...
}
```

[ManyToOne](https://www.notion.so/ManyToOne-47ca31c23fd7451bb50d553c796e42c9) 注解表明一个订单只能属于一个用户，但是一个用户却可以使用多个订单。

同时在OrderController中，processOrder()方法负责保护订单，这个方法需要修改以便确定当前的认证用户是谁，别调用Order对象的setUser()方法来建立和用户之间的关联。

确定用户是谁的方法：

- 注入Principal对象到控制器方法中

  ```java
  @PostMapping
  public String processOrder(@Valid Order order, Errors errors,
   SessionStatus sessionStatus,
   Principal principal) {
  ...
   User user = userRepository.findByUsername(
   principal.getName());
   order.setUser(user);
  ...
  }
  ```

  这种方法能够运行，但是会在与安全无关的功能中参杂安全性的代码，有耦合不太好。

- 注入Authentication对象到控制器方法中

  ```java
  @PostMapping
  public String processOrder(@Valid Order order, Errors errors,
   SessionStatus sessionStatus,
   Authentication authentication) {
  ...
   User user = (User) authentication.getPrincipal();
   order.setUser(user);
  ...
  }
  ```

  有了Authentication对象之后，我们就可以调⽤getPrincipal()来 获取principal对象,getPrincipal()返回的是java.util.Object，所以我们需要将其转换成User。

- 使用 [AuthenticationPrincipal](https://www.notion.so/AuthenticationPrincipal-0bdbaf5e17504037b21c98e555ed3fb0) 注解来标记方法 💞

  最整洁的方法是在ProcessOrder中直接接受一个User对象，不过我们需要为其添加 @ AuthenticationPrincipal注解，这样他才会变成认证的principal：

  ```java
  @PostMapping
  public String processOrder(@Valid Order order, Errors errors,
   SessionStatus sessionStatus,
   @AuthenticationPrincipal User user) {
   if (errors.hasErrors()) {
   return "orderForm";
   }
   order.setUser(user);
   orderRepo.save(order);
   sessionStatus.setComplete();
   return "redirect:/";
  }
  ```

  使用AuthenticationPrincipal注解的最大好处就是不需要类型转换，同时能够将安全相关的代码仅仅局限于注解本身。

- 使用SecurityContextHolder来获取安全上下文（全局可用）

  ```java
  Authentication authentication =
   SecurityContextHolder.getContext().getAuthentication();
  User user = (User) authentication.getPrincipal();
  ```

  可以从安全上下文中获取一个Authentication对象，然后再获取principal

  这个代码充满了安全性相关的代码，但是可以在应用程序的任何地方使用，而不仅仅是控制器的处理方法中。

## 5️⃣小结

- Spring Security的自动配置是实现基本安全性功能的好本法，但是大多是应用都需要显示的安全配置，这样才能满需特定的安全需求
- 用户详情可以通过用户存储进行管理，它的后端可以是关系型数据库，LDAP或完全自定义实现
- Spring Security会自动方法SCRF攻击
- 一已认证用户的信息可以通过SecurityContext对象（由SecurityContextHolder.getContext()返回），也可以借助 AuthenticationPrincipal注解将其注入到控制器中。