package requests.steps;

import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.BaseUserResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class AdminSteps {
    public static CreateUserRequest createUser() {
        var userRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        new ValidatedCrudRequester<BaseUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        return userRequest;
    }
}
