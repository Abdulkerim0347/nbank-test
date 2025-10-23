package api.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class TransferMoneyResponse extends BaseModel {
    private int receiverAccountId;
    private int senderAccountId;
    private double amount;
    private String message;
}
