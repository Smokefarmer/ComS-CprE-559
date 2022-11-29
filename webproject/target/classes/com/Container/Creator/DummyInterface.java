import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class DummyInterface {

	public void EC2Creator(String awsAccessKeyId, String awsSecretAccessKey, String region) { //Region example: us-east-2
		//TODO
	}
	
	public void CreateEC2( String keyName, String securityGroup, String ImageID, String instanceTyp, String Operationgsystem, Boolean WebServer, Boolean SSH) {
		/*
		 * case "Amazonlinux": {
			ImageID = "ami-089a545a9ed9893b6";
		}
		case "Ubuntu": {
			ImageID = "ami-097a2df4ac947655f";
		}
		case "Windows": {
			ImageID = "ami-06013f13f176912f5";
		}
		case "Red Hat": {
			ImageID = "ami-08d616b7fbe4bb9d0";
		}
		case "SUSE Linux": {
			ImageID = "ami-0535d9b70179f9734";
		}
		case "Debian": {
			ImageID = "ami-0c7c4e3c6b4941f0f";
		}
		 */
	}
	
	public void getAllInstances() {
		/*
		Found instance with 
		id just name
		AMI just operation system
		type large, medium, small
		state running, stopped or deleted
		and monitoring state
		*/
	}
	
	public void deleteInstance(String instance_id) {
		
	}

	public void stopInstance(String instance_id) {

		
	}

	public void startIntance(String instance_id) {
		
	}

	public void rebootIntance(String instance_id) {
				
	}
	
	public void  S3Creator(String awsAccessKeyId, String awsSecretAccessKey, String region) {
		
	}
	
	public Boolean createBucket(String bucketName) {
		return true;
	}

	public void listBuckets() {

	}

	public Boolean deleteBucket(String bucketName) {
		return true;
	}

	public Boolean uploadObject(String bucketName, String Path, File file) {
		return true;
	}

	public void listObjects(String bucketName) {
	}

	public void DownloadObject(String bucketName, String Path) { //Path: "Document/hello.txt",

	}
	
	public void CopyingRenamingMovingObject(String sourceBucketName, String objectPathInSource, String destinationBucketName, String objectPathInDestination) {

	}

	public void deleteObject(String BucketName, String objectPath) {
		
	}

	public void deleteMultipleObjects(String BucketName, String objkeyArr[]) {

	}
	
}
