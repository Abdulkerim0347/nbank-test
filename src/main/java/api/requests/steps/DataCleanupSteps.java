package api.requests.steps;

import api.models.BaseUserResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

public class DataCleanupSteps {
    
    /**
     * Clean up a specific user by username
     */
    public static void cleanupUser(String username) {
        try {
            new ValidatedCrudRequester<BaseUserResponse>(
                    RequestSpecs.adminSpec(),
                    Endpoint.ADMIN_USER,
                    ResponseSpecs.requestReturnsOK())
                    .delete("/" + username);
        } catch (Exception e) {
            // Log but don't fail the test if cleanup fails
            System.out.println("Failed to cleanup user: " + username + " - " + e.getMessage());
        }
    }
    
    /**
     * Clean up all test users (users with test prefixes)
     */
    public static void cleanupAllTestUsers() {
        try {
            List<BaseUserResponse> users = new ValidatedCrudRequester<BaseUserResponse>(
                    RequestSpecs.adminSpec(),
                    Endpoint.ADMIN_USER,
                    ResponseSpecs.requestReturnsOK()).getAll(BaseUserResponse[].class);
            
            for (BaseUserResponse user : users) {
                if (user.getUsername() != null && 
                    (user.getUsername().startsWith("test") || 
                     user.getUsername().startsWith("user"))) {
                    cleanupUser(user.getUsername());
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to cleanup test users: " + e.getMessage());
        }
    }
}
