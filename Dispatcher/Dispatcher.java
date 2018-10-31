package Dispatcher

import java.util.List;
import java.rmi.RemoteException;

public class Dispatcher implements DispatcherInterface{

	private List<String> fileLines = new List<String>();
	private List<CalculatorInterface> calculators= new CalculatorInterface();
	private String DispatcherUsername;
	private String DispatcherPassword;
	private List<List<String>> tasks;
	public Dispatcher(String file, String username, String password, boolean secureMode) {
		BufferedReader br = new BufferedReader(new FilerReader(file));
		DispatcherUsername = username;
		DispatcherPassword = password;
		while((line = br.readLine()) != null)
		{
			fileLines.add(line);
		}
	}
	private void run() {
		//TODO
	}
	
	}
	 public static void main(String args[]) {
		//TODO;
		calculators = nameServerStub.getCalculators(username, password);
		if (secure)
			dispatchSecure(DispatcherUsername, DispatcherPassword);
		else
			dispatchNonSecure(DispatcherUsername, DispatcherPassword);
	 }
	 
	 private int calculationSecureMode(String username, String password) {
	 
	 int total = total;
     Executor executor = Executors.newCachedThreadPool();
     ExecutorCompletionService<int> ecs = new ExecutorCompletionService<>(executor);
     
	 for (CalculatorInterface calculator: calculators)
	 {
		 List<String> task = fileLines.subList(Math.max(fileLines.size() - (calculator.value * 2), 0), filelines.size());
		 calculator.calculate(task, username, password);
		 fileLines.removeAll(task);		
		 tasks.add(task);
	 }
	 
	 for (List<String> task : tasks)
	 {
		 ecs.submit(task);
	 }
	 
	 while (tasks.size() != 0)
	 {
		 int result = ecs.take().get();
		 
		 switch (result)
		 {
		 case -1:
			 System.out.println("Opération invalide. Assurez-vous que le fichier ne contient que prim ou pell comme opération.");
			 exit(1);
			 
		 case -2:
			 System.out.println("Le serveur a refusé la tâche");
			 ecs.submit(task);
			 break;
		
		 case -3:
			 System.out.println("Le serveur de calcul n'a pas pu authentifier le répartiteur.");
			 ecs.submit(task);
			 break;
			 
		 default:
			 total = (total + result) % 4000;
		 }
	 }
	 return total;
}
