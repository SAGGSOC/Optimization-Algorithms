import java.util.*;
import java.math.*;

public class FitnessFunction{

	public int dim = 10; // assumption
	public int lBound = -35;	// X(i) value's lower bound defined by function
	public int uBound = 35;	// X(i) value's upper bound defined by function
	public double fitnessFunction(ArrayList<Double> x){

		double termA = 0;
		double termB = 0;

		for(int i=0;i<dim;i++)
		{
			termA = termA + x.get(i)*x.get(i);
			termB = termB + Math.cos(x.get(i));
		}
		double finalTermA = 20*Math.exp(-0.02*Math.sqrt(termA/dim));
		double finalTermB = Math.exp(termB/dim);
		double result = 20+Math.exp(1)-finalTermA-finalTermB;
		return round(result, 2);
		}

	public static double round(double d, int decimalPlace){
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
}
