package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserResponse extends BaseModel {
    private int id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<CreateAccountResponse> accounts;
}
