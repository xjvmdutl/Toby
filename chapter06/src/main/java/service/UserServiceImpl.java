package service;

import dao.UserDao;
import entity.Level;
import entity.User;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

public class UserServiceImpl implements UserService{
    /**
     * 프록시 : 타깃과 같은 인터페이스를 구현하여 타깃에 부가적인 기능을 부여할수 있다.
     * 클라이언트는 인터페이스를 통해서만 핵심 기능을 사용하게 만들고, 부가기능 자신도 같은 인터페이스를 구현한 뒤에 자신이 그사이에 끼어든다.
     * 클라이언트 -> 프록시 -> 타깃
     *
     * 데코레이트 패턴 : 타깃에 부가적인 기능을 런타임시 다이나믹하게 부여해주기 위해 프록시를 사용한 패턴
     *                어느 데코레이터에서 타깃으로 연결될지 코드레벨에서 알수 없다(런타이 시점에 연결되기 때문에)
     */

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    UserDao userDao;

    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }


    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }


    public void upgradeLevels(){
        /*
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition()); //트랜잭션 시작
        try{
            //upgradeLevelsInternal();    //완벽하게 독립된 코드이기에 메소드로 분리가 가능하다.
            List<User> lists = userDao.getAll();
            for(User user : lists){
                if(canUpgradeLevel(user)){
                    upgradeLevel(user);
                }
            }
            transactionManager.commit(status);
        }catch (Exception e){
            transactionManager.rollback(status);
            throw e;
        }
         */
        List<User> lists = userDao.getAll();
        for(User user : lists){
            if(canUpgradeLevel(user)){
                upgradeLevel(user);
            }
        }
    }
    /*
    private void upgradeLevelsInternal() {
        List<User> lists = userDao.getAll();
        for(User user : lists){
            if(canUpgradeLevel(user)){
                upgradeLevel(user);
            }
        }
    }
    */
    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEMail(user);
    }

    private void sendUpgradeEMail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("upgrade 안내");
        mailMessage.setText("사용자님 등급이 업그레이드 되었습니다");

        this.mailSender.send(mailMessage); //DI 적용
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();

        switch(currentLevel){
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("UnKnown Level" );
        }
    }

    public void add(User user) {
        if(user.getLevel() == null ){
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }

    public User get(String id) {
        return userDao.get(id);
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public void deleteAll() {
        userDao.deleteAll();
    }

    public void update(User user) {
        userDao.update(user);
    }
}

