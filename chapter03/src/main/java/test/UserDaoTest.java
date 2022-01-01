package test;

import dao.UserDao;
import entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-application.xml")
public class UserDaoTest {

    @Autowired
    UserDao dao;

    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp(){
        user1 = new User("gyumee","박성철","springno1");
        user2 = new User("leegw700","이길원","springno2");
        user3 = new User("bumjin","박범진","springno3");
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        JUnitCore.main("test.UserDaoTest");
    }

    
    @Test
    public void addAndGet() throws SQLException{
        dao.deleteAll();
        assertThat(dao.getCount(),is(0));
        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(),is(2));

        User userGet1 = dao.get(user1.getId());
        assertThat(userGet1.getName(),is(user1.getName()));
        assertThat(userGet1.getPassword(),is(user1.getPassword()));
        User userGet2 = dao.get(user2.getId());
        assertThat(userGet2.getName(),is(user2.getName()));
        assertThat(userGet2.getPassword(),is(user2.getPassword()));
    }


    @Test
    public void count() throws SQLException{

        dao.deleteAll();
        assertThat(dao.getCount(),is(0));

        dao.add(user1);
        assertThat(dao.getCount(),is(1));

        dao.add(user2);
        assertThat(dao.getCount(),is(2));

        dao.add(user3);
        assertThat(dao.getCount(),is(3));
    }


    @Test(expected = IllegalArgumentException.class)
    public void getUserFailure() throws SQLException{
        dao.deleteAll();
        assertThat(dao.getCount(),is(0));
        dao.get("unknown_id");
    }
}
