package auto_expert.contoller;

import auto_expert.dto.DiagnosticResult;
import auto_expert.service.DiagnosticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diagnostics")
public class DiagnosticController {
    private final DiagnosticService diagnosticService;

    public DiagnosticController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<DiagnosticResult> diagnose(@PathVariable String vehicleId) {
        DiagnosticResult result = diagnosticService.diagnoseVehicle(vehicleId);
        return ResponseEntity.ok(result);
    }
}
