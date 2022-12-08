package com.ContainerCreator.JSONObjects;

import java.io.File;

public class S3Instance {
	private String bucketName;
	private String ClientID;
	private String Path;
	private String sourceBucketName;
	private String objectPathInSource; 
	private String destinationBucketName;
	private String objectPathInDestination;
	private String Objects[];
	public S3Instance(String ClientID, String bucketName, String Path, String sourceBucketName, String objectPathInSource,
			String destinationBucketName, String objectPathInDestination, String[] Objects) {
		super();
		this.bucketName = bucketName;
		this.Path = Path;
		this.ClientID = ClientID;
		this.sourceBucketName = sourceBucketName;
		this.objectPathInSource = objectPathInSource;
		this.destinationBucketName = destinationBucketName;
		this.objectPathInDestination = objectPathInDestination;
		this.Objects = Objects	;
	}
	public String getBucketName() {
		return bucketName;
	}
	public String getClientID() {
		return ClientID;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getPath() {
		return Path;
	}
	public void setPath(String path) {
		Path = path;
	}
	public String getSourceBucketName() {
		return sourceBucketName;
	}
	public void setSourceBucketName(String sourceBucketName) {
		this.sourceBucketName = sourceBucketName;
	}
	public String getObjectPathInSource() {
		return objectPathInSource;
	}
	public void setObjectPathInSource(String objectPathInSource) {
		this.objectPathInSource = objectPathInSource;
	}
	public String getDestinationBucketName() {
		return destinationBucketName;
	}
	public void setDestinationBucketName(String destinationBucketName) {
		this.destinationBucketName = destinationBucketName;
	}
	public String getObjectPathInDestination() {
		return objectPathInDestination;
	}
	public void setObjectPathInDestination(String objectPathInDestination) {
		this.objectPathInDestination = objectPathInDestination;
	}
	public String[] getObjkeyArr() {
		return Objects;
	}
	public void setObjkeyArr(String[] objkeyArr) {
		this.Objects = objkeyArr;
	}
}
