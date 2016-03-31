package com.jtravan.model;

/**
 * Created by johnravan on 3/31/16.
 */
public enum Category {
    HCHE(0),
    HCLE(1),
    LCHE(2),
    LCLE(3);

    private final int categoryNum;

    private Category(int categoryNum) {
        this.categoryNum = categoryNum;
    }

    public int getCategoryNum() {
        return this.categoryNum;
    }

    public static final Category getCategoryByCategoryNum(int operationNum) {

        if(operationNum == 0) {
            return HCHE;
        } else if(operationNum == 1) {
            return HCLE;
        } else if(operationNum == 2) {
            return LCHE;
        } else if(operationNum == 3) {
            return LCLE;
        } else {
            return null;
        }

    }
}
