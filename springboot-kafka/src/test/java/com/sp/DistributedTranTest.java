package com.sp;

import com.sp.bean.model.User;
import com.sp.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * Created by admin on 2019/12/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = Application.class)
public class DistributedTranTest {

    @Autowired
    private UserService userService;

    @Test
    public void addUser() {
        int userCount = 10;
        Date currentDate = new Date();
        for (int i = 0; i < userCount; i++) {
            User user = User.builder().userName("foo" + i).createTime(currentDate).build();
            userService.addUser(user);
        }
    }
}