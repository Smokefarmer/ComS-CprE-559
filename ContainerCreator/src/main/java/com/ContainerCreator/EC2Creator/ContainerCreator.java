package com.ContainerCreator.EC2Creator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ContainerCreator.JSONObjects.EC2Instance;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.InstanceNetworkInterfaceSpecification;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupEgressRequest;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupEgressResult;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.lightsail.model.DeleteInstanceRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupEgressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupEgressResult;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressResult;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.ec2.model.Instance;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ContainerCreator {
	AWSCredentials credentials = null;
	AmazonEC2 ec2 = null;

	public ContainerCreator(AmazonEC2 AWSec2) {
		this.ec2 = AWSec2;
	}
	
	public ContainerCreator(String awsAccessKeyId, String awsSecretAccessKey, String region) {
		try {
			
			 // Your accesskey and secretkey
			credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
						
			
			
			 //Set up the amazon ec2 client 
			ec2 = AmazonEC2ClientBuilder
					.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials))
	            	.withRegion(region)
					.build();
			
			} catch (Exception e) {
			throw new AmazonClientException(
			"Cannot load the credentials from the credential profiles file. " +
			"Please make sure that your credentials file is at the correct " +
			"location (/Users/wzhang/.aws/credentials), and is in valid format.", e);
			}
	}

	public String createEC2(EC2Instance instanceInformation) {
//Source: https://www.edureka.co/community/36250/launching-an-ec2-instance-using-aws-sdk-java
		String keyName  = instanceInformation.getKeyName();
		JSONObject instanceResponse = new JSONObject();
				
		//create KeyPair
		DescribeKeyPairsResult response = ec2.describeKeyPairs();
		Boolean exists = true;
		String keyNameTemp = keyName;
		int count = 1;
		while(exists) {
			exists = false;
			for(KeyPairInfo key_pair : response.getKeyPairs()) {
				//check if key is already created
				if(keyNameTemp.equals(key_pair.getKeyName())) {
					exists = true;
				}
			}
			if(exists) {
				keyNameTemp = keyName;
				keyNameTemp += count;
				count++;
			}
		}
		keyName = keyNameTemp;
		//create key
		CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
		createKeyPairRequest.withKeyName(keyName);
		CreateKeyPairResult createKeyPairResult = ec2.createKeyPair(createKeyPairRequest);				
		instanceResponse.put("key", createKeyPairResult.getKeyPair().getKeyMaterial().toString() );		   
		System.out.println("Key Pair created");

		
		//create Security group
		String securityGroup = cresteSecurityGroup(instanceInformation.getSecurityGroup(), instanceInformation.getWebServer(), instanceInformation.getSSH());
		System.out.println("security group created");

				
		//create Instance
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		runInstancesRequest.withImageId(instanceInformation.getImageID()) 
				.withInstanceType(instanceInformation.getInstanceTyp())
				.withMinCount(1)
				.withMaxCount(1)
				.withKeyName(keyName)
				.withSecurityGroups("launch-wizard-1"); //change varible
		System.out.println("instance created");
	
		RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
		
		Instance instance = runInstancesResult.getReservation().getInstances().get(0);
        String instanceId = instance.getInstanceId();
		
        instanceResponse.put("instanceID", instanceId);
        instanceResponse.put("KeyName", keyName);
		return instanceResponse.toString();
	}
	
	public String cresteSecurityGroup(String groupName, Boolean webServerBoolean, Boolean SSHBoolean) {
		String securityGroupName = groupName;
		String description = "Created by ContainorCreator";
		String VpcID = getVPCID();
		Boolean WebServer = webServerBoolean;
		Boolean SSH = SSHBoolean;
		
		
		//check if group name already exists
		int counter = 1;
		String groupTemp = securityGroupName;
		while(securityGroupExists(groupTemp)) {
			groupTemp = securityGroupName;
			groupTemp += counter;
			counter++;
		}
		securityGroupName = groupTemp;
		
		//Create a security group	
		CreateSecurityGroupRequest create_request = new
		    CreateSecurityGroupRequest()
		        .withGroupName(securityGroupName)
		        .withDescription(description)
		        .withVpcId(VpcID);
	
		CreateSecurityGroupResult create_response =
		    ec2.createSecurityGroup(create_request);
		    
		
		//Revoke the default egress ipPermissions
		IpRange ip_range_delete = new IpRange()
			    .withCidrIp("0.0.0.0/0");
		
		IpPermission delete = new IpPermission()
			    .withIpProtocol("-1")
			    .withIpv4Ranges(ip_range_delete);
				
		RevokeSecurityGroupEgressRequest revokeRequest = 
				new RevokeSecurityGroupEgressRequest()
					.withGroupId(create_response.getGroupId())
					.withIpPermissions(delete);
					
		RevokeSecurityGroupEgressResult revokeResponse = ec2.revokeSecurityGroupEgress(revokeRequest);		
		if(WebServer || SSH) {
			//Inboud Rules
			IpRange ip_range = new IpRange()
				    .withCidrIp("129.186.0.0/16");
			Collection<IpPermission> ipPermissions = new  ArrayList<IpPermission>();
			
			if(WebServer) {
				IpPermission inboundOne = new IpPermission()
					    .withIpProtocol("tcp")
					    .withToPort(80)
					    .withFromPort(80)
					    .withIpv4Ranges(ip_range);
				ipPermissions.add(inboundOne);
			}
			
			if(SSH) {
				IpPermission inboundTwo = new IpPermission()
					    .withIpProtocol("tcp")
					    .withToPort(22)
					    .withFromPort(22)
					    .withIpv4Ranges(ip_range);
				ipPermissions.add(inboundTwo);
			}
			
			
			AuthorizeSecurityGroupIngressRequest auth_request_inbound = new
			    AuthorizeSecurityGroupIngressRequest()
			        .withGroupName(securityGroupName)
			        .withIpPermissions(ipPermissions);
		
			AuthorizeSecurityGroupIngressResult auth_response_inbound =
			    ec2.authorizeSecurityGroupIngress(auth_request_inbound);
			
			//Outbound Rule TODO WEBSERVER outbound and SSH if needed
			IpPermission outbound = new IpPermission()
				    .withIpProtocol("tcp")
				    .withToPort(22)
				    .withFromPort(22)
				    .withIpv4Ranges(ip_range);
			
			AuthorizeSecurityGroupEgressRequest auth_request_outbound = new
					AuthorizeSecurityGroupEgressRequest()
				        .withGroupId(create_response.getGroupId())
				        .withIpPermissions(outbound);
		
			AuthorizeSecurityGroupEgressResult auth_response_outbound =
				    ec2.authorizeSecurityGroupEgress(auth_request_outbound);
		}
		return securityGroupName;
	 }
	 
	private boolean securityGroupExists(String groupName) {
		Boolean exists = false;
		DescribeSecurityGroupsRequest describeUpdate = new DescribeSecurityGroupsRequest();
		DescribeSecurityGroupsResult descripeResponse = ec2.describeSecurityGroups(describeUpdate);
		
		for(SecurityGroup SecurityGroup: descripeResponse.getSecurityGroups()) {
			if(groupName.equals(SecurityGroup.getGroupName())) {
				exists = true;
			}
		}
		return exists;
	}
	
	public String getVPCID() {
		String VPC_ID ="";
		DescribeVpcsRequest describeVPCs = new DescribeVpcsRequest();
		DescribeVpcsResult  descripeResponse = ec2.describeVpcs(describeVPCs);
		
		for(Vpc VPCID: descripeResponse.getVpcs()) {
			if(VPCID.getIsDefault()) {
				VPC_ID = VPCID.getVpcId();
			}
		}
	 
	  return VPC_ID;
		
	}
	
	 public SecurityGroup updateSecurityGroup(AmazonEC2 ec2, String GroupId){
		 DescribeSecurityGroupsRequest describeUpdate =
		            new DescribeSecurityGroupsRequest()
		                .withGroupIds(GroupId);
	
	     DescribeSecurityGroupsResult descripeResponseUpdate =
	        ec2.describeSecurityGroups(describeUpdate);
	    
	     return descripeResponseUpdate.getSecurityGroups().get(0);
	 }
	 
	
	//remove security Group for debug purposes
	 public void deleteSecurityGroup(String groupName){
	
		 DescribeSecurityGroupsRequest describeRequestDebug =
		            new DescribeSecurityGroupsRequest()
		                .withGroupNames(groupName);
	
	     DescribeSecurityGroupsResult descripeResponseDebug =
	         ec2.describeSecurityGroups(describeRequestDebug);
			
		 SecurityGroup securityGroupDebug = descripeResponseDebug.getSecurityGroups().get(0);
	
	     
		 DeleteSecurityGroupRequest request = new DeleteSecurityGroupRequest()
				    .withGroupId(securityGroupDebug.getGroupId());
	
		 DeleteSecurityGroupResult response = ec2.deleteSecurityGroup(request);
	 }
	
	public String getInstanceIP(String instanceID) {
		// TODO Auto-generated method stub
		String publicIP = "";
		
		publicIP = ec2.describeInstances(new DescribeInstancesRequest()
	            .withInstanceIds(instanceID))
	               .getReservations()
	               .stream()
	               .map(Reservation::getInstances)
	               .flatMap(List::stream)
	               .findFirst()
	               .map(Instance::getPublicIpAddress)
	               .orElse(null);
		
		return publicIP;
		
	}
	
	
	public String getAllInstances() {
		//Source: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-ec2-instances.html
		
		JSONObject instanceResponse = new JSONObject();
		boolean done = false;
		int count = 0;
		
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while(!done) {
			
		    DescribeInstancesResult response = ec2.describeInstances(request);
		    
		    for(Reservation reservation : response.getReservations()) {
		    	JSONObject JSONinstance = new JSONObject();
		        for(Instance instance : reservation.getInstances()) {        	
		    		//instanceResponse.put("key", createKeyPairResult.getKeyPair().getKeyMaterial().toString() );		   
		        	JSONinstance.put("ID", instance.getInstanceId().toString());
		        	JSONinstance.put("AMI", instance.getImageId().toString());
		        	JSONinstance.put("Type", instance.getInstanceType().toString());
		        	JSONinstance.put("State", instance.getState().getName().toString());
		        	JSONinstance.put("Monitoring State", instance.getMonitoring().getState().toString());
		            count++;
		        }
		        instanceResponse.put("Instance" + count, JSONinstance);
		    }
	
		    request.setNextToken(response.getNextToken());
	
		    if(response.getNextToken() == null) {
		        done = true;
		    }
		}
		return instanceResponse.toJSONString();
		
	}
	
	
	public String deleteInstance(String instance_id) {
		JSONObject instanceResponse = new JSONObject();
		TerminateInstancesRequest request = new TerminateInstancesRequest()
			    .withInstanceIds(instance_id);
	
		ec2.terminateInstances(request);
		instanceResponse.put("Delete", "True");
		return instanceResponse.toJSONString();
	}
	
	public String stopInstance(String instance_id) {
		//Source: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-ec2-instances.html
		JSONObject instanceResponse = new JSONObject();
		StopInstancesRequest request = new StopInstancesRequest()
			    .withInstanceIds(instance_id);
	
			ec2.stopInstances(request);
			instanceResponse.put("Stop", "True");
			return instanceResponse.toJSONString();	
	}
	
	public String startIntance(String instance_id) {
		//Source: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-ec2-instances.html
		JSONObject instanceResponse = new JSONObject();
		StartInstancesRequest request = new StartInstancesRequest()
			    .withInstanceIds(instance_id);
	
		ec2.startInstances(request);
		instanceResponse.put("Stop", "True");
		return instanceResponse.toJSONString();	
	}
	
	public String rebootIntance(String instance_id) {
		//Source: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-ec2-instances.html
		JSONObject instanceResponse = new JSONObject();
		RebootInstancesRequest request = new RebootInstancesRequest()
			    .withInstanceIds(instance_id);
	
		RebootInstancesResult response = ec2.rebootInstances(request);
		instanceResponse.put("Stop", "True");
		return instanceResponse.toJSONString();	
	}

	
}
