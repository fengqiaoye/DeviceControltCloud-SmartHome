package cooxm.devicecontrol.encyco;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class AESUtil2 {

    


    /**
     * 加密
     * @param content   待加密字符串
     * @param key   密码
     * @return
     * @throws Exception
     */
    public static String encodeAES(String content, String key){

        String str_encode = null;
        try{
            byte[] input = content.getBytes("utf-8");

            byte[] thedigest = encodeMD5(key);

            String iv = getIv();
            byte[] byte_iv = iv.getBytes();

            SecretKeySpec skc = new SecretKeySpec(thedigest, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skc, new IvParameterSpec(byte_iv));

            byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
            int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
            ctLength += cipher.doFinal(cipherText, ctLength);

            String str_base64 = Base64.getEncoder().encodeToString(cipherText).trim();
            // 带上iv再base64
            str_encode = Base64.getEncoder().encodeToString((iv+str_base64).getBytes());
        }catch (Exception e){
            e.printStackTrace();
        }


        return str_encode;
    }

    /**
     * AES 解密
     * @throws Exception
     */
    public static String decodeAES(String encrypted, String key) {
        String str_decode = null;
        try{
            byte[] decode_base64 = Base64.getDecoder().decode(encrypted);
            String str = new String(decode_base64);

            String str_iv = str.substring(0, 16);
            String content_base64 = str.substring(16);
            byte[] content = Base64.getDecoder().decode(content_base64);

            byte[] thedigest = encodeMD5(key);
            SecretKeySpec skey = new SecretKeySpec(thedigest, "AES");
            Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            dcipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(str_iv.getBytes()));

            byte[] clearbyte = dcipher.doFinal(content);

            str_decode = new String(clearbyte);
        }catch (Exception e){
            e.printStackTrace();
        }

        return str_decode;
    }
    

    /**
     * 获取随机IV
     * @return 16位IV
     */
    public static String getIv(){
        StringBuffer str = new StringBuffer();
        Random rdm = new Random();
        for(int i=0;i<16;i++){
            char ch = (char)rdm.nextInt(128);
            str.append(ch);
        }

        return str.toString();
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

    
	public static void main(String[] args) {

		
		String tes=AESUtil2.encodeAES("keyfiles3/AC/codes/85.txt","token_key");
		String encyco=AESUtil2.decodeAES(tes,"token_key");
		System.out.println("encyco = "+tes);
		System.out.println("decyco = "+encyco);
		
		
		
	}
}
