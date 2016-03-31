package com.jtravan.model;

/**
 * Created by johnravan on 3/30/16.
 */
public enum Operation {
    READ(1),
    WRITE(2);

    private final int operationNum;

    private Operation(int operationNum) {
        this.operationNum = operationNum;
    }

    public int getOperationNum() {
        return this.operationNum;
    }

    public static final Operation getOperationByOperationNum(int operationNum) {

        if(operationNum == 1) {
            return READ;
        } else if(operationNum == 2) {
            return WRITE;
        } else {
            return null;
        }

    }
}
