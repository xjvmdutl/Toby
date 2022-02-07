package configuration;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration //DI설정 정보를 담은 클래스
@ImportResource("/testApplication.xml") //Java 설정정보에서 XML 설정정보를 가져올수 있게 도와준다
public class TestApplicationContext {


}
