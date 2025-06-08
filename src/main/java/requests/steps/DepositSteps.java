package requests.steps;

import models.*;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class DepositSteps {
    public static BaseAccountResponse createAccount(CreateUserRequest userRequest) {
        return new ValidatedCrudRequester<BaseAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);
    }

    public static BaseAccountResponse depositMoney(CreateUserRequest userRequest, BaseAccountResponse account, double deposit) {
        var depositRequest = DepositRequest.builder()
                .id(account.getId())
                .balance(deposit)
                .build();

        return new ValidatedCrudRequester<BaseAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);
    }
}
