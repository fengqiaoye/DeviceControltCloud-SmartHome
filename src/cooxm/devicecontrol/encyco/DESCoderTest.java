package cooxm.devicecontrol.encyco;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：16 Jan 2015 10:09:32 
 */

//import static org.junit.Assert.*;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
 
/**
 * 
 * @author 梁栋
 * @version 1.0
 * @since 1.0
 */
public class DESCoderTest {
 
    @Test
    public void test() throws Exception {
        String inputStr = "abcdefghdnk-1248729m/.,=-90()";
        String key = DESCoder.initKey();
        System.err.println("原文:\t" + inputStr);
 
        //System.err.println("密钥:\t" + key);
 
        byte[] inputData = inputStr.getBytes();
        inputData = DESCoder.encrypt(inputData, key);
 
        //System.err.println("加密后:\t" + DESCoder.encryptBASE64(inputData));
		System.out.println(new Date());
		byte[] outputData=null;
        for (int i = 0; i < 5000; i++) {
		     outputData = DESCoder.decrypt(inputData, key);
		} 
        System.out.println(new Date());
        String outputStr = new String(outputData);
 
        System.err.println("解密后:\t" + outputStr);
 
        assertEquals(inputStr, outputStr);
    }
}
