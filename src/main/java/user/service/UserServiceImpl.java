package user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import user.dao.UserDao;
import user.domain.Level;
import user.domain.User;

import java.util.List;

/**
 * Created by mileNote on 2021-03-01
 * Blog : https://milenote.tistory.com
 * Github : https://github.com/SimKyunam
 */
@Service("userService")
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDao userDao;

    @Autowired
    private MailSender mailSender;

    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public User get(String id) {
        return userDao.get(id);
    }

    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public void deleteAll() {
        userDao.deleteAll();
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    public void upgradeLevels(){
        List<User> users = userDao.getAll();
        for(User user: users){
            if(canUpgradeLevel(user)){
                upgradeLevel(user);
            }
        }
    }

    public static final int MIN_LOGIN_COUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GORD = 30;
    private boolean canUpgradeLevel(User user){
        Level currentLevel = user.getLevel();
        switch (currentLevel){
            case BASIC: return (user.getLogin() >= MIN_LOGIN_COUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GORD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " +currentLevel);
        }
    }

    protected void upgradeLevel(User user){
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEmail(user);
    }

    public void add(User user) {
        if(user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    public void sendUpgradeEmail(User user){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());

        this.mailSender.send(mailMessage);
    }
}
