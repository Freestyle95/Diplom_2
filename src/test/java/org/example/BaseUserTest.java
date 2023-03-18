package org.example;

import org.example.steps.UserSteps;

import java.util.ArrayList;

public class BaseUserTest extends BaseTest {
    UserSteps userSteps;

    @Override
    public void init() {
        super.init();
        userSteps = new UserSteps();
        createdUsers = new ArrayList<>();
    }

    public void clearUsers() {
        new UserSteps().deleteUsersPostcondition(createdUsers);
    }
}
