package connectionMaker;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionMaker {
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException;
    //기존 SimpleConnectionMaker는 Dao와 너무 밀접한 관계를 가졌으므로 인터페이스를 통해 관계를 멀리 떨어트린다.
    //기능만 존재하고 해당 기능을 구현하는것은 이걸 구현받은 객체에서 해야된다.

    //connectionMaker와 UserDao는 직접적인 연관관계를 맺고 있으므로 해당 인터페이스가 바뀌면 userDao도 변경되어야 한다.
    //단, 해당 인터페이스를 구현한 클래스가 변경되는것은 userDao와 연관관계를 맺고있지않으므로 영향을 받지 않는다.
    //인터페이스를 통해 의존관계를 제한한다면 그만큼 변경에 자유로워 지는 것이다.
    //런타임 의존관계 주입의 핵심은 설계시점에는 알지 못했던 두 오브젝트의 관계를 맺도록 도와주는 3자가 존재한다는 것이다.


}
