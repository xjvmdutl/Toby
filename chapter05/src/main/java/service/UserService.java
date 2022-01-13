package service;

import dao.UserDao;
import entity.Level;
import entity.User;

import java.util.List;

public class UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    UserDao userDao;

    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }

    public void upgradeLevels(){

        List<User> lists = userDao.getAll();
         /*
        for(User user : lists){
            Boolean changed = null;// Level에 변화가 있는지를 확인
            if(user.getLevel() == Level.BASIC && user.getLogin() >= 50){
                user.setLevel(Level.SILVER);
                changed = true;
            }else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30){
                user.setLevel(Level.GOLD);
                changed = true;
            }else if(user.getLevel() == Level.GOLD){
                changed = false;
            }else{
                changed = false;
            }
            if(changed){
                userDao.update(user);
            }
        }
         */
        for(User user : lists){
            if(canUpgradeLevel(user)){ //업그레이드가 가능한가
                upgradeLevel(user);
            }
        }
    }

    private void upgradeLevel(User user) {
        /*
         * if(user.getLevel() == Level.BASIC) user.setLevel(Level.SILVER);
         * else if(user.getLevel() == Level.SILVER) user.setLevel(Level.GOLD);
         *
         */
        user.upgradeLevel();

        userDao.update(user);
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();

        switch(currentLevel){
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("UnKnown Level" );
            //default: return false;
        }
    }

    public void add(User user) {
        if(user.getLevel() == null ){
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}
