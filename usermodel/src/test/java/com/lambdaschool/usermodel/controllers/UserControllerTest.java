package com.lambdaschool.usermodel.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.persistence.EntityNotFoundException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private List<User> myUsers;

    @Before
    public void setUp() throws Exception
    {
        myUsers = new ArrayList<>();

        Role r1 = new Role("admin");
        Role r2 = new Role("user");
        Role r3 = new Role("data");

        // admin, data, user
        User u1 = new User("admin",
                "password",
                "admin@lambdaschool.local");
        u1.setUserid(0);
        u1.getRoles().add(new UserRoles(u1, r1));
        u1.getRoles().add(new UserRoles(u1, r2));
        u1.getRoles().add(new UserRoles(u1, r3));
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@email.local"));
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@mymail.local"));
        myUsers.add(u1);

        // data, user
        User u2 = new User("cinnamon",
                "1234567",
                "cinnamon@lambdaschool.local");
        u2.setUserid(1);
        u2.getRoles().add(new UserRoles(u2, r2));
        u2.getRoles().add(new UserRoles(u2, r3));
        u2.getUseremails()
                .add(new Useremail(u2,
                        "cinnamon@mymail.local"));
        u2.getUseremails()
                .add(new Useremail(u2,
                        "hops@mymail.local"));
        u2.getUseremails()
                .add(new Useremail(u2,
                        "bunny@email.local"));
        myUsers.add(u2);

        // user
        User u3 = new User("barnbarn",
                "ILuvM4th!",
                "barnbarn@lambdaschool.local");
        u3.setUserid(2);
        u3.getRoles().add(new UserRoles(u3, r2));
        u3.getUseremails()
                .add(new Useremail(u3,
                        "barnbarn@email.local"));
        myUsers.add(u3);

        User u4 = new User("puttat",
                "password",
                "puttat@school.lambda");
        u4.setUserid(3);
        u4.getRoles().add(new UserRoles(u4, r2));
        myUsers.add(u4);

        User u5 = new User("misskitty",
                "password",
                "misskitty@school.lambda");
        u5.setUserid(4);
        u5.getRoles().add(new UserRoles(u5, r2));
        myUsers.add(u5);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void alistAllUsers() throws Exception
    {
        String apiUrl = "/users/users";
        Mockito.when(userService.findAll()).thenReturn(myUsers);
        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(myUsers);

        assertEquals(er, tr);
    }

    @Test
    public void bgetUserById() throws Exception
    {
        String apiUrl = "/users/user/0";
        Mockito.when(userService.findUserById(0)).thenReturn(myUsers.get(0));
        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(myUsers.get(0));

        assertEquals(er, tr);
    }

    @Test
    public void cgetUserByName() throws Exception
    {
        String apiUrl = "/users/user/name/barnbarn";
        Mockito.when(userService.findByName("barnbarn")).thenReturn(myUsers.get(2));
        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(myUsers.get(2));

        assertEquals(er, tr);
    }

    @Test
    public void dgetUserLikeName() throws Exception
    {
        List<User> thisList = new ArrayList<>();
        thisList.add(myUsers.get(2));
        String apiUrl = "/users/user/name/like/barn";
        Mockito.when(userService.findByNameContaining("barn")).thenReturn(thisList);
        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(thisList);

        assertEquals(er, tr);
    }

    @Test
    public void eaddNewUser() throws Exception
    {
        String apiUrl = "/users/user";
        ObjectMapper mapper = new ObjectMapper();

        User newUser = new User("nick", "12345", "nicknick@nick.com");
        newUser.setUserid(100);
        String userString = mapper.writeValueAsString(newUser);

        Mockito.when(userService.save(any(User.class))).thenReturn(newUser);

        RequestBuilder rb = MockMvcRequestBuilders.post(apiUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userString);



        mockMvc.perform(rb).andExpect(status().isCreated()).andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void fupdateFullUser() throws Exception
    {
        String apiUrl = "/users/user/1";
        ObjectMapper mapper = new ObjectMapper();
        Role r1 = new Role("admin");
        Role r2 = new Role("user");
        Role r3 = new Role("data");

        User updateUser = new User("nick", "12345", "nicknick@nick.com");
        updateUser.getRoles().add(new UserRoles(updateUser, r1));
        updateUser.getRoles().add(new UserRoles(updateUser, r2));
        updateUser.getRoles().add(new UserRoles(updateUser, r3));
        String em1 = "nick@ncik.com";
        String em2 = "nikc@nick.com";
        String em3 = "icnk@nick.com";


        updateUser.getUseremails().add(new Useremail(updateUser, em1));
        updateUser.getUseremails().add(new Useremail(updateUser, em2));
        updateUser.getUseremails().add(new Useremail(updateUser, em3));

        String userString = mapper.writeValueAsString(updateUser);

        Mockito.when(userService.update(updateUser, 1)).thenReturn(updateUser);




        RequestBuilder rb = MockMvcRequestBuilders.put(apiUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userString);

        String ou = mapper.writeValueAsString(myUsers.get(1));


        mockMvc.perform(rb).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void gupdateUser() throws Exception
    {
        String apiUrl = "/users/user/1";
        ObjectMapper mapper = new ObjectMapper();

        User updateUser = new User("nick", "12345", "nicknick@nick.com");
        String userString = mapper.writeValueAsString(updateUser);

        myUsers.get(1).setUsername("nick");
        myUsers.get(1).setPrimaryemail("nicknick@nick.com");
        myUsers.get(1).setPassword("12345");

        Mockito.when(userService.update(updateUser, 1)).thenReturn(myUsers.get(1));




        RequestBuilder rb = MockMvcRequestBuilders.put(apiUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userString);


        mockMvc.perform(rb).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void hdeleteUserById() throws Exception
    {
        String apiUrl = "/users/user/1";

        RequestBuilder rb = MockMvcRequestBuilders.delete(apiUrl)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(rb).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
    }
}