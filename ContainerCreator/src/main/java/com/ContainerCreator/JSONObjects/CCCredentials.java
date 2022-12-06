package com.ContainerCreator.JSONObjects;

public class CCCredentials {
	private String awsAccessKeyId;
	private String awsSecretAccessKey;
	private String region;
	
	public CCCredentials(String awsAccessKeyId, String awsSecretAccessKey, String region) {
		super();
		this.awsAccessKeyId = awsAccessKeyId;
		this.awsSecretAccessKey = awsSecretAccessKey;
		this.region = region;
	}
	
	public String getAwsAccessKeyId() {
		return awsAccessKeyId;
	}
	public void setAwsAccessKeyId(String awsAccessKeyId) {
		this.awsAccessKeyId = awsAccessKeyId;
	}
	public String getAwsSecretAccessKey() {
		return awsSecretAccessKey;
	}
	public void setAwsSecretAccessKey(String awsSecretAccessKey) {
		this.awsSecretAccessKey = awsSecretAccessKey;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}

	
	

}
