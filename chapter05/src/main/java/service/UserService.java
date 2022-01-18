package service;

import dao.UserDao;
import entity.Level;
import entity.User;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

public class UserService {

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

    /*
        //Connection 을 생성할 떄 사용할 DataSource DI
        private DataSource dataSource;


        public void setDataSource(DataSource dataSource){
            this.dataSource = dataSource;
        }
        */
    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }


    /*
    public void upgradeLevels(){

        List<User> lists = userDao.getAll();
         */
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
         *//*

        for(User user : lists){
            if(canUpgradeLevel(user)){ //업그레이드가 가능한가
                upgradeLevel(user);
            }
        }
    }
    */
    public void upgradeLevels() throws Exception{
        /*
        List<User> lists = userDao.getAll();
        for(User user : lists){
            if(canUpgradeLevel(user)){ //업그레이드가 가능한가
                upgradeLevel(user);
            }
        }
         */
        /**
         *하나의 트렌젝션은 같은 하나의 Connection에서 발생하여 같은 Connection까지의 법위를 지정한다.
         *트렌젝션은 반드시 트렌젝션을 경계값을 지정하여야 되는데 이때, 지정한 경계설정 구조는
         * 1.DBConnection 생성
         * 2.트랜잭션 시작
         * try{
         *      3.DAO메소드 호출
         *      4.트랜잭션 커밋
         * }catch(){
         *  5.트랜잭션 롤백
         * }finally{
         *  6.DBConnection 종료
         * }
         *이와같이 되어야 한다.
         *
         * 결국 upgradeLevels()메소드 안에서 트랜잭션의 경계설정 작업이 일어나야 된고, 그트랜잭션을 갖고 있는 DBConnection을 이용하도록 해야만 별도의 클래스에 만들어둔 DAO내의 코드도 트랜잭션이 젹용이 된다
         * 이렇게 코드를 적용하면 발생하는 문제점
         * 1. JDBCTemplate 코드를 더이상 사용하지 못하고, 결국 try/catch/finally 문이 Service 코드에 존재하게된다.
         * 2. Dao의 메소드와 비지니스 로직을 담고있는 Service 메소드에 Connection 파라미터가 추가되어야한다.
         * 3. Connection 파라미터가 UserDao 인터페이스 메소드에 추가되면 UserDao는 더이상 데이터 엑세스 기술에 독립적일 수가 없다.
         * 4. Dao메소드에 Connection 파라미터를 받게되면 테스트 코드에도 영양을 미친다.
         *
         * 이를 해결하기 위해 스프링이 제공한 해결 방식
         * 독립적인 트랜잭션 동기화방식(Service 에서 트랜잭션을 시작하기 위해 만든 Connection 오브젝트를 특별한 저장소에 보관하여 두고, 이후에 호출되는 Dao에 해당 Connection을 사용하도록 하는것)
         */
        /*
        TransactionSynchronizationManager.initSynchronization(); //트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화한다.
        Connection c = DataSourceUtils.getConnection(dataSource); //DBConnection 생성과, 동기화를 함께해주는 유틸리티 메소드
        c.setAutoCommit(false); //트랜잭션 경계설정
        try{
            List<User> lists = userDao.getAll();
            for(User user : lists){
                if(canUpgradeLevel(user)){ //업그레이드가 가능한가
                    upgradeLevel(user);
                }
            }
            c.commit();
        }catch (Exception e){
            c.rollback();
            throw e;
        }finally {
            DataSourceUtils.releaseConnection(c, dataSource); // DB커넥션을 안전하게 닫는다.
            TransactionSynchronizationManager.unbindResource(this.dataSource); //동기화 작업 종료및 정리
            TransactionSynchronizationManager.clearSynchronization();
        }
         */
        /**
         * 새로운 문제의 도입
         * 하나의 트렌젝션에서 여러개의 DB을 값을 넣어주어야한다면? 트랜잭션은 하나의 Connection 에서 유효하고 경계설정을 하게 되는데 문제가 생긴다.
         * 글로벌 트랜잭션을 사용해야한다.
         * 자바는 JDBC 이외에 글로벌 트랜잭션을 지원하는 트랜잭션 매니저를 지원하기 위한 API인 JTA를 제공한다.
         * UserService 에서 트랜잭션을 경계를 설정하게 되므로서, 특정 데이터 엑세스 기술에 종속되는 구조가 되어버렸다.
         * 트렌젝션을 경계설정하는 코드는 일정한 패턴을 가지는 구조임으로 추상화를 생각할수 있다.
         * 트랜잭션 경계설정 방법을 공통적인 부분을 모아서 추상화된 트랜잭션 관리계층을 이용할수 있도록 도와주는 API가 존재한다..
         * 어떤 트랜잭션 매니저 구현 클래스를 사용할지 UserService 코드가 알고 있는 것은 DI을 원칙을 위배한다.
         * 스프링 빈으로 등록한다.
         */
        //해당 manager를 빈으로 등록하고, 스프링이 관리하도록 해야한다.
        //PlatformTransactionManager transactionManager
        //        = new DataSourceTransactionManager(dataSource); //JDBC 추상 오브젝트 생성
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition()); //트랜잭션 시작
        try{
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
    }

    protected void upgradeLevel(User user) {
        /*
         * if(user.getLevel() == Level.BASIC) user.setLevel(Level.SILVER);
         * else if(user.getLevel() == Level.SILVER) user.setLevel(Level.GOLD);
         *
         */
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEMail(user);//업그레이드시 USER에게 EMail을 전송
    }

    private void sendUpgradeEMail(User user) {
        /*
        //해당 코드는 테스트 코드를 태워도, 메일 서버가 올라가 있지 않아도 실행되어 예외가 발생한다.
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.ksug.org");
        Session s = Session.getInstance(props, null);

        MimeMessage message = new MimeMessage(s);
        try {
            message.setFrom(new InternetAddress("useradmin@ksug.org"));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(user.getEmail()));
            message.setSubject("Upgrade 안내");
            message.setText("사용자님의 등급이 " + 1 + "로 업그레이드 되었습니다.");
            Transport.send(message);
        } catch (AddressException e) {
            throw new RuntimeException();
        } catch (MessagingException e) {
            throw new RuntimeException();
        }
         */
        //메일 발송기능 추상화
        //JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        //mailSender.setHost("mail.server.com");
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

