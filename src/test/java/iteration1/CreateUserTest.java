package iteration1;

import generators.RandomData;
import models.CreateUserRequest;
import models.BaseUserResponse;
import models.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.AdminCreateUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {

    @Test
    public void adminCanCreateUserWithCorrectData() {
        var userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        var userResponse = new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest).extract().as(BaseUserResponse.class);

        softly.assertThat(userRequest.getUsername()).isEqualTo(userResponse.getUsername());
        softly.assertThat(userRequest.getPassword()).isNotEqualTo(userResponse.getPassword());
        softly.assertThat(userRequest.getRole()).isEqualTo(userResponse.getRole());
    }

    public static Stream<Arguments> userInvalidData() {
        return Stream.of(
                // username field validation
                Arguments.of("   ", "Kate1998$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots"),
                Arguments.of("ab", "Kate1998$", "USER", "username", "Username must be between 3 and 15 characters"),
                Arguments.of("ab123456789Cde-.", "Kate1998$", "USER", "username", "Username must be between 3 and 15 characters"),
                Arguments.of("Ak1234.-_@", "Kate1998$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots"),
                // password field validation
                Arguments.of("kate1998", "", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),
                Arguments.of("kate1998", "Ab1234!", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),
                Arguments.of("kate1998", "Ab12 3!", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),
                Arguments.of("kate1998", "A123456!", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),
                Arguments.of("kate1998", "Ab123456", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),
                // role field validation
                Arguments.of("kate1998", "Ab123456!", "MODERATOR", "role", "Role must be either 'ADMIN' or 'USER'")
        );
    }

    @ParameterizedTest
    @MethodSource("userInvalidData")
    public void adminCannotCreateUserWithInvalidData(String username, String password, String role, String errorKey, String errorValue) {
        var userRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorValue))
                .post(userRequest);
    }
}
