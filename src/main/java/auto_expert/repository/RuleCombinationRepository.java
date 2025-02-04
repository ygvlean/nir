package auto_expert.repository;

import auto_expert.model.RuleCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RuleCombinationRepository extends JpaRepository<RuleCombination, Long> {
    List<RuleCombination> findAll();
}
