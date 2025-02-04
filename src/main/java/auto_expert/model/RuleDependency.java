package auto_expert.model;
import jakarta.persistence.*;

@Entity
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

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Rule getParentRule() { return parentRule; }
    public void setParentRule(Rule parentRule) { this.parentRule = parentRule; }

    public Rule getChildRule() { return childRule; }
    public void setChildRule(Rule childRule) { this.childRule = childRule; }
}
