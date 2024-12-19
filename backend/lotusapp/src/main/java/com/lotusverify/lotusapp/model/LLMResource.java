package com.lotusverify.lotusapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LLMResource {
    private ResourceType resourceType;
    private String azureOpenAIEndpoint;
    private String azureOpenAIDeploymentName;

    public LLMResource(ResourceType resourceType, String azureOpenAIEndpoint, String azureOpenAIDeploymentName) {
        this.resourceType = resourceType;
        this.azureOpenAIEndpoint = azureOpenAIEndpoint;
        this.azureOpenAIDeploymentName = azureOpenAIDeploymentName;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getAzureOpenAIEndpoint() {
        return azureOpenAIEndpoint;
    }

    public void setAzureOpenAIEndpoint(String azureOpenAIEndpoint) {
        this.azureOpenAIEndpoint = azureOpenAIEndpoint;
    }

    public String getAzureOpenAIDeploymentName() {
        return azureOpenAIDeploymentName;
    }

    public void setAzureOpenAIDeploymentName(String azureOpenAIDeploymentName) {
        this.azureOpenAIDeploymentName = azureOpenAIDeploymentName;
    }

    @Override
    public String toString() {
        return "LLMResource{" +
                "resourceType=" + resourceType +
                ", azureOpenAIEndpoint='" + azureOpenAIEndpoint + '\'' +
                ", azureOpenAIDeploymentName='" + azureOpenAIDeploymentName + '\'' +
                '}';
    }
}
