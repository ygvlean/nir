package auto_expert.repository;

import auto_expert.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
    List<Rule> findByParameterName(String parameterName);  // Найти все правила по параметру
}

