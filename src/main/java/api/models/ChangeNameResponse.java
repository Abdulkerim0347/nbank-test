package api.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ChangeNameResponse extends BaseModel {
    private String message;
    private BaseUserResponse customer;
}

