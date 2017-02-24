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
    	String testArtifacts;
    	List<String> testArtifactIds = new ArrayList<>();
    	if(args.length >= 3){
    		testArtifacts = args[2];
    		testArtifacts.replaceAll("\\s+",""); // removing whitespaces
    		testArtifactIds = Utils.separateTestArtifactsString(testArtifacts);
    		internalComponentString = args[1];
    	} else if(args.length == 2){
    		internalComponentString = args[1];
    	} else {
    		internalComponentString = "";
    	}
    	
    	if (args.length==0){
    		System.out.println("Please specify at least a path!");
    		return;
    	}
    	
    	// Getting the list of POM files to look into
    	List<String> fileNames = Utils.findPomFiles(args[0]);	
		System.out.println("\nPOM files found: " + fileNames.size());
		
		List<MvnDep> dependencies = new ArrayList<>(); // for storing dependencies
		Map<String, String> properties = new HashMap<>(); // for storing property values
		
		ExcelWriter xlsWriter = new ExcelWriter(); // excel writer
		
		// Collecting the properties and their values
		properties = Utils.collectPropertiesFromFiles(fileNames);
		
		// Collecting the all the dependencies (dependencyManagement included)
		dependencies = Utils.collectDependenciesFromFiles(fileNames);		
		System.out.println("\nDependencies found: " + dependencies.size());
		
		// Removing test dependencies
		dependencies = Utils.removeTestDepencency(dependencies);
		System.out.println("\nTest dependencies removed, remaining dependencies: " + dependencies.size());
		
		//Removing duplicated dependencies
		dependencies = Utils.removeDuplicates(dependencies);
		// Removing dependencies containing the internal component string
		dependencies = Utils.removeInternalComponents(dependencies, internalComponentString);		
		System.out.println("Duplicated dependencies and internal components removed, remaining dependencies: " + dependencies.size() + "\n");
		
		// Creating dependencyManagement list
		List<MvnDep> dependencyManagementDeps = Utils.selectDependencyManagement(dependencies);
		
		// Removing dependencyManagement items from the dependencies (We don't have to list them)
		dependencies = Utils.removeDependencyManagement(dependencies);
		System.out.println("\ndependencyManagement items removed, remaining dependencies: " + dependencies.size());
		
		/*for (MvnDep dep : dependencies){
			System.out.println("Project's artifactId: " + dep.getProjectArtifactId());
		}*/
		
		if(!testArtifactIds.isEmpty()){
			// Removing dependencies inside test-only artifact
			dependencies = Utils.removeDependenciesByProjectArtifact(dependencies, testArtifactIds);
		
			System.out.println("\nDependencies with the requested project artifactId-s removed, remaining dependencies: " + dependencies.size());
		}
		
		// Resolve versions of the dependencies in the dependencyManagement list
		dependencyManagementDeps = Utils.resolveVersionParameters(dependencyManagementDeps, properties);
		
		System.out.println("\nResolved dependencies from dependencyManagement: ");
		for (String dependencyName: properties.keySet()){

	        System.out.println(dependencyName + " : " + properties.get(dependencyName));
		} 
		
		// Adding version info to dependencies
		dependencies = Utils.addVersionFromDepManagement(dependencies, dependencyManagementDeps);
		
		// Write the remaining dependencies in Excel
		xlsWriter.createExcel(dependencies);
		
    }
}
