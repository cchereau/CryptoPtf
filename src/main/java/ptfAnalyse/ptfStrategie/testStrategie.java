package ptfAnalyse.ptfStrategie;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.mXparser;

//http://mathparser.org/

public class testStrategie {


    public static void main(String[] args) {

        String strExpression = "(A<3) & (B>6) & (E<J)";
        Expression e = new Expression("(2=3) & (2<3)");
        mXparser.consolePrintln("Res: " + e.getExpressionString() + " = " + e.calculate());

    }


}
