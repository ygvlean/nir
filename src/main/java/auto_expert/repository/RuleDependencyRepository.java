package auto_expert.repository;

import auto_expert.model.RuleDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RuleDependencyRepository extends JpaRepository<RuleDependency, Long> {
    List<RuleDependency> findByParentRuleId(Long parentRuleId);  // Найти все зависимые правила для родительского правила
}
