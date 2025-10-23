package api.models;

import api.configs.Config;
import api.generators.GeneratingRule;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CreateUserRequest extends BaseModel {
    @GeneratingRule(regex = "^[A-Za-z0-9]{3,15}$")
    private String username;
    @GeneratingRule(regex = "^[A-Z]{3}[a-z]{4}[0-9]{3}[$%&]{2}$")
    private String password;
    @GeneratingRule(regex = "^USER$")
    private String role;

    public static CreateUserRequest getAdmin() {
        return CreateUserRequest.builder().username(Config.getProperty("admin.username"))
                .password(Config.getProperty("admin.password")).build();
    }
}
