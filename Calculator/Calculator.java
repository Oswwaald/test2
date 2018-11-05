package ca.polymtl.inf8480.tp2.calculator;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.util.*;

import ca.polymtl.inf8480.tp2.shared.*;

public class Calculator implements CalculatorInterface {
	
	private NameServiceInterface nameServiceStub;
	
	private static String calculatorIP;
	private static int calculatorCapacity;
	private static int calculatorErrorProb;
	private static int calculatorPort;
	private static Configuration configuration;
	private static String nameServiceIP;
	
	public Calculator() {
		
		//TODO
		
	 }
	 
	 public static void main(String args[]) {
		//Enregistrement de la capacite et de la probabilité d'erreur en variables globales.
		if (args.length == 5) {
			calculatorIP = args[0];
			calculatorCapacity = Integer.parseInt(args[1]);
			calculatorErrorProb = Integer.parseInt(args[2]);
			calculatorPort = Integer.parseInt(args[3]);
			nameServiceIP = args[4];
			configuration = new Configuration(calculatorIP,calculatorPort,calculatorCapacity);
		} else {
			System.out.println("Le nombre d'argument est incorrect.");
			System.exit(1);
		}
		 
		// Lancement le serveur du Calculator
        Calculator calculator = new Calculator();
        calculator.run();
	 }
	 
	 /*
	  * Mise en service du Calculator pour la procédure RMI.
	  */
	 private void run() {
		try {
			System.setProperty("rmi.server.hostname", Inet4Address.getLocalHost().getHostName());
			CalculatorInterface stub = (CalculatorInterface) UnicastRemoteObject.exportObject(this, 5000);
			Registry registry = LocateRegistry.getRegistry(5000);
			registry.rebind("calculator", stub);
			System.out.println("Calculator ready.");
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
		
		// Definition des informations du serveur de nameService.
		nameServiceStub = loadNameServiceStub(nameServiceIP);
	    try {
	    	nameServiceStub.setCalculator(configuration.unique(),configuration);
	    } catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	 }
	 
	public int calculate(List<String> task, String username, String password) {
		int value = 0;
		
		try {
			if (nameServiceStub.verifyDispatcher(username, password))
			{
				float rate = (task.size() - calculatorCapacity)/(4*calculatorCapacity);
				Random random = new Random();
				float randomNum = random.nextFloat();
				if (rate <= randomNum)
				{
					HashMap<String, Integer> operationElements = new HashMap<String, Integer>();
					operationElements = splitOperations(task);
					//Map.Entry<CalculatorInterface, Integer> calculator: calculatorStub.entrySet()
					//Map.Entry<CalculatorInterface, Integer> calculator: operationElements.entrySet()
					//HashMap<String, Integer> op : operationElements
					for (Map.Entry<String, Integer> op : operationElements.entrySet())
					{
						if(op.getKey() == "pell")
							value = (value + Operations.pell(op.getValue())) %4000;
						else if(op.getKey() == "prim")
							value = (value + Operations.prime(op.getValue())) %4000;
						else
						{
							// Problème de syntaxe dans le fichier.
							value = -1;
							break;
						}
					}
				}
				else
					// Tache refusée car surcharge.
					value = -2;
			}
			else
				// Probleme d'identification Dispatcher
				value = -3;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	private HashMap<String, Integer> splitOperations(List<String> task) {		
		HashMap<String, Integer> operationElements = new HashMap<String, Integer>();
		for (String op : task){
			String operation;
			int operande;
			String[] arguments = op.split(" ");
			operation = arguments[0];
			operande = Integer.parseInt(arguments[1]);
			operationElements.put(operation, operande);

		}
		return operationElements;
	}
	
	private NameServiceInterface loadNameServiceStub(String hostname) {
		NameServiceInterface stub = null;
		try {
			Registry registry = LocateRegistry.getRegistry(hostname,5000);
			stub = (NameServiceInterface) registry.lookup("nameservice");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
		return stub;
	}
}
