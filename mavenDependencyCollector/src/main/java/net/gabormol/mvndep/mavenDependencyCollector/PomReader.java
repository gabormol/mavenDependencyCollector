package net.gabormol.mvndep.mavenDependencyCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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
    			
    			if (!checkDependencyManagement(eElement)){
    				aDependency.setDepMan(true);
    			
    			}
	        
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
    				aDependency.setProjectArtifactId(getProjectArtifactId(eElement));
    			
    				if (!checkPluginDependency(eElement)){
    					depList.add(aDependency);
    				}
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
    
    public Map<String, String> getAllPropertiesFromPom(){
    	Map<String, String> propList = new HashMap<>();
    	
    	try {	
    		db = dbf.newDocumentBuilder();
    		document = db.parse(file);
    		document.getDocumentElement().normalize();
		
    		NodeList nList = document.getElementsByTagName("*");
    		
    		for (int i=0; i<nList.getLength(); i++){
    			
    			Node node = nList.item(i);
    			String nodeName = node.getNodeName();
    			String nodeVersion;
    			
    			if (nodeName.contains("version")){
    				nodeVersion = node.getTextContent();
    				if (!nodeVersion.contains("$")){
    					propList.put(nodeName, nodeVersion);
    				}
    			} else {
    				nodeName = "";
    			}
    		}
    		
    	} catch (ParserConfigurationException | SAXException | IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		return null;
    	}
    	
		
    	return propList;	
    	
    }
    
    private boolean checkDependencyManagement(Element dependencyElement){
    	boolean retRes = false;
    	
    	if (dependencyElement.getParentNode().getParentNode().getNodeName() == "dependencyManagement"){
    		return true;
    	} 
    	return retRes;
    }
    
    private boolean checkPluginDependency(Element dependencyElement){
    	boolean retRes = false;
    	
    	if (dependencyElement.getParentNode().getParentNode().getNodeName() == "plugin"){
    		return true;
    	} 
    	return retRes;
    }
    
    private String getProjectArtifactId(Element element){
    	String retRes = "";
    	Node aNode = element.getParentNode();
    	String parentNodeName = aNode.getNodeName();
    	
    	while (!parentNodeName.equals("project")){  
    		aNode = aNode.getParentNode();
    		if (aNode != null){
    			parentNodeName = aNode.getNodeName();
    		} 
    	}
    	
    	Node firstRealChild = aNode.getFirstChild().getNextSibling();
    	
    	if (aNode.getNodeName().equals("project")){
    		while(!firstRealChild.getNodeName().equals("artifactId")){
    			firstRealChild = firstRealChild.getNextSibling().getNextSibling();
    			if (firstRealChild != null){
    				if (firstRealChild.getNodeName().equals("artifactId")){
    					retRes = firstRealChild.getTextContent();
    				}
    			} else {
    				retRes = "not found";
    				break;
    			}
    		}
    		
    	} else {
    		retRes = "not found";
    	}
    	return retRes;
    }
}

