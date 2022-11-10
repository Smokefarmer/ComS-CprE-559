import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupEgressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupEgressResult;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupEgressRequest;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupEgressResult;
import com.amazonaws.services.ec2.model.SecurityGroup;

public class ContainerCreation {
	private static String groupName = "SecurityGroupSecond";
	private static String description = "Problem 2";
	private static String VpcID = "vpc-0b2fd1716d055a9ec";


	
	 public void createContainer(String[] args) {
		 //Create an instance of AWSCredentials
		 AWSCredentials credentials = null;
		try {
		credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
		throw new AmazonClientException(
		"Cannot load the credentials from the credential profiles file. " +
		"Please make sure that your credentials file is at the correct " +
		"location (/Users/wzhang/.aws/credentials), and is in valid format.", e);
		}
		
		//Create an ec2 instance 
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		//delete Security Group for debug purposes
		deleteSecurityGroup(ec2);
		
		//Create a security group
		
		CreateSecurityGroupRequest create_request = new
		    CreateSecurityGroupRequest()
		        .withGroupName(groupName)
		        .withDescription(description)
		        .withVpcId(VpcID);

		CreateSecurityGroupResult create_response =
		    ec2.createSecurityGroup(create_request);
		
		
	 }
	 
	 public static SecurityGroup updateSecurityGroup(AmazonEC2 ec2, String GroupId){
		 DescribeSecurityGroupsRequest describeUpdate =
		            new DescribeSecurityGroupsRequest()
		                .withGroupIds(GroupId);

	     DescribeSecurityGroupsResult descripeResponseUpdate =
	        ec2.describeSecurityGroups(describeUpdate);
	    
	     return descripeResponseUpdate.getSecurityGroups().get(0);
	 }
	 
	
	//remove security Group for debug purposes
	 public static void deleteSecurityGroup(AmazonEC2 ec2){

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
		
}
