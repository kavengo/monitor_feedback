package ch.fhnw.cere.orchestrator.models;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name="`key`")
    private String key;
    private String value;
    private Date createdAt;
    private Date updatedAt;
    private String language;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="parent_parameter_id")
    private Parameter parentParameter;
    @OneToMany(mappedBy="parentParameter")
    private List<Parameter> parameters;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="general_configuration_id")
    private GeneralConfiguration generalConfiguration;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="mechanism_id")
    private Mechanism mechanism;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    public Parameter() {

    }

    public Parameter(String key, String value, Date createdAt, Date updatedAt, String language, Parameter parentParameter, List<Parameter> parameters, GeneralConfiguration generalConfiguration, Mechanism mechanism) {
        this.key = key;
        this.value = value;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.language = language;
        this.parentParameter = parentParameter;
        this.parameters = parameters;
        this.generalConfiguration = generalConfiguration;
        this.mechanism = mechanism;
    }

    public Parameter(String key, String value, Date createdAt, Date updatedAt, String language, Parameter parentParameter, GeneralConfiguration generalConfiguration, Mechanism mechanism) {
        this.key = key;
        this.value = value;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.language = language;
        this.parentParameter = parentParameter;
        this.generalConfiguration = generalConfiguration;
        this.mechanism = mechanism;
    }

    @Override
    public String toString() {
        return String.format(
                "Parameter[id=%d, key='%s', vale='%s']",
                id, key, value);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public Parameter getParentParameter() {
        return parentParameter;
    }

    public void setParentParameter(Parameter parentParameter) {
        this.parentParameter = parentParameter;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public Mechanism getMechanism() {
        return mechanism;
    }

    public void setMechanism(Mechanism mechanism) {
        this.mechanism = mechanism;
    }

    public GeneralConfiguration getGeneralConfiguration() {
        return generalConfiguration;
    }

    public void setGeneralConfiguration(GeneralConfiguration generalConfiguration) {
        this.generalConfiguration = generalConfiguration;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}




