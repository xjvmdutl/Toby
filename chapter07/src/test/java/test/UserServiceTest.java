package test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

import dao.UserDao;
import entity.Level;
import entity.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import service.UserService;
import service.UserServiceImpl;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplication.xml")
@TransactionConfiguration(defaultRollback = false)
public class UserServiceTest {
    static class TestUserServiceException extends RuntimeException {

    }

    static class TestUserService extends UserServiceImpl {

        private String id = "madnite1";

        @Override
        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            super.upgradeLevel(user);
        }

        @Override
        public List<User> getAll() {
            for (User user : super.getAll()) {
                super.update(user);
            }
            return null;
        }
    }

    @Autowired
    UserService userService;

    @Autowired
    UserService testUserService;

    @Autowired
    UserDao userDao;

    List<User> users;

    @Before
    public void setup() {
        this.users = Arrays.asList(
            new User("bunjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0,
                "widn45@naver.com"),
            new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0,
                "widn45@naver.com"),
            new User("erwins", "신승환", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1,
                "widn45@naver.com"),
            new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD,
                "widn45@naver.com"),
            new User("green", "오규민", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "widn45@naver.com")
        );
    }

    @Test
    public void bean() {
        assertThat(this.testUserService, is(notNullValue()));
    }


    @Test
    @DirtiesContext
    public void upgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class); //다이나믹 목 생성, 리턴값설정, DI 까지 3줄이면 된다
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class); //리턴값 없는 객체는 더욱 쉽게
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel(), is(Level.SILVER));
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel(), is(Level.GOLD));

        ArgumentCaptor<SimpleMailMessage> mailMessageArgumentCaptor
            = ArgumentCaptor.forClass(SimpleMailMessage.class); //파라미터를 정밀 검사하기위해 캡처도 가능
        verify(mockMailSender, times(2)).send(mailMessageArgumentCaptor.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArgumentCaptor.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
    }

    private void checkUserAndLevel(User updated, String expectId, Level expectLevel) {
        assertThat(updated.getId(), is(expectId));
        assertThat(updated.getLevel(), is(expectLevel));
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithOutLevel = users.get(0);
        userWithOutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithOutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithOutLevelRead = userDao.get(userWithOutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithOutLevelRead.getLevel(), is(Level.BASIC));
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) //업데이트가 일어났는지 확인
        {
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        } else {
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    @Test
    public void upgradeAllOrNothing() throws Exception {
        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }
        try {
            testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {

        }
        checkLevelUpgraded(users.get(1), false);
    }

    @Test
    public void advisorAutoProxyCreator() {
        assertThat(testUserService,
            is(java.lang.reflect.Proxy.class));
    }

    @Test
    public void readOnlyTransactionAttribute() {
        testUserService.getAll();
    }


    @Test
    @Transactional
    @Rollback(value = false)
    public void transactionSync() {
        userService.deleteAll();
        userService.add(users.get(0));
        userService.add(users.get(1));
    }

    public static void main(String[] args) {
        JUnitCore.main("test.UserServiceTest");
    }

}
