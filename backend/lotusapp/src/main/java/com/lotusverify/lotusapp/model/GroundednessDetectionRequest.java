package com.lotusverify.lotusapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroundednessDetectionRequest {
    private TaskName taskName;
    private String text; // Ejemplo de un campo adicional requerido
    private DomainType domainType; // Ejemplo de otro campo requerido

    // Constructor
    public GroundednessDetectionRequest(TaskName taskName, String text, DomainType domainType) {
        this.taskName = taskName;
        this.text = text;
        this.domainType = domainType;
    }

    // Getters y Setters
    public TaskName getTaskName() {
        return taskName;
    }

    public void setTaskName(TaskName taskName) {
        this.taskName = taskName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DomainType getDomainType() {
        return domainType;
    }

    public void setDomainType(DomainType domainType) {
        this.domainType = domainType;
    }

    @Override
    public String toString() {
        return "GroundednessDetectionRequest{" +
                "taskName=" + taskName +
                ", text='" + text + '\'' +
                ", domainType=" + domainType +
                '}';
    }
}
