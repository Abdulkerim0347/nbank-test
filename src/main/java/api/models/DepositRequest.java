package api.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class DepositRequest extends BaseModel {
    private int id;
    private double balance;
}
