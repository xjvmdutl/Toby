package sqlservice;

public interface SqlReader {
    void read(SqlRegistry sqlRegistry); //SQL을 외부에서 가져와 SqlRegistry에 등록한다.
}
