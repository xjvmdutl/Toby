import connectionMaker.CountingConnectionMaker;
import dao.UserDaoJdbc;
import entity.User;
import factory.CountingDaoFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class UserDaoConnectionCountingTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
        UserDaoJdbc dao = context.getBean("userDao", UserDaoJdbc.class);

        User user = new User();
        user.setId("whiteShip");
        user.setName("백기선");
        user.setPassword("married");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());
        System.out.println(user2.getId() + " 조회 성공");

        CountingConnectionMaker ccm = context.getBean("connectionMaker",CountingConnectionMaker.class);
        System.out.println("Connection counter : " + ccm.getCounter());
    }
}
