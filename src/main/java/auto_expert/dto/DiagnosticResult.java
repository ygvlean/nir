package auto_expert.dto;

import java.util.List;

public class DiagnosticResult {
    private List<String> diagnoses;
    private List<String> explanations;
    private List<String> recommendations;

    public DiagnosticResult(List<String> diagnoses, List<String> explanations, List<String> recommendations) {
        this.diagnoses = diagnoses;
        this.explanations = explanations;
        this.recommendations = recommendations;
    }

    public List<String> getDiagnoses() { return diagnoses; }
    public List<String> getExplanations() { return explanations; }
    public List<String> getRecommendations() { return recommendations; }
}

