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
public class DepositResponse {
    private int id;
    private double balance;
    private String accountNumber;
    private List<Transaction> transactions;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class Transaction {
    private int id;
    private double amount;
    private String type;
    private String timestamp;
    private int relatedAccountId;
}
