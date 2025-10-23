package api.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ChangeNameRequest extends BaseModel {
    private String name;
}
