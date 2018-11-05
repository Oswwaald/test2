package ca.polymtl.inf8480.tp2.dispatcher;

import java.util.*;
import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;

import ca.polymtl.inf8480.tp2.shared.*;

public class Dispatcher {

	private NameServiceInterface nameServiceStub;
	private HashMap<CalculatorInterface, Integer> calculatorStub;

	private List<String> fileLines = new ArrayList<String>();
	
	public static String dispatcherFile;
	public static String dispatcherUsername;
	public static String dispatcherPassword;
	private static boolean dispatcherSecureMode;
	
	/*
	 * Recuperation des arguments fournis dans la commande.
	 */
	public static void main(String[] args) {
		//Enregistrement de l'identifiants et du mot de passe en variable local.
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

		// Definition des informations du serveur de nameService.
		// Hypothese de simplification : NameService a une adresse IP fixe (172.0.0.1).
		nameServiceStub = loadNameServiceStub("127.0.0.1");
		
		// Recuperer de la liste des calculators references dans NameService.
		try {
			calculatorStub = nameServiceStub.getCalculators(dispatcherUsername, dispatcherPassword);
		}
		catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
		
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
	 * Calcul du fichier dans le mode sécurisé. 
	 */
	private void secureCalculation(String username, String password) {
		
		//Inititialisation du résultat final
		int resultat = 0;
   

		// TODO
		while (fileLines.size() != 0)
		{
			ArrayList<ManageCommunicationThread> listThreads = new ArrayList<ManageCommunicationThread>();
			for (Map.Entry<CalculatorInterface, Integer> calculator: calculatorStub.entrySet())
			{
				List<String> task = fileLines.subList(Math.max(fileLines.size() - (calculator.getValue() * 2), 0), fileLines.size());
				ManageCommunicationThread thread = new ManageCommunicationThread(calculator.getKey(),new ArrayList <String>(task));
				thread.start();
				listThreads.add(thread);
				//calculatorStub.calculate(task, username, password);
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
				
				// L'erreur -1 met en évidence un problème de syntaxe dans le contenu du fichier (notamment sur la composition des opérations).
				// Interruption du calcul et renvoie de message à l'utilisateur.
				case -1:
					System.out.println("Calcul impossible : Revoir le contenu du fichier transmis.");
					System.exit(1);
				
				// L'erreur -2 met en evidence un problème de surcharge du Calculator. 
				// Interruption de la tâche et renvoie de la tache au Calculator.
				case -2:
					System.out.println("Surcharge du Calculator : Renvoi de la tache.");
					thread.start();
					listThreads.add(thread);
					break;
				
				//	L'erreur -2 met en evidence un problème d'identification au niveau du Dispatcher.
				// Interruption du calcul et renvoie de message à l'utilisateur.
				case -3:
					System.out.println("Echec de l'identification Dispatcher : Revoir les identifiants Dispatcher.");
					System.exit(1);
					break;
					 
				// Le calcul s'est effectué correctement, on ajoute cette nouvelle valeur au résultat global.
				default:
					resultat = (resultat + result) % 4000;
				}
			}
		}
		System.out.printf("Le resultat du calcul est : ", resultat);
	}

	// DOUBLONS de secure !!!!!
	private void unSecureCalculation(String username, String password) {
		
		//Inititialisation du résultat final
		int resultat = 0;

		// TODO
		while (fileLines.size() != 0)
		{
			ArrayList<ManageCommunicationThread> listThreads = new ArrayList<ManageCommunicationThread>();
			for (Map.Entry<CalculatorInterface, Integer> calculator: calculatorStub.entrySet())
			{
				List<String> task = fileLines.subList(Math.max(fileLines.size() - (calculator.getValue() * 2), 0), fileLines.size());
				ManageCommunicationThread thread = new ManageCommunicationThread(calculator.getKey(),new ArrayList <String>(task));
				thread.start();
				listThreads.add(thread);
				//calculatorStub.calculate(task, username, password);
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
				
				// L'erreur -1 met en évidence un problème de syntaxe dans le contenu du fichier (notamment sur la composition des opérations).
				// Interruption du calcul et renvoie de message à l'utilisateur.
				case -1:
					System.out.println("Calcul impossible : Revoir le contenu du fichier transmis.");
					System.exit(1);
				
				// L'erreur -2 met en evidence un problème de surcharge du Calculator. 
				// Interruption de la tâche et renvoie de la tache au Calculator.
				case -2:
					System.out.println("Surcharge du Calculator : Renvoi de la tache.");
					thread.start();
					listThreads.add(thread);
					break;
				
				//	L'erreur -2 met en evidence un problème d'identification au niveau du Dispatcher.
				// Interruption du calcul et renvoie de message à l'utilisateur.
				case -3:
					System.out.println("Echec de l'identification Dispatcher : Revoir les identifiants Dispatcher.");
					System.exit(1);
					break;
					 
				// Le calcul s'est effectué correctement, on ajoute cette nouvelle valeur au résultat global.
				default:
					resultat = (resultat + result) % 4000;
				}
			}
		}
		System.out.printf("Le resultat du calcul est : ", resultat);
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
