import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        //ConnectionMaker connectionMaker = new DConnectionMaker();  //생성하는 것은 클라이언트의 책임
        //UserDao dao = new DaoFactory().userDao();
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class); //어플리케이션 컨택스트
        //어플리케이션에서 Ioc를 적용해서 관리할 모든 오브젝트에 대한 생성과 관계설정을 담당(설정 정보를 통해 얻는다->@Configuration)
        UserDao dao = context.getBean("userDao",UserDao.class); //ApplicationContext 에 등록된 빈의 이름을 가지고 온다.
        //자바 5이상부터 제네릭 메소드 방식이 추가되어 두번쨰 파라밑터에 리턴타입을 주어 캐스팅 없이 사용 가능
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

    }
}
