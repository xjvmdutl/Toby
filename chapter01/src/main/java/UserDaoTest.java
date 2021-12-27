import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        /*
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
        */
        /*
        DaoFactory daoFactory = new DaoFactory();
        UserDao dao1 = daoFactory.userDao();
        UserDao dao2 = daoFactory.userDao();
        //동일한 객체인가? -> 결과는 다른 오브젝트를 반환한다.
        System.out.println(dao1);
        System.out.println(dao2);
        */
        /*
        ApplicationContext context = new GenericXmlApplicationContext("application.xml");
        //ClassPathXmlApplicationContext() 로 클래스 경로로 가지고 올수도 있다.

        UserDao dao3 = context.getBean("userDao",UserDao.class);
        UserDao dao4 = context.getBean("userDao",UserDao.class);
        //동일한 객체인가? -> 동일한 객체 , 하나의 인스턴스
        System.out.println(dao3);
        System.out.println(dao4);
         */
        //스프링은 기본적으로 별다른 설정을 하지 않을 경우 내부에서 생성하는 빈 오브젝트를 싱글톤으로 만든다.
        //싱글톤 : 애플리케이션안에 제한된 수, 대개 1개의 오브젝트만 만들어서 사용
        //ApplicationContext context = new AnnotationConfigApplicationContext(DataFactory.class);
        ApplicationContext context = new GenericXmlApplicationContext("application.xml");
        UserDao dao = context.getBean("userDao",UserDao.class);
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
        /**
         * 자바의 싱글톤 구현
         * 1. 클래스 밖에서는 오브젝트를 생성하지 못하도록 생성자를 private
         * 2. 생성된 싱글톤 오브젝트를 저장할 수 있는 자신과 같은 타입의 스태틱 필드를 정의
         * 3. 스태틱 팩토리 메소드인 getInstance()를 만들고 이 메소드가 최초 호출되는 시점에서 한번만 오브젝트가 만들어지도록 한다.
         * 4. 오브젝트가 만들어 진뒤, getInstance() ㅔ소드를 통해 이미 만들어진 스태틱 필드에 저장된 오브젝트를 리턴한다.
         */


    }
}
