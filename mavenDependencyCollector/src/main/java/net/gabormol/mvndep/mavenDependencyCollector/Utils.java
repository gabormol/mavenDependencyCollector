package net.gabormol.mvndep.mavenDependencyCollector;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.gabormol.mvndep.mavenDependencyCollector.model.MvnDep;

public class Utils {

	public static List<String> getFileNames(List<String> fileNames, Path dir) {
	    try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
	        for (Path path : stream) {
	            if(path.toFile().isDirectory()) {
	                getFileNames(fileNames, path);
	            } else {
	                fileNames.add(path.toAbsolutePath().toString());
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
}
