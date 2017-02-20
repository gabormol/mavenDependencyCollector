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
    	
    	String internalComponentString = args[1];
    	
    	System.out.println("Searching for POM files...");
    	List<String> fileNames = new ArrayList<>();
		fileNames = Utils.getFileNames(fileNames, Paths.get(args[0]));
		
		System.out.println("\nPOM files found: " + fileNames.size());
		
		List<MvnDep> dependencies = new ArrayList<>();
		Map<String, String> properties = new HashMap<>();
		
		ExcelWriter xlsWriter = new ExcelWriter();
		
		for (String f : fileNames){
						
			PomReader aPomReader = new PomReader(f);
			
			dependencies.addAll(aPomReader.getAllDependenciesFromPom());
			properties.putAll(aPomReader.getAllPropertiesFromPom());
			
		}
		
		System.out.println("Dependencies found: " + dependencies.size());
		
		dependencies = Utils.removeTestDepencency(dependencies);
		
		System.out.println("Test dependencies removed, remaining dependencies: " + dependencies.size());
		
		dependencies = Utils.removeDuplicates(dependencies);
		dependencies = Utils.removeWithoutVersion(dependencies);
		dependencies = Utils.removeInternalComponents(dependencies, internalComponentString);
		
		System.out.println("Duplicated dependencies removed, remaining dependencies: " + dependencies.size());
		
		//System.out.println(properties.toString());
		
		System.out.println("\nResolved dependency versions: ");
		
		for (String dependencyName: properties.keySet()){

	        System.out.println(dependencyName + " : " + properties.get(dependencyName));
		} 
		
		dependencies = Utils.resolveVersionParameters(dependencies, properties);
		
		xlsWriter.createExcel(dependencies);
		
    }
}
