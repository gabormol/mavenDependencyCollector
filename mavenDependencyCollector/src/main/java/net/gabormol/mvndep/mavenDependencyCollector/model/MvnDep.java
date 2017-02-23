package net.gabormol.mvndep.mavenDependencyCollector.model;

public class MvnDep {
	private String groupId;
	private String artifact;
	private String scope;
	private String version;
	private boolean depMan = false;
	private String projectArtifactId;
	
	public MvnDep (String groupId, String artifact, String scope, String version, boolean depMan){
		this.groupId = groupId;
		this.artifact = artifact;
		this.scope = scope;
		this.version = version;
		this.depMan = depMan;
	}
	
	// It returns a unique string needed for filtering the duplicates
	public String compareString(){
		if (depMan == true){
			return groupId+artifact+scope+version+"depman";
		} else {
			return groupId+artifact+scope+version;
		}
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
	
	public boolean isDepMan() {
		return depMan;
	}

	public void setDepMan(boolean depMan) {
		this.depMan = depMan;
	}
	
	public String getProjectArtifactId() {
		return projectArtifactId;
	}

	public void setProjectArtifactId(String projectArtifactId) {
		this.projectArtifactId = projectArtifactId;
	}

	public MvnDep (){
		
	}
}
