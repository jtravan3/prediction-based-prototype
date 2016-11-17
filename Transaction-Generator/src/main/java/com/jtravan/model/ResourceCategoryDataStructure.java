package com.jtravan.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by johnravan on 6/22/16.
 */
public class ResourceCategoryDataStructure {


    private static ResourceCategoryDataStructure theReadInstance;
    private static ResourceCategoryDataStructure theWriteInstance;
    private Map<Resource, Heap<ResourceOperation>> resourceMinHeapMap;

    private ResourceCategoryDataStructure() {

        resourceMinHeapMap = new HashMap<Resource, Heap<ResourceOperation>>();

    }

    public static final ResourceCategoryDataStructure getReadInstance() {

        if(theReadInstance == null) {
            theReadInstance = new ResourceCategoryDataStructure();
        }
        return theReadInstance;

    }

    public static final ResourceCategoryDataStructure getWriteInstance() {

        if(theWriteInstance == null) {
            theWriteInstance = new ResourceCategoryDataStructure();
        }
        return theWriteInstance;

    }

    public ResourceOperation getHighestPriorityForResource(Resource resource) {
        if(resource == null) {
            return null;
        }

        Heap<ResourceOperation> resourceOperationHeap = resourceMinHeapMap.get(resource);

        if(resourceOperationHeap == null) {
            resourceMinHeapMap.put(resource, new Heap<ResourceOperation>(new ResourceOperationComparator()));
            return null;
        } else {
            return resourceOperationHeap.top();
        }
    }

    public void insertResourceOperationForResource(Resource resource, ResourceOperation resourceOperation) {

        if(resourceOperation.isCommitOperation()) {
            return;
        }

        if(resource == null || resourceOperation == null) {
            throw new IllegalArgumentException("Resource or Resource Operation is null");
        }

        Heap<ResourceOperation> resourceOperationHeap = resourceMinHeapMap.get(resource);
        if(resourceOperationHeap == null) {
            resourceOperationHeap = new Heap<ResourceOperation>(new ResourceOperationComparator());
            resourceOperationHeap.insert(resourceOperation);
            resourceMinHeapMap.put(resource, resourceOperationHeap);
        } else {
            resourceOperationHeap.insert(resourceOperation);
        }
    }

    public Set<Resource> getResourceSet() {
        return resourceMinHeapMap.keySet();
    }

    public Heap getHeapForResource(Resource resource) {
        return resourceMinHeapMap.get(resource);
    }

    public void clearHeapForResource(Resource resource) {
        resourceMinHeapMap.get(resource).clear();
        resourceMinHeapMap.put(resource, null);
    }

    public void printHeap(Heap heap) {
        for (int i = 0; i <= heap.size() / 2; i++) {

            // parent
            int parentIndex = i;
            String parent;
            HeapNode heapNode = (HeapNode)heap.getHeapNodes().get(parentIndex);
            ResourceOperation parentRO = (ResourceOperation)heapNode.element;
            parent = parentRO.getAssociatedTransaction().getCategory().name();

            // right child
            int rightChildIndex = 2 * i + 1;
            String rightChild = getChildString(rightChildIndex, heap);

            // left child
            int leftChildIndex = 2 * i;
            String leftChild = getChildString(leftChildIndex, heap);

            System.out.print(" PARENT : " + parent + " LEFT CHILD : " + leftChild + " RIGHT CHILD :" + rightChild);
            System.out.println();
        }
    }

    private String getChildString(int index, Heap heap) {
        String rtnString;
        if(index >= heap.size()) {
            rtnString = "NULL";
        } else {
            HeapNode heapNode =  (HeapNode)heap.getHeapNodes().get(index);
            ResourceOperation resourceOperation = (ResourceOperation)heapNode.element;
            rtnString = resourceOperation.getAssociatedTransaction().getCategory().name();
        }
        return rtnString;
    }

    private class ResourceOperationComparator implements Comparator<ResourceOperation> {

        public int compare(ResourceOperation o1, ResourceOperation o2) {
            int category1 = o1.getAssociatedTransaction().getCategory().getCategoryNum();
            int category2 = o2.getAssociatedTransaction().getCategory().getCategoryNum();

            if(category1 > category2) {
                return 1;
            } else if (category1 < category2) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
