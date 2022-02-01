package sqlservice;

import java.util.Map;

public class SimpleSqlService implements SqlService {

    /**
     * 이전 코드와의 차이점 : 이제 UserDao를 포함한 모든 DAO는 SQL을 어디에 저장해두고 가지고 오는지에 대해서는 전혀 관심이 없어진다
     * 또한 sqlService 빈에는 Dao에는 전혀 영향을 주지 않은 채로 다양한 방법으로 구현된 SqlService 타입 클래스를 적용할 수가 있다
     * XML 파일 정보 읽기
     * JAXB(java Architecture for XML Binding) : XML 문서정보를 전통적인 XML API와 동일한 구조의 오브젝트로 직접 매핑해 준다
     * XML정보를 그대로 담고 있는 오브젝트 트리구조로 만들어 주기 떄문에 XML 정보를 오브젝트처럼 다룰수 있어 편리하다(스키마 컴파일러를 통해 자동생성된 오브젝트에는 매필정보가 어노테이션으로 등록되어 있는데 해당 정보를 이용하여 XML과 메핑된 오브젝트 트리사이의 자동변환 작업을 수행해준다)
     */
    private Map<String, String> sqlMap; //설정파일에 Map으로 정의된 SQL 정보를 가져오도록 프로퍼티로 등록

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key); //내부 SQLMap 에서 읽어 온다
        if (sql == null) {
            throw new SqlRetrievalFailureException(key); //인터페이스에 정의된 규약대로 SQL 가져오는데 실패한다면 예외 발생
        } else {
            return sql;
        }
    }
}
