# MyBatis实用教程

声明：MyBatis是目前JavaEE开发中非常火热的持久层框架，这个框架负责与数据库进行交互，处理持久层的相关操作，本套教程使用IDEA2018开发，代码会全部上传至github仓库，喜欢的可以star一下。



[TOC]



## 1.MyBatis概述

mybatis 是一个优秀的基于 java 的持久层框架，它内部封装了 jdbc，使**开发者只需要关注sql 语句本身**，而不需要花费精力去处理加载驱动、创建连接、创建 statement 等繁杂的过程。
mybatis 通过 **xml 或注解**的方式将要执行的各种 statement 配置起来，并通过 java 对象和 statement 中sql 的动态参数进行映射生成最终执行的 sql 语句，最后由 mybatis 框架执行 sql 并将结果映射为 java 对象并返回。
采用 ORM 思想解决了实体和数据库映射的问题，对 jdbc 进行了封装，屏蔽了 jdbc api 底层访问细节，使我们不用与 jdbc api 打交道，就可以完成对数据库的持久化操作。

关于持久层的处理，有以下几种技术栈

java web阶段：**Apache的DBUtils**：对jdbc的简单封装，使用的数据源也叫数据连接池为c3p0

Spring框架：**JdbcTemplate**：对jdbc的简单封装，使用的数据源为Spring自带的DataSourceManager

而MyBatis是一个框架，比以上两种工具类要复杂很多，简述一下什么是ORM思想

**Object Rational Mapping**：对象关系映射，简单解释就是将domain中的实体类与数据库表的每一行对应起来，让我们操作实体类就像操作表一样纵享丝滑。

![](D:\githubinbendi\mybatis\images\1.png)

## 2.MyBatis快速入门

### 2.1 快速入门xml版本（MybatisQuickXml）

 **（1）新建maven项目，并导入依赖**

```xml
    <dependencies>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.4.6</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
    </dependencies>
```

**（2）编写实体类User和Dao层接口**

具体代码查看仓库MybatisQuickXml项目

**（3）编写MyBatis主配置文件**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<!-- mybatis的主配置文件 -->
<configuration>
    <!-- 1.配置环境 -->
    <environments default="mysql">

        <environment id="mysql">
            <!-- 配置事务的类型-->
            <transactionManager type="JDBC"></transactionManager>
            <!-- 配置数据源（连接池） -->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"></property>
                <property name="url" value="jdbc:mysql://192.168.1.104:3306/database"></property>
                <property name="username" value="root"></property>
                <property name="password" value="root"></property>
            </dataSource>
        </environment>

    </environments>
    <!-- 2.指定映射配置文件的位置，映射配置文件指的是每个dao独立的配置文件 -->
    <mappers>
        <mapper resource="com/wanglei/dao/UserDao.xml"></mapper>
    </mappers>
</configuration>
```

**（4）编写Dao层接口的配置文件**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.wanglei.dao.UserDao">
    <select id="findAll" resultType="com.wanglei.domain.User">
    	select * from user
    </select>
</mapper>
```

**（5）编写测试方法测试**

```java
public class MybatisTest {

    public static void main(String[] args) throws IOException {
        //1.读取配置文件
        InputStream in = Resources.getResourceAsStream("SqlMapConfig.xml");
        //2.创建SqlSessionFactory工厂
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        //3.使用工厂生产SqlSession对象
        SqlSession session = factory.openSession();
        //4.使用SqlSession创建Dao接口的代理对象
        UserDao userDao = session.getMapper(UserDao.class);
        //5.使用代理对象执行方法
        List<User> users = userDao.findAll();
        for(User user : users){
            System.out.println(user);
        }
        //6.释放资源
        session.close();
        in.close();
    }
}
```

**（6）注意事项**

第一，命名问题，会发现项目中Dao层的接口名为UserDao.java，映射文件叫UserDao.xml。包括这两个文件的包路径都一样。

第二，Dao接口的mapper配置文件即UserDao.xml的namespace属性必须是接口的全类名，UserDao.xml中方法的id属性也必须对应接口中方法的名称。

第三，MyBatis中Dao==Mapper，什么意思呢？就是说在Dao层我们定义了一个接口比方说叫CommodityDao，那么在创建它的映射文件时可以叫CommodityDao.xml也可以叫CommodityMapper.xml

为什么有这么多规矩呢？只要我们遵循这些规矩，那么过去我们在Dao层需要写接口还需要写实现类Impl，现在就不需要写实现类了，这个映射文件Mapper就是我们的实现类。

### 2.2 快速入门Anno版本（MybatisQuickAnno）

与以上项目一样，只是Dao的接口不再需要一个mapper映射了，而是使用注解代替，主要有以下两步

**（1）主配置文件修改**

```xml
    <mappers>
        <mapper class="com.wanglei.dao.UserDao"></mapper>
    </mappers>
```

**（2）在接口上添加注解**

```java
public interface UserDao {

    @Select("select * from user")
    List<User> findAll();
}
```

**（3）主方法测试**

```java
public class MybatisTest {

    public static void main(String[] args) throws IOException {
        //1.读取配置文件
        InputStream in = Resources.getResourceAsStream("SqlMapConfig.xml");
        //2.创建SqlSessionFactory工厂
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        //3.使用工厂生产SqlSession对象
        SqlSession session = factory.openSession();
        //4.使用SqlSession创建Dao接口的代理对象
        UserDao userDao = session.getMapper(UserDao.class);
        //5.使用代理对象执行方法
        List<User> users = userDao.findAll();
        for(User user : users){
            System.out.println(user);
        }
        //6.释放资源
        session.close();
        in.close();
    }
}

```

![](D:\githubinbendi\mybatis\images\2.png)



## 3.Mybatis基本使用和单表CRUD

### 3.1 总结入门案例

这里总结一下Mybatis的入门案例，因为Mybatis的用法比较复杂，不像Spring那样只涉及到一个配置文件，主要也是对IOC容器的使用。

**Mybatis的配置文件（2个）**：主配置文件和接口mapper文件

**Mybatis的使用步骤**：

1.使用InputStream读取主配置文件SqlMapConfig.xml

2.通过读取的主配置文件创建SqlSessionFactory，这里用到了设计模式中建造者模式

3.通过SqlSessionFactory创建SqlSession对象，这里用到了工厂模式

4.通过SqlSession对象包装Dao接口中的对象，获得dao代理对象

5.使用dao对象执行方法

6.关闭session对象和读取文件流

### 3.2 Mybatis的增删改操作

代码位于仓库项目MybatisCRUD中

**（1）在Dao接口中添加增删改操作**

```java
public interface UserDao {

    void saveUsdr(User user);

    void deleteUser(Integer id);
    
    void updateUser(User user);
}
```

**（2）编写主配置文件SqlMapConfig.xml**

```xml
<!-- mybatis的主配置文件 -->
<configuration>
    <!-- 1.配置环境 -->
    <environments default="mysql">

        <environment id="mysql">
            <!-- 配置事务的类型-->
            <transactionManager type="JDBC"></transactionManager>
            <!-- 配置数据源（连接池） -->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"></property>
                <property name="url" value="jdbc:mysql://192.168.1.104:3306/database"></property>
                <property name="username" value="root"></property>
                <property name="password" value="root"></property>
            </dataSource>
        </environment>

    </environments>
    <!-- 2.指定Dao的mapper文件 -->
    <mappers>
        <mapper resource="com/wanglei/dao/UserDao.xml"></mapper>
    </mappers>
</configuration>
```

**（3）编写UserDao配置文件UserDao.xml**

注意目录路径要与Dao接口路径相同

```xml
<mapper namespace="com.wanglei.dao.UserDao">

    <insert id="saveUser" parameterType="com.wanglei.domain.User">
        insert into user(username,birthday,sex,address) values(#{username},#{birthday},#{sex},#{address})
    </insert>

    <delete id="deleteUser" parameterType="Integer">
        delete from user where id=#{id}
    </delete>

    <update id="updateUser" parameterType="com.wanglei.domain.User">
        update user set username=#{username},birthday=#{birthday},sex=#{sex},address=#{address} where id=#{id}
    </update>
</mapper>
```

**（4）存储中文乱码问题**

如果在添加中文数据时出现了中文乱码问题，修改主配置文件中与数据库的连接

```xml
jdbc:mysql://192.168.1.104:3306/database?useUnicode=true&amp;characterEncoding=utf-8
```

**（5）在Junit中测试**

```java
public class UserTest {

    private InputStream in;
    private SqlSession sqlSession;
    private UserDao userDao;

    @Before//用于在测试方法执行之前执行
    public void init()throws Exception{
        //1.读取配置文件，生成字节输入流
        in = Resources.getResourceAsStream("SqlMapConfig.xml");
        //2.获取SqlSessionFactory
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in);
        //3.获取SqlSession对象
        sqlSession = factory.openSession();
        //4.获取dao的代理对象
        userDao = sqlSession.getMapper(UserDao.class);
    }

    @After//用于在测试方法执行之后执行
    public void destroy()throws Exception{
        //提交事务
        sqlSession.commit();
        //6.释放资源
        sqlSession.close();
        in.close();
    }

    @Test
    public void testSaveUser(){
        User u = new User();
        u.setUsername("wanglei");
        u.setBirthday(new Date());
        u.setSex("男");
        u.setAddress("浙江杭州");

        userDao.saveUser(u);
    }

    @Test
    public void testDeleteUser(){

        userDao.deleteUser(49);
    }

    @Test
    public void testUpdateUser(){
        User u = new User();
        u.setId(50);
        u.setUsername("吴倩");
        u.setBirthday(new Date());
        u.setSex("女");
        u.setAddress("安徽省阜阳市");

        userDao.updateUser(u);
    }
}

```

### 3.3 Mybatis的查询操作

**单个查询**

```xml
User findById(Integer id);

   <select id="findById" parameterType="Integer" resultType="com.wanglei.domain.User">
        select * from user where id=#{id}
    </select>
```

**模糊查询**

```xml
List<User> findByName(String username);

   <select id="findByName" parameterType="String" resultType="com.wanglei.domain.User">
        select * from user where username like #{username}
    </select>
```

**聚合查询**

```xml
Integer findTotal();

    <select id="findTotal" resultType="Integer">
        select count(id) from user
    </select>
```

**OGNL表达式**

通过对象的取值方法来获取数据，在写法上省略了get，这就是Mybatis取值的原因，根据get方法取值

类中写法：user.getUsername()

OGNL表达式写法：user.username

那为什么我们在mybatis中直接#{username}呢？因为我们在ParameterType已经指定了User类型

### 3.4 POJO对象查询

什么是POJO，就是javaBean，利用POJO查询主要就是，查询条件是一个对象的某一个属性

**（1）新建一个查询对象Queryvo.java**

```java
public class Queryvo {
    
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

```

**（2）写接口方法和映射**

```xml
List<User> findByVo(Queryvo queryvo);

	<select id="findByVo" parameterType="com.wanglei.domain.Queryvo" resultType="com.wanglei.domain.User">
        select * from user where username like #{user.username}
    </select>
```

**（3）测试**

```java
    @Test
    public void testFindByVo(){
        Queryvo vo = new Queryvo();
        User user = new User();
        user.setUsername("%王%");
        vo.setUser(user);

        List<User> users = userDao.findByVo(vo);
        for (User u : users){
            System.out.println(u);
        }
    }
```

### 3.5 ORM和实体与表之间的映射

不知道前面提及的ORM思想还记不记得，ORM就是将实体类和mysql中的表格对应起来，前面的所有例子可以发现，我们对于User这个实体类的属性编写和mysql数据库中的列名完全一样，如下

![](D:\githubinbendi\mybatis\images\3.png)

![](D:\githubinbendi\mybatis\images\4.png)

但是在Java编程规范中，我们会发现其实属性的命名规范为驼峰法，所以实体类应该和下面一样

```java
public class User1 implements Serializable {

    private Integer userId;
    private String userName;
    private Date userBirthday;
    private String userSex;
    private String userAddress;
```

那这样的话属性名称都不一样了，如何映射呢？在UserDao的Mapper映射文件中配置resultMap

```xml
    <resultMap id="UserMap" type="com.wanglei.domain.User1">
        <id property="userId" column="id"></id>
        <result property="userName" column="username"></result>
        <result property="userBirthday" column="birthday"></result>
        <result property="userSex" column="sex"></result>
        <result property="userAddress" column="address"></result>
    </resultMap>

    <select id="findUser1ById" resultMap="UserMap" parameterType="Integer">
        select * from user where id=#{userId}
    </select>

```



## 4.SqlMapConfig.xml中的相关配置

### 4.1 数据库连接信息的问题

首先，关于数据库的连接，我们是直接写死在主配置文件中，这样是不好的，正确的做法是从jdbc.properties中取出连接信息

**（1）准备jdbc.properties文件**

**（2）在主配置文件中读取properties**

```xml
<configuration>
    <properties resource="jdbc.properties"></properties>
    <!-- 1.配置环境 -->
    <environments default="mysql">
        <environment id="mysql">
            <!-- 配置事务的类型-->
            <transactionManager type="JDBC"></transactionManager>
            <!-- 配置数据源（连接池） -->
            <dataSource type="POOLED">
                <property name="driver" value="${jdbc.driver}"></property>
                <property name="url" value="${jdbc.url}"></property>
                <property name="username" value="${jdbc.username}"></property>
                <property name="password" value="jdbc.password"></property>
            </dataSource>
        </environment>
    </environments>
    <!-- 2.指定Dao的mapper文件 -->
    <mappers>
        <mapper resource="com/wanglei/dao/UserDao.xml"></mapper>
    </mappers>
</configuration>
```

注意properties标签的位置，以及表达式取值的方式为${jdbc.driver}

### 4.2 typeAliases和package标签

其实在我们的接口mapper文件中，一直还存在一个疑问，就是不论是parameterType或者是ResultType中当涉及到基本类型时，我们写int、Integer、java.lang.Integer都可以，这是为什么呢？因为Mybatis中对于这些都做了别名，但是我们自己定义的实体类就必须指定包名了，这样如何解决呢？

主配置类中

```xml
<configuration>
    <properties resource="jdbc.properties"></properties>
    <typeAliases>
        <typeAlias type="com.wanglei.domain.User" alias="user"></typeAlias>
        <package name="com.wanglei.domain"></package>
    </typeAliases>
```

注意：typeAlias和package任写一个即可，写第一个很明显，将User类起一个别名叫user，不区分大小写；如果写package，就更方便了，这个包下所有类都会有别名，别名就是类名。

**package**也可以在mappers标签中使用，使用之后就不用再单独指定接口的mapper映射文件了

```xml
    <mappers>
        <package name="com.wanglei.dao"></package>
    </mappers>
```

