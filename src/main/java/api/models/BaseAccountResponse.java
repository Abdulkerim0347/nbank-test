package api.models;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class BaseAccountResponse extends BaseModel {
    private int id;
    private String accountNumber;
    private double balance;
    private List<Transaction> transactions;
}
