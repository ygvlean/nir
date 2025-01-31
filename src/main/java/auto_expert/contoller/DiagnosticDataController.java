package auto_expert.contoller;

import auto_expert.model.DiagnosticData;
import auto_expert.repository.DiagnosticDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diagnostic-data")
public class DiagnosticDataController {
    private final DiagnosticDataRepository repository;

    public DiagnosticDataController(DiagnosticDataRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public DiagnosticData saveData(@RequestBody DiagnosticData data) {
        return repository.save(data); // Сохраняем в базу
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<List<DiagnosticData>> getData(@PathVariable String vehicleId) {
        List<DiagnosticData> data = repository.findByVehicleId(vehicleId);
        if (data.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(data);
    }
}

