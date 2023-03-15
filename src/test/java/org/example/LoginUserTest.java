package org.example;

import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.example.domain.AuthorizationResponse;
import org.example.domain.ErrorResponse;
import org.example.models.User;
import org.example.utils.CheckUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static io.qameta.allure.Allure.step;

public class LoginUserTest extends BaseUserTest {
    @After
    public void tearDown() {
        clearUsers();
    }

    @Before
    public void setUp() {
        init();
    }

    @Test
    public void successfulLoginUser() {
        User userToCreate = userSteps.generateRandomUser();
        createdUsers.add(userToCreate);
        step("Create user", () -> {
            ValidatableResponse createUserResponse = userSteps.createUser(userToCreate);
            CheckUtils.checkStatusCode(createUserResponse.extract().statusCode(), HttpStatus.SC_OK);
        });

        AtomicReference<ValidatableResponse> loginUserResponse = new AtomicReference<>();
        step("Login user", () -> {
            loginUserResponse.set(userSteps.loginUser(userToCreate));
            CheckUtils.checkStatusCode(loginUserResponse.get().extract().statusCode(), HttpStatus.SC_OK);
        });

        step("Check response", () -> {
            AuthorizationResponse authUserResponse = loginUserResponse.get().extract().as(AuthorizationResponse.class);
            User loginedUser = authUserResponse.getUser();
            CheckUtils.checkFieldEquals("success", true, authUserResponse.isSuccess());
            CheckUtils.checkFieldContainsString("accessToken", "Bearer ", authUserResponse.getAccessToken());
            CheckUtils.checkFieldIsNotBlank("refreshToken", authUserResponse.getRefreshToken());
            CheckUtils.checkFieldEquals("user name", userToCreate.getName(), loginedUser.getName());
            CheckUtils.checkFieldEquals("user email", userToCreate.getEmail(), loginedUser.getEmail());
        });
    }

    @Test
    public void failLoginInvalidCredentialsUser() {
        User userToCreate = userSteps.generateRandomUser();
        createdUsers.add(userToCreate);
        AtomicReference<ValidatableResponse> loginUserResponse = new AtomicReference<>();
        step("Login user with invalid credentials", () -> {
            loginUserResponse.set(userSteps.loginUser(userToCreate));
            CheckUtils.checkStatusCode(loginUserResponse.get().extract().statusCode(), HttpStatus.SC_UNAUTHORIZED);
        });

        step("Check response", () -> {
            ErrorResponse errorResponse = loginUserResponse.get().extract().as(ErrorResponse.class);
            CheckUtils.checkFieldEquals("success", false, errorResponse.isSuccess());
            CheckUtils.checkFieldEquals("message", "email or password are incorrect", errorResponse.getMessage());
        });
    }
}
