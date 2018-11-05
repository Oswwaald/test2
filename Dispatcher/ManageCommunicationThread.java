package ca.polymtl.inf8480.tp2.dispatcher;

import java.rmi.RemoteException;
import java.util.*;

import ca.polymtl.inf8480.tp2.shared.*;

public class ManageCommunicationThread extends Thread{
	
	private CalculatorInterface calculator;
	private ArrayList<String> tasks;
	public int resultat;
	
	public void run() {
		try {
			resultat = calculator.calculate(tasks, Dispatcher.dispatcherUsername, Dispatcher.dispatcherPassword);
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}

	public ManageCommunicationThread(CalculatorInterface _calculator, ArrayList<String> _tasks) {
		super();
		calculator = _calculator;
		tasks = _tasks;
	}
}
