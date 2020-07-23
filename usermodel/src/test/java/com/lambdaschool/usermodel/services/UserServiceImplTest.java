package com.lambdaschool.usermodel.services;

import com.lambdaschool.usermodel.UserModelApplication;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static junit.framework.TestCase.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserModelApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceImplTest {
    private List<User> myList;

    @Autowired
    private UserService userService;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        List<User> myList = userService.findAll();
        for(User u: myList)
        {
            System.out.println(u.getUserid() + " " + u.getUsername());
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void afindUserById()
    {
        assertEquals("cinnamon", userService.findUserById(7).getUsername());
    }

    @Test
    public void bfindByNameContaining() {
        assertEquals(1, userService.findByNameContaining("cinn").size());
    }

    @Test
    public void cfindAll() {
        assertEquals(5, userService.findAll().size());
    }

    @Test
    public void ddelete() {
        userService.delete(11);
        assertEquals(4, userService.findAll().size());
    }

    @Test
    public void efindByName() {
        assertEquals("cinnamon", userService.findByName("cinnamon").getUsername());
    }

    @Test(expected = EntityNotFoundException.class)
    public void ffindByNameFailed()
    {
        assertEquals(1, userService.findByName("lollipop"));
    }

    @Test
    public void gsave() {
        User newUser = new User("nick", "12345", "nicknick@nick.com");
        userService.save(newUser);
        assertEquals(5, userService.findAll().size());
    }

    @Test
    public void hupdate() {
        User updatedUser = new User("nickussery", "password", "nick@nick.com");
        userService.update(updatedUser, 7);
        assertEquals("nickussery", userService.findUserById(7).getUsername());
        assertEquals("password", userService.findUserById(7).getPassword());
    }

    @Test
    public void ideleteAll() {
        userService.deleteAll();
        assertEquals(0, userService.findAll().size());
    }
}