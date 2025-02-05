package auto_expert.model;
import jakarta.persistence.*;
import lombok.*
        ;
@Entity
@Getter
@Setter
@Table(name = "rule_dependencies")
public class RuleDependency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parent_rule_id", nullable = false)
    private Rule parentRule;

    @ManyToOne
    @JoinColumn(name = "child_rule_id", nullable = false)
    private Rule childRule;
}
