package ca.polymtl.inf8480.tp2.dispatcher;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;

import ca.polymtl.inf8480.tp2.shared.*;

public class Dispatcher {

	private NameServiceInterface nameServiceStub;
	private HashMap<CalculatorInterface, Integer> calculatorStub;

	private List<String> fileLines = new ArrayList<String>();
	
	private static String dispatcherUsername;
	private static String dispatcherPassword;
	private static boolean dispatcherSecureMode;
	
	private List<List<String>> tasksToDo;
	
	/*
	 * Recuperation des arguments fournis dans la commande.
	 */
	public static void main(String[] args) {
		
		//     Dispatcher dispatcher = new Dispatcher();
		//     dispatcher.run();
		
		//Enregistrement de l'identifiants et du mot de passe en variable local.
		dispatcherUsername = args[0];
		dispatcherPassword = args[1];
		dispatcherSecureMode = Boolean.parseBoolean(args[2]);
		
	}
	
	public Dispatcher(String file, String username, String password, boolean secureMode) throws IOException {
				
		// Repartition du contenu du fichier en ligne dans la liste fileLines.
		String line;
		BufferedReader br = new BufferedReader(new FileReader(file));
		while((line = br.readLine()) != null)
		{	
			fileLines.add(line);
		}

		// Definition des informations du serveur de nameService.
		// Hypothese de simplification : NameService a une adresse IP fixe (172.0.0.1).
		nameServiceStub = loadNameServiceStub("127.0.0.1");
		
		/*
		ServerInterface stub = null;

		ServerInterface stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(hostname, port);
            stub = (ServerInterface) registry.lookup("server");
        } catch (NotBoundException e) {
            System.out.println("Erreur: Le nom '" + e.getMessage() + "' n'est pas défini dans le registre.");
        } catch (AccessException e) {
            System.out.println("Erreur: " + e.getMessage());
        } catch (RemoteException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
		*/
		
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
		if (secureMode) {
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
		
		// Lancement de l'executeur de Threads
		ExecutorService executor = Executors.newCachedThreadPool();
     
		for (CalculatorInterface calculator: calculatorStub)
		{
			List<String> task = fileLines.subList(Math.max(fileLines.size() - (calculatorStub.key * 2), 0), fileLines.size());
			//calculatorStub.calculate(task, username, password);
			fileLines.removeAll(task);
			tasksToDo.add(task);
		}
	 
		// Envoi des Threads.
		for (List<String> task : tasksToDo)
		{
			ecs.submit(task);
		}
		
		while (tasksToDo.size() != 0)
		{
			int result = ecs.take().get();
			 
			switch (result)
			{
			
			// L'erreur -1 met en évidence un problème de syntaxe dans le contenu du fichier (notamment sur la composition des opérations).
			// Interruption du calcul et renvoie de message à l'utilisateur.
			case -1:
				System.out.println("Calcul impossible : Revoir le contenu du fichier transmis.");
				System.exit(1);
			
			//
			//
			case -2:
				System.out.println("Le serveur a refusé la tâche");
				ecs.submit(task);
				break;
			
			//	
			case -3:
				System.out.println("Le serveur de calcul n'a pas pu authentifier le répartiteur.");
				ecs.submit(task);
				break;
				 
			// Le calcul s'est effectué sans encombre, on ajoute cet nouvelle valeur au résultat global.
			default:
				resultat = (resultat + result) % 4000;
			}
		}
		System.out.printf("Le resultat du calcul est : ", resultat);
	}
	 
	// DOUBLONS de secure !!!!!
	private int unSecureCalculation(String username, String password) {
		int total = total;
	 
		//Executor executor = Executors.newCachedThreadPool();
		//ExecutorCompletionService<ClientTask.ClientTaskInfo> ecs = new ExecutorCompletionService<>(ex);
     
		for (CalculatorInterface calculator: calculators)
		{
			List<String> task = fileLines.subList(Math.max(fileLines.size() - (calculator.value * 2), 0), filelines.size());
			//calculatorStub.calculate(task, username, password);
			fileLines.removeAll(task);		
		 	tasks.add(task);
		}
	 
		for (List<String> task : tasksToDo)
		{
			ecs.submit(task);
		}
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
