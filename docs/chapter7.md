# 第七章 消费REST服务

> 本章内容
> 使用RestTemplate消费RestAPI
> 使用Traverson导航超媒体API

前面章节中定义的Rest端点，可以被外部的客户端所消费，也可以被另一个Java应用消费。

Spring应用可以采用多种方式来先消费RestAPI：

- RestTemplate：Spring核心框架提供的简单、同步REST客户端
- Traverson：Spring HATEOAS提供的支持超链接，同步的REST客户端
- WebClient：Spring5所引入的反应式异步REST客户端(这个到之后才学)

## 1️⃣使用RestTemplate消费REST端点

与REST资源进⾏交互涉及很多⼯作，⽽且 ⼤多数都是很单调乏味的样板式代码。可能涉及：创建实例、请求对象、执行请求、解析响应、将响应映射为领域对象，以及处理异常等。这些事情其实RestTemplate都代为封装了。

RestTemplate有很多方法，不过集中有很多是重载形式，独立的操作有12个：

- RestTemplate常用的方法

  ⽅法 描述

    - **delete(…)** 在特定的URL上对资源执⾏HTTP DELETE操作
    - **exchange(…)**
      在URL上执⾏特定的HTTP⽅法，返回包含对象的 ResponseEntity，这个对象是从响应体中映射得到的
    - **execute(…)**
      在URL上执⾏特定的HTTP⽅法，返回⼀个从响应体映射 得到的对象
    - **getForEntity(…)**
      发送⼀个HTTP GET请求，返回的ResponseEntity包含 了响应体所映射成的对象
    - **getForObject(…)**
      发送⼀个HTTP GET请求，返回的请求体将映射为⼀个对 象
    - **headForHeaders(…)**
      发送HTTP HEAD请求，返回包含特定资源URL的HTTP头 信息
    - **optionsForAllow(…)** 发送HTTP OPTIONS请求，返回特定URL的Allow头信息
    - **patchForObject(...)**
      发送HTTP PATCH请求，返回⼀个从响应体映射得到的对 象
    - **postForEntity(…)**
      POST数据到⼀个URL，返回包含⼀个对象的 ResponseEntity，这个对象是从响应体中映射得到的
    - **postForLocation(…)** POST数据到⼀个URL，返回新创建资源的URL
      ⽅法 描述
    - **postForObject(…)** POST数据到⼀个URL，返回根据响应体匹配形成的对象
    - **put(…)** PUT资源到特定的URL

- 重载的形式一般有：

    - 使⽤String作为URL格式，并使⽤可变参数列表指明URL参数。
    - 使⽤String作为URL格式，并使⽤Map<String,String>指明
    - URL参数。 使⽤java.net.URI作为URL格式，不⽀持参数化URL。

- 使用RestTemplate，需要先在某个地方创建实例：

  ```java
  RestTemplate rest = new RestTemplate();
  ```

  或者通过bean注入

  ```java
  @Bean
  public RestTemplate restTemplate() {
   return new RestTemplate();
  }
  ```

- GET资源

  假设我们想要通过Taco Cloud API获取API 某个配料，并且API 没有实现HATEOAS，那么可以使用getForObject()

    - 使用URL 字符串以及可变长参数列表

    ```java
    public Ingredient getIngredientById(String ingredientId) {
     return rest.getForObject("http://localhost:8080/ingredients/{id}",
     Ingredient.class, ingredientId);
    }
    ```

    变量参数会按照它们出现的顺序设置到占位符中。getForObject()⽅法的第⼆个参数是响应应该绑定的类型。

- 亦可以使用Map来指定URL变量

  ```java
  public Ingredient getIngredientById(String ingredientId) {
   Map<String,String> urlVariables = new HashMap<>();
   urlVariables.put("id", ingredientId);
   return rest.getForObject("http://localhost:8080/ingredients/{id}",
   Ingredient.class, urlVariables);
  }
  ```

- 使用URI对象参数

  使用前首先需要构建一个URI对象

  ```java
  public Ingredient getIngredientById(String ingredientId) {
   Map<String,String> urlVariables = new HashMap<>();
   urlVariables.put("id", ingredientId);
   URI url = UriComponentsBuilder
   .fromHttpUrl("http://localhost:8080/ingredients/{id}")
   .build(urlVariables);
   return rest.getForObject(url, Ingredient.class);
  }
  ```

getForEntity()的⼯作⽅式和getForObject()类似，但是它所返 回的并不是代表响应载荷的领域对象，⽽是⼀个包裹领域对象的ResponseEntity对象。借助ResponseEntity对象能够访问很多的响 应细节，⽐如响应头信息。

  ```java
  public Ingredient getIngredientById(String ingredientId) {
   ResponseEntity<Ingredient> responseEntity =
   rest.getForEntity("http://localhost:8080/ingredients/{id}",
   Ingredient.class, ingredientId);
   log.info("Fetched time: " +
   responseEntity.getHeaders().getDate());
   return responseEntity.getBody();
  }
  ```

- PUT资源

  put()⽅法的3个变种形式都会接收⼀个Object，它会被序列化并发送 ⾄给定的URL。就URL本⾝来讲，它可以以URI对象或String的形式来指定，URL变量能够以可 变参数列表或Map的形式来提供。

  ```java
  public void updateIngredient(Ingredient ingredient) {
   rest.put("http://localhost:8080/ingredients/{id}",
   ingredient,
   ingredient.getId());
  }
  ```

- DELETE资源

  ```java
  public void deleteIngredient(Ingredient ingredient) {
   rest.delete("http://localhost:8080/ingredients/{id}",
   ingredient.getId());
  }
  ```

- POST资源

    - 希望在POST请求之后得到新创建的Ingredient资源：

      ```java
      public Ingredient createIngredient(Ingredient ingredient) {
       return rest.postForObject("http://localhost:8080/ingredients",
       ingredient,
       Ingredient.class);
      }
      ```

    - 想要知道新创建资源的地址

      ```java
      public URI createIngredient(Ingredient ingredient) {
       return rest.postForLocation("http://localhost:8080/ingredients",
       ingredient);
      }
      ```

    - 同时需要地址和响应载荷

      ```java
      public Ingredient createIngredient(Ingredient ingredient) {
       ResponseEntity<Ingredient> responseEntity =
       rest.postForEntity("http://localhost:8080/ingredients",
       ingredient,
       Ingredient.class);
       log.info("New resource created at " +
       responseEntity.getHeaders().getLocation());
       return responseEntity.getBody();
      }
      ```

如果所消费的API在响应中包含了超链接，那么 RestTemplate就⼒所不及了

## 2️⃣使用Traverson导航

Traverson来源于Spring Data HATEOAS项⽬，是Spring应⽤中开箱即⽤的消费超媒体API的解决⽅案。

- 实例化Traverson对象

  需要使⽤API的基础URI

  ```java
  Traverson traverson = new Traverson(
   URI.create("http://localhost:8080/api"), MediaTypes.HAL_JSON);
  ```

  给Traverson指定的唯⼀URL，并指定了API将会⽣成JSON格式的响应，并且具有HAL⻛格的超链接。可以选择在 使⽤Traverson对象之前实例化它，也可以将其声明为⼀个bean并在 需要的地⽅注⼊进来

- 检索所有的配料列表

  配料链接有⼀个href属性，它会链接到配料资源，跟踪这个链接：

  ```java
  ParameterizedTypeReference<Resources<Ingredient>> ingredientType =
   new ParameterizedTypeReference<Resources<Ingredient>>() {};
  Resources<Ingredient> ingredientRes =
   traverson
   .follow("ingredients")
   .toObject(ingredientType);
  Collection<Ingredient> ingredients = ingredientRes.getContent();
  ```

  通过调⽤Traverson对象的follow()⽅法，可以导航⾄链接 关系名为ingredients的资源，通过调⽤toObject()来提取资源的内容。需要告诉toObject()⽅法要将数据读⼊到哪种对象之中，创建ParameterizedTypeReference能够帮助解决这个问题。

- 获取最新创建的taco

  ```java
  ParameterizedTypeReference<Resources<Taco>> tacoType =
   new ParameterizedTypeReference<Resources<Taco>>() {};
  Resources<Taco> tacoRes =
   traverson
   .follow("tacos")
   .follow("recents")
   .toObject(tacoType);
  Collection<Taco> tacos = tacoRes.getContent();
  ```

  可以通过列出关系名称列表的形式来简 化“.follow()”

  ```java
  Resources<Taco> tacoRes =
   traverson
   .follow("tacos", "recents")
   .toObject(tacoType);
  ```

Traverson能够很容易地导航HATEOAS的API并消费其资源，但并没有提供通过这些API写⼊或删除资源 的⽅法。相反，RestTemplate能够写⼊和删除资源，但是在导航API⽅⾯⽀持得并不太好。

既要导航API⼜要更新或删除资源时，需要组合使⽤ RestTemplate和Traverson：

```java
private Ingredient addIngredient(Ingredient ingredient) {
 String ingredientsUrl = traverson
 .follow("ingredients")
 .asLink()
 .getHref();
 return rest.postForObject(ingredientsUrl,
 ingredient,
 Ingredient.class);
}
```

## 🈴小结

- 客户端可以使用RestTemplate来针对Rest API发送HTTP请求、
- Traverson能够让客户端导航响应中的内嵌超链接的API