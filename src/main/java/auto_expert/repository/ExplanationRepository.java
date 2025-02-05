package auto_expert.repository;

import auto_expert.model.Explanation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ExplanationRepository extends JpaRepository<Explanation, Long> {
}
