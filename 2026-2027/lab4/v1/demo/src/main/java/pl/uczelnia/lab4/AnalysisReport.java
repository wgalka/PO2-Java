package pl.uczelnia.lab4;

import jakarta.persistence.*;

@Entity
@Table(name = "analysis_report")
public class AnalysisReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false)
    private EconomicDataset dataset;

    @Column(name = "method_name", nullable = false, length = 80)
    private String methodName;

    @Column(name = "r_squared")
    private Double rSquared;

    @Column(length = 255)
    private String summary;

    protected AnalysisReport() {}

    public AnalysisReport(String methodName, Double rSquared, String summary) {
        this.methodName = methodName;
        this.rSquared = rSquared;
        this.summary = summary;
    }

    public Long getId() { return id; }
    public EconomicDataset getDataset() { return dataset; }
    public void setDataset(EconomicDataset dataset) { this.dataset = dataset; }
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public Double getRSquared() { return rSquared; }
    public void setRSquared(Double rSquared) { this.rSquared = rSquared; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
