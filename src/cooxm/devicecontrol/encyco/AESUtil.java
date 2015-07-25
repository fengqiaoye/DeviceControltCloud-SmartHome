package cooxm.devicecontrol.encyco;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
//import org.apache.commons.codec.binary.Base64;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;


public enum AESUtil {
	;
	// 共通鍵
	private static final String ENCRYPTION_KEY = "token_key";
	private static final String ENCRYPTION_IV = "0000000000000000";
	//private static SecretKey secretKey;
	

	
	public static String encrypt(String src) {
		try {
			byte[] iv = RandomStringUtils.randomAlphanumeric(16).getBytes("utf-8");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, makeKey(), new IvParameterSpec(iv));
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			result.write(iv);
			cipher.init(Cipher.ENCRYPT_MODE, makeKey(), makeIv());
			return Base64.getEncoder().encodeToString(cipher.doFinal(src.getBytes()))  ;//encodeBytes(cipher.doFinal(src.getBytes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String decrypt(String src) {		
		String decrypted = "";
		try {
			ByteArrayInputStream bytes = new ByteArrayInputStream(Base64.getDecoder().decode(src)); //Base64.decodeBase64(src)
			byte[] iv = new byte[16];
			if (bytes.read(iv) != 16) {
				return null;
			}
			byte[] encrypted = new byte[bytes.available()];
			bytes.read(encrypted);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, makeKey(), makeIv());
			decrypted = new String(cipher.doFinal(Base64.getDecoder().decode(src)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return decrypted;
	}
	
	/**将二进制转换成16进制 
	 * @param buf 
	 * @return 
	 */  
	public static String parseByte2HexStr(byte buf[]) {  
        StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < buf.length; i++) {  
                String hex = Integer.toHexString(buf[i] & 0xFF);  
                if (hex.length() == 1) {  
                        hex = '0' + hex;  
                }  
                sb.append(hex.toUpperCase());  
        }  
        return sb.toString();  
	}  
	
	/**将16进制转换为二进制 
	 * @param hexStr 
	 * @return 
	 */  
	public static byte[] parseHexStr2Byte(String hexStr) {  
	        if (hexStr.length() < 1)  
	                return null;  
	        byte[] result = new byte[hexStr.length()/2];  
	        for (int i = 0;i< hexStr.length()/2; i++) {  
	                int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
	                int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
	                result[i] = (byte) (high * 16 + low);  
	        }  
	        return result;  
	} 
	
	static AlgorithmParameterSpec makeIv() {
		try {
			return new IvParameterSpec(ENCRYPTION_IV.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static Key makeKey() {
		try {
			/*MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] key = md.digest(ENCRYPTION_KEY.getBytes("UTF-8"));*/
			byte[] key = encodeMD5(ENCRYPTION_KEY);
			
			return new SecretKeySpec(key, "AES");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	

    public static byte[] encodeMD5(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        
        return byteArray;
    }
}
