package auto_expert.dto;
import java.util.List;

public class DiagnosisResult {
    private String finalDiagnosis;
    private List<String> missingData; // Список недостающих входов для продолжения диагностики
    private String explanation;

    public String getFinalDiagnosis() {
        return finalDiagnosis;
    }
    public void setFinalDiagnosis(String finalDiagnosis) {
        this.finalDiagnosis = finalDiagnosis;
    }
    public List<String> getMissingData() {
        return missingData;
    }
    public void setMissingData(List<String> missingData) {
        this.missingData = missingData;
    }
    public String getExplanation() {
        return explanation;
    }
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
