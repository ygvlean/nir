package auto_expert.service;

import auto_expert.dto.DiagnosticResult;
import auto_expert.model.*;
import auto_expert.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DiagnosticService {
    private final DiagnosticDataRepository diagnosticDataRepository;
    private final RuleRepository ruleRepository;
    private final RuleDependencyRepository ruleDependencyRepository;
    private final ExplanationRepository explanationRepository;
    private final RecommendationRepository recommendationRepository;
    private final DiagnosticLogRepository diagnosticLogRepository;
    private final FactGroupMemberRepository factGroupMemberRepository;

    public DiagnosticService(
            DiagnosticDataRepository diagnosticDataRepository,
            RuleRepository ruleRepository,
            RuleDependencyRepository ruleDependencyRepository,
            ExplanationRepository explanationRepository,
            RecommendationRepository recommendationRepository,
            DiagnosticLogRepository diagnosticLogRepository,
            FactGroupMemberRepository factGroupMemberRepository) {
        this.diagnosticDataRepository = diagnosticDataRepository;
        this.ruleRepository = ruleRepository;
        this.ruleDependencyRepository = ruleDependencyRepository;
        this.explanationRepository = explanationRepository;
        this.recommendationRepository = recommendationRepository;
        this.diagnosticLogRepository = diagnosticLogRepository;
        this.factGroupMemberRepository = factGroupMemberRepository;
    }

    public DiagnosticResult diagnoseVehicle(String vehicleId) {
        List<DiagnosticData> facts = diagnosticDataRepository.findByVehicleId(vehicleId);
        Set<Rule> triggeredRules = new HashSet<>();

        // 1️⃣ Проверяем факты по правилам
        for (DiagnosticData fact : facts) {
            List<Rule> rules = ruleRepository.findByParameterName(fact.getParameterName());
            for (Rule rule : rules) {
                if (evaluateCondition(fact.getParameterValue(), rule)) {
                    triggeredRules.add(rule);
                }
            }
        }

        // 2️⃣ Формируем список диагностируемых систем ДО обработки зависимостей
        Set<String> diagnosedSystems = new HashSet<>();
        for (Rule rule : triggeredRules) {
            List<FactGroupMember> relatedGroups = factGroupMemberRepository.findByParameterName(rule.getParameterName());
            for (FactGroupMember group : relatedGroups) {
                diagnosedSystems.add(group.getFactGroup().getName());
            }
        }

        // 3️⃣ Проверяем зависимости между правилами, но ТОЛЬКО в диагностируемых системах
        Set<Rule> finalTriggeredRules = new HashSet<>(triggeredRules);
        Queue<Rule> queue = new LinkedList<>(triggeredRules);
        while (!queue.isEmpty()) {
            Rule parentRule = queue.poll();
            List<RuleDependency> dependencies = ruleDependencyRepository.findByParentRuleId(parentRule.getId());
            for (RuleDependency dependency : dependencies) {
                Rule childRule = dependency.getChildRule();

                // Фильтруем только те правила, которые принадлежат к диагностируемым системам
                List<FactGroupMember> childGroups = factGroupMemberRepository.findByParameterName(childRule.getParameterName());
                boolean isRelevant = childGroups.stream().anyMatch(group -> diagnosedSystems.contains(group.getFactGroup().getName()));

                if (isRelevant && !finalTriggeredRules.contains(childRule)) {
                    finalTriggeredRules.add(childRule);
                    queue.add(childRule);
                }
            }
        }

        // 4️⃣ Проверяем, есть ли достаточные данные в этих системах
        Set<Rule> missingDataRules = new HashSet<>();
        for (String system : diagnosedSystems) {
            List<FactGroupMember> groupMembers = factGroupMemberRepository.findAll(); // Получаем все параметры системы

            boolean hasEnoughData = facts.stream()
                    .anyMatch(f -> groupMembers.stream()
                            .anyMatch(gm -> gm.getParameterName().equals(f.getParameterName()) && diagnosedSystems.contains(system)));

            if (!hasEnoughData) {
                missingDataRules.addAll(
                        ruleRepository.findAll().stream()
                                .filter(rule -> groupMembers.stream()
                                        .anyMatch(gm -> gm.getParameterName().equals(rule.getParameterName()) && diagnosedSystems.contains(system)))
                                .toList()
                );
            }
        }

        // 5️⃣ Собираем недостающие параметры ТОЛЬКО из этих систем
        List<String> missingParameters = missingDataRules.stream()
                .map(Rule::getParameterName)
                .distinct()
                .toList();

        // Проверяем, является ли текущий диагноз только симптомом без выявленной причины
        Set<Rule> missingCauseRules = new HashSet<>();
        for (Rule rule : finalTriggeredRules) {
            List<RuleDependency> dependencies = ruleDependencyRepository.findByParentRuleId(rule.getId());
            for (RuleDependency dependency : dependencies) {
                Rule childRule = dependency.getChildRule();
                boolean hasFact = facts.stream()
                        .anyMatch(f -> f.getParameterName().equals(childRule.getParameterName()));

                if (!hasFact) {
                    missingCauseRules.add(childRule);
                }
            }
        }

// 🔥 Теперь фильтруем недостающие параметры ТОЛЬКО из систем, которые уже диагностируются
        List<String> missingCauseParameters = missingCauseRules.stream()
                .filter(rule -> {
                    List<FactGroupMember> groups = factGroupMemberRepository.findByParameterName(rule.getParameterName());
                    return groups.stream().anyMatch(group -> diagnosedSystems.contains(group.getFactGroup().getName())); // Только связанные системы!
                })
                .map(Rule::getParameterName)
                .distinct()
                .toList();


        // 7️⃣ Если диагноз — это только симптом (например, "низкое давление масла"), но причина не найдена, запрашиваем недостающие параметры
        if (!missingCauseParameters.isEmpty()) {
            return new DiagnosticResult(
                    List.of("Недостаточно данных для точного диагноза"),
                    List.of("Для точного выявления причины неисправности требуется больше данных."),
                    List.of("Добавьте данные по параметрам: " + String.join(", ", missingCauseParameters))
            );
        }

        // 8️⃣ Формируем список диагнозов, фильтруя нерелевантные проблемы
        List<String> diagnoses = new ArrayList<>();
        List<String> explanations = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        for (Rule rule : finalTriggeredRules) {
            List<FactGroupMember> groups = factGroupMemberRepository.findByParameterName(rule.getParameterName());
            boolean isRelevant = groups.stream().anyMatch(group -> diagnosedSystems.contains(group.getFactGroup().getName()));

            if (isRelevant) {
                diagnoses.add(rule.getAction());

                explanationRepository.findByRuleId(rule.getId()).ifPresent(explanation ->
                        explanations.add(explanation.getExplanation()));

                recommendationRepository.findByDiagnosis(rule.getAction()).ifPresent(recommendation ->
                        recommendations.add(recommendation.getRecommendation()));
            }
        }

        // 9️⃣ Если проблем нет, возвращаем сообщение, что авто исправно
        if (diagnoses.isEmpty()) {
            diagnoses.add("Автомобиль исправен");
            explanations.add("Все параметры находятся в допустимых пределах.");
        }

        // 🔟 Сохраняем лог диагностики
        DiagnosticLog log = new DiagnosticLog();
        log.setVehicleId(vehicleId);
        log.setDiagnosis(String.join("; ", diagnoses));
        log.setExplanation(String.join("; ", explanations));
        diagnosticLogRepository.save(log);

        return new DiagnosticResult(diagnoses, explanations, recommendations);
    }


    private boolean evaluateCondition(double factValue, Rule rule) {
        switch (rule.getComparisonOperator()) {
            case ">": return factValue > rule.getThresholdValue();
            case "<": return factValue < rule.getThresholdValue();
            case "=": return factValue == rule.getThresholdValue();
            case "!=": return factValue != rule.getThresholdValue();
            default: throw new IllegalArgumentException("Неизвестный оператор: " + rule.getComparisonOperator());
        }
    }
}


