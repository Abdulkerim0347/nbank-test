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
                .post(request).extract().as(ChangeNameResponse.class);

        softly.assertThat(response.getMessage()).isEqualTo("Profile updated successfully");
        softly.assertThat(response.getCustomer().getName()).isEqualTo(request.getName());
    }
}
