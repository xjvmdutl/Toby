package test;

import message.Message;
import message.MessageFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations = "/FactoryBeanTest-context.xml")//설정파일 지정 안하면 클래스명 + -context.xml 이 지정된다
public class FactoryBeanTest {
    @Autowired
    ApplicationContext context;

    @Test
    public void getMessageFromFactoryBean(){

        /*
        Object message = context.getBean("message");
        assertThat(message, is(Message.class));
        assertThat(((Message)message).getText(), is("Factory Bean"));
         */
        Object factory = context.getBean("&message");//펙토리 빈 자체를 돌려준다
        assertThat(factory, is(MessageFactoryBean.class));
    }
}
