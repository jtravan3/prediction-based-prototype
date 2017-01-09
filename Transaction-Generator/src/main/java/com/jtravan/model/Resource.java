package com.jtravan.model;

/**
 * Created by johnravan on 3/30/16.
 */
public enum Resource {
    A(0, false),
    B(1, false),
    C(2, false),
    D(3, false),
    E(4, false),
    F(5, false),
    G(6, false),
    H(7, false),
    I(8, false),
    J(9, false),
    K(10,false),
    L(11,false),
    M(12,false),
    N(13,false),
    O(14,false),
    P(15,false),
    Q(16,false),
    R(17,false),
    S(18,false),
    T(19,false),
    U(20,false),
    V(21,false),
    W(22,false),
    X(23,false),
    Y(24,false),
    Z(25,false);

    private final int resourceNum;
    private boolean isLocked;

    Resource(int resourceNum, boolean isLocked) {

        this.resourceNum = resourceNum;
        this.isLocked = isLocked;
    }

    public int getResourceNum() {
        return this.resourceNum;
    }

    public static final Resource getResourceByResourceNum(int resourceNum) {

        if(resourceNum == 0) {
            return A;
        } else if(resourceNum == 1) {
            return B;
        } else if(resourceNum == 2) {
            return C;
        } else if(resourceNum == 3) {
            return D;
        } else if(resourceNum == 4) {
            return E;
        } else if(resourceNum == 5) {
            return F;
        } else if(resourceNum == 6) {
            return G;
        } else if(resourceNum == 7) {
            return H;
        } else if(resourceNum == 8) {
            return I;
        } else if(resourceNum == 9) {
            return J;
        } else if(resourceNum == 10) {
            return K;
        } else if(resourceNum == 11) {
            return L;
        } else if(resourceNum == 12) {
            return M;
        } else if(resourceNum == 13) {
            return N;
        } else if(resourceNum == 14) {
            return O;
        } else if(resourceNum == 15) {
            return P;
        } else if(resourceNum == 16) {
            return Q;
        } else if(resourceNum == 17) {
            return R;
        } else if(resourceNum == 18) {
            return S;
        } else if(resourceNum == 19) {
            return T;
        } else if(resourceNum == 20) {
            return U;
        } else if(resourceNum == 21) {
            return V;
        } else if(resourceNum == 22) {
            return W;
        } else if(resourceNum == 23) {
            return X;
        } else if(resourceNum == 24) {
            return Y;
        } else if(resourceNum == 25) {
            return Z;
        } else {
            return null;
        }

    }

    public final boolean isLocked() {
        return this.isLocked;
    }

    public final synchronized void lock() {
        this.isLocked = true;
    }

    public final synchronized void unlock() {
        this.isLocked = false;
    }
}
