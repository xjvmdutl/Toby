package test;

import dao.UserDao;
import entity.Level;
import entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import service.UserService;
import service.UserServiceImpl;
import service.UserServiceTx;
import test.UserServiceTest.TestUserService.TestUserServiceException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplication.xml")
public class UserServiceTest {
    static class MockMailSender implements MailSender{
        private List<String> requests = new ArrayList<>();

        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage simpleMailMessage) throws MailException {
            requests.add(simpleMailMessage.getTo()[0]); //전송 요청받은 이메일 주소를 저장
        }

        @Override
        public void send(SimpleMailMessage[] simpleMailMessages) throws MailException {
        }
    }


    static class TestUserService extends UserServiceImpl {
        static class TestUserServiceException extends RuntimeException{

        }
        private String id;
        private TestUserService(String id){ //예외를 발생시킬 User 오브젝트의 id지정 가능
            this.id = id;
        }
        @Override
        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id))
                throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class MockUserDao implements UserDao{

        private List<User> users; //레벨 업그레이드 후보 // User오브젝트 목록
        private List<User> updated = new ArrayList<>(); //업그레이드 대상 오브젝트를 저장해둘 목록

        public MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return updated;
        }

        @Override
        public List<User> getAll() { //스텁 기능 제공
            return this.users;
        }

        @Override
        public void update(User user) {
            updated.add(user);
        }


        /**
         * 테스트에 사용되지 않는 메소드들
         */

        @Override
        public void add(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public User get(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getCount() {
            throw new UnsupportedOperationException();
        }


    }
    
    
    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    UserDao userDao;

    @Autowired
    MailSender mailSender;

    @Autowired
    PlatformTransactionManager transactionManager;


    List<User> users;

    @Before
    public void setup(){
        this.users = Arrays.asList(
                new User("bunjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "widn45@naver.com"),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "widn45@naver.com"),
                new User("erwins", "신승환", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1, "widn45@naver.com"),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "widn45@naver.com"),
                new User("green", "오규민", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "widn45@naver.com")
        );
    }

    @Test
    public void bean(){
        assertThat(this.userServiceImpl,is(notNullValue()));
    }


    @Test
    @DirtiesContext
    public void upgradeLevels() throws Exception{
        /*
        //직접 생성하므로서 컨테이너 설정에 관련된 RunWith와 같은 어노테이션을 지울수가 있다.
        //사용자 DB를 모두 제거하고 모두 DB에 등록하는 번거로운 작업도 필요 없다
        UserServiceImpl userServiceImpl = new UserServiceImpl(); //고립된 테스트 대상은 직접 생성하면 된다.
        
        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();  //메일 발송 여부 확인을 위한 목 오브젝트
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels(); //테스트 대상 실행

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size(), is(2));
        checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
        checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

        List<String> request = mockMailSender.getRequests();      //목오브젝트를 통한 결과 확인
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));  
        assertThat(request.get(1), is(users.get(3).getEmail()));
         */
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
    public void add(){
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithOutLevel = users.get(0);
        userWithOutLevel.setLevel(null);

        userServiceImpl.add(userWithLevel);
        userServiceImpl.add(userWithOutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithOutLevelRead = userDao.get(userWithOutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithOutLevelRead.getLevel(), is(Level.BASIC));
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if(upgraded) //업데이트가 일어났는지 확인
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        else
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
    }

    @Test
    public void upgradeAllOrNothing() throws Exception {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setMailSender(mailSender);

        UserServiceTx userServiceTx = new UserServiceTx();
        userServiceTx.setTransactionManager(transactionManager);
        userServiceTx.setUserService(testUserService);
        //testUserService.setTransactionManager(transactionManager); //수동 DI

        userDao.deleteAll();
        for(User user : users){
            userDao.add(user);
        }
        try{
            userServiceTx.upgradeLevels();
            fail("TestUserServiceException expected");
        }catch (TestUserServiceException e){

        }


        checkLevelUpgraded(users.get(1),false);
    }

    public static void main(String[] args)  {
        JUnitCore.main("test.UserServiceTest");
    }
}
