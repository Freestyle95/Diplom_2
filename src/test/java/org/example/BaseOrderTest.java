package org.example;

import org.example.domain.AuthorizationResponse;
import org.example.models.User;
import org.example.steps.OrderSteps;
import org.example.steps.UserSteps;

import java.util.ArrayList;

public class BaseOrderTest extends BaseTest {
    OrderSteps orderSteps;
    UserSteps userSteps;
    User user;
    String accessToken;

    @Override
    public void init() {
        super.init();
        orderSteps = new OrderSteps();
        userSteps = new UserSteps();
        createdUsers = new ArrayList<>();
        user = userSteps.generateRandomUser();
        userSteps.createUser(user);
        accessToken = userSteps.loginUser(user).extract().as(AuthorizationResponse.class).getAccessToken();
        createdUsers.add(user);
    }

    public void clearUsers() {
        new UserSteps().deleteUsersPostcondition(createdUsers);
    }
}
