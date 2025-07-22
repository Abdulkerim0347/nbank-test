package iteration1.api;

import api.models.BaseAccountResponse;
import org.junit.jupiter.api.Test;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        var userRequest = AdminSteps.createUser();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);


        var account = new ValidatedCrudRequester<BaseAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

        var accounts = new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .get(null)
                .extract()
                .jsonPath()
                .getList("", BaseAccountResponse.class);

        assertTrue(accounts.stream().anyMatch(a -> a.getId() == account.getId()),
                "Created account should exist in the account list");
    }
}
