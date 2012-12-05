package ar.edu.itba.tpf.chatterbot.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.User;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserDAOTest extends GenericDAOTest<User> {

    @Autowired
    private UserDAO userDAO;
    
    @Override
    protected GenericDAO<User, Long> getDao() {
        return userDAO;
    }
    
    @Override
    protected User getSample() {
        return new User("testUser", "testPassword", "testName", "testName", "mail");
    }
    
    @Override
    protected void updateSample(User sample) {
        sample.setLastName("lastName");
    }
    
    @Test
    public void testFindUserByUsername() {
        User testUser = new User("test", "test", "test", "test", "test");
        userDAO.save(testUser);
        assertThat(userDAO.findUserByUsername("test"), equalTo(testUser));
        assertThat(userDAO.findUserByUsername("unknown"), nullValue());
    }
}
