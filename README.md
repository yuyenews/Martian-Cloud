<div align=center>
<img width="200px;" src="http://mars-framework.com/img/logo-github.png"/>
</div>

<br/>

<div align=center>

<img src="https://img.shields.io/badge/licenes-MIT-brightgreen.svg"/>
<img src="https://img.shields.io/badge/jdk-1.8+-brightgreen.svg"/>
<img src="https://img.shields.io/badge/maven-3.5.4+-brightgreen.svg"/>
<img src="https://img.shields.io/badge/release-master-brightgreen.svg"/>

</div>

<br/>

<div align=center>

Martian 框架的官方分布式组件

</div>

## 项目简介

Martian-cloud 是Martian的官方分布式组件，基于传染机制，不再需要注册中心

1. 完全丢弃了注册中心，且不依赖任何注册中心，采用传染机制实现服务的发现与治理
2. 服务间通话采用rest风格
3. 对Martian的侵入非常小

## 什么是传染机制

- 常规的分布式采用的是【生产者->注册中心->消费者】模型，生产者将接口给注册中心，消费者从注册中心发现其他的服务，实现调用
- 传染机制就是丢弃注册中心，可以把接口看做病毒，服务看做是人，服务之间只要有直接或者间接的联系，最终都会被染上病毒（接口）

## 比如我们现在有三个服务

<img src="http://mars-framework.com/img/ws-blank.png" width="500px"/>
<br/>
这些服务之间是相互独立的，他们无法发现对方，所以我们需要做一些事

## 可以将他们连接起来

比如像这个样子【图1】

<img src="http://mars-framework.com/img/ws-one.png" width="500px"/>

<br/>

也可以是这样子【图2】

<img src="http://mars-framework.com/img/ws-two.png" width="500px"/>
<br/>
连接方式随意，只要别让任何服务落单即可

## 当这些服务连接后，会发生什么

我们用图1来举例

1. 当A启动时，此时只有一台服务，所以相安无事，完全独立
2. 当B启动时，由于A连接的是B，所以A,B之间产生了关系，他们的接口会互相传染，此时A中有B的接口，B中有A的接口
3. 当C启动时，由于B连接的是C，所以B,C之间产生了关系，而B和A又存在关系，所以三台服务器都产生了关系，他们的接口再一次相互传染了，此时A,B,C都有对方的完整接口列表
4. 如果三台服务中任意一个宕机了，也没关系，因为他们的接口已经传染开了，所有服务都产生了联系，可以跳过一开始的传染途径，直接进行感染
5. 宕机的这个服务的接口会从其他的服务上自动消失

## 详细原理介绍

[https://www.bilibili.com/read/cv8314554](https://www.bilibili.com/read/cv8314554)

## 官方文档

[http://mars-framework.com/doc.html?tag=compent](http://mars-framework.com/doc.html?tag=compent)

## 使用示例

[https://github.com/yuyenews/Mars-Cloud-Example](https://github.com/yuyenews/Mars-Cloud-Example)

## 使用起来也很简单

### 一、 仅需一个依赖

```xml
<dependency>
    <groupId>com.github.yuyenews</groupId>
    <artifactId>mars-cloud-starter</artifactId>
    <version>最新版，具体看《组件介绍》</version>
</dependency>
```

### 二、 支持Feign调用
```java
/* 
这个注解的serverName跟你要调用的那个服务的name一致（配置类里cloud配置的name） 
beanName 不写的话，默认为类名首字母小写
*/
@MarsFeign(serverName="mars-demo",beanName="demoFeign")
public interface DemoFeign {
    /* 
        这里面的所有方法，跟你要调用的那个API中的方法名一致
    */
    返回类型 insert(DemoEntity entity);

    /*
        可以用@MarsContentType注解 来指定本次请求的ContentType
    */
    @MarsContentType(ContentType = ContentType.JSON)
    返回类型 selectList(DemoEntity entity);
}
```

### 三、 也支持RestTemplate

```java
返回类型 result = MarsRestTemplate.request(服务name,MarsApi接口的方法名,new Object[]{参数对象1，参数对象2},返回类型.class, ContentType.FORM);
```