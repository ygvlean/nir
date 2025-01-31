package auto_expert.repository;

import auto_expert.model.DiagnosticData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiagnosticDataRepository extends JpaRepository<DiagnosticData, Long> {
    List<DiagnosticData> findByVehicleId(String vehicleId);
}

