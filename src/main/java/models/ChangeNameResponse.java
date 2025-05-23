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
public class ChangeNameResponse {
    private String message;
    private Customer customer;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class Customer {
    private long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<String> accounts;
}
