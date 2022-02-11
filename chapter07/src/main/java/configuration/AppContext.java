package configuration;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import configuration.AppContext.ProductionAppContext;
import configuration.AppContext.TestAppContext;
import dao.UserDao;
import javax.sql.DataSource;
import org.h2.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import service.DummyMailSender;
import service.UserService;

import test.UserServiceTest.TestUserService;


@Configuration //DI설정 정보를 담은 클래스
//@ImportResource("/testApplication.xml") //Java 설정정보에서 XML 설정정보를 가져올수 있게 도와준다
@EnableTransactionManagement //XML을 전용태그 속성 어려운 문제점을 해결하고자 등장한 어노테이션 //@Enable 로 시작하는 애노테이션으로 많은 전용태그를 대체 가능하다
@ComponentScan(basePackages = {"dao", "service"}) //특정 패키지 아래에서만 찾도록 기준 설정(@Component 어노테이션이 붙은 클래스를 찾는다)
//@Import({SqlServiceContext.class, TestAppContext.class, ProductionAppContext.class}) //보조설정 정보를 가지고온다(Java 설정파일)
//@Import(SqlServiceContext.class) //내부 클래스로 설정하게 된다면 기존에 설정했던 두개의 파일을 불러오는 부분을 제거할 수 있다 //스프링이 자동으로 포함시켜주기 떄문
@EnableSqlService //SqlService를 사용하겠다는 뜻
@PropertySource("/database.properties") //프로퍼티 파일을 읽도록 스프링프레임워크가 지원한다 //Environment 타입의 환경변수 오브젝트에 저장된다
public class AppContext implements SqlMapConfig{
    //@Autowired
    //Environment env;

    @Value("${db.driverClass}") Class<? extends Driver> driverClass; //타입변환이 필요한 프로퍼티를 스프링이 알아서 처리해준다
    @Value("${db.url}") String url;
    @Value("${db.username}") String username;

    //@Autowired
    //UserDao userDao;//같은 클래스내에 빈으로 등록해 메소드도 빈으로 가지고 올수 있다
    
    //@Autowired //타입으로 빈을 읽어온다
    //private SqlService sqlService;

    //@Resource //이름으로 Bean을 읽어온다
    //private SimpleDriverDataSource embeddedDatabase;

    @Bean
    public DataSource dataSource() {
        /*
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//        dataSource.setDriverClass(Driver.class);
        try {
            dataSource.setDriverClass((Class<? extends java.sql.Driver>) Class.forName(env.getProperty("db.driverClass"))); //드라이버 클래스 가지고 오기
            //클래스 타입을 오브젝트를 넣어야되기 떄문에 문제가 있다
        }catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        */
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(this.driverClass);
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        return dataSource;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer(){
        //@Value 치환자를 이용해 프로퍼티 값을 필드에 주입하기 위해서는 해당 빈을 선언해 주어야 한다
        //빈후 처리기로 사용되는 빈을 정의해 주것이다(반드시 static 메소드로 선언이 되어야 한다)
        return new PropertySourcesPlaceholderConfigurer();
    }


    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Override
    public Resource getSqlMapResource() {
        //AppContext가 직접 SqlMapConfig를 인터페이스로 구현받아 리턴값을 정의해 준다면 번거롭게 UserSqlMapConfig 파일을 생성할 필요가 없으며 빈으로 만들기 위해 sqlMapConfig() 메소드를 만들 필요도 없다
        return new ClassPathResource("sqlmap.xml", UserDao.class);
    }

    /*
    @Bean
    public SqlMapConfig sqlMapConfig(){
        return new UserSqlMapConfig();
    }
     */



    /*
    @Bean
    public UserDao userDao() {
        *//*
        UserDaoJdbc dao = new UserDaoJdbc();
        dao.setDataSource(dataSource());
        dao.setSqlService(this.sqlService); //XML에 있는 빈을 Java 설정 파일에서 사용할때 @Autowired 를 사용해서 주입받아 쓴다
        return dao;
         *//*
        return new UserDaoJdbc();
    }*/

   /*
   @Bean
    public UserService userService() {
        UserServiceImpl service = new UserServiceImpl();
        service.setUserDao(this.userDao);
        service.setMailSender(mailSender());
        return service;
    }
    */

 /*   @Bean
    public UserService testUserService() {
        TestUserService testService = new TestUserService();
        testService.setUserDao(this.userDao);
        testService.setMailSender(mailSender());
        return testService;
    }*/

/*
    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }
*/

    /*@Bean
    public SqlService sqlService() {
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());
        return sqlService;
    }

    @Bean
    public SqlRegistry sqlRegistry() {
        EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
        //sqlRegistry.setDataSource(this.embeddedDatabase);
        sqlRegistry.setDataSource(embeddedDatabase());
        return sqlRegistry;
    }

    @Bean
    public Unmarshaller unmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("sqlservice.jaxb");
        return marshaller;
    }

    @Bean
    public DataSource embeddedDatabase() {
        return new EmbeddedDatabaseBuilder()
            .setName("embeddedDatabase")
            .setType(H2)
            .addScript(
                "classpath:/sqlRegistrySchema.sql"
            )
            .build();
    }*/

    @Configuration
    @Profile("prodution")
    public static class ProductionAppContext{

    }


    @Configuration
    @Profile("test")
    public static class TestAppContext {
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
}
