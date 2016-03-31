package com.jtravan.model;

/**
 * Created by johnravan on 3/30/16.
 */
public enum Resource {
    A(0),
    B(1),
    C(2),
    D(3),
    E(4),
    F(5),
    G(6),
    H(7),
    I(8),
    J(9),
    K(10),
    L(11),
    M(12),
    N(13),
    O(14),
    P(15),
    Q(16),
    R(17),
    S(18),
    T(19),
    U(20),
    V(21),
    W(22),
    X(23),
    Y(24),
    Z(25);

    private final int resourceNum;

    private Resource(int resourceNum) {
        this.resourceNum = resourceNum;
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
}
