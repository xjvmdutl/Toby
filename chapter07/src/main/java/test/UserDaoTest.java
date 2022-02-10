package test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import configuration.AppContext;
import configuration.TestAppContext;
import dao.UserDao;
import entity.Level;
import entity.User;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@ContextConfiguration(locations = "/testApplication.xml") //XML 설정파일을 읽어들이는 UserDaoTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppContext.class, TestAppContext.class})
public class UserDaoTest {


    @Autowired
    UserDao dao;

    @Autowired
    DataSource dataSource;

    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {
        user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0, "widn45@naver.com");
        user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 0, "widn45@naver.com");
        user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40, "widn45@naver.com");
    }


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        JUnitCore.main("test.UserDaoTest");
    }


    @Test
    public void addAndGet() {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));
        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userGet1 = dao.get(user1.getId());
        checkSameUser(userGet1, user1);

        User userGet2 = dao.get(user2.getId());
        checkSameUser(userGet2, user2);
    }


    @Test
    public void count() {

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));

        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        dao.add(user3);
        assertThat(dao.getCount(), is(3));
    }


    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));
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
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user3, users3.get(0));
        checkSameUser(user1, users3.get(1));
        checkSameUser(user2, users3.get(2));
    }

    @Test(expected = DuplicateKeyException.class)
    public void duplicateKey() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user1);
    }

    @Test
    public void sqlExceptionTranslate() {
        dao.deleteAll();
        try {
            dao.add(user1);
            dao.add(user1);
        } catch (DuplicateKeyException ex) {
            SQLException sqlException = (SQLException) ex.getRootCause();
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(
                this.dataSource);
            assertThat(set.translate(null, null, sqlException),
                is(DuplicateKeyException.class));
        }
    }

    @Test
    public void update() {
        dao.deleteAll();
        dao.add(user1);
        dao.add(user2);

        user1.setName("오민규");
        user1.setPassword("springno6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);

        dao.update(user1);

        User user1Update = dao.get(user1.getId());
        checkSameUser(user1, user1Update);
        User user2Update = dao.get(user2.getId());
        checkSameUser(user2, user2Update);
    }


    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));

        assertThat(user1.getLevel(), is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
        assertThat(user1.getEmail(), is(user2.getEmail()));
    }
}