package strategy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        //모든 dao에서 사용 가능하지 분리 하여 보자
        /**
         * try/catch/finally 문법 사용
         */
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = dataSource.getConnection();

            //ps = makeStatement(c);//해당 위치에서 에러 발생시 메소드 실행이 중지, //변하는 부분
            //1.변하는 부분만 메소드로 빼기
            //2.템플릿메소드 패턴의 적용 : 변하지 않는 부분은 슈퍼클래스에 두고 변하는 부분은 추상메소드로 정의해서 서브클래스에서 오버라이드 하여 새롭게 정의해 쓰도록 한다
            //메소드를 만들때마다 서브 클래스를 만들어서 사용해야 한다
            //3.전략패턴의 적용 : 오브젝트를 둘로 분리하고 클래스 레벨에서는 인터페이스를 통해서만 의존하도록 만드는 전략패턴
            //StatementStrategy strategy = new DeleteAllStatement(); //Strategy 가 인터페이스 뿐만 아니라 특정 구현 클래스 DeleteAllStatement까지 알고 있는게 이상하다
            //ps = strategy.makeStatement(c);
            ps = stmt.makePrepareStatement(c);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally { //무조건 반환이 되도록
            if (ps != null) {//해당 리소스들이 제대로 반환되지 않을 수 있다.
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                }
            }
        }
    }
    public void execute(final String query) throws SQLException{
        workWithStatementStrategy(
            new StatementStrategy() {
                @Override
                public PreparedStatement makePrepareStatement(Connection c) throws SQLException {
                    return c.prepareStatement(query);
                }
        });
    }
}
