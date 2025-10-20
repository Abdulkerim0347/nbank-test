package iteration2.api;

import api.generators.RandomData;
import iteration1.api.BaseTest;
import api.models.ChangeNameRequest;
import api.models.ChangeNameResponse;
import org.junit.jupiter.api.Test;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public class ChangeNameTest extends BaseTest {

    @Test
    public void userCanChangeItsNameTest() {
        var userRequest = AdminSteps.createUser();

        var request = ChangeNameRequest.builder()
                .name(RandomData.getNewValidName())
                .build();

        var response = new ValidatedCrudRequester<ChangeNameResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .update(request);

        softly.assertThat(response.getMessage()).isEqualTo("Profile updated successfully");
        softly.assertThat(response.getCustomer().getName()).isEqualTo(request.getName());
    }
}