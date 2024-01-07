# spring-go

#### 介绍
🔥🔥🔥Spring-GO是一款易用、高速、高效、功能丰富的开源Spring-Cloud脚手架。

#### 软件架构

SpringAuthorizationServer   
SpringSecurity   
SpringDataJpa   
SpringRestDocs   
SpringCloudConfig   
SpringCloudAlibabaNacos


#### 安装教程



#### 使用说明

1.  [前端地址](https://gitee.com/sdake/spring-go-admin)
2. authorization 是独立项目可以单独提取出来 [使用文档](https://github.com/sdack-cloud/spring-go/wiki/%E6%8E%88%E6%9D%83%E6%9C%8D%E5%8A%A1%E5%99%A8%E7%9A%84%E4%BD%BF%E7%94%A8)
3. common 模块是公共模块，存放实体类对象等共用的工具类
4. SpringDataJpa   
> 数据层使用 SpringDataJpa。对于初接触Jpa人群来说并不适应，但是当你尝试了使用了Jpa，你会发现它的优势。   
> 1.   JPA可移植性好，支持Hibernate方言,包括国产数据库也会提供Hibernate方言包。自动创建数据库表结构，自动增加字段与索引，这避免了您引用数据库迁移框架 
> 2.   减少sql语句的编写，开发效率高。最简单的sql便是最高效的。     以往我们需要编写非常多的复杂的sql来完成业务，这对日后功能的新增与维护是非常不利的。     
        时间久了sql运行效率极大降低，也是因为sql语言的灵活性以至于初级程序员对编写sql细节做的不到位
> 3.   面向对象开发思想，对象化程度更高。    数据库是一个软件项目的根基，万丈高楼平地起。一个好的数据库设计理念往往能带给项目持久的生命力。     
        由于sql语言的灵活性也导致设计数据已面向过程编写，用到才给到。也忽视了数据库与软件层面的结合

5.  SpringRestDocs 作为API文档框架
>  在Swagger中，我们必须使用注解使rest控制器的代码混乱，并降低了其可读性。此外，文档与代码紧密耦合，并将进入生产环境。   维护文档是这里的另一个挑战。如果SwaggerAPI中的某些内容发生了变化，程序员能总是记得更新相应的注解吗    
> REST Docs 看起来既不像其他 UI 那样吸引人，也不能用于验收测试。 它已单元测试的编写形成 API 文档。   
>  测试的成功完成不仅为我们提供了代码片段，而且还像任何其他单元测试一样验证了我们的 API。    
> 这迫使我们进行与 API 修改相对应的文档更改。此外，文档代码与实现完全分开。   它还需要更多步骤来生成最终的 HTML文档
