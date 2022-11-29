import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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




public class ContainerCreator {
	AWSCredentials credentials = null;
	AmazonEC2 ec2 = null;

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
	
	public String createEC2() {
//Source: https://www.edureka.co/community/36250/launching-an-ec2-instance-using-aws-sdk-java
		String keyName  = "ContainerCreator";
		String securityGroup = null;
		String ImageID = null;
		String instanceTyp = null;
		String Operationgsystem = "Amazonlinux";
		
		//create KeyPair
		DescribeKeyPairsResult response = ec2.describeKeyPairs();
		Boolean exists = false;
		for(KeyPairInfo key_pair : response.getKeyPairs()) {
			//check if key is already created
			if(keyName.equals(key_pair.getKeyName())) {
				exists = true;
			}
		}		
		//create key
		if(!exists) {
			CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
			createKeyPairRequest.withKeyName(keyName);
			CreateKeyPairResult createKeyPairResult = ec2.createKeyPair(createKeyPairRequest);				
			
		    try {
		    	File PEM = new File(keyName+".pem");
				FileWriter myWriter = new FileWriter(keyName+".pem");
				myWriter.write(createKeyPairResult.getKeyPair().getKeyMaterial().toString());
				myWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
		}

		
		//create Security group
		securityGroup = cresteSecurityGroup();
		
		//set ImageID 
		
		switch (Operationgsystem) {
		case "Amazonlinux": {
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
		default:
			System.out.println("nicht gefunden"); //TODO check why always default
			ImageID = "ami-089a545a9ed9893b6";
		}		
		
		
		//create Instance
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		runInstancesRequest.withImageId("ami-e81b308d") //change varible
				.withInstanceType("t2.micro")
				.withMinCount(1)
				.withMaxCount(1)
				.withKeyName(keyName)
				.withSecurityGroups("launch-wizard-1"); //change varible
			
		RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
		
		Instance instance = runInstancesResult.getReservation().getInstances().get(0);
        String instanceId = instance.getInstanceId();
		
		return instanceId;
	}
	
public String cresteSecurityGroup() {
	String groupName = "ContainorCreator";
	String description = "Created by ContainorCreator";
	String VpcID = getVPCID();
	Boolean WebServer = true;
	Boolean SSH = true;
	
	
	//check if group name already exists
	while(securityGroupExists(groupName)) {
		groupName += "1";
	}
	
	//Create a security group	
	CreateSecurityGroupRequest create_request = new
	    CreateSecurityGroupRequest()
	        .withGroupName(groupName)
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
		        .withGroupName(groupName)
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
	return groupName;
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


public void getAllInstances() {
	//Source: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-ec2-instances.html
	boolean done = false;

	DescribeInstancesRequest request = new DescribeInstancesRequest();
	while(!done) {
	    DescribeInstancesResult response = ec2.describeInstances(request);

	    for(Reservation reservation : response.getReservations()) {
	        for(Instance instance : reservation.getInstances()) {
	            System.out.printf(
	                "\nFound instance with id %s, " +
	                "AMI %s, " +
	                "type %s, " +
	                "state %s " +
	                "and monitoring state %s",
	                instance.getInstanceId(),
	                instance.getImageId(),
	                instance.getInstanceType(),
	                instance.getState().getName(),
	                instance.getMonitoring().getState());
	        }
	    }

	    request.setNextToken(response.getNextToken());

	    if(response.getNextToken() == null) {
	        done = true;
	    }
	}
	
}


public void deleteInstance(String instance_id) {
	TerminateInstancesRequest request = new TerminateInstancesRequest()
		    .withInstanceIds(instance_id);

	ec2.terminateInstances(request);
	
}

public void stopInstance(String instance_id) {
	//Source: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-ec2-instances.html

	StopInstancesRequest request = new StopInstancesRequest()
		    .withInstanceIds(instance_id);

		ec2.stopInstances(request);
	
}

public void startIntance(String instance_id) {
	//Source: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-ec2-instances.html

	StartInstancesRequest request = new StartInstancesRequest()
		    .withInstanceIds(instance_id);

	ec2.startInstances(request);
	
}

public void rebootIntance(String instance_id) {
	//Source: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-ec2-instances.html

	RebootInstancesRequest request = new RebootInstancesRequest()
		    .withInstanceIds(instance_id);

	RebootInstancesResult response = ec2.rebootInstances(request);
	
}

	
}
