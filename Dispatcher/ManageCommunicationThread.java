package ca.polymtl.inf8480.tp2.dispatcher;

import java.rmi.RemoteException;
import java.util.*;

import ca.polymtl.inf8480.tp2.shared.*;

public class ManageCommunicationThread extends Thread{
	
	private CalculatorInterface calculatorThread;
	private ArrayList<String> taskThread;
	public int resultat;
	
	/*
	 * Execution de chaque Thread pour chaque Calculator en fonction de chaque tache.
	 */
	public void run() {
		try {
			resultat = calculatorThread.calculate(taskThread, Dispatcher.dispatcherUsername, Dispatcher.dispatcherPassword);
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}

	public ManageCommunicationThread(CalculatorInterface calculator, ArrayList<String> taskSent) {
		super();
		calculatorThread = calculator;
		taskThread = taskSent;
	}
}
