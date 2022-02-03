package sqlservice;

public class DefaultSqlService extends BaseSqlService{
    //외부에서 DI받지 않을경우 기본적으로 자동적용되는 의존관계를 디폴트의존관계라고 한다
    public DefaultSqlService(){ //생성자에서 디폴트 의존 오브젝트를 직접 만들어서 스스로 DI 해준다
        setSqlReader(new JaxbXmlSqlReader());
        setSqlRegistry(new HashMapSqlRegistry());
        //테스트시 실패한다 ? JaxbXmlSqlReader의 sqlmapFile 프로퍼티가 비어있기 떄문이다
        //해결방법 sqlmapFile을 DefaultSqlService의 프로퍼티로 정의하는 방법-> 적절하지 않다 : JaxbXmlSqlReader는 디폴트 의존 오브젝트에 불과하기 떄문이다
        //디폴트 의존 오브젝트는 설정이 있다면 디폴트는 무시되어야 하므로 반드시 필요하지 않는 sqlmapfile을 프로퍼티로 등록해 두는건 바람직 하지 않다.
        //디폴트 의존 오브젝트에 디폴트값을 설정하므로 해결가능
        
        //해당 생성자에서 일단 디폴트 의존 오브젝트를 무조건 다 만들어 버린다는 문제가있다(사용하지 않는 오브젝트가 만들어지는 꺼림직함이 있다)
        //이럴경우 @PostConstruct에서 프로퍼티가 설정되었는지를 확인후 오브젝트를 생성하거나 생성하지 않거나 해서 해결이 가능하다

    }
}
