package auto_expert.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rule_combinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleCombination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long combinationId;

    @ManyToOne
    @JoinColumn(name = "final_rule_name", referencedColumnName = "ruleName")
    private Rule finalRule;

    @ManyToOne
    @JoinColumn(name = "component_rule_name", referencedColumnName = "ruleName")
    private Rule componentRule;
}