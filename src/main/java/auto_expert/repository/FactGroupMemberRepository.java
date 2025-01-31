package auto_expert.repository;

import auto_expert.model.FactGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FactGroupMemberRepository extends JpaRepository<FactGroupMember, Long> {
    List<FactGroupMember> findByFactGroupId(Long factGroupId);  // Найти все параметры в группе
    List<FactGroupMember> findByParameterName(String parameterName);
}
