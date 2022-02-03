package sqlservice;

public interface SqlRegistry {
    void registerSql(String key, String sql); // SQL을 키와 함께 등록한다
    String findSql(String key) throws SqlNotFoundException; //키로 SQL을 검색한다
}
