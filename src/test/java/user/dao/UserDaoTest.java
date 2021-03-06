package user.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import user.domain.Level;
import user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by mileNote on 2021-01-15
 * Blog : https://milenote.tistory.com
 * Github : https://github.com/SimKyunam
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = AppContext.class)
class UserDaoTest {

    @Autowired
    private UserDao dao;

    @Autowired
    private DataSource dataSource;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp(){
        //dao = new UserDaoJdbc();
        //DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost:3306/testdb?serverTimezone=UTC", "root", "root", true);
        //dao.setDataSource(dataSource);

        user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0, "test@naver.com");
        user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10, "test1@naver.com");
        user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40, "test2@naver.com");
    }

    @Test
//    @DirtiesContext //컨텍스트 설정이 다른 경우 사용 (클래스, 메소드 가능)
    public void addAndGet() {
        dao.deleteAll();
        assertThat(dao.getCount(), equalTo(0));

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), equalTo(2));

        User userget1 = dao.get(user1.getId());
        checkSameUser(userget1, user1);

        User userget2 = dao.get(user2.getId());
        checkSameUser(userget2, user2);
    }

    @Test
    public void count(){
        dao.deleteAll();
        assertThat(dao.getCount(), equalTo(0));

        dao.add(user1);
        assertThat(dao.getCount(), equalTo(1));

        dao.add(user2);
        assertThat(dao.getCount(), equalTo(2));

        dao.add(user3);
        assertThat(dao.getCount(), equalTo(3));
    }

    @Test
    public void getUserFailure() {
        dao.deleteAll();
        assertThat(dao.getCount(), equalTo(0));

        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            dao.get("unkwon_id");
        });
    }

    @Test
    public void getAll() {
        dao.deleteAll();

        List<User> users0 = dao.getAll();
        assertThat(users0.size(), is(0));

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user3, users3.get(0));
        checkSameUser(user1, users3.get(1));
        checkSameUser(user2, users3.get(2));
    }

    private void checkSameUser(User user1, User user2){
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
        assertThat(user1.getLevel(), is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
    }

    @Test
    public void duplicateKey(){
        dao.deleteAll();
        Assertions.assertThrows(DataAccessException.class, () -> {
            dao.add(user1);
            dao.add(user1);
        });
    }

    @Test
    public void sqlExceptionTranslate(){
        dao.deleteAll();
        try{
            dao.add(user1);
            dao.add(user1);
        }catch(DuplicateKeyException ex){
            SQLException sqlEx = (SQLException) ex.getRootCause();
            SQLExceptionTranslator set =
                new SQLErrorCodeSQLExceptionTranslator(this.dataSource);

            assertThat(set.translate(null, null, sqlEx), instanceOf(DuplicateKeyException.class));
        }
    }

    @Test
    public void update(){
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);

        user1.setName("오민규");
        user1.setPassword("springno6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        user1.setEmail("test@naver.com");
        dao.update(user1);

        User user1updte = dao.get(user1.getId());
        checkSameUser(user1, user1updte);
        User user2same = dao.get(user2.getId());
        checkSameUser(user2, user2same);
    }

}