import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration//에플리케이션 컨택스트, 빈 팩토리가 사용할 설정정보
public class DaoFactory {

    @Bean //오브젝트를 생성을 담당하는 IOC 매소드라는 표시
    public UserDao userDao(){

        //return new UserDao(connectionMaker());
        UserDao userDao = new UserDao();
        userDao.setConnectionMaker(connectionMaker());
        return userDao;
    }
    @Bean
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }
}
