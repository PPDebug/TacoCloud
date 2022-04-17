# 第三章 使用数据

> * 使用Spring 的jdbcTemplate
> * 使用SimpleJdbcInsert插入数据
> * 使用SpringData声明JPA repository

## 1️⃣使用JDBC读取和写入数据

尽管近年来涌现了许多可选的数据库类型，但是关系型数据库依然是通用数据库存储的首选。

处理关系型数据库的时候，Java开发人员有多种可选方案，其中最常见的是JDBC和JPA。Spring两种的都支持并做了相应的简化。Spring对JDBC的封装是JDBC template：

在不使用JDBCTemplate的情况下执行一个简单的查询：

- findOne

  ```java
  @Override 
  public Ingredient findOne(String id){
  	 Connection connection = null;
  	 PreparedStatement statement = null;
  	 ResultSet resultSet = null;
  	 try {
  		 connection = dataSource.getConnection();
  		 statement = connection.prepareStatement(
  			 "select id, name, type from Ingredient where id=?");
  		 statement.setString(1, id);
  		 resultSet = statement.executeQuery();
  		 Ingredient ingredient = null;
  		 if(resultSet.next()) {
  			 ingredient = new Ingredient(
  			 resultSet.getString("id"),
  			 resultSet.getString("name"),
  			 Ingredient.Type.valueOf(resultSet.getString("type")));
  		 }
  		 return ingredient;
  	 } catch (SQLException e) {
  	 // ??? What should be done here ???
  	 } finally {
  		 if (resultSet != null) {
  		 try {
  			 resultSet.close();
  		 } catch (SQLException e) {}
  	 }
  	 if (statement != null) {
  	 try {
  	 statement.close();
  	 } catch (SQLException e) {}
  	 }
  	 if (connection != null) {
  	 try {
  	 connection.close();
  	 } catch (SQLException e) {}
  	 }
  	 }
   return null;
  }
  ```

使用常规的JDBC代码，必须自己编写代码来实现创建连接，创建语句，以及关闭连接，语句和结果集的清理功能完全包围起来。（尽管这些代码是十分重要的，但是它在大多数情况下都是重复的，所以没有必要反复编写同样的代码）

使用jdbcTemplate来达到相同的作用

- findOne

  ```java
  private JdbcTemplate jdbc;
  
  @Override
  public Ingredient findOne(String id){
  	return jdbc.queryForObject(
  	"select id , name, type from Ingredient where id="?", this::mapRowToIngredient, id);
  }
  
  private Igredient mapRowToIngredient(ReslutSet rs, int rowNum) throws SQLException{
  	return new Ingredient(rs.getString("id"), rs.getString("name"), Ingredient.Type.valueOf(valueOf(rs.getString("type")));
  }
  ```

使用jdbcTemplate之后，我们无需手动的去创建连接和关闭连接和异常检查，只需要关注业务功能就可以了。

- 1️⃣调整领域模型对象以适应持久化

  在将对象持久化到数据库的时候，一般需要一个id，所以在Taco和Order类中需要加上一个id字段，同时Taco和Order最好加上一个创建的时间和日期

  ```java
  private Long id;
  private Date createdAt;
  ```

  （使用lombok的好处就是修改了类的字段之后不需要再手动的更新getter或setter方法）

- 2️⃣使用jdbcTemplate

  在使用jdbcTemplate之前，需要首先将它添加到项目的类路径中。

  ```xml
  <dependency>
  	<groupId>org.springframework.boot</groupId>
  	<artifactId>spring-boot-starter-jdbc</artifactId>
  </dependency>
  ```

  使用一个嵌入式式的数据库，H2数据库(在开发阶段使用完全足够)

  ```xml
  <dependency>
  	<groupId>com.h2database</groupId>
  	<artifactId>h2</artifactId>
  	<scope>runtime</scope>
  </dependency>
  ```

  定义JDBC repository

  Ingredient repository需要完成以下功能：

    - 查询所有配料信息，并放到一个Ingredient对象的集合中。
    - 根据id，查询单个Ingredient
    - 保存Ingredient对象

  声明IngredientRepository接口及其方法：

    - IngredientRepository.java

      ```java
      package online.pengpeng.tacocloud.repository;
      
      import online.pengpeng.tacocloud.entity.Ingredient;
      import org.springframework.beans.factory.annotation.Autowired;
      import org.springframework.jdbc.core.JdbcTemplate;
      import org.springframework.stereotype.Repository;
      
      @Repository
      public class JdbcIngredientRepository implements IngredientRepository{
      
          private JdbcTemplate jdbc;
      
          @Autowired
          public JdbcIngredientRepository(JdbcTemplate jdbc) {
              this.jdbc = jdbc;
          }
          // ...
      }
      ```

      JdbcIngredientRepository添加了 [Repository](https://www.notion.so/Repository-7d8b28ac9acd4af48cf4380b8fef1d2c) 注解，这是Spring定义的一系列构造型(Stereo)注解

      当Spring创JdbcIngredientRepository bean的 时候，他会通过 [Autowired](https://www.notion.so/Autowired-b1907e552c0647f09e6922e42040022b) 标注的构造器将jdbcTemplate注入进来，然后可以被其它方法使用。

        - findOne() findAll()

          ```java
          @Override
          public Iterable<Ingredient> findAll() {
              return jdbc.query("select id, name, type from Ingredient", this::mapRowToIngredient);
          }
          
          @Override
          public Ingredient findOne(String id) {
              return jdbc.queryForObject("select id, name, type from Ingredient where id=?", this::mapRowToIngredient, id);
          }
          
          private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
              return new Ingredient(rs.getString("id"), rs.getString("name"), Ingredient.Type.valueOf(rs.getString("type")));
          }
          ```

      findAll()⽅法预期返回⼀个对象的集合，它使⽤了JdbcTemplate的query()⽅法。query()会接受要执⾏的SQL以及Spring RowMapper的⼀个实现（⽤来将结果集中的每⾏数据映射为⼀个对象）

      findOne()⽅法预期只会返回⼀个Ingredient对象，所以它使⽤了
      JdbcTemplate的queryForObject()⽅法，⽽不是query()⽅法。queryForObject()⽅法返回⼀个对象，⽽不是对象的List。

      当然，如果RowMapper只会被使用一次,那么推荐使用lambda表达式来化简,如下

        - findOne lambda

          ```java
          @Override
          public Ingredient findOne(String id) {
               return jdbc.queryForObject(
                       "select id, name, type from Ingredient where id=?",
                           new RowMapper<Ingredient>() {
                           public Ingredient mapRow(ResultSet rs, int rowNum)
                           throws SQLException {
                               return new Ingredient(
                                   rs.getString("id"),
                                   rs.getString("name"),
                                   Ingredient.Type.valueOf(rs.getString("type")));
                           };
                   }, id);
          }
          ```

      插入一条数据

        - save()

          ```java
          @Override
          public Ingredient save(Ingredient ingredient) {
              jdbc.update(
                      "insert into Ingredient (id, name, type) values (?, ?, ?)",
                      ingredient.getId(),
                      ingredient.getName(),
                      ingredient.getType().toString());
              return ingredient;
          }
          ```

          update方法不需要设置结果映射ResultSet，只需要⼀个包含待 执⾏SQL的String以及每个查询参数对应的值即可

      JdbcIngredientRepository编写完成之后，我们就可以将其注⼊DesignTacoController中了，然后使⽤它来提供Ingredient对象的 列表，不⽤再使⽤硬编码的值

        - DesignTacoController

          ```java
          @Slf4j
          @Controller
          @RequestMapping("/design")
          public class DesignTacoController {
          
              private final IngredientRepository ingredientRepository;
          
              @Autowired
              public DesignTacoController(IngredientRepository ingredientRepository){
                  this.ingredientRepository = ingredientRepository;
              }
          
              @GetMapping
              public  String showDesignFrom(Model model) {
                  List<Ingredient> ingredients = new ArrayList<>();
                  ingredientRepository.findAll().forEach(i -> ingredients.add(i));
                  Type[] types = Ingredient.Type.values();
                  for (Type type:types) {
                      model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
                  }
                  model.addAttribute("design", new Taco());
                  return "design";
          //...
          }
          ```

      findAll()⽅法会从数据库中 获取所有的配料，并将它们过滤成不同的类型，然后放到模型中。

- 3️⃣定义模式和预加载数据

  除了Ingredient表之外，还需要其他的⼀些表来保存订单和设计信息

  ![ER Diagram](images/chapter3_ER.png)

  Ingredient：保存配料信息。 Taco：保存taco设计相关的信息。 Taco_Ingredients：Taco中的每⾏数据都对应⼀⾏或多⾏，将
  taco和与之相关的配料映射在⼀起。 Taco_Order：保存必要的订单细节。 Taco_Order_Tacos：Taco_Order中的每⾏数据都对应⼀⾏或多⾏将订单和与之相关的taco映射在⼀起。

    - src/main/resources/schema.sql

      ```sql
      create table if not exists Ingredient(
          id varchar(4) not null,
          name varchar(25) not null,
          type varchar(10) not null
      );
      
      create table if not exists Taco (
          id identity,
          name varchar(50) not null,
          createdAt timestamp not null
      );
      
      create table if not exists Taco_Ingredients (
          taco bigint not null,
          ingredient varchar(4) not null
      );
      
      alter table Taco_Ingredients
          add foreign key (taco) references Taco(id);
      alter table Taco_Ingredients
          add foreign key (ingredient) references Ingredient(id);
      
      create table if not exists Taco_Order (
          id identity,
          Name varchar(50) not null,
          Street varchar(50) not null,
          City varchar(50) not null,
          State varchar(2) not null,
          Zip varchar(10) not null,
          ccNumber varchar(16) not null,
          ccExpiration varchar(5) not null,
          ccCVV varchar(3) not null,
          placedAt timestamp not null
      );
      
      create table if not exists Taco_Order_Tacos (
          tacoOrder bigint not null,
          taco bigint not null
      );
      
      alter table Taco_Order_Tacos
          add foreign key (tacoOrder) references Taco_Order(id);
      alter table Taco_Order_Tacos
          add foreign key (taco) references Taco(id);
      ```

  如果在应⽤的根类路径下存在名为schema.sql的⽂件，那么在应 ⽤启动的时候将会基于数据库执⾏这个⽂件中的SQL

    - src/main/resources/data.sql

      ```sql
      delete from Taco_Order_Tacos;
      delete from Taco_Ingredients;
      delete from Taco;
      delete from Taco_Order;
      delete from Ingredient;
      insert into Ingredient (id, name, type)
      values ('FLTO', 'Flour Tortilla', 'WRAP');
      insert into Ingredient (id, name, type)
      values ('COTO', 'Corn Tortilla', 'WRAP');
      insert into Ingredient (id, name, type)
      values ('GRBF', 'Ground Beef', 'PROTEIN');
      insert into Ingredient (id, name, type)
      values ('CARN', 'Carnitas', 'PROTEIN');
      insert into Ingredient (id, name, type)
      values ('TMTO', 'Diced Tomatoes', 'VEGGIES');
      insert into Ingredient (id, name, type)
      values ('LETC', 'Lettuce', 'VEGGIES');
      insert into Ingredient (id, name, type)
      values ('CHED', 'Cheddar', 'CHEESE');
      insert into Ingredient (id, name, type)
      values ('JACK', 'Monterrey Jack', 'CHEESE');
      insert into Ingredient (id, name, type)
      values ('SLSA', 'Salsa', 'SAUCE');
      insert into Ingredient (id, name, type)
      values ('SRCR', 'Sour Cream', 'SAUCE');
      ```

- 4️⃣插入数据

  借助JdbcTemplate，有以下两种保存数据的⽅法。 直接使⽤update()⽅法。 使⽤SimpleJdbcInsert包装器类：

  ```java
  public interface TacoRepository {
      Taco save(Taco design);
  }
  ```

  ```java
  public interface OrderRepository {
      Order save(Order order);
  }
  ```

  保存taco的时候需要同时将与该taco关联的配料保存到Taco_Ingredients表中。与之类似，保存订单 的时候，需要同时将与该订单关联的taco保存到Taco_Order_Tacos表中。

  为了实现TacoRepository，我们需要⽤save()⽅法⾸先保存必要 的taco设计细节（⽐如，名称和创建时间），然后对Taco对象中的每 种配料都插⼊⼀⾏数据到Taco_Ingredients中。

    - jdbcTacoRepository.java

      ```java
      package online.pengpeng.tacocloud.repository;
      
      import online.pengpeng.tacocloud.entity.Taco;
      import org.springframework.beans.factory.annotation.Autowired;
      import org.springframework.jdbc.core.JdbcTemplate;
      import org.springframework.jdbc.core.PreparedStatementCreator;
      import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
      import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
      import org.springframework.jdbc.support.GeneratedKeyHolder;
      import org.springframework.jdbc.support.KeyHolder;
      import org.springframework.stereotype.Repository;
      
      import java.sql.*;
      import java.util.Arrays;
      import java.util.Date;
      import java.util.HashMap;
      import java.util.Map;
      
      @Repository
      public class JdbcTacoRepository implements TacoRepository{
      
          private JdbcTemplate jdbc;
      
          private SimpleJdbcInsert tacoInserter;
      
          @Autowired
          public JdbcTacoRepository (JdbcTemplate jdbc) {
              this.jdbc = jdbc;
              this.tacoInserter = new SimpleJdbcInsert(jdbc)
                      .withTableName("Taco")
                      .usingGeneratedKeyColumns("id");
          }
      
          @Override
          public Taco save(Taco taco) {
      //        long tacoId = saveTacoInfo(taco); // 这种方法一直不成功，我也不知道原因
              long tacoId = saveTacoInfoSimple(taco);
              taco.setId(tacoId);
              for (String ingredient : taco.getIngredients()) {
                  saveIngredientToTaco(ingredient, tacoId);
              }
              return taco;
          }
      
          private long saveTacoInfo(Taco taco) {
              taco.setCreatedAt(new Date());
              PreparedStatementCreator psc = new PreparedStatementCreatorFactory(
                      "insert into Taco (name, createdAt) values (?, ?)",
                      Types.VARCHAR,
                      Types.TIMESTAMP)
                      .newPreparedStatementCreator(
                          Arrays.asList(
                              taco.getName(),
                              new Timestamp(taco.getCreatedAt().getTime())
                      )
                      );
              KeyHolder keyHolder = new GeneratedKeyHolder();
              jdbc.update(psc, keyHolder);
              return keyHolder.getKey().longValue();
          }
      
          private long saveTacoInfoSimple(Taco taco){
              taco.setCreatedAt(new Date());
              Map<String, Object> values = new HashMap<>();
              values.put("name", taco.getName());
              values.put("createdAt", new Timestamp(taco.getCreatedAt().getTime()));
              int id = tacoInserter.execute(values);
              return id;
          }
      
          private void saveIngredientToTaco(String ingredient, long tacoId){
              jdbc.update(
                      "insert into Taco_Ingredients (taco, ingredient) values (?, ?)",
                      tacoId,
                      ingredient
              );
          }
      }
      ```

  save()⽅法⾸先调⽤了私有的saveTacoInfo()⽅ 法，然后使⽤该⽅法所返回的taco ID来调⽤saveIngredientToTaco()，最后的这个⽅法会保存每种配料。这⾥的 问题在于saveTacoInfo()⽅法的细节

  当向Taco中插⼊⼀⾏数据的时候，我们需要知道数据库⽣成的ID，这样我们才可以在每个配料信息中引⽤它。保存配料数据时所使⽤ 的update()⽅法⽆法帮助我们得到所⽣成的ID，所以在这⾥我们需要 ⼀个不同的update()⽅法。 这⾥的update()⽅法需要接受⼀个PreparedStatementCreator和⼀个KeyHolder。KeyHolder将会为我们提供⽣成的taco ID。但是， 为了使⽤该⽅法，我们必须还要创建PreparedStatementCreator。

  将TacoRepository注入到DesignTacoController中，并在保存taco的时候调用它。

    - DesignTacoController注入TacoRepositoy

      ```java
      private final TacoRepository designRepository;
      
      @Autowired
      public DesignTacoController(IngredientRepository ingredientRepository, TacoRepository designRepository){
          this.ingredientRepository = ingredientRepository;
          this.designRepository = designRepository;
      }
      ```

    - 新的processDesign()方法

      ```java
      @Slf4j
      @Controller
      @RequestMapping("/design")
      @SessionAttributes("order")
      public class DesignTacoController {
      
          @ModelAttribute(name = "order")
          public Order order(){
              return new Order();
          }
      
          @ModelAttribute(name = "taco")
          public Taco taco(){
              return new Taco();
          }
          @PostMapping
          public String processDesign(
                  @Valid Taco design, Errors errors,
                  @ModelAttribute Order order) {
              if (errors.hasErrors()){
                  log.error(errors.toString());
                  return "design";
              }
              Taco saved = designRepository.save(design);
              order.addDesign(saved);
              return "redirect:/orders/orderForm";
          }
      // ...
      }
      ```

    - 在order中也增加了一个字段和方法

      ```java
      private List<Taco> tacos;
      public void addDesign(Taco taco){
          if (tacos == null){
              tacos = new ArrayList<>();
          }
          tacos.add(taco);
      }
      ```

  DesignTacoController类添加了 [SessionAttributes](https://www.notion.so/SessionAttributes-619f8176d89847ad931d91bb0062a7f7) ("order")注 解, 以及 [ModelAttribute](https://www.notion.so/ModelAttribute-33798f2693c44ee3820f9ab24fec1cb7) 注解

  order()⽅法上的@ModelAttribute注解能够确保会在模型中创建⼀个Order对象。与模型中的Taco对象不 同，我们需要订单信息在多个请求中都能出现，这样的话我们就能创建 多个taco并将它们添加到该订单中。类级别的@SessionAttributes能 够指定模型对象（如订单属性）要保存在session中，这样才能跨请求使⽤。

  对taco设计的处理位于processDesign()⽅法中。该⽅法接受Order对象作为参数，同时还包括Taco和Errors对象。Order参数带有
  @ModelAttribute注解，表明它的值应该是来⾃模型的，SpringMVC不会尝试将请求参数绑定到它上⾯。

  之前在在保存Taco获取id时，通过 KeyHolder和PreparedStatementCreator获取的步骤十分繁琐，我们可以使用⼊SimpleJdbcInsert更容易地将数据插⼊到表中：

  创建⼀个JdbcOrderRepository，在构造器中创建SimpleJdbcInsert的两个实例，分别⽤来把值插⼊到Taco_Order和Taco_Order_Tacos表中

    - JdbcOrderRepository

      ```java
      @Repository
      public class JdbcOrderRepository implements OrderRepository{
      
          private SimpleJdbcInsert orderInserter;
          private SimpleJdbcInsert orderTacoInserter;
          private ObjectMapper objectMapper;
      
          @Autowired
          public JdbcOrderRepository (JdbcTemplate jdbc) {
              this.orderInserter = new SimpleJdbcInsert(jdbc)
                      .withTableName("Taco_Order")
                      .usingGeneratedKeyColumns("id");
              this.orderTacoInserter = new SimpleJdbcInsert(jdbc)
                      .withTableName("Taco_Order_Tacos");
              this.objectMapper = new ObjectMapper();
          }
      
         @Override
          public Order save(Order order) {
              order.setPlaceAt(new Date());
              long orderId = saveOrderDetails(order);
              order.setId(orderId);
              List<Taco> tacos = order.getTacos();
              for (Taco taco : tacos) {
                  saveTacoToOrder(taco, orderId);
              }
              return order;
          }
      
          private long saveOrderDetails(Order order) {
              @SuppressWarnings("unchecked")
              Map<String, Object> values = objectMapper.convertValue(order, Map.class);
              values.put("placedAt", order.getPlaceAt());
              long orderId = orderInserter.executeAndReturnKey(values).longValue();
              return orderId;
          }
      
          private void saveTacoToOrder(Taco taco, long orderId) {
              Map<String, Object> values = new HashMap<>();
              values.put("tacoOrder", orderId);
              values.put("taco", taco.getId());
              orderTacoInserter.execute(values);
          }
      
      }
      ```

  使用JdbcTemplate构建了两个SimpleJdbcInsert实例，

  第⼀个实例赋值给了orderInserter实例变量，配置为与Taco_Order
  表协作，并且假定id属性将会由数据库提供或⽣成。第⼆个实例赋值给
  了orderTacoInserter实例变量，配置为与Taco_Order_Tacos表协 作，但是没有声明该表中ID是如何⽣成的。

  创建了Jackson中ObjectMapper类的⼀个实例，并将 其赋值给⼀个实例变量。尽管Jackson的初衷是进⾏JSON处理，但也可以使⽤它来帮助保存订单和关联的taco。

  save()⽅法实际上没有保存任何内容，只是定义了保存Order及其 关联的Taco对象的流程，并将实际的持久化任务委托给了
  saveOrderDetails()和saveTacoToOrder()

  使⽤Jackson的ObjectMapper及其convertValue()⽅法，以便于将Order转换为
  Map

  将OrderRepository注⼊到OrderController中并 开始使⽤

    - OrderController

      ```java
      package online.pengpeng.tacocloud.controller;
      
      import lombok.extern.slf4j.Slf4j;
      import online.pengpeng.tacocloud.entity.Order;
      import online.pengpeng.tacocloud.repository.OrderRepository;
      import org.springframework.beans.factory.annotation.Autowired;
      import org.springframework.stereotype.Controller;
      import org.springframework.ui.Model;
      import org.springframework.validation.Errors;
      import org.springframework.web.bind.annotation.*;
      import org.springframework.web.bind.support.SessionStatus;
      
      import javax.validation.Valid;
      
      @Slf4j
      @Controller
      @RequestMapping("/orders")
      @SessionAttributes("order")
      public class OrderController {
      
          private OrderRepository orderRepo;
      
          @Autowired
          public OrderController(OrderRepository orderRepo) {
              this.orderRepo = orderRepo;
          }
      
          @GetMapping("/orderForm")
          public String orderForm(@ModelAttribute Order order){
              return "current";
          }
      
          @PostMapping
          public String processOrder(
      //            @Valid // 推荐写代码时把验证关上，使用时才启动 TODO
                  @ModelAttribute Order order,
                  Errors errors,
                  SessionStatus sessionStatus) {
              if (errors.hasErrors()){
                  return "current";
              }
              orderRepo.save(order);
              sessionStatus.setComplete();
              return "redirect:/";
          }
      }
      ```

  除了将OrderRepository注⼊到控制器中，OrderController唯⼀ 明显的变化就是processOrder()⽅法。

  由于我们使用的是在session中传过来的order model所以，getMapping时不需要new一个新的实例了

  在这个⽅法中，通过表单提交 的Order对象（同时也是session中持有的Object对象）会通过注⼊的
  OrderRepository的save()⽅法进⾏保存。

  订单保存完成之后，我们就不需要在session中持有它了。实际 上，如果我们不把它清理掉，那么订单会继续保留在session中，其中 包括与之关联的taco，下⼀次的订单将会从旧订单中保存的taco开始。

  processOrder()⽅法请求了⼀个SessionStatus参数，并 调⽤了它的setComplete()⽅法来重置session。

  可以在浏览器中访[http://localhost:8080/h2-console](http://localhost:8080/h2-console)以查看H2 Console

## 2️⃣使用Spring Data JPA持久化数据

## 3️⃣ 小结