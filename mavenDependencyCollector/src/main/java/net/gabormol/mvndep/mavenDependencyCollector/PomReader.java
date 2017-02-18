package net.gabormol.mvndep.mavenDependencyCollector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.gabormol.mvndep.mavenDependencyCollector.model.MvnDep;

public class PomReader {
	private String filePath = "";
	private File file;
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
    private Document document;
	
	public PomReader (String path){
		this.filePath = path;
		file = new File(filePath);
		dbf = DocumentBuilderFactory.newInstance();
	}
	
    
    public List<MvnDep> getAllDependenciesFromPom(){
    	
    	List<MvnDep> depList = new ArrayList<>();
    	
    	try {	
    		db = dbf.newDocumentBuilder();
    		document = db.parse(file);
    		document.getDocumentElement().normalize();
		
    		NodeList nList = document.getElementsByTagName("dependency");
		
    		for (int temp = 0; temp < nList.getLength(); temp++) {

    			Node nNode = nList.item(temp);
    			MvnDep aDependency = new MvnDep();

    			Element eElement = (Element) nNode;
	        
    			String groupId = eElement.getElementsByTagName("groupId").item(0).getTextContent();
    			String artifactId = eElement.getElementsByTagName("artifactId").item(0).getTextContent();
    			String scope;
    			String version;
	        
    			if (eElement.getElementsByTagName("scope").item(0) != null){
    				scope = eElement.getElementsByTagName("scope").item(0).getTextContent();
    			} else {scope = "";}
	
    			if (eElement.getElementsByTagName("version").item(0) != null){
    				version = eElement.getElementsByTagName("version").item(0).getTextContent();
    			} else {version = "";}
    			
    			aDependency.setGroupId(groupId);
    			aDependency.setArtifact(artifactId);
    			aDependency.setScope(scope);
    			aDependency.setVersion(version);
    			
    			depList.add(aDependency);
    		}
    		System.out.println("Found " + depList.size() + " dependencies in: "
    				+ "     " + filePath);
    		return depList;
    	} catch (ParserConfigurationException | SAXException | IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		return null;
    	}
    }
}

