package com.jtravan.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by johnravan on 3/30/16.
 */
public class Transaction {

    private List<ResourceOperation> resourceOperationList;
    private Category category;

    public Transaction() {
        resourceOperationList = new LinkedList<ResourceOperation>();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void addResourceOperation(ResourceOperation resourceOperation) {
        resourceOperationList.add(resourceOperation);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for(ResourceOperation resourceOperation: resourceOperationList) {
            builder.append(resourceOperation.toString());
            builder.append(", ");
        }

        builder.append("COMMIT - ");
        builder.append(category.name());

        return builder.toString();
    }

}
