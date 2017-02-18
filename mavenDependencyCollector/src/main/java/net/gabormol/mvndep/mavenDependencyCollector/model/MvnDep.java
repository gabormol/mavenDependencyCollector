package net.gabormol.mvndep.mavenDependencyCollector.model;

public class MvnDep {
	private String groupId;
	private String artifact;
	private String scope;
	private String version;
	
	public MvnDep (){
		
	}
	
	public MvnDep (String groupId, String atrifact, String scope, String version){
		this.groupId = groupId;
		this.artifact = artifact;
		this.scope = scope;
		this.version = version;
	}
	
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifact() {
		return artifact;
	}

	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
