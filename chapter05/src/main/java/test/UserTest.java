package test;

import entity.Level;
import entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

//SpringContainer에서 굳이 사용하지 않아도 됨으로 어노테이션을 제거한다.
public class UserTest {
    User user;

    @Before
    public void setUp(){
        user = new User();
    }

    @Test()
    public void upgradeLevel(){
        Level[] levels = Level.values();
        for(Level level : levels){
            if(level.nextLevel() == null)
                continue;
            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel(), is(level.nextLevel()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotUpgradeLevel(){
        Level[] levels = Level.values();
        for(Level level : levels){
            if(level.nextLevel() != null)
                continue;
            user.setLevel(level);
            user.upgradeLevel();
        }
    }
}
