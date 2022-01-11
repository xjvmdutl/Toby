package exception;

public class DuplicateUserIdException extends RuntimeException{
    public DuplicateUserIdException(Throwable cause){
        //중첩예외를 만들수 있도록 생성자 추가
        super(cause);
    }
}
