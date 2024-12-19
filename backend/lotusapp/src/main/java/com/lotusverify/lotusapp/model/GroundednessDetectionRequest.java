package com.lotusverify.lotusapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroundednessDetectionRequest {
    private String task;
    private String domain;
    private String text;
    private String[] groundingSources;
    private boolean reasoning;
    private QnA qna;
    private LLMResource llmResource;

    public GroundednessDetectionRequest(TaskName taskName, DomainType domain, String text, String[] groundingSources,
                                        boolean reasoning, String query, LLMResource llmResource) {
        this.task = taskName.toString();
        this.domain = domain.toString();
        this.text = text;
        this.groundingSources = groundingSources;
        this.reasoning = reasoning;
        if (TaskName.QnA == taskName) {
            this.qna = new QnA(query);
        }
        this.llmResource = llmResource;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getGroundingSources() {
        return groundingSources;
    }

    public void setGroundingSources(String[] groundingSources) {
        this.groundingSources = groundingSources;
    }

    public boolean isReasoning() {
        return reasoning;
    }

    public void setReasoning(boolean reasoning) {
        this.reasoning = reasoning;
    }

    public QnA getQna() {
        return qna;
    }

    public void setQna(QnA qna) {
        this.qna = qna;
    }

    public LLMResource getLlmResource() {
        return llmResource;
    }

    public void setLlmResource(LLMResource llmResource) {
        this.llmResource = llmResource;
    }

    @Override
    public String toString() {
        return "GroundednessDetectionRequest{" +
                "task='" + task + '\'' +
                ", domain='" + domain + '\'' +
                ", text='" + text + '\'' +
                ", groundingSources=" + String.join(", ", groundingSources) +
                ", reasoning=" + reasoning +
                ", qna=" + qna +
                ", llmResource=" + llmResource +
                '}';
    }
}
