package net.gabormol.mvndep.mavenDependencyCollector;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import net.gabormol.mvndep.mavenDependencyCollector.model.MvnDep;

public class App 
{
    public static void main( String[] args )
    {
    	
    	List<String> fileNames = new ArrayList<>();
		fileNames = Utils.getFileNames(fileNames, Paths.get(args[0]));
		
		System.out.println("POM files found: " + fileNames.size());
		
		List<MvnDep> dependencies = new ArrayList<>();
		List<String> output = new ArrayList<>();
		
		ExcelWriter xlsWriter = new ExcelWriter();
		
		for (String f : fileNames){
			//System.out.println(f);
			
			PomReader aPomReader = new PomReader(f);
			
			dependencies.addAll(aPomReader.getAllDependenciesFromPom());
			
		}
		
		System.out.println("Dependencies found: " + dependencies.size());
		
		dependencies = Utils.removeTestDepencency(dependencies);
		
		System.out.println("Test dependencies removed, remaining dependencies: " + dependencies.size());
		
		dependencies = Utils.removeDuplicates(dependencies);
		
		System.out.println("Duplicated dependencies removed, remaining dependencies: " + dependencies.size());
		
		xlsWriter.createExcel(dependencies);
		
		/*for (MvnDep dep : dependencies){
				
			output.add(dep.getGroupId() + " " + dep.getArtifact() 
				+ " " + dep.getVersion() + "scope: " + dep.getScope());
			
		}
		
		System.out.println(output.toString());*/
    }
}
