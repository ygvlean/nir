package auto_expert.service;

import auto_expert.dto.DiagnosisResult;
import auto_expert.model.*;
import auto_expert.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DiagnosticService {

    private final DiagnosticDataRepository diagnosticDataRepository;

    private final RuleCombinationRepository ruleCombinationRepository;

    private final RuleRepository ruleRepository;

    public DiagnosticService(DiagnosticDataRepository diagnosticDataRepository, RuleCombinationRepository ruleCombinationRepository, RuleRepository ruleRepository) {
        this.diagnosticDataRepository = diagnosticDataRepository;
        this.ruleCombinationRepository = ruleCombinationRepository;
        this.ruleRepository = ruleRepository;
    }

    /**
     * Выполняет диагностику и возвращает результат:
     * - Если все данные для финального правила собраны, возвращает финальный диагноз.
     * - Если нет, возвращает список недостающих сигналов для продолжения диагностики.
     */
    public DiagnosisResult processDiagnosticsAndReturnResult() {
        DiagnosisResult result = new DiagnosisResult();

        // 1. Считываем все входные данные
        List<DiagnosticData> dataList = diagnosticDataRepository.findAll();
        // Формируем множество активных сигналов (например, "LowCoolantLevel", "EngineOverheat", etc.)
        Set<String> activeInputs = new HashSet<>();
        for (DiagnosticData data : dataList) {
            activeInputs.add(data.getRuleName());
        }

        // 2. Получаем все комбинации и группируем их по целевому правилу
        List<RuleCombination> combinations = ruleCombinationRepository.findAll();
        // Map: ключ - имя правила, значение - список входных сигналов, необходимых для этого правила
        Map<String, List<String>> comboMap = new HashMap<>();
        for (RuleCombination rc : combinations) {
            comboMap.computeIfAbsent(rc.getFinalRuleName(), k -> new ArrayList<>())
                    .add(rc.getComponentRuleName());
        }

        // 3. Итеративно определяем активные правила (начиная с базовых входов)
        // Пока мы не достигли стабильного состояния
        Set<String> activeRules = new HashSet<>(activeInputs);
        boolean updated;
        do {
            updated = false;
            for (Map.Entry<String, List<String>> entry : comboMap.entrySet()) {
                String targetRule = entry.getKey();
                List<String> requiredComponents = entry.getValue();
                if (!activeRules.contains(targetRule) && activeRules.containsAll(requiredComponents)) {
                    activeRules.add(targetRule);
                    updated = true;
                }
            }
        } while (updated);

        // 4. Теперь проверяем, есть ли финальное правило среди активных
        Optional<Rule> finalRuleOpt = activeRules.stream()
                .map(ruleRepository::findById)  // явная лямбда, чтобы тип был String -> Optional<Rule>
                .filter(Optional::isPresent)
                .map(Optional::get)          // преобразуем Optional<Rule> в Rule
                .filter(rule -> "final".equalsIgnoreCase(rule.getRuleType()))
                .findFirst();


        if (finalRuleOpt.isPresent()) {
            // Если финальное правило найдено, возвращаем его как диагноз
            Rule finalRule = finalRuleOpt.get();
            result.setFinalDiagnosis(finalRule.getTextValue());
            result.setExplanation("Финальный диагноз сформирован на основе активных правил: " + activeRules);
            // Можно оставить missingData пустым, т.к. диагноз полный
            result.setMissingData(Collections.emptyList());
        } else {
            // Если финального правила нет, пытаемся найти правило с наибольшей полнотой или берем одно из
            // Например, возьмем правило, для которого недостающих входов меньше всего
            String bestCandidate = null;
            List<String> missingForBest = null;
            for (Map.Entry<String, List<String>> entry : comboMap.entrySet()) {
                String targetRule = entry.getKey();
                List<String> requiredComponents = entry.getValue();
                // Если targetRule уже активировано, возможно оно промежуточное и может быть возвращено
                if (activeRules.contains(targetRule)) {
                    bestCandidate = targetRule;
                    missingForBest = Collections.emptyList();
                    break;
                } else {
                    // Вычисляем, каких компонентов не хватает
                    List<String> missing = new ArrayList<>();
                    for (String component : requiredComponents) {
                        if (!activeRules.contains(component)) {
                            missing.add(component);
                        }
                    }
                    // Выбираем правило, у которого недостающих компонентов меньше
                    if (bestCandidate == null || missing.size() < missingForBest.size()) {
                        bestCandidate = targetRule;
                        missingForBest = missing;
                    }
                }
            }
            // Формируем результат диагностики с промежуточным диагнозом
            if (bestCandidate != null) {
                Optional<Rule> candidateRuleOpt = ruleRepository.findById(bestCandidate);
                if (candidateRuleOpt.isPresent()) {
                    Rule candidateRule = candidateRuleOpt.get();
                    result.setFinalDiagnosis(candidateRule.getTextValue());
                    result.setExplanation("Промежуточный диагноз. Недостающие данные для продолжения: " + missingForBest);
                    result.setMissingData(missingForBest);
                } else {
                    result.setFinalDiagnosis("Не удалось сформировать диагноз");
                    result.setExplanation("Не найдено правило " + bestCandidate);
                    result.setMissingData(missingForBest);
                }
            } else {
                result.setFinalDiagnosis("Недостаточно данных для диагностики");
                result.setExplanation("Пожалуйста, добавьте входные данные");
                result.setMissingData(new ArrayList<>());
            }
        }
        return result;
    }
}


