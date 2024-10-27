package week06;

import java.io.Serializable;

public class CalcExpr implements Serializable {

    double op1;
    double op2;
    char operator;

    public CalcExpr(double op1, char operator, double op2) {
        this.op1 = op1;
        this.operator = operator;
        this.op2 = op2;
    }

    @Override
    public String toString() {
        return op1 + " " + operator + " " + op2;
    }
}
