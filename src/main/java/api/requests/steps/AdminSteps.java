package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.BaseUserResponse;
import api.models.CreateUserRequest;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;

import java.util.List;

public class AdminSteps {
    public static CreateUserRequest createUser() {
        var userRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        return StepLogger.log("Admin creates user " + userRequest.getUsername(), () -> {
                new ValidatedCrudRequester<BaseUserResponse>(
                        RequestSpecs.adminSpec(),
                        Endpoint.ADMIN_USER,
                        ResponseSpecs.entityWasCreated())
                        .post(userRequest);

                return userRequest;
            }
        );
    }

    public static List<BaseUserResponse> getAllUsers() {
        return StepLogger.log("Admin gets all users", () -> {
            return new ValidatedCrudRequester<BaseUserResponse>(
                    RequestSpecs.adminSpec(),
                    Endpoint.ADMIN_USER,
                    ResponseSpecs.requestReturnsOK()).getAll(BaseUserResponse[].class);
        });
    }
}
