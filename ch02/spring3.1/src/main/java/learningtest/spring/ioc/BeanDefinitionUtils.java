package learningtest.spring.ioc;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

public class BeanDefinitionUtils {
    public static void printBeanDefinitions(ConfigurableApplicationContext cwac) {
        List<List<String>> roleBeanInfos = new ArrayList<List<String>>();
        roleBeanInfos.add(new ArrayList<String>());
        roleBeanInfos.add(new ArrayList<String>());
        roleBeanInfos.add(new ArrayList<String>());

        ConfigurableListableBeanFactory clbf = cwac.getBeanFactory();
        for(String name : clbf.getBeanDefinitionNames()) {
            int role = clbf.getBeanDefinition(name).getRole();
            List<String> beanInfos = roleBeanInfos.get(role);
            beanInfos.add(role + "\t" + name + "\t" + cwac.getBean(name).getClass().getName());
        }

        for(List<String> beanInfos : roleBeanInfos) {
            for(String beanInfo : beanInfos) {
                System.out.println(beanInfo);
            }
        }
    }
}
