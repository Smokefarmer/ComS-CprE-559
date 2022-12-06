package com.ContainerCreator.EC2Creator;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class Client {
	
	private @Id String id;
	private String awsAccessKeyId;
	private String awsSecretAccessKey; 
	private String region;
	
	Client(){}
	
	public Client(String ID, String awsAccessKeyId, String awsSecretAccessKey, String region) {
		this.id = ID;
		this.awsAccessKeyId = awsAccessKeyId;
		this.awsSecretAccessKey = awsSecretAccessKey;
		this.region = region;
	}

	public Client (String awsAccessKeyId, String awsSecretAccessKey, String region){
		
		this.awsAccessKeyId = awsAccessKeyId;
		this.awsSecretAccessKey = awsSecretAccessKey;
		this.region = region;
	}

	public String getAwsAccessKeyId() {
		return awsAccessKeyId;
	}


	public String getAwsSecretAccessKey() {
		return awsSecretAccessKey;
	}


	public String getRegion() {
		return region;
	}

	  @Override
	  public boolean equals(Object o) {

	    if (this == o)
	      return true;
	    if (!(o instanceof Client))
	      return false;
	    Client EC2Client = (Client) o;
	    return Objects.equals(this.awsAccessKeyId, EC2Client.awsAccessKeyId)  && Objects.equals(this.awsSecretAccessKey, EC2Client.awsSecretAccessKey) 
	    		&& Objects.equals(this.region, EC2Client.region) ;
	 }
	  
	  @Override
	  public int hashCode() {
		String input = this.id + this.awsAccessKeyId + this.awsSecretAccessKey + this.region;
		return Objects.hash(this.id, this.awsAccessKeyId, this.awsSecretAccessKey, this.region);
	  }
	  
	  
	  @Override
	  public String toString() {
	    return "Client{" + "id=" + this.id + "key=" + this.awsAccessKeyId + "}";
	  }
	  
	
}
