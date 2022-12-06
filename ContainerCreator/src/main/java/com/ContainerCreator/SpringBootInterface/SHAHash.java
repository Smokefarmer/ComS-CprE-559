package com.ContainerCreator.SpringBootInterface;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class SHAHash {
	
	 public String generateSHAHash(String awsAccessKeyId, String awsSecretAccessKey, String region) {
			String input = awsAccessKeyId + awsSecretAccessKey + region;
		    try {
				return createSHAHash(input);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return String.valueOf(Objects.hash(awsAccessKeyId, awsSecretAccessKey, region));
	}
	
	
	 private String createSHAHash(String input)  
	          throws NoSuchAlgorithmException {

	      String hashtext = null;
	      MessageDigest md = MessageDigest.getInstance("SHA-256");
	      byte[] messageDigest =
	              md.digest(input.getBytes(StandardCharsets.UTF_8));

	      hashtext = convertToHex(messageDigest);
	      return hashtext;
	   }
	
	private String convertToHex(final byte[] messageDigest) {
	      BigInteger bigint = new BigInteger(1, messageDigest);
	      String hexText = bigint.toString(16);
	      while (hexText.length() < 32) {
	         hexText = "0".concat(hexText);
	      }
	      return hexText;
	   }

}
