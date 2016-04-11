package com.jtravan.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by johnravan on 4/10/16.
 */
public class Schedule {

    private List<ResourceOperation> resourceOperationList;

    public Schedule() {
        resourceOperationList = new LinkedList<ResourceOperation>();
    }

    public void addResourceOperation(ResourceOperation resourceOperation) {
        if(resourceOperation == null) {
            return;
        }
        resourceOperationList.add(resourceOperation);
    }

    public List<ResourceOperation> getResourceOperationList() {
        return resourceOperationList;
    }

    public void setResourceOperationList(List<ResourceOperation> resourceOperationList) {
        this.resourceOperationList = resourceOperationList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for(ResourceOperation resourceOperation: resourceOperationList) {

            builder.append(resourceOperation.toString());
            builder.append(", ");
        }

        builder.append(" - END");

        return builder.toString();
    }

}
