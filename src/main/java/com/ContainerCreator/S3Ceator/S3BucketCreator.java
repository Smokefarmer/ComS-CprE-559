package com.ContainerCreator.S3Ceator;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class S3BucketCreator {
	AWSCredentials credentials = null;
	AmazonS3 s3 = null;
	
	public S3BucketCreator(String awsAccessKeyId, String awsSecretAccessKey, String region) {
		
		 // Your accesskey and secretkey
		credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
		
		 s3 = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(region)
				  .build();
	}
	

	
	public String createBucket(String bucketName) {
		JSONObject instanceResponse = new JSONObject();
		if(s3.doesBucketExistV2(bucketName)) {
			System.out.println("Bucket name is not available."
		      + " Try again with a different Bucket name.");
			instanceResponse.put("Request", "False");
			return instanceResponse.toJSONString();
		}
	
		s3.createBucket(bucketName);
		System.out.println("Successfull bucket created: " + bucketName);
		instanceResponse.put("Request", "True");
		return instanceResponse.toJSONString();
	}
	
	public String listBuckets() {
		//Source: https://www.baeldung.com/aws-s3-java
		JSONObject instanceResponse = new JSONObject();
		JSONArray JSONinstance = new JSONArray();
		List<Bucket> buckets = s3.listBuckets();
		for(Bucket bucket : buckets) {
			JSONinstance.add(bucket.getName());
		}
		instanceResponse.put("Buckets", JSONinstance);
		return instanceResponse.toJSONString();
	}
	
	public String deleteBucket(String bucketName) {
		//Source: https://www.baeldung.com/aws-s3-java
		JSONObject instanceResponse = new JSONObject();
		try {
		    s3.deleteBucket(bucketName);
		} catch (AmazonServiceException e) {
		    System.err.println(e.getErrorMessage());
		    return e.toString();
		}
		System.out.println("Successfull bucket Deleted: " + bucketName);
		instanceResponse.put("Request", "True");
		return instanceResponse.toJSONString();
	}
	
	public String uploadObject(String bucketName, String Path, File file) {
		//Source: https://www.baeldung.com/aws-s3-java
		JSONObject instanceResponse = new JSONObject();
		try {
			s3.putObject(
					  bucketName, 
					  Path, 
					  file
					);
		} catch (Exception e) {
			return e.toString();
		}
	
		instanceResponse.put("Request", "True");
		return instanceResponse.toJSONString();
	}
	
	public String listObjects(String bucketName) {
		JSONObject instanceResponse = new JSONObject();
		JSONObject JSONinstance = new JSONObject();
		int counter = 1;
		ObjectListing objectListing = s3.listObjects(bucketName);
		for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
			JSONinstance.put("Object" + counter, os.getKey());
			JSONinstance.put("Object" + counter, os.getKey());
			counter++;
		}
		instanceResponse.put("Objects", JSONinstance);
		return instanceResponse.toJSONString();
	}
	
	public InputStream DownloadObject(String bucketName, String Path) { //TODO
		
		S3Object s3object = s3.getObject(bucketName, Path);
		return s3object.getObjectContent();	
		
	}
	public String CopyObjects(String sourceBucketName, String objectPathInSource, String destinationBucketName, String objectPathInDestination) {
		JSONObject instanceResponse = new JSONObject();
		s3.copyObject(
				sourceBucketName, 
				objectPathInSource, 
				destinationBucketName, 
				objectPathInDestination
				);
		instanceResponse.put("Copy", "True");
		return instanceResponse.toJSONString();
		
	}
	
	public String deleteObject(String BucketName, String objectPath) {
		JSONObject instanceResponse = new JSONObject();
		try {
			s3.deleteObject(BucketName,objectPath);
		} catch (Exception e) {
			return e.toString();
		}
		s3.deleteObject(BucketName,objectPath);
		instanceResponse.put("Delete", "True");
		return instanceResponse.toJSONString();
	}
	
	public String deleteMultipleObjects(String BucketName, String objkeyArr[]) {
		JSONObject instanceResponse = new JSONObject();
		DeleteObjectsRequest delObjReq = new DeleteObjectsRequest(BucketName)
		  .withKeys(objkeyArr);
		s3.deleteObjects(delObjReq);
		instanceResponse.put("Delete", "True");
		return instanceResponse.toJSONString();
	}
	
}