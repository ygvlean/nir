package auto_expert.contoller;

import auto_expert.model.DiagnosticData;
import auto_expert.model.DiagnosticLog;
import auto_expert.service.DiagnosticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/diagnostics")
@RequiredArgsConstructor
public class DiagnosticController {
    private final DiagnosticService diagnosticService;

    @PostMapping("/addData")
    public ResponseEntity<String> addDiagnosticData(@RequestBody DiagnosticData data) {
        diagnosticService.addData(data);
        return ResponseEntity.ok("Data added successfully");
    }

    @DeleteMapping("/deleteData/{id}")
    public ResponseEntity<String> deleteDiagnosticData(@PathVariable Long id) {
        diagnosticService.deleteData(id);
        return ResponseEntity.ok("Data deleted successfully");
    }

    @GetMapping("/run")
    public ResponseEntity<DiagnosticLog> runDiagnostics() {
        DiagnosticLog result = diagnosticService.runDiagnostics();
        return ResponseEntity.ok(result);
    }
}
