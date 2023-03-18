package org.example;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.example.domain.AuthorizationResponse;
import org.example.domain.ErrorResponse;
import org.example.domain.UserResponse;
import org.example.models.User;
import org.example.steps.UserSteps;
import org.example.utils.CheckUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.qameta.allure.Allure.step;

@RunWith(Parameterized.class)
public class UpdateUserParameterizedTest extends BaseUserTest {
    User userToCreate;
    User userToUpdate;
    User expectedUser;
    UserSteps userSteps;

    public UpdateUserParameterizedTest(String name, User userToCreate, User userToUpdate, User expectedUser) {
        this.userToCreate = userToCreate;
        this.userToUpdate = userToUpdate;
        this.expectedUser = expectedUser;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[][] getUsersForTest() {
        UserSteps userSteps = new UserSteps();
        User userToCreate1 = userSteps.generateRandomUser();
        User userToCreate2 = userSteps.generateRandomUser();
        User userToCreate3 = userSteps.generateRandomUser();
        User updatedUserData = userSteps.generateRandomUser();
        return new Object[][]{
                {
                        "Update name",
                        userToCreate1,
                        User.builder()
                                .name(updatedUserData.getName())
                                .build(),
                        User.builder()
                                .name(updatedUserData.getName())
                                .email(userToCreate1.getEmail())
                                .password(userToCreate1.getPassword())
                                .build()
                },
                {
                        "Update email",
                        userToCreate2,
                        User.builder()
                                .email(updatedUserData.getEmail())
                                .build(),
                        User.builder()
                                .name(userToCreate2.getName())
                                .email(updatedUserData.getEmail())
                                .password(userToCreate2.getPassword())
                                .build()
                },
                {
                        "Update password",
                        userToCreate3,
                        User.builder()
                                .password(updatedUserData.getPassword())
                                .build(),
                        User.builder()
                                .name(userToCreate3.getName())
                                .email(userToCreate3.getEmail())
                                .password(updatedUserData.getPassword())
                                .build()
                }
        };
    }

    @Before
    public void setUp() {
        init();
        userSteps = new UserSteps();
    }

    @After
    public void tearDown() {
        clearUsers();
    }

    @Test
    @DisplayName("Update user with authorization")
    public void updateUserWithAuthorization() {
        createdUsers.add(userToCreate);
        createdUsers.add(expectedUser);

        userSteps.createUser(userToCreate);

        ValidatableResponse updateResponse = userSteps.updateUserWithAuthorization(userToUpdate, userToCreate);
        CheckUtils.checkStatusCode(HttpStatus.SC_OK, updateResponse.extract().statusCode());

        ValidatableResponse getUserResponse = userSteps.getUser(expectedUser);
        CheckUtils.checkStatusCode(HttpStatus.SC_OK, getUserResponse.extract().statusCode());
        User newUserData = getUserResponse.extract().as(UserResponse.class).getUser();
        step("Check new user data", () -> {
            CheckUtils.checkFieldEquals("name", expectedUser.getName(), newUserData.getName());
            CheckUtils.checkFieldEquals("email", expectedUser.getEmail(), newUserData.getEmail());
            step("Login with new email and password", () -> {
                ValidatableResponse newLoginResponse = userSteps.loginUser(expectedUser);
                CheckUtils.checkStatusCode(HttpStatus.SC_OK, newLoginResponse.extract().statusCode());
                CheckUtils.checkFieldEquals("success", true, newLoginResponse.extract().as(AuthorizationResponse.class).isSuccess());
            });
        });
    }

    @Test
    @DisplayName("Update user without authorization")
    public void updateUserWithoutAuthorization() {
        createdUsers.add(userToCreate);
        createdUsers.add(expectedUser);

        userSteps.createUser(userToCreate);

        ValidatableResponse updateResponse = userSteps.updateUserWithoutAuthorization(userToUpdate);
        CheckUtils.checkStatusCode(HttpStatus.SC_UNAUTHORIZED, updateResponse.extract().statusCode());

        ErrorResponse errorResponse = updateResponse.extract().as(ErrorResponse.class);

        step("Check response", () -> {
            CheckUtils.checkFieldEquals("success", false, errorResponse.isSuccess());
            CheckUtils.checkFieldEquals("message", "You should be authorised", errorResponse.getMessage());
        });
    }
}
