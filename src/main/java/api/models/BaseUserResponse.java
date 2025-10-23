package api.models;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class BaseUserResponse extends BaseModel {
    private int id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<BaseAccountResponse> accounts;
}
