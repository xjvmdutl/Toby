package test;

import dao.UserDao;
import entity.User;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException { //테스트가 main()메소드로 만들어 졌다는건 제어권을 직접 갖는다는 의미이다.
        /**
         * 웹 MVC에서 일반적으로 폼을 만들고 눌러서 테스트하는 방식은 해당 테스트에 직접적으로 연관을 주는 요소가 너무 많다.
         * 테스트하고자 하는 대상이 명확하다면 그 대상에만 집중해서 테스트를 해야한다.
         * 지금과 같은 UserDaoTest 는 한가지 관심에 집중할 수 있게 만들어진 단위 테스트이다.
         * 통제할 수 없는 외부의 리소스에 의존하는 테스트는 단위 테스트가 아니라고 본다.
         * 단위테스트로 이미 검증된 테스트를 통합테스트를 통해 검증을 하게 된다면 이는 단위테스트를 검증하지 않은체 통합테스트를 진행한 코드보다 오류수정이 훨씬 간결하다.
         * 만약 개발자가 테스트를 진행하기위해 매번 테스트 데이터를 넣고, 수정하고 생성하면 지루하기 짝이 없을 것이다.
         * 그렇기에 테스트는 자동으로 실행되도록 작성하는것이 좋다
         * 테스트를 작성하여 지속적인 개선과 점진적으로 확장하여 개발해야한다.
         */
        /**
         * UserDaoTester 문제점
         * 1. 수동확인 작업을 번거로움 : 값이 일치하는지를 테스트가 확인하는 것이 아닌 콘솔에 찍힌 값을 가지고 개발자가 비교해 주어야한다.
         * 2. 실행 작업의 번거로움 : 매번 메인메소드를 통해 실행하므로 무수히 많은 메인코드가 있다면 이를 모두 실행시켜 주어야한다.
         */

        JUnitCore.main("test.UserDaoTest");
    }

    @Test
    public void addAndGet() throws SQLException{
        //모든 테스트는 성공 / 실패(에러 , 기대값 다름) 으로 결과를 가진디
        //JUnit은 프레임 워크로 자바로 단위테스트를 작성할 때 유용하게 사용 가능하다.
        //Junit 2가지 조건 : 1. 메소드 public 선언, 2. @Test 어노테이션이 붙어야한다.

        ApplicationContext context = new GenericXmlApplicationContext("application.xml");
        UserDao dao = context.getBean("userDao",UserDao.class);
        User user = new User();
        user.setId("gyumee");
        user.setName("박성철");
        user.setPassword("springno1");

        dao.add(user);
        System.out.println(user.getId() + " 등록 성공");
        User user2 = dao.get(user.getId());
        //name,password 어떤 필드 떄문에 성공 실패했는지 알수 있다.
        /*
        if(!user.getName().equals(user2.getName())){
            System.out.println("테스트 실패 (name)");
        }else if(!user.getPassword().equals(user2.getPassword())){
            System.out.println("테스트 실패 (password)");
        }else{
            System.out.println("조회 테스트 성공");
        }
         */
        assertThat(user2.getName(),is(user.getName())); //첫번째 파라미터의 뒤에 나오는 매처 조건으로 비교해서 일치하면 다음으로 넘어가고,아니면 실패하도록 넘어간다.ㄴ 
        assertThat(user2.getPassword(),is(user.getPassword()));
    }
}
