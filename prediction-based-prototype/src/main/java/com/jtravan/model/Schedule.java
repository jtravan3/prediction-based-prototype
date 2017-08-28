package com.jtravan.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by johnravan on 4/10/16.
 */
public class Schedule {

    private List<ResourceOperation> resourceOperationList;
    private Category category;

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
        this.resourceOperationList = new LinkedList<ResourceOperation>(resourceOperationList);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for(ResourceOperation resourceOperation: resourceOperationList) {

            builder.append(resourceOperation.toString());
            builder.append(", ");
        }

        builder.append(category);
        builder.append(" - END");

        return builder.toString();
    }

}
