package service;

import entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional //<tx:method name="" /> 과 같은 효과, 기본 속성으로 적용
public interface UserService {
    void add(User user);
    @Transactional(readOnly = true)//<tx:method name="" readOnly="true" /> 과 같은 효과
    User get(String id);  //신규 메소드 추가 , dao 와 상응하는 CRUD 메소드이지만 add처럼 단순 위임 이상 로직을 가질 수 있기 때문에 서비스 계층을 통해 접근하여야 한다
    @Transactional(readOnly = true)
    List<User> getAll();
    void deleteAll();
    void update(User user);  
    
    void upgradeLevels();
}
