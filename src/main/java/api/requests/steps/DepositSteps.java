package api.requests.steps;

import api.models.BaseAccountResponse;
import api.models.CreateUserRequest;
import api.models.DepositRequest;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;

public class DepositSteps {
    public static BaseAccountResponse createAccount(CreateUserRequest userRequest) {
        return StepLogger.log("User " + userRequest.getUsername() + " creates account", () -> {
            return new ValidatedCrudRequester<BaseAccountResponse>(
                    RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                    Endpoint.ACCOUNTS,
                    ResponseSpecs.entityWasCreated())
                    .post(null);
        });
    }


    public static BaseAccountResponse depositMoney(CreateUserRequest userRequest, BaseAccountResponse account, double deposit) {
        return StepLogger.log("User " + userRequest.getUsername() + " deposits money", () -> {
            var depositRequest = DepositRequest.builder()
                    .id(account.getId())
                    .balance(deposit)
                    .build();

            return new ValidatedCrudRequester<BaseAccountResponse>(
                    RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                    Endpoint.DEPOSIT,
                    ResponseSpecs.requestReturnsOK())
                    .post(depositRequest);
        });
    }
}
