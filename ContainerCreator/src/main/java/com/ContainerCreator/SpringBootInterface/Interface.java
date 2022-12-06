package com.ContainerCreator.SpringBootInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;

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
import com.ContainerCreator.EC2Creator.ContainerCreator;
import com.ContainerCreator.EC2Creator.Client;
import com.ContainerCreator.JSONObjects.CCCredentials;
import com.ContainerCreator.JSONObjects.EC2Instance;
import com.ContainerCreator.JSONObjects.S3Instance;
import com.ContainerCreator.S3Ceator.S3BucketCreator;

//https://spring.io/guides/tutorials/rest/

@RestController
public class Interface {

	private UserRepository repository;

	Interface(UserRepository repository) {
	    this.repository = repository;
	  }
	
	@GetMapping("/")
	public String index() {
		return "Greetings from Container Creator!";
	}
	
	@GetMapping("/user/{id}")
	public String getClient(@PathVariable String id) {
		try {
			return repository.findById(id).get().toString();
		} catch (Exception e) {
			System.out.println(e);
		}
		return "User not found";
	}
	
	//@RequestBody String awsAccessKeyId,@RequestBody String awsSecretAccessKey,@RequestBody String region
	@PostMapping(path="/login", consumes="application/json", produces="application/json")
	public String EC2Creator(@RequestBody CCCredentials credentials) { //Region example: us-east-2
		SHAHash Hash = new SHAHash();
		String ID = Hash.generateSHAHash(credentials.getAwsAccessKeyId(), credentials.getAwsSecretAccessKey(), credentials.getRegion());
		Client newUser = new Client(ID,credentials.getAwsAccessKeyId(), credentials.getAwsSecretAccessKey(), credentials.getRegion());

		if(repository.findById(ID).isEmpty()) {
			try {
				repository.save(newUser);
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			
		}
		
		JSONObject response = new JSONObject();
		response.put("id", ID);
		return response.toString();
	}
	
	@PostMapping(path="/createEC2/{id}", consumes="application/json", produces="application/json")
	public String CreateEC2(@PathVariable String id, @RequestBody EC2Instance instanceInformation) { 
		
		Client user = null;
		try {
			user = repository.findById(id).get();
			ContainerCreator creator = new ContainerCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.createEC2(instanceInformation);
		} catch (Exception e) {
			System.out.println(e);
			return "User not found";
		}
		
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
	
	@GetMapping(path="/getALLInstances/{id}", produces="application/json")
	public String getAllInstances(@PathVariable String id) {
		Client user = null;
		try {
			user = repository.findById(id).get();
			ContainerCreator creator = new ContainerCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.getAllInstances();
		} catch (Exception e) {
			System.out.println(e);
			return "User not found";
		}
		/*
		Found instance with 
		id just name
		AMI just operation system
		type large, medium, small
		state running, stopped or deleted
		and monitoring state
		*/
	}
	
	@PostMapping(path="/deleteEC2/{id}", consumes="application/json", produces="application/json")
	public String deleteInstance(@PathVariable String id, @RequestBody EC2Instance instanceInformation) {
		Client user = null;
		try {
			user = repository.findById(id).get();
			ContainerCreator creator = new ContainerCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.deleteInstance(instanceInformation.getinstanceID());
		} catch (Exception e) {
			System.out.println(e);
			return "User not found";
		}
	}

	@PostMapping(path="/stopEC2/{id}", consumes="application/json", produces="application/json")
	public String  stopInstance(@PathVariable String id, @RequestBody EC2Instance instanceInformation) {
		Client user = null;
		try {
			user = repository.findById(id).get();
			ContainerCreator creator = new ContainerCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.stopInstance(instanceInformation.getinstanceID());
		} catch (Exception e) {
			System.out.println(e);
			return "User not found";
		}
		
	}

	@PostMapping(path="/startEC2/{id}", consumes="application/json", produces="application/json")
	public String startIntance(@PathVariable String id, @RequestBody EC2Instance instanceInformation) {
		Client user = null;
		try {
			user = repository.findById(id).get();
			ContainerCreator creator = new ContainerCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.startIntance(instanceInformation.getinstanceID());
		} catch (Exception e) {
			System.out.println(e);
			return "User not found";
		}
	}

	@PostMapping(path="/rebootEC2/{id}", consumes="application/json", produces="application/json")
	public String rebootIntance(@PathVariable String id, @RequestBody EC2Instance instanceInformation) {
		Client user = null;
		try {
			user = repository.findById(id).get();
			ContainerCreator creator = new ContainerCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.rebootIntance(instanceInformation.getinstanceID());
		} catch (Exception e) {
			System.out.println(e);
			return "User not found";
		}		
	}
	
	@PostMapping(path="/createBucket/{id}", consumes="application/json", produces="application/json")
	public String createBucket(@PathVariable String id, @RequestBody S3Instance bucketInformation) {
		Client user = null;
		try {
			user = repository.findById(id).get();
			S3BucketCreator creator = new S3BucketCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.createBucket(bucketInformation.getBucketName());
		} catch (Exception e) {
			System.out.println(e);
			return "User not found";
		}
		
	}

	@GetMapping(path="/getALLBuckets/{id}", produces="application/json")
	public String listBuckets(@PathVariable String id) {
		Client user = null;
		try {
			user = repository.findById(id).get();
			S3BucketCreator creator = new S3BucketCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.listBuckets();
		} catch (Exception e) {
			System.out.println(e);
			return e.toString();
		}
	}

	@PostMapping(path="/deleteBucket/{id}", consumes="application/json", produces="application/json")
	public String deleteBucket(@PathVariable String id, @RequestBody S3Instance bucketInformation) {
		Client user = null;
		try {
			user = repository.findById(id).get();
			S3BucketCreator creator = new S3BucketCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.deleteBucket(bucketInformation.getBucketName());
		} catch (Exception e) {
			System.out.println(e);
			return "User not found";
		}
	}

	@PostMapping(path="/uploadObject/{id}/{bucketname}", produces="application/json")
	public String uploadObject(@PathVariable("id") String id,@PathVariable("bucketname") String bucketname, @RequestParam MultipartFile file) {
		File uploadfile = new File("src/main/resources/"+file.getOriginalFilename());
		try {
			InputStream initialStream = file.getInputStream();
			byte[] buffer = new byte[initialStream.available()];
			initialStream.read(buffer);
			OutputStream outStream = new FileOutputStream(uploadfile);
			outStream.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Client user = null;
		try {
			user = repository.findById(id).get();
			S3BucketCreator creator = new S3BucketCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.uploadObject(bucketname,file.getOriginalFilename(), uploadfile); //String bucketName, String Path, File file
		} catch (Exception e) {
			System.out.println(e);
			return e.toString();
		}
		
	}

	@PostMapping(path="/listObjects/{id}",consumes="application/json", produces="application/json")
	public String listObjects(@PathVariable String id, @RequestBody S3Instance bucketInformation) {
		Client user = null;
		try {
			user = repository.findById(id).get();
			S3BucketCreator creator = new S3BucketCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.listObjects(bucketInformation.getBucketName());
		} catch (Exception e) {
			System.out.println(e);
			return e.toString();
		}
	}

	@PostMapping(path="/downloadObjects/{id}",consumes="application/json", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] DownloadObject(@PathVariable String id, @RequestBody S3Instance bucketInformation) { //Path: "Document/hello.txt",
		Client user = null;
		InputStream in = null;
		try {
			user = repository.findById(id).get();
			S3BucketCreator creator = new S3BucketCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			in = creator.DownloadObject(bucketInformation.getBucketName(), bucketInformation.getPath());
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			return IOUtils.toByteArray(in);
		} catch (IOException e) {
			System.out.print(e.toString());
		}
		return null;
	}

	@PostMapping(path="/deleteObject/{id}", consumes="application/json", produces="application/json")
	public String deleteObject(@PathVariable String id, @RequestBody S3Instance bucketInformation) {

		Client user = null;
		try {
			user = repository.findById(id).get();
			S3BucketCreator creator = new S3BucketCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.deleteObject(bucketInformation.getBucketName(), bucketInformation.getPath());
		} catch (Exception e) {
			System.out.println(e);
			return e.toString();
		}		
	}

	@PostMapping(path="/deleteObjects/{id}", consumes="application/json", produces="application/json")
	public String deleteMultipleObjects(@PathVariable String id, @RequestBody S3Instance bucketInformation) {
		Client user = null;
		try {
			user = repository.findById(id).get();
			S3BucketCreator creator = new S3BucketCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.deleteMultipleObjects(bucketInformation.getBucketName(), bucketInformation.getObjkeyArr());
		} catch (Exception e) {
			System.out.println(e);
			return e.toString();
		}	
	}
	
	
	@PostMapping(path="/copyObject/{id}",consumes="application/json", produces="application/json")
	public String CopyingRenamingMovingObject(@PathVariable String id, @RequestBody S3Instance bucketInformation) { //String sourceBucketName, String objectPathInSource, String destinationBucketName, String objectPathInDestination) {
		System.out.println(bucketInformation.getSourceBucketName());
		System.out.println(bucketInformation.getObjectPathInSource());
		System.out.println(bucketInformation.getDestinationBucketName());
		System.out.println(bucketInformation.getDestinationBucketName());
		
		Client user = null;
		try {
			user = repository.findById(id).get();
			S3BucketCreator creator = new S3BucketCreator(user.getAwsAccessKeyId(), user.getAwsSecretAccessKey(), user.getRegion());
			return creator.CopyObjects(bucketInformation.getSourceBucketName(), bucketInformation.getObjectPathInSource(), bucketInformation.getDestinationBucketName(), bucketInformation.getObjectPathInDestination());
		} catch (Exception e) {
			System.out.println(e);
			return e.toString();
		}
	}
	
	
}