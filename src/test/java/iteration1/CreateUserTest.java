package iteration1;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class CreateUserTest {
    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter())
        );
    }

    @Test
    public void adminCanCreateUserWithCorrectData() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                         {
                          "username": "kate199811",
                          "password": "Kate1998$",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("username", Matchers.equalTo("kate199811"))
                .body("password", Matchers.not(Matchers.equalTo("Kate1998$")))
                .body("role", Matchers.equalTo("USER"));
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
        String requestBody = String.format(
                """
                         {
                          "username": "%s",
                          "password": "%s",
                          "role": "%s"
                        }
                        """, username, password, role
        );

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(errorKey, Matchers.equalTo(errorValue));
    }
}
