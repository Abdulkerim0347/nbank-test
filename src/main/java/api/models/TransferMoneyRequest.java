package api.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class TransferMoneyRequest extends BaseModel {
    private int senderAccountId;
    private int receiverAccountId;
    private double amount;
}
