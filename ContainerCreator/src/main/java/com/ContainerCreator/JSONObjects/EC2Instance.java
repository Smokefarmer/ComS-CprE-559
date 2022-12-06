package com.ContainerCreator.JSONObjects;

public class EC2Instance {
	
	private String ID;
	private String keyName;
	private String securityGroupName;
	private String ImageID;
	private String instanceTyp;
	private Boolean WebServer;
	private Boolean SSH;
	public EC2Instance(String ID, String keyName, String securityGroupName, String ImageID, String instanceTyp,
			Boolean WebServer, Boolean SSH) {
		this.ID = ID;
		this.keyName = keyName;
		this.securityGroupName = securityGroupName;
		this.ImageID = ImageID;
		this.instanceTyp = instanceTyp;
		this.WebServer = WebServer;
		this.SSH = SSH;
	}
	public String getinstanceID() {
		return ID;
	}
	public void setinstanceID(String instanceID) {
		this.ID = instanceID;
	}	
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public String getSecurityGroup() {
		return securityGroupName;
	}
	public void setSecurityGroup(String securityGroup) {
		this.securityGroupName = securityGroup;
	}
	public String getImageID() {
		return ImageID;
	}
	public void setImageID(String imageID) {
		ImageID = imageID;
	}
	public String getInstanceTyp() {
		return instanceTyp;
	}
	public void setInstanceTyp(String instanceTyp) {
		this.instanceTyp = instanceTyp;
	}
	
	public Boolean getWebServer() {
		return WebServer;
	}
	public void setWebServer(Boolean webServer) {
		WebServer = webServer;
	}
	public Boolean getSSH() {
		return SSH;
	}
	public void setSSH(Boolean sSH) {
		SSH = sSH;
	}


}
