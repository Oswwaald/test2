package ca.polymtl.inf8480.tp2.shared;

import java.io.Serializable;

public class Configuration implements Serializable {

	private static final long serialVersionUID = 1L;
	public String calculatorIP;
	public int calculatorPort;
	public int calculatorCapacity;
	
	public Configuration(String ip, int port, int capacity) {
		super();
		calculatorIP = ip;
		calculatorPort = port;
		calculatorCapacity = capacity;
	}
	
	/*
	 * Creation d'un identifiant unique base sur l'adresse IP et le port.
	 */
	public String unique() {
		return calculatorIP + Integer.toString(calculatorPort);
	}
}