package org.example;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.example.models.User;
import org.example.steps.UserSteps;

import java.util.ArrayList;
import java.util.List;

public class BaseTest {
    List<User> createdUsers;

    public void init() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
        createdUsers = new ArrayList<>();
    }

    public void clearUsers() {
        new UserSteps().deleteUsersPostcondition(createdUsers);
    }
}
