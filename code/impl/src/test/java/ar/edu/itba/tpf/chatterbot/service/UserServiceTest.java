package ar.edu.itba.tpf.chatterbot.service;

import java.util.Arrays;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import ar.edu.itba.tpf.chatterbot.dao.RoleDAO;
import ar.edu.itba.tpf.chatterbot.dao.UserDAO;
import ar.edu.itba.tpf.chatterbot.model.Role;
import ar.edu.itba.tpf.chatterbot.model.User;

@RunWith(JMock.class)
public class UserServiceTest {

    private Mockery context;
    private UserServiceImpl userService;
    
    private UserDAO userDAO;
    private RoleDAO roleDAO;
    
    private User user;
    
    @Before
    public void setup() {
        context = new JUnit4Mockery();

        userService = new UserServiceImpl();
        userDAO = context.mock(UserDAO.class);
        roleDAO = context.mock(RoleDAO.class);
        
        userService.setUserDAO(userDAO);
        userService.setRoleDAO(roleDAO);
        
        user = new User("user", "pass", "fname", "lname", "mail");
        
    }

    @Test
    public void testGetUserByUsername() {
        final String unknownUser = "unknown";
        context.checking(new Expectations() {
            {
                one(userDAO).findUserByUsername(user.getUsername());
                will(returnValue(user));
                one(userDAO).findUserByUsername(unknownUser);
                will(returnValue(null));
            }
        });
        assertThat(userService.getUserByUsername(user.getUsername()), equalTo(user));
        assertThat(userService.getUserByUsername(unknownUser), nullValue());
    }

    @Test
    public void testPersistUser() {
        context.checking(new Expectations() {
            {
                one(userDAO).save(user);
            }
        });
        userService.persistUser(user);
    }
    
    @Test
    public void testGetAvailableRoles() {
        final Collection<Role> roles = Arrays.asList(new Role("test", "test"));
        context.checking(new Expectations() {
            {
                one(roleDAO).findAvailableRoles();
                will(returnValue(roles));
            }
        });
        assertThat(userService.getAvailableRoles(), equalTo(roles));   
    }
    
    @Test
    public void testLoadUserByUsername() {
        final String unknownUser = "unknown";
        context.checking(new Expectations() {
            {
                one(userDAO).findUserByUsername(user.getUsername());
                will(returnValue(user));
                one(userDAO).findUserByUsername(unknownUser);
                will(returnValue(null));
            }
        });
        assertThat((User)userService.loadUserByUsername(user.getUsername()), equalTo(user));
        assertThat((User)userService.loadUserByUsername(unknownUser), nullValue());
    }
  
    public void testGetRoleByRoleDescription() {
        final String unknownRole = "unknown";
        final Role role = new Role("test", "test");
        
        context.checking(new Expectations() {
            {
                one(roleDAO).findRoleByRoleDescription(role.getRoleDescription());
                will(returnValue(role));
                one(roleDAO).findRoleByRoleDescription(unknownRole);
                will(returnValue(null));
            }
        });
        assertThat(userService.getRoleByRoleDescription(role.getRoleDescription()), equalTo(role));
        assertThat(userService.getRoleByRoleDescription(role.getRoleDescription()), nullValue());
    }
    
    @Test
    public void testGetUsers() {
        final Collection<User> users = Arrays.asList(user);
        context.checking(new Expectations() {
            {
                one(userDAO).findAll();
                will(returnValue(users));
            }
        });
        assertThat(userService.getUsers(), equalTo(users));
    }
    
    @Test
    public void testRemoveUser() {
        context.checking(new Expectations() {
            {
                one(userDAO).delete(user);
            }
        });
        userService.removeUser(user);
    }
    
    @Test
    public void testPersistRole() {
        final Role role = new Role("testRole", "testRole");
        context.checking(new Expectations() {
            {
                one(roleDAO).save(role);
            }
        });
        userService.persistRole(role);
    }

}