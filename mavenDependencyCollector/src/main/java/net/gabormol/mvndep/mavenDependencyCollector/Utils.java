package net.gabormol.mvndep.mavenDependencyCollector;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

	public static List<String> getFileNames(List<String> fileNames, Path dir) {
	    try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
	        for (Path path : stream) {
	            if(path.toFile().isDirectory()) {
	                getFileNames(fileNames, path);
	            } else {
	                fileNames.add(path.toAbsolutePath().toString());
	                //System.out.println(path.getFileName());
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
}
