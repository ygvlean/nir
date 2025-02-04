package auto_expert.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rule {
    @Id
    private String ruleName;

    private String parameterName;
    private String comparisonOperator;
    private BigDecimal thresholdValue;
    private String textValue;
    private String action;
    private String ruleType;
}

