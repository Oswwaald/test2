package Calculator;

import java.rmi.RemoteException;
import java.net.InetAdress;
import java.util.List;
import java.util.HashMap;
import Interface.CalculatorInterface;

public class Calculator implements CalculatorInterface {
	
	private int calculatorCapacity;
	private int calculatorErrorProb;
	
	 public Calculator(int capacity, int errorProb) {
		 super();
	     calculatorCapacity = capacity;
	     calculatorErrorProb = errorProb;
	     nameServerStub.setCalculator(InetAdress.getLocalHost().getHostAdress(), capacity)
	 }
	 
	 public static void main(String args[]) {
		 //TODO;
	 }
	 
	 private void run() {
		 //TODO;
	 }
	 
	int calculate(List<String> operations, String username, String password) {
		double value = 0;
		if (nameServerStub.verifyDispatcher(username, password))
		{
			float rate = (operations.size() - capacity)/(4*capacity);
			float randomNum = Math.random();
			if (rate <= randomNum)
			{
				HashMap<String, Integer> ops = new new HashMap<String, Integer>();
				ops = splitOperations(operations);
				for (Pair<String, Integer> op : ops)
				{
					if(op.key == "pell")
						value = (value + Operations.pell(op.value)) %4000;
					else if(op.key == "prim")
						value = (value + Operations.prim(op.value)) %4000;
					else
					{
						value = -1;
						break;
					}
				}
			}
			else
				value = -2;
		}
		else
			value = -3;
		return value;
	}
	
	int public getCalculatorCapacity() {
		return calculatorCapacity;
	}
	
	private HashMap<String, Integer> splitOperations(List<String> operations) {		
		HashMap<String, Integer> values = new HashMap<String, Integer>();
		foreach (String op in operations)
		{
			String operation;
			int operande;
			String[] arguments = op.split(" ");
			operation = arguments[0];
			operande = Integer.parseInt(arguments[1]);
			values.put(operation, operande);

		}
		return values;
	}
}
