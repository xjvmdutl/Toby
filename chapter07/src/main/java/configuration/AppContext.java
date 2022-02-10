package configuration;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import dao.UserDao;
import dao.UserDaoJdbc;
import javax.sql.DataSource;
import org.h2.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import service.DummyMailSender;
import service.UserService;
import service.UserServiceImpl;
import sqlservice.OxmSqlService;
import sqlservice.SqlRegistry;
import sqlservice.SqlService;
import sqlservice.updatable.EmbeddedDbSqlRegistry;
import test.UserServiceTest.TestUserService;

@Configuration //DI설정 정보를 담은 클래스
//@ImportResource("/testApplication.xml") //Java 설정정보에서 XML 설정정보를 가져올수 있게 도와준다
@EnableTransactionManagement //XML을 전용태그 속성 어려운 문제점을 해결하고자 등장한 어노테이션 //@Enable 로 시작하는 애노테이션으로 많은 전용태그를 대체 가능하다
@ComponentScan(basePackages = {"dao", "service"}) //특정 패키지 아래에서만 찾도록 기준 설정(@Component 어노테이션이 붙은 클래스를 찾는다)
@Import(SqlServiceContext.class) //보조설정 정보를 가지고온다(Java 설정파일)
public class AppContext {
    
    //@Autowired
    //UserDao userDao;//같은 클래스내에 빈으로 등록해 메소드도 빈으로 가지고 올수 있다
    
    //@Autowired //타입으로 빈을 읽어온다
    //private SqlService sqlService;

    //@Resource //이름으로 Bean을 읽어온다
    //private SimpleDriverDataSource embeddedDatabase;

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(Driver.class);
        dataSource.setUrl("jdbc:h2:tcp://localhost/~/toby");
        dataSource.setUsername("sa");
        return dataSource;
    }


    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }
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
}
