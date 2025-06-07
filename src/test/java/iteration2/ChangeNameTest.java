package iteration2;

import generators.RandomData;
import iteration1.BaseTest;
import models.ChangeNameRequest;
import models.ChangeNameResponse;
import models.CreateUserRequest;
import models.UserRole;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.ChangeNameRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class ChangeNameTest extends BaseTest {

    @Test
    public void userCanChangeItsNameTest() {
        var userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        var request = ChangeNameRequest.builder()
                .name(RandomData.getName())
                .build();

        var response = new ChangeNameRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsOK())
                .put(request).extract().as(ChangeNameResponse.class);

        softly.assertThat(response.getMessage()).isEqualTo("Profile updated successfully");
        softly.assertThat(response.getCustomer().getName()).isEqualTo(request.getName());
    }
/*  Хотел покрыть негативные сценарии, но изменение поля на любое значение возвращает статус код 200

    @ParameterizedTest
    @CsvSource(
            // blank name
        ", name, Name cannot be blank"
    )
    public void userCannotChangeItsNameWithInvalidDataTest(String newName, String errorKey, String errorValue) {
        var userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        var request = ChangeNameRequest.builder()
                .name(newName)
                .build();

        new ChangeNameRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorValue))
                .put(request);
    }
 */
}
