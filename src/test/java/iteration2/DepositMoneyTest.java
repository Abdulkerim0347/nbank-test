package iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class DepositMoneyTest {
    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    public void userCanDepositMoneyTest() {
        given()
                .header("Authorization", "Basic a2F0ZTE5OTgxOkthdGUxOTk4JA==")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "id": 1,
                          "balance": 100
                        }
                        
                        """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.equalTo(1));
    }

    @Test
    public void userCanTransferMoneyTest() {
        given()
                .header("Authorization", "Basic a2F0ZTE5OTgxOkthdGUxOTk4JA==")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "senderAccountId": 1,
                          "receiverAccountId": 2,
                          "amount": 50
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("senderAccountId", Matchers.equalTo(1))
                .body("receiverAccountId", Matchers.equalTo(2));
    }

    @Test
    public void userCanChangeItsNameTest() {
        given()
                .header("Authorization", "Basic a2F0ZTE5OTgxOkthdGUxOTk4JA==")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "name": "new name"
                        }
                        """)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("customer.name", Matchers.equalTo("new name"));
    }
}
