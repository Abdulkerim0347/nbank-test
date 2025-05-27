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
public class TransferMoneyResponse {
    private int id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<String> accounts;

    private int receiverAccountId;
    private int senderAccountId;
    private double amount;
    private String message;
}
