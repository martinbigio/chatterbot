package ar.edu.itba.tpf.chatterbot.dao;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.Role;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RoleDAOTest extends GenericDAOTest<Role> {

    @Autowired
    private RoleDAO roleDAO;
    
    @Override
    protected GenericDAO<Role, Long> getDao() {
        return roleDAO;
    }
    
    @Override
    protected Role getSample() {
        return new Role("testRole", "testRole");
    }
    
    @Override
    protected void updateSample(Role sample) {
        sample.setRoleName("anotherRole");
    }
    
    @Test
    public void testFindRoleByRoleDescription() {
        Role role = new Role("test", "test");
        roleDAO.save(role);
        assertThat(roleDAO.findRoleByRoleDescription("test"), equalTo(role));
        assertThat(roleDAO.findRoleByRoleDescription("unknown"), nullValue());
    }
    
    @Test
    public void testFindAvailableRoles() {
        Role role1 = new Role("role1", "description1");
        Role role2 = new Role("role2", "description2");
        
        roleDAO.save(role1);
        roleDAO.save(role2);
        
        Collection<Role> roles = roleDAO.findAvailableRoles();
        assertThat(roles.size(), equalTo(2));
        assertTrue(roles.contains(role1));
        assertTrue(roles.contains(role2));
        
    }
}
