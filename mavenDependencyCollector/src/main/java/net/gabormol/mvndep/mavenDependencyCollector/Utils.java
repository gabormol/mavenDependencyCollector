package net.gabormol.mvndep.mavenDependencyCollector;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
		return retRes;
	}
	
	/*public static List<MvnDep> removeWithoutVersion(List<MvnDep> depList){
		List<MvnDep> nonDuplicatedDependencies = depList.stream()
				   .filter(dep -> hasVersion(dep))
				   .collect(Collectors.toList());
		return nonDuplicatedDependencies;
	}
	
	private static boolean hasVersion(MvnDep dependency){
		if (dependency.getVersion().isEmpty()){
			return false;
		} else {
			return true;
		}
	}*/
	
	public static List<MvnDep> removeInternalComponents(List<MvnDep> depList, String internalPrefix){
		if (!internalPrefix.isEmpty()){
			return depList.stream().filter(dep -> !dep.getGroupId().contains(internalPrefix))
				.collect(Collectors.toList());
		} else {
			return depList;
		}
	}
	
}
