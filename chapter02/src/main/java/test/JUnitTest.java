package test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.either;
import static org.junit.matchers.JUnitMatchers.hasItem;

//스프링 테스트 컨택스트 테스트
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/junit.xml")
public class JUnitTest {
    //실제 JUnit이 테스트 오브잭트를 메소드를 수행할때마다 만드는지 테스트

    @Autowired
    ApplicationContext context;//테스트 컨택스트가 매번 주입해주는 애플리케이션 컨택스트는 항상 같은 오브젝트인지 확인해보자

    static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();

    static ApplicationContext contextObject = null;
    
    
    @Test
    public void test1(){
        //assertThat(this,is(not(sameInstance(testObject)))); //직전 오브젝트와만 비교하는 단점이 있다.
        //testObject = this;
        assertThat(testObjects,not(hasItem(this)));
        testObjects.add(this);
        assertThat(contextObject == null || contextObject == this.context,is(true));
        contextObject = this.context;
    }
    @Test
    public void test2(){
        /*
        assertThat(this,is(not(sameInstance(testObject))));
        testObject = this;
         */
        assertThat(testObjects,not(hasItem(this)));
        testObjects.add(this);
        assertTrue(contextObject == null || contextObject == this.context);
        contextObject = this.context;
    }
    @Test
    public void test3(){
        /*
        assertThat(this,is(not(sameInstance(testObject))));
        testObject = this;
         */
        assertThat(testObjects,not(hasItem(this)));
        testObjects.add(this);
        assertThat(contextObject,either(is(nullValue())).or(is(this.context)));
        contextObject = this.context;
    }
}
