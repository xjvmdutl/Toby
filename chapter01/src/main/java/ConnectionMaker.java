import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionMaker {
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException;
    //기존 SimpleConnectionMaker는 Dao와 너무 밀접한 관계를 가졌으므로 인터페이스를 통해 관계를 멀리 떨어트린다.
    //기능만 존재하고 해당 기능을 구현하는것은 이걸 구현받은 객체에서 해야된다.

}
