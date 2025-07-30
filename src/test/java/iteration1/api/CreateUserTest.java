package iteration1.api;

import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.BaseUserResponse;
import models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {

    @Test
    public void adminCanCreateUserWithCorrectData() {
        var userRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        var userResponse = new ValidatedCrudRequester<BaseUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        ModelAssertions.assertThatModels(userRequest, userResponse).match();
    }

    public static Stream<Arguments> userInvalidData() {
        return Stream.of(
                // username field validation
                Arguments.of("   ", "Kate1998$", "USER", "username", "Username cannot be blank"),
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

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorValue))
                .post(userRequest);
    }
}
