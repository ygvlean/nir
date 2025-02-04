package auto_expert.dto;

public class DiagnosticDataRequest {
    private String ruleName;
    private String value; // Например, "true", "45", и т.д.

    public String getRuleName() {
        return ruleName;
    }
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
