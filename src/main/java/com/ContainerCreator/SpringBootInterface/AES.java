package com.ContainerCreator.SpringBootInterface;

import java.security.*;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	
	private String algorithm = "AES";	
	//https://www.baeldung.com/java-aes-encryption-decryption
	public String encrypt(String input, String myKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
		    InvalidAlgorithmParameterException, InvalidKeyException,
		    BadPaddingException, IllegalBlockSizeException {

			
		    Cipher cipher = Cipher.getInstance(algorithm);
		    cipher.init(Cipher.ENCRYPT_MODE, getKey(myKey));		    
		    byte[] cipherText = cipher.doFinal(input.getBytes());
		    return Base64.getEncoder()
		        .encodeToString(cipherText);
		}
	
	private Key getKey(String myKey) {
		byte[] key = null;
		try {
		key = myKey.getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
	    key = sha.digest(key);
	    key = Arrays.copyOf(key, 16);
		}catch(Exception e) {
			System.out.println(e.toString());
		}
	    return new SecretKeySpec(key, "AES");
	}

	public String decrypt(String cipherText, String iD) throws NoSuchPaddingException, NoSuchAlgorithmException,
		    InvalidAlgorithmParameterException, InvalidKeyException,
		    BadPaddingException, IllegalBlockSizeException {
		    
		    Cipher cipher = Cipher.getInstance(algorithm);
		    cipher.init(Cipher.DECRYPT_MODE, getKey(iD));
		    byte[] plainText = cipher.doFinal(Base64.getDecoder()
		        .decode(cipherText));
		    return new String(plainText);
		}
}
