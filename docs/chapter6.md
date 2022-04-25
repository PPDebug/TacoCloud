# 第六章 创建REST服务

> 本章内容
> 在Spring MVC中定义REST端点
> 启动超链接REST资源
> 自动化基于repository的REST端点

<aside>
⛔ 注意：这一部分开始采用前后端分离架构，并使用angular来创建一个前端的单页面应用(Single Page Application， SPA)，相关的代码放在本仓库的ui-angular分支，是从之前的较旧的版本重构过来的，然后对angualr不是很熟悉，没有进行测试，所以可能有些错误。


</aside>

![taco-cloud-ui](%E7%AC%AC%E5%85%AD%E7%AB%A0%20%E5%88%9B%E5%BB%BAREST%E6%9C%8D%E5%8A%A1%203a6cb28ac401476484b2f2c05dce39d2/Untitled.png)

taco-cloud-ui

另外值得一提的是，尽管现在是在基于前后端分离的开发，但实际上之前的thymeleaf模板以及controller接口并没有删除，所以现在浏览器上依然能访问8080端口的之前的页面，而且数据还是一致的 😉

## 1️⃣编写RESTFUL控制器

- 从服务器检索数据

  现在需要添加一个功能，即在单击Latest Designs链接时显示最近创建的taco列表:

    - angualar的RecentsComponent.ts的代码如下

      ```jsx
      import { Component, OnInit, Injectable } from '@angular/core';
      import { Http } from '@angular/http';
      import { HttpClient } from '@angular/common/http';
      @Component({
           selector: 'recent-tacos',
           templateUrl: 'recents.component.html',
           styleUrls: ['./recents.component.css']
      })
      @Injectable()
      export class RecentTacosComponent implements OnInit {
           recentTacos: any;
      
           constructor(private httpClient: HttpClient) { }
      
           ngOnInit() {
               this.httpClient.get('http://localhost:8080/design/recent') ⇽--- 从服务器端获取最近的taco
               .subscribe(data => this.recentTacos = data);
           }
      }
      ```

      主要关注ngOninit()方法，使用注入的Http模块来针对http://localhost:8080/design/recent的地址发送HTTP GET请求，并期望得到一个包含taco设计的列表, 他们被放在名为recentTacos的模型中，视图在(recents.component.html)会将模型数据展示为HTML的形式，以便在浏览器中渲染。

    - 后端创建一个新的控制器来请求

      ```java
      @RestController("restDesignTacoController") // 由于之前的MVC架构的代码依然保留着，所以需要自定义bean的名字（类可以重名是因为放在了不同的包下）
      @RequestMapping(path="design", produces="application/json") // 处理针对"/design"的请求，同时返回类型是json避免与MVC冲突
      @CrossOrigin(origins="*") // 允许跨域请求
      public class DesignTacoController {
          private final TacoRepository tacoRepo;
      
          @Autowired
          public DesignTacoController(TacoRepository tacoRepo) {
              this.tacoRepo = tacoRepo;
          }
      
          @GetMapping("/recent")
          public Iterable<Taco> recentTaco() {
              PageRequest page = PageRequest.of(
                      0, 12, Sort.by("createdAt").descending()
              );
              return tacoRepo.findAll(page).orElse(null);
          }
      }
      ```

      这个类实际上在MVC时就存在了，不过当时是用来处理多页Taco Cloud应用的，之前使用的是@Controller注解，现在是 [RestController](https://www.notion.so/RestController-7bdfd3181ff74b3891239f10330c867c) 注解，这个注解有两个目的：首先这也是一个构造型注解，能够让类被组件扫描功能发现，同时@RestController注解会告诉Spring，控制器中所有的处理器方法的返回值都要直接写入响应体中，而不是将值传递给一个视图并进行渲染。@RestController注解可以被@Controller注解加上  [ResponseBody](https://www.notion.so/ResponseBody-208a5b499b60475ebbe8a460b462d768) 代替，或者返回ResponseEntity对象。

      类级别的 [RequestMapping](https://www.notion.so/RequestMapping-781a599393774008b1a4ecacfa8c0b1d) 注解 中的produces属性可以指明DesignTacoController中的所有处理器方法只会处理Accept头信息包中含”appication/json”的请求。这样会限制API必须基于JSON，但同时又可以实现一个接口路径的复用(只要其它请求不要求json格式就可以)

      [CrossOrigin](https://www.notion.so/CrossOrigin-48edd61926054779a8beee8a13f567c2) 注解会让会在服务器响应头中添加(Cross-Origin-Resource Sharing， CORS)信息头来图突破这一限制。

      提供一个按照ID查询信息的接口，通过在处理器方的路径上使用占位符让方法接受一个路径变量

      ```java
      @GetMapping("/{id}")
        public Taco tacoById(@PathVariable("id") Long id) {
            Optional<Taco> optTaco = tacoRepo.findById(id);
            return optTaco.orElse(null);
        }
      ```

      请求路径”/design/{id}”中，{id}是占位符，实际的请求值会传递给id参数，通过 [PathVariable](https://www.notion.so/PathVariable-961fd2998213467ba9a23749cb25ef67) 注解与id占位符进行匹配

      在tacoById中，id参数被传递到了repository的findById()方法中,以便于获取指定的Taco，返回值Optional<Taco>表示可能获取得到，也可能获取不到。

      这种方法，如果获取不到，服务器会获得一个空的响应体但是状态码为ok(200)，显然不太好，更好的方式是使用ResponseEntity<T>

      ```java
      @GetMapping("/{id}")
      public ResponseEntity<Taco> tacoById(@PathVariable("id") Long id) {
          Optional<Taco> optTaco = tacoRepo.findById(id);
          if (optTaco.isPresent()) {
              return ResponseEntity.ok(optTaco.get());
          }
          return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
      }
      ```

      使用ResponseEntity不仅可以指定返回数据，还可以指定返回Http状态, 方面的代码可以化简为

      ```java
      @GetMapping("/{id}")
      public ResponseEntity<Taco> tacoById(@PathVariable("id") Long id) {
          Optional<Taco> optTaco = tacoRepo.findAllById(id);
          return optTaco.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
      }
      ```

      验证一下：(-i选项可看见返回的响应信息)

      ```bash
      curl localhost:8080/design/recent
      ```

      ```bash
      curl -i localhost:8080/design/2
      ```

- 发送数据到服务器

  创建Taco时前端收集SPA用户数据，然后提交到服务器处理，一个前端发送数据的接口实例

  ```jsx
  onSubmit() {
      this.taco.name = this.tacoName;
      this.api.postCommon("design", this.taco)
        .subscribe(res => {
          window.alert("Success");
          this.router.navigateByUrl("/recents")
        })
    }
  
  // api对应的方法
  rootUrl = "http://localhost:8080/rest/";
  
    private headers = new HttpHeaders()
    // content-type
    .append("content-type", "application/json")
    // COSR
    .append("Access-Control-Allow-Origin","*")
    .append("Access-Control-Allow-Methods","GET, POST, PUT, OPTIONS, PATCH, DELETE")
    .append("Access-Control-Allow-Headers","X-Requested-With, content-type");
  
  public postCommon(subUrl:String, data: any) {
      console.log("api post: ", data);
      return this.httpClient.post(
        this.rootUrl + subUrl,
        data,
        {
          headers: this.headers
        }
      );
    }
  ```

  同时需要在DesignTacoController中有一个接收的接口：

  ```java
  @PostMapping(consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public Taco poseTaco(@RequestBody Taco taco) {
      return tacoRepo.save(taco);
  }
  ```

  <aside>
  ⚠️ 注意：由于我们之前配置了Spring Security安全模块，所以这样直接访问POST方法，然后就属于没有登录，然后就跳转到MVC 的 login界面，然后就跨域了
  为了减少麻烦，将SercurityConfig类上面的注解`@Configuration`, `@EnableWebSecurity`  的两个注解注释掉（就是不再使用），然后新建了一个RestSecurityConfig类


  </aside>

- RestSecurityConfig

  这个配置类目前允许大部分请求通过，并且关闭了其COSR安全防护

  ```java
  @Configuration
  @EnableWebSecurity
  public class RestSecurityConfig extends WebSecurityConfigurerAdapter {
  
      @Autowired
      DataSource dataSource;
      @Autowired
      private UserDetailsService userDetailsService;
  
      @Bean
      public PasswordEncoder encoder(){
          return new BCryptPasswordEncoder();
      }
  
      @Bean RestLoginUrlAuthenticationEntryPoint restLogUrlAuthenticationEntrypoint(){
          return new RestLoginUrlAuthenticationEntryPoint("/rest/login");
      }
  
      @Override
      protected void configure(AuthenticationManagerBuilder auth) throws Exception {
          auth
                  .userDetailsService(userDetailsService)
                  .passwordEncoder(encoder())
          ;
      }
  
      @Override
      protected void configure(HttpSecurity http) throws Exception {
          http.csrf().disable();
          http
              .authorizeRequests()
              .antMatchers("/rest/orders")
                  .hasRole("USER")
              .antMatchers("/", "/**").permitAll()
  
              .and()
                  .formLogin()
                  .loginProcessingUrl("/rest/login")
                  .usernameParameter("username")
                  .passwordParameter("password")
  
              .and()
                  .exceptionHandling()
                  .authenticationEntryPoint(restLogUrlAuthenticationEntrypoint())
          ;
      }
  }
  
  ```

  其中主要用到了一个处理未登录返回值的一个类：

  ```java
  public class RestLoginUrlAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {
  
      private static final Log logger = LogFactory.getLog(RestLoginUrlAuthenticationEntryPoint.class);
  
      private PortMapper portMapper = new PortMapperImpl();
      private PortResolver portResolver = new PortResolverImpl();
      private String loginFormUrl;
      private boolean forceHttps = false;
      private boolean useForward = false;
      private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
  
      public RestLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
          Assert.notNull(loginFormUrl, "loginFormUrl cannot be null");
          this.loginFormUrl = loginFormUrl;
      }
  
      @Override
      public void afterPropertiesSet() throws Exception {
          Assert.isTrue(StringUtils.hasText(loginFormUrl) && UrlUtils
                  .isValidRedirectUrl(loginFormUrl), "loginFromUrl must be specified and must be valid redirect URL");
          if (useForward && UrlUtils.isAbsoluteUrl(loginFormUrl)){
              throw  new IllegalArgumentException(
                      "useForward must be false if using an absolute loginFormURL");
          }
          Assert.notNull(portMapper, "portMapper must be specified");
          Assert.notNull(portResolver, "portResolver must be specified");
      }
  
      @Override
      public void commence(HttpServletRequest request, HttpServletResponse  response, AuthenticationException e) throws IOException, ServletException {
          response.setContentType("application/json;charset=utf-8");
          response.setStatus(HttpStatus.FORBIDDEN.value());
          PrintWriter out = response.getWriter();
          String sb = "{\"status\":\"error\", \"msg\":\"" +
                  "未登录" +
                  "\"}";
          out.write(sb);
          out.flush();
          out.close();
      }
  }
  ```

- 然后将新加的conntroller接口都放在了restcontroller的包里

  ```java
  @RestController("restDesignTacoController")
  @RequestMapping( path = "/rest/design", produces = "application/json")
  @CrossOrigin(origins="*") // 允许跨域请求
  public class DesignTacoController {
  
      private final IngredientRepository ingredientRepo;
      private final TacoRepository tacoRepo;
  
      @Autowired
      public DesignTacoController(IngredientRepository ingredientRepo, TacoRepository tacoRepo) {
          this.ingredientRepo = ingredientRepo;
          this.tacoRepo = tacoRepo;
      }
  
      @GetMapping("/ingredients")
      public ResponseEntity<Iterable<Ingredient>> allIngredient() {
          return ResponseEntity.ok(ingredientRepo.findAll());
      }
  
      @GetMapping("/recent")
      public Iterable<Taco> recentTaco() {
          Pageable page = PageRequest.of(
                  0, 12, Sort.by("createdAt").descending()
          );
          return tacoRepo.findAll(page).orElse(null);
      }
  
      @PostMapping(consumes = "application/json")
      @ResponseStatus(HttpStatus.CREATED)
      public Taco poseTaco(@RequestBody @Valid Taco taco) {
          return tacoRepo.save(taco);
      }
  }
  ```

在这个Post请求的方法体上使用了注解 [ResponseStatus](https://www.notion.so/ResponseStatus-ac5e3a698e6e45e38491b13808ebf904) (HttpStatus.CREATE)表示在请求成功的情况下，返回成功代码201而不仅仅是通过成功代码200

- 附：完善用户和订单模块的rest接口 **TODO**

    - 用户接口

      ```java
      
      ```

- 在服务器上更新数据

  PUT请求其实是与GET请求对立的，即GET表示从服务器往客户端 发送数据，而PUT请求则是从客户端往服务器发送数据。

  PUT请求其实是执行大规模的替换（replacement）操作，而不是更新操作。HTTP PACTH的目的是对资源大补丁或局部更新。

  更新某个订单的信息： [PutMapping](https://www.notion.so/PutMapping-cfe79352042e4f8eb21c4d7e8ed99b6b)

  ```java
  @PutMapping("/{orderId}")
  public Order putOrder(@RequestBody Order order) {
   return repo.save(order);
  }
  ```

  这种方式可以运行，但是它需要将完整的订单从PUT请求中提交上来。如果省略了订单上的某个数据，那么该属性的值应该被null所覆盖，甚至订单中的taco也需要和订单数据一起设置，否咋，他们会从订单中移除。

  如果只想更新局部数据，那么应该则么办呢？

  使用HTTP PATCH请求和Spring的注解 [PatchMapping](https://www.notion.so/PatchMapping-fe25b300c6fb4cf6856d136039156626)

  ```java
  @PatchMapping(path="/{orderId}", consumes="application/json")
  public Order patchOrder(@PathVariable("orderId") Long orderId,
   @RequestBody Order patch) {
   Order order = repo.findById(orderId).get();
   if (patch.getDeliveryName() != null) {
   order.setDeliveryName(patch.getDeliveryName());
   }
   if (patch.getDeliveryStreet() != null) {
   order.setDeliveryStreet(patch.getDeliveryStreet());
   }
   if (patch.getDeliveryCity() != null) {
   order.setDeliveryCity(patch.getDeliveryCity());
   }
   if (patch.getDeliveryState() != null) {
   order.setDeliveryState(patch.getDeliveryState());
   }
   if (patch.getDeliveryZip() != null) {
   order.setDeliveryZip(patch.getDeliveryState());
   }
   if (patch.getCcNumber() != null) {
   order.setCcNumber(patch.getCcNumber());
   }
   if (patch.getCcExpiration() != null) {
   order.setCcExpiration(patch.getCcExpiration());
   }
   if (patch.getCcCVV() != null) {
   order.setCcCVV(patch.getCcCVV());
   }
   return repo.save(order);
  }
  ```

  patchOrder（）方法比putOrder方法()更加复杂一些这是因为Spring MVC的映射注解，虽然能够分辨出不同的方法，但处理请求的逻辑还是要自己写的。

- 删除服务器上的数据

  删除不需要的资源HTTP DELETE [DeleteMapping](https://www.notion.so/DeleteMapping-2d6aadf53c844ea0aa846e1ae6a0a24f)

  ```java
  @DeleteMapping("/{orderId}")
  @ResponseStatus(code=HttpStatus.NO_CONTENT)
  public void deleteOrder(@PathVariable("orderId") Long orderId) {
   try {
   repo.deleteById(orderId);
   } catch (EmptyResultDataAccessException e) {}
  }
  ```

  删除一个资源之后，应该返回一个资源已不再的状态 [ResponseStatus](https://www.notion.so/ResponseStatus-ac5e3a698e6e45e38491b13808ebf904) HttpStatus.NO_CONTENT 204

## 2️⃣启动超媒体

目前的方式需要客户端知道API的URL模式，例如客户端需要以硬编码的方式来对/design/recent发送GET请求，但是如果api的URL发生发生变化之后，客户端就无法正常运行了

**超媒体作为应用状态引擎(Hypermedia as the Engine of Applications State， HATEOAS)**是一种自描述API的方，API所返回的资源中会包含相关资源的链接，客户端只需要知道最少的API URL信息就可以导航整个API，这种方式能够掌握API所提供的资源之间的关系，客户端能够基于API中的URL所发现的关系对他们进行遍历。

- 例子

  比如，一个原始请求最近设计的taco列表的响应体可以是：

  ```json
  [
  	 {
  		 "id": 4,
  		 "name": "Veg-Out",
  		 "createdAt": "2018-01-31T20:15:53.219+0000",
  		 "ingredients": [
  			 {"id": "FLTO", "name": "Flour Tortilla", "type": "WRAP"},
  			 {"id": "COTO", "name": "Corn Tortilla", "type": "WRAP"},
  			 {"id": "TMTO", "name": "Diced Tomatoes", "type": "VEGGIES"},
  			 {"id": "LETC", "name": "Lettuce", "type": "VEGGIES"},
  			 {"id": "SLSA", "name": "Salsa", "type": "SAUCE"}
  			 ]
  		 },
  	 ...
  ]
  ```

  如果客户端想要获取某个taco或者对其进⾏其他HTTP操作，就需要将它的id属性以硬 编码的⽅式拼接到⼀个路径为“/design”的URL上。与之类似，如果客户端想要对某个配料
  执⾏HTTP请求，就需要将该配料id属性的值拼接到路径为“/ingredients”的URL上。在这 两种情况下，都需要在路径上添加“http://”或“https://”前缀以及API的主机名。

  如果API启⽤了超媒体功能，那么API将会描述⾃⼰的URL

  ```json
  {
  	 "_embedded": {
  	 "tacoResourceList": [
  	 {
  	 "name": "Veg-Out",
  	 "createdAt": "2018-01-31T20:15:53.219+0000",
  	 "ingredients": [
  	 {
  	 "name": "Flour Tortilla", "type": "WRAP",
  	 "_links": {
  	 "self": { "href": "http://localhost:8080/ingredients/FLTO" }
  	 }
  	 },
  	 {
  	 "name": "Corn Tortilla", "type": "WRAP",
  	 "_links": {
  	 "self": { "href": "http://localhost:8080/ingredients/COTO" }
  	 }
  	 },
  	 {
  	 "name": "Diced Tomatoes", "type": "VEGGIES",
  	 "_links": {
  	 "self": { "href": "http://localhost:8080/ingredients/TMTO" }
  	 }
  	 },
  	 {
  	 "name": "Lettuce", "type": "VEGGIES",
  	 "_links": {
  	 "self": { "href": "http://localhost:8080/ingredients/LETC" }
  	 }
  	 },
  	 {
  	 "name": "Salsa", "type": "SAUCE",
  	 "_links": {
  	 "self": { "href": "http://localhost:8080/ingredients/SLSA" }
  	 }
  	 }
  	 ],
  	 "_links": {
  	 "self": { "href": "http://localhost:8080/design/4" }
  	 }
  	 },
  	 ...
  	 ]
  	 },
  	 "_links": {
  	 "recents": {
  	 "href": "http://localhost:8080/design/recent"
  	 }
  	 }
  	}
  ```

  这种特殊风格的HATEOAS被称为超文本应用语言, Hypertext APPlication Language，这是一种JSON相应中嵌入超链接的简单通用格式。

  这个新taco列表中的每个元素都包含了⼀个名为“_links”的属性，为客户端提供导航API的超链接

  如果客户端应⽤需要对列表中的taco执⾏HTTP请求，那么在开发的时候不需要关⼼taco资源的URL是什么样⼦。相反，它只需要请求“self”链接就可以了

  为了能够使用HATEOAS需要添加依赖：

    - Spring HATEOAS starter

      这个依赖不仅会将Spring HATEOAS添加到项目的类路径中，还会提供自动配置功能来启动Spring HATEOAS，我们所要做的就是重新实现控制器，让他们返回资源类型而不是领域类型。

      ```xml
      <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-hateoas</artifactId>
      </dependency>
      ```

- 添加超链接（hateoas更新了，之前的方法用不了了，这里就是当前的方法）

  Spring HATEOAS提供了两个主要的类型来表示超链接：Resource和Resources。Resource代表一个资源，而Resource代表资源的集合。两种类型都能携带到其他资源的链接。

  为返回的taco列表添加超链接：

  获取指定的Taco

    - 未使用超链接

      ```java
      @GetMapping("/{id}")
      public Taco tacoById(@PathVariable("id") Long id) {
          Optional<Taco> optTaco = tacoRepo.findById(id);
          return optTaco.orElse(null);
      }
      ```

      ```java
      {
          "id": 22,
          "createdAt": "2022-04-24T10:35:51.672+0000",
          "name": "taco3",
          "ingredients": [
              {
                  "id": "CARN",
                  "name": "Carnitas",
                  "type": "PROTEIN"
              },
              {
                  "id": "CHED",
                  "name": "Cheddar",
                  "type": "CHEESE"
              }
          ]
      }
      ```

    - 添加超链接：

      ```java
      @GetMapping("/hateoas/{id}")
      public HttpEntity<TacoResource> tacoByIdHateoas(@PathVariable("id") Long id) {
          Optional<Taco> optTaco = tacoRepo.findById(id);
          if (optTaco.isPresent()){
              TacoResource tacoResource = new TacoResource(optTaco.get());
              tacoResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DesignTacoController.class).tacoByIdHateoas(id)).withSelfRel());
              return ResponseEntity.ok(tacoResource);
          }
          return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
      }
      ```

      WebMvcLinkBuilder可以使用类名加方法名来得到请求路径避免使用硬编码的方式。

      其中TacoResooource实现了`RepresentationModel`  来确保可以使用对应的方法（我们新创建一个对象来保证只让客户端获得该获得的数据信息，比如数据库的主键信息就不该告诉客户端，与之代替，客户端可以通过self链接来获得详细信息）：

      ```java
      @Data
      public class TacoResource extends RepresentationModel<TacoResource> {
          private Date createdAt;
          private String name;
          private List<Ingredient> ingredients;
      
          public TacoResource(Taco taco) {
              this.createdAt = taco.getCreatedAt();
              this.name = taco.getName();
              this.ingredients = taco.getIngredients();
          }
      }
      ```

      返回值

      ```java
      {
          "createdAt": "2022-04-24T10:35:51.672+0000",
          "name": "taco3",
          "ingredients": [
              {
                  "id": "CARN",
                  "name": "Carnitas",
                  "type": "PROTEIN"
              },
              {
                  "id": "CHED",
                  "name": "Cheddar",
                  "type": "CHEESE"
              }
          ],
          "_links": {
              "self": {
                  "href": "http://localhost:8080/rest/design/hateoas/22"
              }
          }
      }
      ```

- 创建资源装配器

  现在我们需要为列表中的Taco添加链接，当然，可以遍历所有的TacoModel像之前的方式一样依次添加链接 。

  当然，这里还有另一种方式：创建一个Assembler对象然后实现`RepresentationModelAssembler<T，E>`  接口，实现其中的toModel方法

  ，然后它就有一个默认的`toCollectionModel` 方法，其实也是遍历调用toModel方法，就可以实现这个服务了：

    - TacoResourceAssembler

      ```java
      @Component
      public class TacoResourceAssembler implements RepresentationModelAssembler<Taco, TacoResource> {
          @Override
          public TacoResource toModel(Taco taco) {
              TacoResource tacoResource = new TacoResource(taco);
              tacoResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DesignTacoController.class).tacoByIdHateoas(taco.getId())).withSelfRel());
              return tacoResource;
          }
      }
      ```

    - 然后在controller中自动注入然后使用即可

      ```java
      @Autowired
      private TacoResourceAssembler tacoResourceAssembler;
      
      @GetMapping("/recent/hateoas")
      public ResponseEntity<CollectionModel<TacoResource>> recentTacoResources(){
          Pageable page = PageRequest.of(
                  0, 12, Sort.by("createdAt").descending()
          );
          Optional<List<Taco>> all = tacoRepo.findAll(page);
          if (all.isPresent()){
              CollectionModel<TacoResource> tacoResources = tacoResourceAssembler.toCollectionModel(all.get());
              tacoResources.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DesignTacoController.class).recentTacoResources()).withRel("recentTaco"));
              return ResponseEntity.ok(tacoResources);
          }
          return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
      }
      ```

    - 结果：

      ```java
      {
          "_embedded": {
              "tacoResourceList": [
                  {
                      "createdAt": "2022-04-24T10:35:51.672+0000",
                      "name": "taco3",
                      "ingredients": [
                          {
                              "id": "CARN",
                              "name": "Carnitas",
                              "type": "PROTEIN"
                          },
                          {
                              "id": "CHED",
                              "name": "Cheddar",
                              "type": "CHEESE"
                          }
                      ],
                      "_links": {
                          "self": {
                              "href": "http://localhost:8080/rest/design/hateoas/22"
                          }
                      }
                  },
                  {
                      "createdAt": "2022-04-24T07:16:48.964+0000",
                      "name": "mytaco",
                      "ingredients": [
                          {
                              "id": "GRBF",
                              "name": "Ground Beef",
                              "type": "PROTEIN"
                          },
                          {
                              "id": "LETC",
                              "name": "Lettuce",
                              "type": "VEGGIES"
                          },
                          {
                              "id": "SRCR",
                              "name": "Sour Cream",
                              "type": "SAUCE"
                          }
                      ],
                      "_links": {
                          "self": {
                              "href": "http://localhost:8080/rest/design/hateoas/21"
                          }
                      }
                  },
      ```

  为Ingredient也添加超链接

    - IngredientResource

      ```java
      @Data
      public class IngredientResource extends RepresentationModel<IngredientResource> {
          private String name;
          private String type;
      
          public IngredientResource(Ingredient ingredient){
              this.name = ingredient.getName();
              this.type = ingredient.getType().toString();
          }
      }
      ```

    - 通过id查询单个的Ingredient

      ```java
      @GetMapping("/ingredients/{id}")
      public ResponseEntity<IngredientResource> getIngredientById(@PathVariable String id) {
          Optional<Ingredient> opt = ingredientRepo.findById(id);
          if (opt.isPresent()) {
              IngredientResource ingredientResource = new IngredientResource(opt.get());
              ingredientResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DesignTacoController.class).getIngredientById(id)).withSelfRel());
              return ResponseEntity.ok(ingredientResource);
          }
          return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
      }
      ```

    - IngredientResourceAssembler

      ```java
      @Component
      public class IngredientResourceAssembler implements RepresentationModelAssembler<Ingredient, IngredientResource> {
          @Override
          public IngredientResource toModel(Ingredient entity) {
              IngredientResource ingredientResource = new IngredientResource(entity);
              return ingredientResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DesignTacoController.class).getIngredientById(entity.getId())).withSelfRel());
          }
      }
      ```

    - 修改TacoResource使其携带IngredientResource

      ```java
      @Data
      public class TacoResource extends RepresentationModel<TacoResource> {
      
          private static final IngredientResourceAssembler
                  ingredientAssembler = new IngredientResourceAssembler();
          private Date createdAt;
          private String name;
          private CollectionModel<IngredientResource> ingredients;
      
          public TacoResource(Taco taco) {
              this.createdAt = taco.getCreatedAt();
              this.name = taco.getName();
              this.ingredients = ingredientAssembler.toCollectionModel(taco.getIngredients());
          }
      }
      ```

    - 对应结果

      ```java
      {
          "_embedded": {
              "tacoResourceList": [
                  {
                      "createdAt": "2022-04-24T10:35:51.672+0000",
                      "name": "taco3",
                      "ingredients": {
                          "_embedded": {
                              "ingredientResourceList": [
                                  {
                                      "name": "Carnitas",
                                      "type": "PROTEIN",
                                      "_links": {
                                          "self": {
                                              "href": "http://localhost:8080/rest/design/ingredients/CARN"
                                          }
                                      }
                                  },
                                  {
                                      "name": "Cheddar",
                                      "type": "CHEESE",
                                      "_links": {
                                          "self": {
                                              "href": "http://localhost:8080/rest/design/ingredients/CHED"
                                          }
                                      }
                                  }
                              ]
                          }
                      },
                      "_links": {
                          "self": {
                              "href": "http://localhost:8080/rest/design/hateoas/22"
                          }
                      }
                  },
                  {
                      "createdAt": "2022-04-24T07:16:48.964+0000",
                      "name": "mytaco",
                      "ingredients": {
                          "_embedded": {
                              "ingredientResourceList": [
                                  {
                                      "name": "Ground Beef",
                                      "type": "PROTEIN",
                                      "_links": {
                                          "self": {
                                              "href": "http://localhost:8080/rest/design/ingredients/GRBF"
                                          }
                                      }
                                  },
                                  {
                                      "name": "Lettuce",
                                      "type": "VEGGIES",
                                      "_links": {
                                          "self": {
                                              "href": "http://localhost:8080/rest/design/ingredients/LETC"
                                          }
                                      }
                                  },
                                  {
                                      "name": "Sour Cream",
                                      "type": "SAUCE",
                                      "_links": {
                                          "self": {
                                              "href": "http://localhost:8080/rest/design/ingredients/SRCR"
                                          }
                                      }
                                  }
                              ]
                          }
                      },
                      "_links": {
                          "self": {
                              "href": "http://localhost:8080/rest/design/hateoas/21"
                          }
                      }
                  },
      ```

- 命名嵌套式的关联关系

  _embedded之下一个属性tacoResourceList，这个是因为Resources对象是通过List<TacoResource>创建出来的，假设我们将TacoResource类重构成了其他的名称，那么结果JSON中的字段名 将会随之发⽣变化。这样，所有依赖该名称的客户端代码都会产⽣问题。

  可以使用 [Ralation](https://www.notion.so/Ralation-8d8391154ae745d9a67ee89ecbaf2a68)  `(value = "taco", collectionRelation = "tacos")`  注解来消除定义资源名之间的耦合

  ```java
  @Relation(value = "taco", collectionRelation = "tacos")
  public class TacoResource extends RepresentationModel<TacoResource> {
  	// ...
  }
  ```

  ```java
  {
      "_embedded": {
          "tacos": [
              {
                  "createdAt": "2022-04-24T10:35:51.672+0000",
                  "name": "taco3",
                  "ingredients": {
                      "_embedded": {
                          "ingredients": [
                              {
                                  "name": "Carnitas",
                                  "type": "PROTEIN",
                                  "_links": {
                                      "self": {
                                          "href": "http://localhost:8080/rest/design/ingredients/CARN"
                                      }
                                  }
                              },
                              {
                                  "name": "Cheddar",
                                  "type": "CHEESE",
                                  "_links": {
                                      "self": {
                                          "href": "http://localhost:8080/rest/design/ingredients/CHED"
                                      }
                                  }
                              }
                          ]
                      }
                  },
                  "_links": {
                      "self": {
                          "href": "http://localhost:8080/rest/design/hateoas/22"
                      }
                  }
              },
  ```

## 3️⃣启动数据后端服务

Spring Data可以基于接口自动创建repository实现，同时Spring Data还可以帮助定义应用的API

Spring Data Rest会为Spring Data的repository自动生成REST API，只需要将Spring Data REST添加到构建文件中，就可以得到一套API，他的操作与我们定义的repository接口是一致的：

所需依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>
```

只需要添加这一项依赖，就可以自动生成jpa所对应的repository的所有方法对应的controller接口。

这种接口默认都是在根路径下的，所以我们设置一下基础路径

```yaml
spring:
	data:
		rest: 
			base-path: /api
```

- 调整资源路径和关系名称

  当使用Spring Data repository的时候，默认会尝试使用相关实体类的 复数形式，对于Ingredient来说，端点是/ingredients, 但对于taco时，会被计算成/tacoes, 这显然是不正确的，我们需要添加一个注解来映射关系名和路径 :

  [RestResource](https://www.notion.so/RestResource-30b3d87616a8496ea4d824dcc912ee7d) (rel = “tacos”, path = “tacos”)

  ```json
  "tacos" : {
   "href" : "http://localhost:8080/api/tacos{?page,size,sort}",
   "templated" : true
  },
  ```

- 分页和排序

  主资源上所有的链接都提供了可选的page、size和sort参数，默认情况下，请求集合都会返回第一页的20个条目，但是可以设置在请求中指定page、size参数调整具体的页数（从0开始计算）和每页的数量

  ```json
  "tacoes" : {
   "href" : "http://localhost:8080/api/tacoes{?page,size,sort}",
   "templated" : true
   },
  ```

  ```bash
  $ curl "localhost:8080/api/tacos?size=5&page=1"
  ```

  同时，HATEOAS还 自动提供了第一页，上一页，下一页和最后一页等链接

  ```json
  "_links" : {
   "first" : {
   "href" : "http://localhost:8080/api/tacos?page=0&size=5"
   },
   "self" : {
   "href" : "http://localhost:8080/api/tacos"
   },
   "next" : {
   "href" : "http://localhost:8080/api/tacos?page=1&size=5"
   },
   "last" : {
   "href" : "http://localhost:8080/api/tacos?page=2&size=5"
   },
   "profile" : {
   "href" : "http://localhost:8080/api/profile/tacos"
   },
   "recents" : {
   "href" : "http://localhost:8080/api/tacos/recent"
   }
  }
  ```

  sort参数允许根据实体的某个属性对结果进行排序，例如获最近创建的

  12条taco

  ```bash
  $ curl "localhost:8080/api/tacos?sort=createdAt,desc&page=0&size=12
  ```

- 添加自定义的端点

  Spring Data REST可以很好的执行CRUD操作的Spring Data repository创建端点，但有时候需要我们舍子自定义的端点

  可以简单的使用之前的控制器，它们可以和rest一起工作，但这样有一些问题：

    - 自定义的控制器没有映射到Spring Data REST的基础路径下，需要手动调节
    - 自定义的端点不会自动包含超链接，及客户端无法通过关系名发现自定义的端点

  基础路径问题可以使用 [RepositoryRestController](https://www.notion.so/RepositoryRestController-859206d7c6344becafdd29f67540c973) 注解来解决，使用了该注解的控制器会具有和spring.data.rest.base-path属性值一样的前缀。注意：使用RepositoryRestController时并不会和restController一样指定返回josn响应，需要返回 [ResponseBody](https://www.notion.so/ResponseBody-208a5b499b60475ebbe8a460b462d768) 注解，或将响应数据放到 ResponseEntity中。

- 为Spring Data端点添加自定义的超链接

  超链接可以通过设置 resource processor（资源处理器） bean来实现，

  ```java
  @Bean
  public ResourceProcessor<PagedResources<Resource<Taco>>>
   tacoProcessor(EntityLinks links) {
   return new ResourceProcessor<PagedResources<Resource<Taco>>>() {
   @Override
   public PagedResources<Resource<Taco>> process(
   PagedResources<Resource<Taco>> resource) {
   resource.add(
   links.linkFor(Taco.class)
   .slash("recent")
   .withRel("recents"));
   return resource;
   }
   };
  }
  ```

  定义了一个匿名内部类，并将其申明为Spring 应用上下文所创建的 bean，Spring Hateoas会自动发现这个bean,并将其应用到对应的资源上，如果控制器返回PagedResources<Resource<Taco>>，就会包含⼀个最近创建的taco链接

## 🈴小结

- REST端点可以通过Spring MVC来创建，这里的控制器与面向浏览器的控制器遵循相同的编程模型。
- 为了绕过视图和模型的逻辑并直接价格数据写入响应体中，控制器处理方法既可以添加 [ResponseBody](https://www.notion.so/ResponseBody-208a5b499b60475ebbe8a460b462d768) 注解也可以返回ResponseEntity对象。
- [RestController](https://www.notion.so/RestController-7bdfd3181ff74b3891239f10330c867c) 注解简化了REST控制器，使用它的话，处理器发方法中就不需要再添加@ResponseBody注解了。
- Spring HATEOAS为SpringMVC 控制器返回的资源启动了超链接功能
- 借助Spring Data REST， Spring Data repository可以自动导出为REST API