package test;

import dao.UserDao;
import dao.UserDaoJdbc;
import entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.SQLTransientException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application.xml")
public class UserDaoTest {

    @Autowired
    UserDao dao;

    @Autowired
    DataSource dataSource;

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
    public void addAndGet(){
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
    public void count(){

        dao.deleteAll();
        assertThat(dao.getCount(),is(0));

        dao.add(user1);
        assertThat(dao.getCount(),is(1));

        dao.add(user2);
        assertThat(dao.getCount(),is(2));

        dao.add(user3);
        assertThat(dao.getCount(),is(3));
    }


    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure(){
        dao.deleteAll();
        assertThat(dao.getCount(),is(0));
        dao.get("unknown_id");
    }


    @Test
    public void getALl() {
        dao.deleteAll();

        List<User> users = dao.getAll();
        assertThat(users.size(), is(0));


        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1,users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1,users2.get(0));
        checkSameUser(user2,users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user3,users3.get(0));
        checkSameUser(user1,users3.get(1));
        checkSameUser(user2,users3.get(2));
    }

    @Test(expected = DuplicateKeyException.class)
    public void duplicateKey(){
        dao.deleteAll();

        dao.add(user1);
        dao.add(user1);//같은 사용자를 2번 넣는다.
    }

    @Test
    public void sqlExceptionTranslate(){
        dao.deleteAll();
        try {
            dao.add(user1);
            dao.add(user1);
        }catch (DuplicateKeyException ex){
            SQLException sqlException = (SQLException)ex.getRootCause(); //중첩에러를 가지고 올 수 있다.
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource); //코드를 이용한 SQLException 전환
            assertThat(set.translate(null,null,sqlException), is(DuplicateKeyException.class));//에러메세지를 만들때 사용하는 정보로 null넣어두 된다.
        }
    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(),is(user2.getId()));
        assertThat(user1.getName(),is(user2.getName()));
        assertThat(user1.getPassword(),is(user2.getPassword()));
    }
}
