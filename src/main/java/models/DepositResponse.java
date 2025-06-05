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
public class DepositResponse extends BaseModel {
    private int id;
    private double balance;
    private String accountNumber;
    private List<Transaction> transactions;
}

