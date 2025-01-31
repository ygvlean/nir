package auto_expert.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "fact_groups")
public class FactGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "factGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FactGroupMember> members;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<FactGroupMember> getMembers() { return members; }
    public void setMembers(Set<FactGroupMember> members) { this.members = members; }
}
