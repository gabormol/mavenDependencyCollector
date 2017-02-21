package net.gabormol.mvndep.mavenDependencyCollector;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gabormol.mvndep.mavenDependencyCollector.model.MvnDep;

public class App 
{
    public static void main( String[] args )
    {
    	String internalComponentString;
    	if(args.length == 2){
    		internalComponentString = args[1];
    	} else {
    		internalComponentString = "";
    	}
    	
    	System.out.println("Searching for POM files...");
    	List<String> fileNames = new ArrayList<>();
		fileNames = Utils.getFileNames(fileNames, Paths.get(args[0]));
		
		System.out.println("\nPOM files found: " + fileNames.size());
		
		List<MvnDep> dependencies = new ArrayList<>();
		Map<String, String> properties = new HashMap<>();
		
		ExcelWriter xlsWriter = new ExcelWriter();
		
		for (String f : fileNames){
						
			PomReader aPomReader = new PomReader(f);
			
			properties.putAll(aPomReader.getAllPropertiesFromPom());
			
		}
		
		for (String f : fileNames){
			PomReader aPomReader = new PomReader(f);
			dependencies.addAll(aPomReader.getAllDependenciesFromPom());
		}
		System.out.println("Dependencies found: " + dependencies.size());
		
		dependencies = Utils.removeTestDepencency(dependencies);
		
		System.out.println("Test dependencies removed, remaining dependencies: " + dependencies.size());
		
		dependencies = Utils.removeDuplicates(dependencies);
		dependencies = Utils.removeInternalComponents(dependencies, internalComponentString);
		
		System.out.println("Duplicated dependencies and internal components removed, remaining dependencies: " + dependencies.size() + "\n");
		
		//System.out.println(properties.toString());
		
		List<MvnDep> dependencyManagementDeps = Utils.selectDependencyManagement(dependencies);
		dependencies = Utils.removeDependencyManagement(dependencies);
		System.out.println("Dependencies without DM: " + dependencies.size());
		dependencyManagementDeps = Utils.resolveVersionParameters(dependencyManagementDeps, properties);
		
		/*for (int i=0; i<dependencyManagementDeps.size(); i++){
			System.out.println(dependencyManagementDeps.get(i).getGroupId());
			System.out.println(dependencyManagementDeps.get(i).getArtifact());
			System.out.println(dependencyManagementDeps.get(i).getVersion());
			System.out.println(dependencyManagementDeps.get(i).getScope());
		}*/
		
		System.out.println("\nResolved dependency versions: ");
		
		for (String dependencyName: properties.keySet()){

	        System.out.println(dependencyName + " : " + properties.get(dependencyName));
		} 
		
		dependencies = Utils.addVersionFromDepManagement(dependencies, dependencyManagementDeps);
		
		//dependencies = Utils.resolveVersionParameters(dependencies, properties);
		
		xlsWriter.createExcel(dependencies);
		
    }
}
