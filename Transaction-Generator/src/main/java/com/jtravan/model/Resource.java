package com.jtravan.model;

/**
 * Created by johnravan on 3/30/16.
 */
public enum Resource {
    A(1),
    B(2),
    C(3),
    D(4),
    E(5),
    F(6),
    G(7),
    H(8),
    I(9),
    J(10),
    K(11),
    L(12),
    M(13),
    N(14),
    O(15),
    P(16),
    Q(17),
    R(18),
    S(19),
    T(20),
    U(21),
    V(22),
    W(23),
    X(24),
    Y(25),
    Z(26);

    private final int resourceNum;

    private Resource(int resourceNum) {
        this.resourceNum = resourceNum;
    }

    public int getResourceNum() {
        return this.resourceNum;
    }

    public static final Resource getResourceByResourceNum(int resourceNum) {

        if(resourceNum == 1) {
            return A;
        } else if(resourceNum == 2) {
            return B;
        } else if(resourceNum == 3) {
            return C;
        } else if(resourceNum == 4) {
            return D;
        } else if(resourceNum == 5) {
            return E;
        } else if(resourceNum == 6) {
            return F;
        } else if(resourceNum == 7) {
            return G;
        } else if(resourceNum == 8) {
            return H;
        } else if(resourceNum == 9) {
            return I;
        } else if(resourceNum == 10) {
            return J;
        } else if(resourceNum == 11) {
            return K;
        } else if(resourceNum == 12) {
            return L;
        } else if(resourceNum == 13) {
            return M;
        } else if(resourceNum == 14) {
            return N;
        } else if(resourceNum == 15) {
            return O;
        } else if(resourceNum == 16) {
            return P;
        } else if(resourceNum == 17) {
            return Q;
        } else if(resourceNum == 18) {
            return R;
        } else if(resourceNum == 19) {
            return S;
        } else if(resourceNum == 20) {
            return T;
        } else if(resourceNum == 21) {
            return U;
        } else if(resourceNum == 22) {
            return V;
        } else if(resourceNum == 23) {
            return W;
        } else if(resourceNum == 24) {
            return X;
        } else if(resourceNum == 25) {
            return Y;
        } else if(resourceNum == 26) {
            return Z;
        } else {
            return null;
        }

    }
}
