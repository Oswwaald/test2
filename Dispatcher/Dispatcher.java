package ca.polymtl.inf8480.tp2.dispatcher;

import java.util.*;
import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;

import ca.polymtl.inf8480.tp2.shared.*;

public class Dispatcher {

	private NameServiceInterface nameServiceStub;
	private HashMap <CalculatorInterface, Integer> calculatorsStub; 
	private ArrayList<Configuration> calculatorInformation;
	private List<String> fileLines = new ArrayList<String>();
	public static String dispatcherFile;
	public static String dispatcherUsername;
	public static String dispatcherPassword;
	private static boolean dispatcherSecureMode;
	
	/*
	 * Recuperation des arguments fournis dans la commande.
	 */
	public static void main(String[] args) {
		// Enregistrement de l'identifiants et du mot de passe en variable local.
		dispatcherFile = args[0];
		dispatcherUsername = args[1];
		dispatcherPassword = args[2];
		dispatcherSecureMode = Boolean.parseBoolean(args[3]);
		try {
			Dispatcher dispatcher = new Dispatcher(dispatcherFile,dispatcherUsername,dispatcherPassword,dispatcherSecureMode);
			dispatcher.run();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public Dispatcher(String file, String username, String password, boolean secureMode) throws IOException {
		
		// Repartition du contenu du fichier en ligne dans la liste fileLines.
		String line;
		BufferedReader br = new BufferedReader(new FileReader(file));
		while((line = br.readLine()) != null)
		{	
			fileLines.add(line);
		}
		br.close();

		// Definition du stub du serveur de nameService.
		// Hypothese de simplification : NameService a une adresse IP fixe (172.0.0.1).
		nameServiceStub = loadNameServiceStub("127.0.0.1");
		
		// Recuperation de la liste des calculators de NameService.
		try {
			calculatorInformation = nameServiceStub.getCalculators(dispatcherUsername, dispatcherPassword);
		}
		catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
		// Recuperation des stubs de Calculators.
		calculatorsStub = loadCalculatorStub(calculatorInformation);
		
		
		// Lancement du mode de calcul selon la securite du mode,
		// Et recuperation du resultat et des temps de calcul.
		long startedTime = System.nanoTime();
		if (dispatcherSecureMode) {
			secureCalculation(dispatcherUsername, dispatcherPassword);
		} 
		else {
			unSecureCalculation(dispatcherUsername, dispatcherPassword);
		}
		long finishedTime = System.nanoTime();
        System.out.println(String.format("Le temps de calcul est de : ", finishedTime - startedTime));
	}
	
	//
	public void run(){
		
		//Pas de stub pour le dispatcher.
		
	}
	
	/*
	 * Calcul du fichier dans le mode securise. 
	 */
	private void secureCalculation(String username, String password) {
		
		//Inititialisation du resultat final
		int resultat = 0;
   

		// TODO
		while (fileLines.size() != 0)
		{
			ArrayList<ManageCommunicationThread> listThreads = new ArrayList<ManageCommunicationThread>();
			//for (Map.Entry<CalculatorInterface, Integer> calculator: calculatorStub.entrySet())
			for (Map.Entry<CalculatorInterface, Integer> calculator: calculatorsStub.entrySet())
			{
				List<String> task = fileLines.subList(Math.max(fileLines.size() - (calculator.getValue() * 2), 0), fileLines.size());
				ManageCommunicationThread thread = new ManageCommunicationThread(calculator.getKey(),new ArrayList <String>(task));
				thread.start();
				listThreads.add(thread);
				fileLines.removeAll(task);
			}
			
			for (ManageCommunicationThread thread : listThreads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				int result = thread.resultat;
				switch (result)
				{
				
				// L'erreur -1 met en evidence un probleme de syntaxe dans le contenu du fichier (notamment sur la composition des operations).
				// Interruption du calcul et renvoie de message a l'utilisateur.
				case -1:
					System.out.println("Calcul impossible : Revoir le contenu du fichier transmis.");
					System.exit(1);
				
				// L'erreur -2 met en evidence un probleme de surcharge du Calculator. 
				// Interruption de la tâche et renvoie de la tache au Calculator.
				case -2:
					System.out.println("Surcharge du Calculator : Renvoi de la tache.");
					thread.start();
					listThreads.add(thread);
					break;
				
				//	L'erreur -2 met en evidence un probleme d'identification au niveau du Dispatcher.
				// Interruption du calcul et renvoie de message a l'utilisateur.
				case -3:
					System.out.println("Echec de l'identification Dispatcher : Revoir les identifiants Dispatcher.");
					System.exit(1);
					break;
					 
				// Le calcul s'est effectue correctement, on ajoute cette nouvelle valeur au resultat global.
				default:
					resultat = (resultat + result) % 4000;
				}
			}
		}
		System.out.printf("Le resultat du calcul est : ", resultat);
	}

	// DOUBLONS de secure !!!!!
	// Faire un test sur les serveurs avant de passer en ModeSecure ??????
	private void unSecureCalculation(String username, String password) {
		
		//Inititialisation du resultat final
		int resultat = 0;
   

		// TODO
		while (fileLines.size() != 0)
		{
			ArrayList<ManageCommunicationThread> listThreads = new ArrayList<ManageCommunicationThread>();
			//for (Map.Entry<CalculatorInterface, Integer> calculator: calculatorStub.entrySet())
			for (Map.Entry<CalculatorInterface, Integer> calculator: calculatorsStub.entrySet())
			{
				List<String> task = fileLines.subList(Math.max(fileLines.size() - (calculator.getValue() * 2), 0), fileLines.size());
				ManageCommunicationThread thread = new ManageCommunicationThread(calculator.getKey(),new ArrayList <String>(task));
				thread.start();
				listThreads.add(thread);
				fileLines.removeAll(task);
			}
			
			for (ManageCommunicationThread thread : listThreads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int result = thread.resultat;
				switch (result)
				{
					// L'erreur -1 met en evidence un probleme de syntaxe dans le contenu du fichier (notamment sur la composition des operations).
					// Interruption du calcul et renvoie de message a l'utilisateur.
					case -1:
						System.out.println("Calcul impossible : Revoir le contenu du fichier transmis.");
						System.exit(1);
					
					// L'erreur -2 met en evidence un probleme de surcharge du Calculator. 
					// Interruption de la tâche et renvoie de la tache au Calculator.
					case -2:
						System.out.println("Surcharge du Calculator : Renvoi de la tache.");
						thread.start();
						listThreads.add(thread);
						break;
					
					// L'erreur -2 met en evidence un probleme d'identification au niveau du Dispatcher.
					// Interruption du calcul et renvoie de message a l'utilisateur.
					case -3:
						System.out.println("Echec de l'identification Dispatcher : Revoir les identifiants Dispatcher.");
						System.exit(1);
						break;
						 
					// Le calcul s'est effectue correctement, on ajoute cette nouvelle valeur au resultat global.
					default:
						resultat = (resultat + result) % 4000;
				}
			}
		}
		System.out.printf("Le resultat du calcul est : ", resultat);
	}
	/*
	 * Mise en place de la recuperation d'acces au NameService par le RMI, sous le format stub.
	 */	
	private NameServiceInterface loadNameServiceStub(String hostname) {
		NameServiceInterface stub = null;
		// Le port du NameService est defini arbitrairement a 5000.
		try {
			Registry registry = LocateRegistry.getRegistry(hostname,5000);
			stub = (NameServiceInterface) registry.lookup("nameservice");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas defini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
		return stub;
	}
	
	/*
	 * Mise en place de la recuperation d'acces aux Calculators par le RMI, sous le format stub + capacite disponible dans un HashMap.
	 */
	private HashMap<CalculatorInterface, Integer> loadCalculatorStub(ArrayList<Configuration> calculatorInformation) {
		int capac = 0;
		CalculatorInterface stub = null;
		HashMap<CalculatorInterface, Integer> localStub = new HashMap<CalculatorInterface, Integer>();
		for (Configuration conf : calculatorInformation) {
			try {
				Registry registry = LocateRegistry.getRegistry(conf.calculatorIP,conf.calculatorPort);
				stub = (CalculatorInterface) registry.lookup(conf.unique());
				capac = conf.calculatorCapacity;
				localStub.put(stub,capac);
			} catch (NotBoundException e) {
				System.out.println("Erreur: Le nom '" + e.getMessage()
						+ "' n'est pas defini dans le registre.");
			} catch (AccessException e) {
				System.out.println("Erreur: " + e.getMessage());
			} catch (RemoteException e) {
				System.out.println("Erreur: " + e.getMessage());
			}
		}
		return localStub;
	}
}
