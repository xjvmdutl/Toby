package test;

import dao.UserDao;
import entity.Level;
import entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.UserTransactionAdapter;
import service.UserService;
import test.UserServiceTest.TestUserService.TestUserServiceException;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static service.UserService.MIN_RECOMMEND_FOR_GOLD;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application.xml")
public class UserServiceTest {

    static class TestUserService extends UserService{ //테스트용 클래스임으로 내부 클래스로 만든다.
        static class TestUserServiceException extends RuntimeException{

        }
        private String id;
        private TestUserService(String id){ //예외를 발생시킬 User 오브젝트의 id지정 가능
            this.id = id;
        }
        @Override
        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id))  //지정한 Id의 user 오브젝트가 발견된면 예외를 발생시켜 작업 중지
                throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    @Autowired
    PlatformTransactionManager transactionManager;

    /*
    @Autowired
    DataSource dataSource;
    */

    List<User> users;

    @Before
    public void setup(){
        this.users = Arrays.asList(
                new User("bunjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("erwins", "신승환", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
                new User("green", "오규민", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    public void bean(){
        //빈의 주입을 확인하는 테스트
        assertThat(this.userService,is(notNullValue()));
    }


    @Test
    public void upgradeLevels() throws Exception{
        userDao.deleteAll();
        for(User user : users)
            userDao.add(user);

        userService.upgradeLevels();
        /*
        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);
        */
        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);
    }

    @Test
    public void add(){
        userDao.deleteAll();

        User userWithLevel = users.get(4); //Level이 이미 지정되면 초기화 X
        User userWithOutLevel = users.get(0);
        userWithOutLevel.setLevel(null); //Level이 지정되지 않았다

        userService.add(userWithLevel);
        userService.add(userWithOutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());  //DB에 저장된 값 가지고 온다.
        User userWithOutLevelRead = userDao.get(userWithOutLevel.getId()); //DB에 저장된 값 가지고 온다.

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithOutLevelRead.getLevel(), is(Level.BASIC));
    }
    /*
    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));
    }
    */
    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if(upgraded) //업데이트가 일어났는지 확인
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        else
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
    }

    @Test
    public void upgradeAllOrNothing() throws Exception {
        UserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        //testUserService.setDataSource(this.dataSource);
        testUserService.setTransactionManager(transactionManager); //수동 DI
        userDao.deleteAll();
        for(User user : users){
            userDao.add(user);
        }
        try{
            testUserService.upgradeLevels();
            fail("TestUserServiceException expected"); //업그레이드중에 예외가 발생하지 않았을경우 fail을 타서 오류가 발생하게 된다.
        }catch (TestUserServiceException e){

        }


        checkLevelUpgraded(users.get(1),false); //예외가 발생하였는데 값이 변경되었나 확인
    }

    public static void main(String[] args)  {
        JUnitCore.main("test.UserServiceTest");
    }
}
