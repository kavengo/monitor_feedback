package ch.fhnw.cere.orchestrator.models;


import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class GeneralConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private Date createdAt;
    private Date updatedAt;

    @OneToMany(mappedBy = "generalConfiguration")
    private List<Parameter> parameters;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    public GeneralConfiguration() {
    }

    public GeneralConfiguration(String name, Date createdAt, Date updatedAt, List<Parameter> parameters) {
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return String.format(
                "GeneralConfiguration[id=%d, name='%s']",
                id, name);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}