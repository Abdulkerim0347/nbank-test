package api.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class LoginUserResponse extends BaseModel {
    private String username;
    private String role;
}
