package test;

import dao.UserDao;
import entity.User;
import org.h2.jdbc.JdbcSQLException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserDaoTest {
    
    private UserDao dao;//로컬 변수였던 dao를 테스트 메소드에서 접근 하능하도록 인스턴스 변수로 변경
    private User user1; //반복 사용되는 변수를 인스턴스 변수로 선언해 둔다.
    private User user2;
    private User user3;

    //JUnit에서는 반복되는 준비작업을 별도의 메소드에 넣게 해주고, 매번 테스트 메소드를 실행하기전에 먼저 실행해주는 기능이 있다.
    /**
     * Junit이 하나의 테스트 클래스를 가져와 테스트를 수행하는 방식
     * 1.테스트 클래스에서 @Test 가 붙은 public 이고 void 형이며 파라미터가 없는 테스트 메소드를 찾는다.
     * 2. 테스트 클래스의 오브젝트를 만든다.
     * 3. @Before가 붙은 메소드가 있으면 실행한다.
     * 4. @Test가 붙은 메소드를 하나 호출하고 테스트 결과를 저장해 둔다.
     * 5. @After가 붙은 메소드가 있으면 실행한다.
     * 6.나머지 테스트 메소드에 대해 2~5을 반복한다.
     * 7.모든 테스트의 결과를 종합해서 돌려준다.
     */
    @Before
    public void setUp(){
        ApplicationContext context = new GenericXmlApplicationContext("application.xml");
        dao = context.getBean("userDao",UserDao.class);
        user1 = new User("gyumee","박성철","springno1");
        user2 = new User("leegw700","이길원","springno2");
        user3 = new User("bumjin","박범진","springno3");
    }

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
        //코드에 변경 사항이 없다면 테스트는 항상 동일한 결과를 내야한다.
        //단위 테스트는 항상 일관성있는 테스트 결과를 반환함을 잊지 말자
        //given


        //count에 대한 검증이 되지 않았기에 add를 하면 1 delete를 하면 0을 리턴하는지 확인한다.
        dao.deleteAll();
        assertThat(dao.getCount(),is(0));



        //when

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(),is(2));

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
        User userGet1 = dao.get(user1.getId());
        assertThat(userGet1.getName(),is(user1.getName())); //첫번째 파라미터의 뒤에 나오는 매처 조건으로 비교해서 일치하면 다음으로 넘어가고,아니면 실패하도록 넘어간다.
        assertThat(userGet1.getPassword(),is(user1.getPassword()));
        User userGet2 = dao.get(user2.getId());
        assertThat(userGet2.getName(),is(user2.getName()));
        assertThat(userGet2.getPassword(),is(user2.getPassword()));
    }


    @Test
    public void count() throws SQLException{
        //모든 테스트는 실행 순서에 상관없이 독립적으로 동작 하게 작성해야 한다.


        dao.deleteAll();
        assertThat(dao.getCount(),is(0));

        dao.add(user1);
        assertThat(dao.getCount(),is(1));

        dao.add(user2);
        assertThat(dao.getCount(),is(2));

        dao.add(user3);
        assertThat(dao.getCount(),is(3));
    }


    @Test(expected = IllegalArgumentException.class)//에러 테스트
    public void getUserFailure() throws SQLException{
        //테스트를 먼저 작성한 뒤, 기능을 작성하였다.
        //추가하고 싶은 기능을 테스트 코드로 표현해서 작성한뒤, 기능을 구현하면 좋다.
        //TDD : 테스트 코드를 먼저 만들고, 테스트를 성공하게 해주는 코드를 작성하는 방식의 개발


        dao.deleteAll();
        assertThat(dao.getCount(),is(0));

        dao.get("unknown_id"); //없는 id 조회, 예외를 발생시켜 테스트를 한다.
    }
}
