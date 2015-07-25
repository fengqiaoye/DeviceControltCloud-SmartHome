package cooxm.devicecontrol.encyco;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import cooxm.devicecontrol.synchronize.HttpDownLoad;

/**
 * 决定AES加密算法的参数有：<br>
 * 模式：固定为CBC<br>
 * 密钥长度：固定为256<br>
 * 密钥：双方确定<br>
 * 密钥偏移量iv：双方确定<br>
 * 补码方式：PKCS5Padding或PKCS7Padding，当前确定使用PKCS5Padding<br>
 * 加密结果编码方式：十六进制或Base64，由于Base64更短，当前确定使用Base64
 * 
 * @author Jonney
 *
 */
public class TuringCatAesCrypto {

	private SecretKey secretKey;

	public void setToken(String token) {
		try {
			secretKey = new SecretKeySpec(DigestUtils.md5Hex(token).getBytes("utf-8"), "AES");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public String encrypt(String plainText) {
		try {
			byte[] iv = RandomStringUtils.randomAlphanumeric(16).getBytes("utf-8");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			result.write(iv);			
			byte[] encrypted = Base64.encodeBase64(cipher.doFinal(plainText.getBytes("utf-8")));
			result.write(encrypted);
			return Base64.encodeBase64String(result.toByteArray());
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException | IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	/**<pre>系统会报出如下异常：
	javax.crypto.IllegalBlockSizeException: Input length must be multiple of 16 when decrypting with padded cipher
	        at com.sun.crypto.provider.SunJCE_f.b(DashoA13*..)
	        at com.sun.crypto.provider.SunJCE_f.b(DashoA13*..)
	        at com.sun.crypto.provider.AESCipher.engineDoFinal(DashoA13*..)
	        at javax.crypto.Cipher.doFinal(DashoA13*..)
	这主要是因为加密后的byte数组是不能强制转换成字符串的，换言之：字符串和byte数组在这种情况下不是互逆的；要避免这种情况，
	我们需要做一些修订，可以考虑将二进制数据转换成十六进制表示，主要有如下两个方法：
	parseByte2HexStr 将二进制转换成16进制 
	parseHexStr2Byte 将16进制转换为二进制 */
	public String decrypt(String encryptedText) {
		try {
			ByteArrayInputStream bytes = new ByteArrayInputStream(Base64.decodeBase64(encryptedText));
			byte[] iv = new byte[16];
			if (bytes.read(iv) != 16) {
				return null;
			}
			byte[] encrypted = new byte[bytes.available()];
			bytes.read(encrypted);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
			return new String(cipher.doFinal(Base64.decodeBase64(encrypted)), "utf-8");
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException | IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
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

	public static void main(String[] args) {
		String src = "keyfiles3/AC/codes/85.txt";//"keyfiles3/AC/codes/85.txt";
		TuringCatAesCrypto crypto = new TuringCatAesCrypto();
		crypto.setToken("token_key");
		System.out.println("src: " + src);
		String encrypted = crypto.encrypt(src);
		System.out.println("encrypted: " + encrypted);
		String dec="Mm1iR1gwaVJKQzlrYnRPUFpmendvdWNnQzg2dk5Oc25ac3hoKzBYcWZIWlVjODdGYjVjaDdKK1pON1E9";
		String decrypted = crypto.decrypt(dec);
		System.out.println("decrypted: " + decrypted);
		
		String url="http://120.24.81.23/file/download.php?info=";
		String newURL=url+dec;
		System.out.println("downloading : " + newURL);
		HttpDownLoad hd=new HttpDownLoad();
		Date start=new Date();
		hd.download(newURL);		
		System.out.println("finished in "+(new Date().getTime()-start.getTime())/1000 +" secods");
		
//		String tes=AESUtil2.encodeAES("keyfiles3/AC/codes/85.txt","token_key");
//		String encyco=AESUtil.decrypt(tes);
//		System.out.println(tes);
//		System.out.println(encyco);
		
	}
}