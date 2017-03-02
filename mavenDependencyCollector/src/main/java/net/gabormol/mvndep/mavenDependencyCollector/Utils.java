package net.gabormol.mvndep.mavenDependencyCollector;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.gabormol.mvndep.mavenDependencyCollector.model.MvnDep;

public class Utils {

	public static List<String> getFileNames(List<String> fileNames, Path dir) {
		int foundFiles = 0;
	    try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
	        for (Path path : stream) {
	            if(path.toFile().isDirectory()) {
	                getFileNames(fileNames, path);
	            } else {
	            	foundFiles++;
	                fileNames.add(path.toAbsolutePath().toString());
	                if ((foundFiles%100)==0){
	                	System.out.print(".");
	                }
	            }
	        }
	    } catch(IOException e) {
	        e.printStackTrace();
	    }
	    
	    return filterNotPoms(fileNames);
	}
	
	public static List<String> filterNotPoms(List<String> fileNames){
		List<String> retRes = fileNames.stream()
				.filter(path -> containPomXml(path))
				.collect(Collectors.toList());
		
		return retRes;
	}
	
	private static boolean containPomXml (String aString){
		if (aString.endsWith("pom.xml")){
			return true;
		} else {
			return false;
		}
	}
	
	public static List<MvnDep> removeTestDepencency(List<MvnDep> depList){
		List<MvnDep> retRes = depList.stream()
				.filter(dep -> isNotTestDependency(dep))
				.collect(Collectors.toList());
		
		return retRes;
	}
	
	private static boolean isNotTestDependency(MvnDep dependency){
		if (dependency.getScope().contains("test")){
			return false;
		} else {
			return true;
		}
	}
	
	public static List<MvnDep> removeDuplicates(List<MvnDep> depList){
		List<MvnDep> nonDuplicatedDependencies = depList.stream()
				   .<Map<String, MvnDep>> collect(HashMap::new,(m,e)->m.put(e.compareString(), e), Map::putAll)
				   .values()
				   .stream()
				   .collect(Collectors.toList());
		return nonDuplicatedDependencies;
	}
	
	public static List<MvnDep> resolveVersionParameters(List<MvnDep> depList , Map<String,String> versionMap){
		List<MvnDep> retRes = depList;
		
		for (MvnDep dep : depList){
			if (dep.getVersion().contains("version")){
				dep.setVersion(versionMap.get(formatVersion(dep.getVersion())));
			}
		}
		return retRes;
	}
		
	private static String formatVersion(String versionString){
		String retRes = versionString.replaceAll("[${}]", "");	
		//System.out.println("Version: " + retRes);
		return retRes;
	}
	
	public static List<MvnDep> addVersionFromDepManagement(List<MvnDep> deps, List<MvnDep>depMan){
		
		//System.out.println("\nResolving versions... ");
		
		for (MvnDep dep : deps){
			String artifact = dep.getArtifact();
			String groupId = dep.getGroupId();
			String version = "";
			
			for (MvnDep dMan : depMan){
				if(dMan.getArtifact().matches(artifact) && dMan.getGroupId().matches(groupId)){
					version = dMan.getVersion();
					//System.out.println("Version found: " + version);
					break;
				}
			}
			dep.setVersion(version);
		}
		return deps;
	}
	
	public static List<MvnDep> removeInternalComponents(List<MvnDep> depList, String internalPrefix){
		if (!internalPrefix.isEmpty()){
			return depList.stream().filter(dep -> !dep.getGroupId().contains(internalPrefix))
				.collect(Collectors.toList());
		} else {
			return depList;
		}
	}
	
	public static List<MvnDep> selectDependencyManagement (List<MvnDep> depList){
		return depList.stream()
				.filter(dep -> !dep.isDepMan())
				.collect(Collectors.toList());
	}
	
	public static List<MvnDep> removeDependencyManagement (List<MvnDep> depList){
		return depList.stream()
				.filter(dep -> dep.isDepMan())
				.collect(Collectors.toList());
	}
	
	public static List<String> findPomFiles(String path){
		System.out.println("Searching for POM files...");
    	List<String> fileNames = new ArrayList<>();
		fileNames = getFileNames(fileNames, Paths.get(path));
		return fileNames;
	}
	
	public static Map<String, String> collectPropertiesFromFiles(List<String> fileNames){
		
		Map<String, String> properties = new HashMap<String, String>();
		
		for (String f : fileNames){
			
			PomReader aPomReader = new PomReader(f);
			
			properties.putAll(aPomReader.getAllPropertiesFromPom());
			
		}
		return properties;
	}
	
	public static List<MvnDep> collectDependenciesFromFiles(List<String> fileNames){
		List<MvnDep> dependencies = new ArrayList<>();
		
		for (String f : fileNames){
			PomReader aPomReader = new PomReader(f);
			dependencies.addAll(aPomReader.getAllDependenciesFromPom());
		}
		
		return dependencies;
	}
	
	public static List<String> separateTestArtifactsString(String string){
		return Arrays.asList(string.split(","));
	}
	
	public static List<MvnDep> removeDependenciesByProjectArtifact(List<MvnDep> dependencies, 
			List<String> artifactsToRemove){
		List<MvnDep> retRes = dependencies;
		
		for (String artifactToRemove : artifactsToRemove){
			retRes = removeDependencyWithAnArtifactId(retRes, artifactToRemove);
			
		}
		return retRes;
		
	}
	
	private static List<MvnDep> removeDependencyWithAnArtifactId (List<MvnDep> depList, String artifactId){
		return depList.stream()
				.filter(dep -> !dep.getProjectArtifactId().equals(artifactId))
				.collect(Collectors.toList());
	}
	
	public static List<MvnDep> sortAlphabetical(List<MvnDep> list){
		List<MvnDep> retRes = new ArrayList<>();
		
		//There are several alternatives to sort the List, both are working
		// Alt #1
		/*retRes = list.stream().sorted((d1, d2) -> (d1.getGroupId().compareTo(d2.getGroupId()))) 
				.collect(Collectors.toList()); */
		// Alt #2
		retRes = list.stream().sorted(
                Comparator.comparing(n->n.getArtifact().toLowerCase())).collect(Collectors.toList());
		// Alt #3
		/*list.sort(Comparator.comparing(MvnDep::getGroupId));
		retRes = list;*/
		return retRes;
	}
}
