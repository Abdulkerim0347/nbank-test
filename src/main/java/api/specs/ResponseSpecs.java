package api.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import java.util.List;

import static org.hamcrest.Matchers.*;

public final class ResponseSpecs {

    private ResponseSpecs() {}

    private static ResponseSpecBuilder defaultResponseBuilder() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON);
    }

    public static ResponseSpecification entityWasCreated() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification requestReturnsOK() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification requestReturnsBadRequest(String errorKey, String errorValue) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(errorKey, anyOf(
                        equalTo(errorValue),                 // если строка
                        hasItem(errorValue),                 // если список
                        equalTo(List.of(errorValue))         // если список из одного
                ))
                .build();
    }

    public static ResponseSpecification requestReturnsDepositAmountMustBeAtLeast001() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(equalTo("Deposit amount must be at least 0.01"))
                .build();
    }

    public static ResponseSpecification requestReturnsInvalidTransfer() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(equalTo("Invalid transfer: insufficient funds or invalid accounts"))
                .build();
    }

    public static ResponseSpecification requestReturnsDepositAmountCannotExceed5000() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(equalTo("Deposit amount cannot exceed 5000"))
                .build();
    }
}
