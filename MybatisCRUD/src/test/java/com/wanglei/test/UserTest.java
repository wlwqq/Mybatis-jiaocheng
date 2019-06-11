package com.wanglei.test;

import com.wanglei.dao.UserDao;
import com.wanglei.domain.Queryvo;
import com.wanglei.domain.User;
import com.wanglei.domain.User1;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jws.soap.SOAPBinding;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

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

    @Test
    public void testFindById(){
        User user = userDao.findById(50);
        System.out.println(user);
    }

    @Test
    public void testFindByName(){
        List<User> users = userDao.findByName("%王%");
        for (User u : users){
            System.out.println(u);
        }
    }

    @Test
    public void testFindTotal(){
        Integer total = userDao.findTotal();
        System.out.println(total);
    }

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

    @Test
    public void testFindUser1(){
        User1 user1 = userDao.findUser1ById(50);
        System.out.println(user1);
    }
}
