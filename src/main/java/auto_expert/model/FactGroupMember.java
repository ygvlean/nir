package auto_expert.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fact_group_members")
public class FactGroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fact_group_id", nullable = false)
    private FactGroup factGroup;

    @Column(nullable = false)
    private String parameterName;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FactGroup getFactGroup() { return factGroup; }
    public void setFactGroup(FactGroup factGroup) { this.factGroup = factGroup; }

    public String getParameterName() { return parameterName; }
    public void setParameterName(String parameterName) { this.parameterName = parameterName; }
}
