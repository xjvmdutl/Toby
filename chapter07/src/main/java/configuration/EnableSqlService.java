package configuration;

import org.springframework.context.annotation.Import;

@Import(value = SqlServiceContext.class) //SQL 서비스를 사용하겠다는 의미
public @interface EnableSqlService {

}
