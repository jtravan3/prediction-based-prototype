package com.jtravan.model;

/**
 * Created by johnravan on 3/30/16.
 */
public class ResourceOperation {

    private Operation operation;
    private Resource resource;
    private int executionTime;
    private Transaction associatedTransaction;
    private boolean isCommitOperation;

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

    public Transaction getAssociatedTransaction() {
        return associatedTransaction;
    }

    public void setAssociatedTransaction(Transaction associatedTransaction) {
        this.associatedTransaction = associatedTransaction;
    }

    public boolean isCommitOperation() {
        return isCommitOperation;
    }

    public void setIsCommitOperation(boolean commitOperation) {
        isCommitOperation = commitOperation;
    }

    @Override
    public String toString() {
        if(isCommitOperation) {
            return "COMMIT";
        }

        if(resource == null || operation == null) {
            return "";
        }

        return resource.name() + "_" + operation.name() + " - " + executionTime + "secs";
    }
}
