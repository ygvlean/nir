package auto_expert.service;

import auto_expert.dto.DiagnosisResult;
import auto_expert.model.*;
import auto_expert.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DiagnosticService {
    private final RuleRepository ruleRepository;
    private final RuleCombinationRepository ruleCombinationRepository;
    private final DiagnosticDataRepository diagnosticDataRepository;
    private final DiagnosticLogRepository diagnosticLogRepository;

    public void addData(DiagnosticData data) {
        diagnosticDataRepository.save(data);
    }

    public void deleteData(Long id) {
        diagnosticDataRepository.deleteById(id);
    }

    public DiagnosticLog runDiagnostics() {
        List<DiagnosticData> inputData = diagnosticDataRepository.findAll();
        List<Rule> activeRules = determineActiveRules(inputData);

        String result = "Diagnosis Incomplete";
        String explanation = "Missing required inputs for final diagnosis.";

        for (Rule rule : activeRules) {
            if ("final".equalsIgnoreCase(rule.getRuleType())) {
                result = rule.getRuleName();
                explanation = "Diagnosis completed successfully.";
                break;
            }
        }

        DiagnosticLog log = new DiagnosticLog();
        log.setResult(result);
        log.setExplanation(explanation);
        return diagnosticLogRepository.save(log);
    }

    private List<Rule> determineActiveRules(List<DiagnosticData> inputData) {
        return ruleRepository.findAll(); // Заглушка, заменим на реальную логику позже
    }
}

