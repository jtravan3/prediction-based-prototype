package com.jtravan.model;

/**
 * Created by johnravan on 3/30/16.
 */
public class ResourceOperation {

    private Operation operation;
    private Resource resource;
    private int executionTime;

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public String toString() {
        return resource.name() + "_" + operation.name() + "-" + executionTime;
    }
}
