package auto_expert.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "diagnostic_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    private LocalDateTime eventTime = LocalDateTime.now();

    private String result;

    @Column(length = 1024)
    private String explanation;
}


