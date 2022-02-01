package sqlservice;

public class SqlRetrievalFailureException extends RuntimeException{ //SQL 키를 가지고 오다 실패할 경우 해당 예외를 던진다
    public SqlRetrievalFailureException(String message) {
        super(message);
    }
    public SqlRetrievalFailureException(String message, Throwable cause) { //중첩예외를 저장할 수 있는 생성자
        super(message, cause);
    }
}
