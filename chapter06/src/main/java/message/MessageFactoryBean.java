package message;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {

    String text;
    public void setText(String text) {
        this.text = text; //오브젝트를 생성할때 필요한 정보를 팩토리 빈의 프로퍼티로 설정해 대신 DI를 받을 수 있게 한다.
    }

    @Override
    public Message getObject() throws Exception {
        return Message.newMessage(this.text);   // 실제 빈으로 사용될 오브젝트를 직접 생성한다.
                                                // 코드를 사용하기 때문에 복잡한 방식의 오브젝트 생성과 초기화 작업도 가능하다.
    }

    @Override
    public Class<?> getObjectType() {
        return Message.class;
    }

    @Override
    public boolean isSingleton() {
        return false; //이것은 팩토리 빈의 동작 방식에 관한 설정이고, 만들어진 빈 오브젝트는 싱그론으로 스프링이 관리해줄수 있다.
    }
}
