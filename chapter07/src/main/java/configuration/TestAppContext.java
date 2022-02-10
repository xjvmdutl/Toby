package configuration;

import dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import service.DummyMailSender;
import service.UserService;
import test.UserServiceTest.TestUserService;

@Configuration
public class TestAppContext {
    @Autowired
    UserDao userDao;

    @Bean
    public UserService testUserService() {//TestUserService는 UserSErviceImpl을 상속받아 만들었으므로 userDao프로퍼티는 자동 와이어링 대상이다
       /* TestUserService testService = new TestUserService();
        testService.setUserDao(this.userDao);
        testService.setMailSender(mailSender());
        return testService;*/
        return new TestUserService();
    }
    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }

}
