package auto_expert.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "diagnostic_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime eventTime = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "rule_name", referencedColumnName = "ruleName")
    private Rule rule;

    private String value;
}

