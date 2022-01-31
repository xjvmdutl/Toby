package test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import message.MessageFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/FactoryBeanTest-context.xml")
public class FactoryBeanTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void getMessageFromFactoryBean() {

        Object factory = context.getBean("&message");//펙토리 빈 자체를 돌려준다
        assertThat(factory, is(MessageFactoryBean.class));
    }
}
