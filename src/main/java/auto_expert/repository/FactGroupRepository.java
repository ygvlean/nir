package auto_expert.repository;

import auto_expert.model.FactGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FactGroupRepository extends JpaRepository<FactGroup, Long> {
    Optional<FactGroup> findByName(String name);  // Найти группу по названию
}
