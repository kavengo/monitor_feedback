package ch.fhnw.cere.orchestrator.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
public class MonitorConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "monitor_tool_id")
    private MonitorTool monitorTool;

    @Column(name="monitor_manager_configuration_id")
    private Integer monitorManagerConfigurationId;

    @NotNull
    @Column(name="config_sender")
    private String configSender;
    @NotNull
    @Column(name="timestamp")
    private String timeStamp;
    @NotNull
    @Column(name="time_slot")
    private String timeSlot;
    @NotNull
    @Column(name="kafka_endpoint")
    private String kafkaEndpoint;
    @NotNull
    @Column(name="kafka_topic")
    private String kafkaTopic;
    @NotNull
    private String state;

    @Column(name="keyword_expression")
    private String keywordExpression;

    //private List<String> accounts;

    @Column(name="package_name")
    private String packageName;
    @Column(name="app_id")
    private String appId;

    public MonitorConfiguration() {
    }

    public MonitorConfiguration(MonitorTool monitorTool, Integer monitorManagerConfigurationId, String configSender, String timeStamp, String timeSlot, String kafkaEndpoint, String kafkaTopic, String state, String keywordExpression, String packageName, String appId) {
        this.monitorTool = monitorTool;
        this.monitorManagerConfigurationId = monitorManagerConfigurationId;
        this.configSender = configSender;
        this.timeStamp = timeStamp;
        this.timeSlot = timeSlot;
        this.kafkaEndpoint = kafkaEndpoint;
        this.kafkaTopic = kafkaTopic;
        this.state = state;
        this.keywordExpression = keywordExpression;
        this.packageName = packageName;
        this.appId = appId;
    }

    @Override
    public String toString() {
        return String.format(
                "MonitorConfiguration[id=%d, monitorTool='%s', monitorManagerConfigurationId=%d, configSender='%s', " +
                        "timeStamp='%s', timeSlot='%s', kafkaEndpoint='%s', " +
                        "kafkaTopic='%s', state='%s', keywordExpression='%s', packageName='%s', appId='%s']",
                id, monitorTool, monitorManagerConfigurationId, configSender,
                timeStamp, timeSlot, kafkaEndpoint,
                kafkaTopic, state, keywordExpression, packageName, appId);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MonitorTool getMonitorTool() {
        return monitorTool;
    }

    public void setMonitorTool(MonitorTool monitorTool) {
        this.monitorTool = monitorTool;
    }

    public Integer getMonitorManagerConfigurationId() {
        return monitorManagerConfigurationId;
    }

    public void setMonitorManagerConfigurationId(Integer monitorManagerConfigurationId) {
        this.monitorManagerConfigurationId = monitorManagerConfigurationId;
    }

    public String getConfigSender() {
        return configSender;
    }

    public void setConfigSender(String configSender) {
        this.configSender = configSender;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getKafkaEndpoint() {
        return kafkaEndpoint;
    }

    public void setKafkaEndpoint(String kafkaEndpoint) {
        this.kafkaEndpoint = kafkaEndpoint;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getKeywordExpression() {
        return keywordExpression;
    }

    public void setKeywordExpression(String keywordExpression) {
        this.keywordExpression = keywordExpression;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}