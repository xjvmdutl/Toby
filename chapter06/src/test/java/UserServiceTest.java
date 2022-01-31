import dao.UserDao;
import entity.Level;
import entity.User;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import service.UserService;
import service.UserServiceImpl;

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
@TransactionConfiguration(defaultRollback = false) //트랜잭션 매니져 빈을 지목하거나 롤백 여부를 설정할 때 사용하는 어노테이션
public class UserServiceTest {

    static class MockMailSender implements MailSender {

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

    static class TestUserServiceException extends RuntimeException {

    }

    static class TestUserService extends UserServiceImpl { //포인트컷의 클래스 필터에 선정되도록 이름 변경

        private String id = "madnite1";

        /* private TestUserServiceImpl(String id){ //예외를 발생시킬 User 오브젝트의 id지정 가능
         }*/
        @Override
        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            super.upgradeLevel(user);
        }

        @Override
        public List<User> getAll() {
            for (User user : super.getAll()) {// 읽기 전용으로 동작하는지 학습테스트를 진행한다.
                super.update(user); //강제로 쓰기동작을 시킨다.(읽기전용이므로 에러 발생)
            }
            return null; //별 의미없는 값이다
        }
    }

    static class MockUserDao implements UserDao {

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
    UserService userService;

    @Autowired
    UserService testUserService;

    @Autowired
    UserDao userDao;

    @Autowired
    MailSender mailSender;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    ApplicationContext context; //팩토리 빈을 가지고 오기위해 선언

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
    // @DirtiesContext //Context 무효화 어노테이션
    public void upgradeAllOrNothing() throws Exception {
     /*   TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setMailSender(mailSender);*/
        /*
        UserServiceTx userServiceTx = new UserServiceTx();
        userServiceTx.setTransactionManager(transactionManager);
        userServiceTx.setUserService(testUserService);
        //testUserService.setTransactionManager(transactionManager); //수동 DI
        */
        /*
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(testUserService);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern("upgradeLevels");
        UserService txUserService = (UserService) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[]{UserService.class}, txHandler
        );
         */
        /**
         *  장점과 한계 : 부가기능을 가진 프록시를 생성하는 팩토리 빈을 만들어 두면 타깃을 타입에 상관없이 재사용이 가능하다.
         *  다미나믹 프록시로 인터페이스를 구현한 프록시 클래스를 일일이 만들어야하는 번거로움을 해결하였고, 하나의 핸들러 메소드를 구현하는 것만으로
         *  많은 메소드에 부가기능을 제공할 수 있으므로 부가기능 코드의 중복문제도 해결하였다.
         *  단, 만약 target 클래스가 여러개라면, 이를 설정하는 설정파일의 코드도 기하급수적으로 늘어난다(문제점1)
         *  TransactionHandler 오브젝트가 프로시 펙토리 빈 갯수만큼 만들어 지는 문제점도 존재한다(문제점2)
         *      트랜잭션 부가기능을 제공하는 동일한 코드임에도 타깃오브젝트가 달라지면 새로운 transactionHandler 오브젝트를 만들어야 한다.
         *
         */
        /*
        TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class);
        txProxyFactoryBean.setTarget(testUserService); //테스트용 타깃 주입
        UserService txUserService = (UserService) txProxyFactoryBean.getObject();
         */
        /*ProxyFactoryBean txProxyFactoryBean =
                context.getBean("&userService", ProxyFactoryBean.class);
        txProxyFactoryBean.setTarget(testUserService);

        UserService txUserService = (UserService)txProxyFactoryBean.getObject();
*/
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
            is(java.lang.reflect.Proxy.class)); //프록시로 구현된 클래스는 Proxy 클래스의 하위클래스 이기 떄문에 성공해야한다.
    }

    @Test //어떤 예외가 발생할지 모르므로 일단 진행
    public void readOnlyTransactionAttribute() {
        testUserService.getAll(); //트랜잭션 속성이 제대로 적용이 되었다면 여기서 읽기 전용 속성을 위배하였으로 예외가 발생해야 한다
        //https://github.com/scratchstudio/toby-spring/issues/7
        //H2 DB에서 에러가 발생하지 않아서 검색해본 결과 H2 DB에서는 readOnly 설정이 되어있어도 update시 예외가 발생하지 않고 성공한다.
    }


   /* @Test
    public void transactionSync(){

        //각 메소드는 독립적인 트랜잭션에서 수행된다
        //기존에 진행중인 트랜잭션이 없고 트랜잭션 전파 속성이 Required 로 새로운 트랜잭션이 시작된다.
        //3개의 트랜잭션을 하나로 통합할 수는 없을까? 세개의 메소드 모두 전파속성이 Required이므로 메소드가 호출되기 전에 트랜잭션만 시작하게만 하면 가능하다.
        //userService의 메소드를 호출하기 전에 트랜잭션을 미리 시작해 주면 메소드를 새로 만들지 않더라도 3개의 트랜잭션을 묶을 수 있다
        //DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
        //txDefinition.setReadOnly(true); //읽기 전용으로 바꾼다(이 뒤에 실행되는 것은 무시된다)
        //TransactionStatus txStatus = transactionManager.getTransaction(txDefinition); //트랜잭션 매니저에게 트랜잭션을 요청한다.
        //기존에 실행된 트랜잭션이 없으니 새로운 트랜잭션을 시작 시키고 트랜잭션 정보를 돌려준다. 또한 다른곳 에서 트랜잭션을 사용할 수 있도록 동기화 한다

        //userService.deleteAll(); //새로운 트랜잭션이 만들어 졌으므로 읽기 전용은 실패해야된다
        *//*
        userDao.deleteAll(); //jdbcTemplate를 통해 이미 시작된 트랜잭션이 있다면 자동으로 참여
        //동일한 결과를 얻는다
        assertThat(userDao.getCount(), is(0));

        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);

        userService.add(users.get(0));
        userService.add(users.get(1));
        assertThat(userDao.getCount(), is(2));

        transactionManager.rollback(txStatus); //강제로 롤백한다

        assertThat(userDao.getCount(), is(0));
         *//*
     *//**
     * 롤백 테스트 : 테스트 내의 모든 DB작업은 하나의 트랜잭션 안에서 동작하게 하고 끝나면 반드시 롤백해버리는 테스트
     * 장점 : DB에 영향을 주지 않기에 장점이 많다.
     * DB에 쓰기 작업을 하는 테스트 같은경우 데이터가 변경될 수가 있기때문에 테스트를 할 때만다 테스트 데이터를 초기화 하는 번거로움이 있지만
     * 롤백 테스트를 진행하면 DB에 영향을 주지 않기때문에 유용하다
     * 해당 테스트도 실제 User 테이블에 데이터가 있더라도 deleteAll을 사용하여 초기 데이터를 지우고 시작하기 때문에 User에 초기 데이터를 넣어도 의미가 없다
     * 그러나 롤백테스트를 진행하게 되면 해당 메소드를 호출 하기 전의 상태와 테스트를 성공/실패 하여도 rollback을 하여 데이터가 같음을 보장해준다
     *//*
        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);

        try{
            userService.deleteAll();
            userService.add(users.get(0));
            userService.add(users.get(1));
        }finally {
            transactionManager.rollback(txStatus); //하나의 트랜잭션으로 통합한 뒤 반드시 끝나면 롤백한다
        }
    }*/

    /**
     * 테스트 메소드 내에서의 @Transactional 을 사용할 수 있다(손쉽게 경계설정이 가능) 트랜잭션이 필요하지 않는 메소드일 경우
     * @NotTransactional을 선언하면 된다(적용되어도 상관 없다면 무시 하여도 된다) 트랜잭션을 전파속성을 NEVER로 설정하여도 똑같이
     * 적용된다(Spring3.0 에서 부터 @NotTransactional 이 제거대상이 되었기에 사용을 지향하지 않는다) 스프링 개발자들은 비트랜잭션 테스트와 트랜잭션
     * 테스트는 클래스를 별도로 만들어 테스트하기를 권장한다 통합 테스트 - 단위테스트는 별도의 클래스를 분리하여 테스트를 진행하는데 DB를 사용하는 통합테스트는 롤백테스트로
     * 만드는 것을 권한다.(독립적이고 자동화된 테스트를 만들기 쉽기때문이다)
     */
    @Test
    @Transactional //테스트용 트랜잭션은 테스트가 끝나면 자동으로 Rollback된다.(강제 Rollback을 원치 않을경우는?? @Rollback을 사용한다)
    @Rollback(value = false)
    //예외가 발생하지 않는 한 커밋된다 //메소드 레벨에만 적용 가능하다 //만약 전체 메소드에 커밋을 원한다면 @TransactionConfiguration(defaultRollback=false)를 주면 된다
    public void transactionSync() {
        userService.deleteAll();
        userService.add(users.get(0));
        userService.add(users.get(1));
    }

    public static void main(String[] args) {
        JUnitCore.main("UserServiceTest");
    }

    /**
     * Transacnal 어노테이션
     @Target({ElementType.METHOD, ElementType.TYPE}) //어노테이션을 사용할 대상을 지정한다, (method, type->클래스,인터페이스) 를 지정할 수 있다
     @Retention(RetentionPolicy.RUNTIME) //어노테이션 정보가 언제까지 유지되는지를 지정(현재는 Runtime)
     @Inherited //상속을 통해서도 어노테이션 정보를 얻을 수 있다
     @Documented public @interface Transactional {
     String value() default "";

     Propagation propagation() default Propagation.REQUIRED;

     Isolation isolation() default Isolation.DEFAULT;

     int timeout() default -1;

     boolean readOnly() default false;

     Class<? extends Throwable>[] rollbackFor() default {};

     String[] rollbackForClassName() default {};

     Class<? extends Throwable>[] noRollbackFor() default {};

     String[] noRollbackForClassName() default {};
     }
     */

}
