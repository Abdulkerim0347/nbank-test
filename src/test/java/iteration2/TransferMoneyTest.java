    package iteration2;
    
    import generators.RandomData;
    import io.restassured.http.ContentType;
    import iteration1.BaseTest;
    import models.*;
    import org.apache.http.HttpStatus;
    import org.hamcrest.Matchers;
    import org.junit.jupiter.api.Test;
    import requests.AdminCreateUserRequester;
    import requests.DepositRequester;
    import requests.LoginUserRequester;
    import requests.TransferMoneyRequester;
    import specs.RequestSpecs;
    import specs.ResponseSpecs;
    
    import static io.restassured.RestAssured.given;
    import static io.restassured.RestAssured.responseSpecification;
    
    public class TransferMoneyTest extends BaseTest {
        @Test
        public void userCanTransferMoneyTest() {
            // create 2 users
            var userRequest1 = CreateUserRequest.builder()
                    .username(RandomData.getUsername())
                    .password(RandomData.getPassword())
                    .role(UserRole.USER.toString())
                    .build();
    
            var userRequest2 = CreateUserRequest.builder()
                    .username(RandomData.getUsername())
                    .password(RandomData.getPassword())
                    .role(UserRole.USER.toString())
                    .build();
    
            var userResponse1 = new AdminCreateUserRequester(
                    RequestSpecs.adminSpec(),
                    ResponseSpecs.entityWasCreated())
                    .post(userRequest1).extract().as(TransferMoneyResponse.class);
    
            var userResponse2 = new AdminCreateUserRequester(
                    RequestSpecs.adminSpec(),
                    ResponseSpecs.entityWasCreated())
                    .post(userRequest2).extract().as(TransferMoneyResponse.class);

            int initialDeposit = 100;
            int transferAmount = 50;
    
            // depositing some money on sender account
            var depositRequest = DepositRequest.builder()
                    .id(userResponse1.getId())
                    .balance(initialDeposit)
                    .build();
    
            new DepositRequester(
                    RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword()),
                    ResponseSpecs.requestReturnsOK())
                    .post(depositRequest);

            // money transfer request
            var transferRequest = TransferMoneyRequest.builder()
                    .senderAccountId(userResponse1.getId())
                    .receiverAccountId(userResponse2.getId())
                    .amount(transferAmount)
                    .build();
    
            var transferResponse = new TransferMoneyRequester(
                    RequestSpecs.authAsUser(userRequest1.getUsername(), userRequest1.getPassword()),
                    ResponseSpecs.requestReturnsOK())
                    .post(transferRequest).extract().as(TransferMoneyResponse.class);

        }
    }
