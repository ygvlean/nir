package auto_expert.contoller;

import auto_expert.dto.*;
import auto_expert.model.DiagnosticData;
import auto_expert.repository.*;
import auto_expert.service.DiagnosticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticController {

    private final DiagnosticDataRepository diagnosticDataRepository;
    private final DiagnosticService diagnosticService;

    @Autowired
    public DiagnosticController(DiagnosticDataRepository diagnosticDataRepository,
                                DiagnosticService diagnosticService) {
        this.diagnosticDataRepository = diagnosticDataRepository;
        this.diagnosticService = diagnosticService;
    }

    /**
     * Эндпоинт для добавления диагностических входных данных.
     * Пример запроса:
     * {
     *   "ruleName": "LowCoolantLevel",
     *   "value": "45"
     * }
     */
    @PostMapping("/addData")
    public ResponseEntity<String> addDiagnosticData(@RequestBody DiagnosticDataRequest request) {
        DiagnosticData data = new DiagnosticData();
        data.setRuleName(request.getRuleName());
        data.setValue(request.getValue());
        data.setEventTime(LocalDateTime.now());
        diagnosticDataRepository.save(data);
        return ResponseEntity.ok("Diagnostic data added for rule: " + request.getRuleName());
    }

    /**
     * Эндпоинт для запуска диагностики.
     * Сервис анализирует текущие входные данные и возвращает:
     * - Окончательный диагноз, если все данные собраны, либо
     * - Список недостающих данных (названия правил), которые нужно добавить для продолжения диагностики.
     */
    @PostMapping("/run")
    public ResponseEntity<DiagnosisResult> runDiagnostics() {
        // Метод processDiagnosticsAndReturnResult должен проводить диагностику
        // и возвращать объект DiagnosisResult, содержащий либо финальный диагноз,
        // либо список недостающих данных и пояснение.
        DiagnosisResult result = diagnosticService.processDiagnosticsAndReturnResult();
        return ResponseEntity.ok(result);
    }
}
