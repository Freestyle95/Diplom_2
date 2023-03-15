package org.example;

import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.example.models.ErrorResponse;
import org.example.models.User;
import org.example.steps.UserSteps;
import org.example.utils.CheckUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class CreateNotFullDataUserTest extends BaseTest {
    User user;
    UserSteps userSteps;

    public CreateNotFullDataUserTest(String name, User user) {
        this.user = user;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[][] getUserForTest() {
        return new Object[][]{
                {
                        "User without name",
                        User.builder()
                                .email(String.format("%s@example.com", RandomStringUtils.randomAlphanumeric(8)).toLowerCase())
                                .password(RandomStringUtils.randomAlphanumeric(10))
                                .build()
                },
                {
                        "User without email",
                        User.builder()
                                .name(String.format("%s %s", RandomStringUtils.randomAlphabetic(8), RandomStringUtils.randomAlphabetic(8)))
                                .password(RandomStringUtils.randomAlphanumeric(10))
                                .build()
                },
                {
                        "User without password",
                        User.builder()
                                .name(String.format("%s %s", RandomStringUtils.randomAlphabetic(8), RandomStringUtils.randomAlphabetic(8)))
                                .email(String.format("%s@example.com", RandomStringUtils.randomAlphanumeric(8)).toLowerCase())
                                .build()
                }
        };
    }

    @Before
    public void setUp() {
        init();
        userSteps = new UserSteps();
    }

    @Test
    public void createNotFullDataUser() {
        ValidatableResponse response = userSteps.createUser(user);
        createdUsers.add(user);
        CheckUtils.checkStatusCode(response.extract().statusCode(), HttpStatus.SC_FORBIDDEN);

        ErrorResponse errorResponse = response.extract().as(ErrorResponse.class);
        CheckUtils.checkFieldEquals("success", false, errorResponse.isSuccess());
        CheckUtils.checkFieldEquals("message", "Email, password and name are required fields", errorResponse.getMessage());
    }
}
