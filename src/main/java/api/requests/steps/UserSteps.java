package api.requests.steps;

import api.models.BaseAccountResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;

import java.util.List;

public class UserSteps {
    private String username;
    private String password;

    public UserSteps(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public List<BaseAccountResponse> getAllAccounts() {
        return StepLogger.log("User " + username + " gets all accounts", () -> {
            return new ValidatedCrudRequester<BaseAccountResponse>(
                    RequestSpecs.authAsUser(username, password),
                    Endpoint.CUSTOMER_ACCOUNTS,
                    ResponseSpecs.requestReturnsOK()).getAll(BaseAccountResponse[].class);
        });
    }
}
