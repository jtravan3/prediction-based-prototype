package com.jtravan.model;

/**
 * Created by johnravan on 3/31/16.
 */
public enum Category {
    HCHE(1),
    HCLE(2),
    LCHE(3),
    LCLE(4);

    private final int categoryNum;

    private Category(int categoryNum) {
        this.categoryNum = categoryNum;
    }

    public int getCategoryNum() {
        return this.categoryNum;
    }

    public static final Category getCategoryByCategoryNum(int operationNum) {

        if(operationNum == 1) {
            return HCHE;
        } else if(operationNum == 2) {
            return HCLE;
        } else if(operationNum == 3) {
            return LCHE;
        } else if(operationNum == 4) {
            return LCLE;
        } else {
            return null;
        }

    }
}
