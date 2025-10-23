package api.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class LoginUserRequest extends BaseModel {
    private String username;
    private String password;
}
