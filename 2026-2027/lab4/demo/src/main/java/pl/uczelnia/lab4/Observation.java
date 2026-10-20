package pl.uczelnia.lab4;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "observation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"dataset_id", "period", "region"})
})
public class Observation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false)
    private EconomicDataset dataset;

    @Column(nullable = false, length = 30)
    private String period;

    @Column(nullable = false, length = 80)
    private String region;

    @Column(name = "value_numeric", nullable = false, precision = 15, scale = 4)
    private BigDecimal valueNumeric;

    protected Observation() {}

    public Observation(String period, String region, BigDecimal valueNumeric) {
        this.period = period;
        this.region = region;
        this.valueNumeric = valueNumeric;
    }

    public Long getId() { return id; }
    public EconomicDataset getDataset() { return dataset; }
    public void setDataset(EconomicDataset dataset) { this.dataset = dataset; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public BigDecimal getValueNumeric() { return valueNumeric; }
    public void setValueNumeric(BigDecimal valueNumeric) { this.valueNumeric = valueNumeric; }
}
