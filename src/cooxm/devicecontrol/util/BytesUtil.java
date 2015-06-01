package cooxm.devicecontrol.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;


/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：22 Dec 2014 16:58:28 
 */

public class BytesUtil {
	

	/*  -----------------------start of   small Endian  --------------------*/
    public static char bytesToChar(byte[] bytes)
    {
        return (char) ((0xff00 & (bytes[1] << 8) | (0xff & bytes[0])  ));
    }
	  
	public static short bytesToShort(byte[] b) {  
		return (short)( (b[1] & 0xff)<<8 | (b[0] & 0xff) );// << 8);  
	}
	
	public static short bytesToShort(byte[] b, int offset) {  
		return (short)    (b[offset + 1] & 0xff <<8 | (b[offset] & 0xff) << 0)   ; 
	}  
	
	public static int bytesToInt(byte[] b) {  
		return (b[3] & 0xff )<<24 | (b[2] & 0xff )<< 16 | (b[1] & 0xff) << 8 | (b[0] & 0xff) << 0;  
	} 
	
	public static long bytesToLong(byte[] array) {  
		return ((((long) array[0] & 0xff) << 0) | (((long) array[1] & 0xff) << 8) | (((long) array[2] & 0xff) << 16)  
				| (((long) array[3] & 0xff) << 24) | (((long) array[4] & 0xff) << 32)  
				| (((long) array[5] & 0xff) << 40) | (((long) array[6] & 0xff) << 48) | (((long) array[7] & 0xff) <<56));  
	} 
	
    public static byte[] toBytes(short data)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }
    public static byte[] toBytes(byte data)
    {
    	byte[] bytes=new byte[1];
         bytes[0]=data;
         return bytes;
    }

    public static byte[] toBytes(char data)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data);
        bytes[1] = (byte) (data >> 8);
        return bytes;
    }

    public static byte[] toBytes(int data)
    {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    public static byte[] toBytes(long data)
    {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }
    
    public static byte[] toBytes(String data, String charsetName)
    {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }
/*  -----------------------   end of   small End  ----------------------------*/ 
	  

/*  -----------------------   start of   Big End  -----------------------------*/ 	  
   public static short getShort(byte[] bytes)
    {
        return (short) ((0xff & bytes[1]) | (0xff00 & (bytes[0] << 8)));
    }

    public static char getChar(byte[] bytes)
    {
        return (char) ((0xff & bytes[1]) | (0xff00 & (bytes[0] << 8)));
    }

    public static int getInt(byte[] bytes)
    {
        return (0xff & bytes[3]) | (0xff00 & (bytes[2] << 8)) | (0xff0000 & (bytes[1] << 16)) | (0xff000000 & (bytes[0] << 24));
    }
    
    public static long getLong(byte[] bytes)
    {
        return(0xffL & (long)bytes[7]) | (0xff00L & ((long)bytes[6] << 8)) | (0xff0000L & ((long)bytes[5] << 16)) | (0xff000000L & ((long)bytes[4] << 24))
         | (0xff00000000L & ((long)bytes[3] << 32)) | (0xff0000000000L & ((long)bytes[2] << 40)) | (0xff000000000000L & ((long)bytes[1] << 48)) | (0xff00000000000000L & ((long)bytes[0] << 56));
    }
    /*** charsetName: utf-8, gbk*/
    public static String getString(byte[] bytes, String charsetName)
    {
        return new String(bytes, Charset.forName(charsetName));
    }
  
//------------------------------------------------------
    public static byte[] getBytes(short data)
    {
        byte[] bytes = new byte[2];
        bytes[1] = (byte) (data & 0xff);
        bytes[0] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }
    public static byte[] getBytes(byte data)
    {
    	byte[] bytes=new byte[1];
         bytes[0]=data;
         return bytes;
    }

    public static byte[] getBytes(char data)
    {
        byte[] bytes = new byte[2];
        bytes[1] = (byte) (data);
        bytes[0] = (byte) (data >> 8);
        return bytes;
    }

    public static byte[] getBytes(int data)
    {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (data & 0xff);
        bytes[2] = (byte) ((data & 0xff00) >> 8);
        bytes[1] = (byte) ((data & 0xff0000) >> 16);
        bytes[0] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    public static byte[] getBytes(long data)
    {
        byte[] bytes = new byte[8];
        bytes[7] = (byte) (data & 0xff);
        bytes[6] = (byte) ((data >> 8) & 0xff);
        bytes[5] = (byte) ((data >> 16) & 0xff);
        bytes[4] = (byte) ((data >> 24) & 0xff);
        bytes[3] = (byte) ((data >> 32) & 0xff);
        bytes[2] = (byte) ((data >> 40) & 0xff);
        bytes[1] = (byte) ((data >> 48) & 0xff);
        bytes[0] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }
    
    public static byte[] getBytes(String data, String charsetName)
    {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

/*  ---------------------------   end of   Big End  --------------------------------*/ 		

		
/* ------------- 深度复制  ----------------------------*/
		public static Object depthClone(Object srcObj){ 
			Object cloneObj = null; 
			try { 
				ByteArrayOutputStream out = new ByteArrayOutputStream(); 
				ObjectOutputStream oo = new ObjectOutputStream(out); 
				oo.writeObject(srcObj); 
				ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray()); 
				ObjectInputStream oi = new ObjectInputStream(in); 
				cloneObj = oi.readObject(); 
			} catch (IOException e) { 
				e.printStackTrace(); 
			} catch (ClassNotFoundException e) { 
				e.printStackTrace(); 
			} 
			return cloneObj; 
		}
		
		
/* ---------------------------- merger -----------------*/
	    public static byte[] mergeBytes(byte[] pByteA, byte[] pByteB){  
	        int aCount = pByteA.length;  
	        int bCount = pByteB.length;  
	        byte[] b = new byte[aCount + bCount];  
	        for(int i=0;i<aCount;i++){  
	            b[i] = pByteA[i];  
	        }  
	        for(int i=0;i<bCount;i++){  
	            b[aCount + i] = pByteB[i];  
	        }  
	        return b;  
	    }
	    public static byte[] subByte(byte[] b,int start,int length){  
	        if(b.length==0 || length==0 || start+length>b.length){  
	            return null;  
	        }  
	        byte[] bjq = new byte[length];  
	        for(int i = 0; i<length;i++){  
	            bjq[i]=b[start+i];  
	        }  
	        return bjq;  
	    }
// -------------------------------------------
	    
	    
		public static void main(String[] args) {

	    	byte[] b2={1,26};
	    	short x=getShort(b2);
	    	byte[] b4=getBytes(x);
	    	
	    	System.out.println(x);	 
	    	System.out.println(b4[0]+","+b4[1]);
	    	
	    	char ch='5';
	    	byte[] bchar=getBytes(ch);
	    	System.out.println(bchar[0]+","+bchar[1]);
		}
}