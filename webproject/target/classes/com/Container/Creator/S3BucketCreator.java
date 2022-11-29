import java.io.File;
import java.util.List;

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
	

	
public void createBucket(String bucketName) {
	if(s3.doesBucketExistV2(bucketName)) {
		System.out.println("Bucket name is not available."
	      + " Try again with a different Bucket name.");
	    return;
	}

	s3.createBucket(bucketName);
	System.out.println("Successfull bucket created: " + bucketName);

}

public void listBuckets() {
	//Source: https://www.baeldung.com/aws-s3-java

	List<Bucket> buckets = s3.listBuckets();
	for(Bucket bucket : buckets) {
	    System.out.println(bucket.getName());
	}
}

public void deleteBucket(String bucketName) {
	//Source: https://www.baeldung.com/aws-s3-java

	try {
	    s3.deleteBucket(bucketName);
	} catch (AmazonServiceException e) {
	    System.err.println(e.getErrorMessage());
	    return;
	}
	System.out.println("Successfull bucket Deleted: " + bucketName);
}

public void uploadObject(String bucketName, String Path, File file) {
	//Source: https://www.baeldung.com/aws-s3-java
	s3.putObject(
			  bucketName, 
			  Path, 
			  file
			);
}

public void listObjects(String bucketName) {
	
	ObjectListing objectListing = s3.listObjects(bucketName);
	for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
	    System.out.println(os.getKey());
	}
}

public void DownloadObject(String bucketName, String Path) { //TODO
	
	S3Object s3object = s3.getObject(bucketName, Path);
	S3ObjectInputStream inputStream = s3object.getObjectContent();
	//FileUtils.copyInputStreamToFile(inputStream, new File("hello.txt")); Convert Input stream to file
}
public void DownloadMultiObject(String sourceBucketName, String objectPathInSource, String destinationBucketName, String objectPathInDestination) {

	s3.copyObject(
			sourceBucketName, 
			objectPathInSource, 
			destinationBucketName, 
			objectPathInDestination
			);
}

public void deleteObject(String BucketName, String objectPath) {
	s3.deleteObject(BucketName,objectPath);
}

public void deleteMultipleObjects(String BucketName, String objkeyArr[]) {

			DeleteObjectsRequest delObjReq = new DeleteObjectsRequest(BucketName)
			  .withKeys(objkeyArr);
			s3.deleteObjects(delObjReq);}
}
