package pl.uczelnia.lab4;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dataset")
public class EconomicDataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "source_org", nullable = false, length = 100)
    private String sourceOrg;

    @Column(nullable = false, length = 30)
    private String frequency;

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Observation> observations = new ArrayList<>();

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AnalysisReport> reports = new ArrayList<>();

    protected EconomicDataset() {
        // Konstruktor bezargumentowy wymagany przez specyfikację JPA / Hibernate
    }

    public EconomicDataset(String code, String title, String sourceOrg, String frequency) {
        this.code = code;
        this.title = title;
        this.sourceOrg = sourceOrg;
        this.frequency = frequency;
    }

    public void addObservation(Observation observation) {
        observations.add(observation);
        observation.setDataset(this);
    }

    public void addReport(AnalysisReport report) {
        reports.add(report);
        report.setDataset(this);
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSourceOrg() { return sourceOrg; }
    public void setSourceOrg(String sourceOrg) { this.sourceOrg = sourceOrg; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public List<Observation> getObservations() { return observations; }
    public List<AnalysisReport> getReports() { return reports; }
}
