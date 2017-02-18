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
		
		for (String f : fileNames){
			System.out.println(f);
			
			PomReader aPomReader = new PomReader(f);
			
			List<MvnDep> dependencies = aPomReader.getAllDependencies();
			
		}
    }
}
