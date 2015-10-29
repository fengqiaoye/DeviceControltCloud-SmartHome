package cooxm.devicecontrol.socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.sparta.xpath.ThisNodeTest;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.util.BytesUtil;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：22 Dec 2014 16:58:28 
 */
public class Message extends Header implements Cloneable {
	public static final short COMMAND_ACK_OFFSET       		   =  0x4000;
	static Logger log= Logger.getLogger(Message.class);
	 //public Header header;
	 private String cookie=null;
	 private JSONObject json;
	 int serverID;
	 Date createTime;
	 long timeOut=-1;
     
	 

	 
	public long getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public void setServerID(int serverID) {
		this.serverID = serverID;
	}	
	public int getServerID() {
		return serverID;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
		this.msgLen=(short) (getWordCount(json.toString())+ getWordCount(cookie) );
	}
	public JSONObject getJson() {
		return json;
	}
	public void setJson(JSONObject json) {
		this.json = json;
		this.msgLen=(short) (getWordCount(json.toString())+ getWordCount(cookie) );
	}
	public Message(){}
	
	public Message(short commandID,String cookie,JSONObject json){
		this.headTag="#XRPC#";
		this.mainVersion=1;
		this.subVersion=1;
		//this.msgLen=(short) (json.toString().length()+cookie.length());
		//2015-05-25 richard备注：如果json字符串含有中文会导致计算长度错误；
		int a=getWordCount(json.toString());
		int b= getWordCount(cookie) ;
		this.msgLen=(short) (a+b);
		this.commandID=commandID;
		this.sequeeceNo=(int) (System.currentTimeMillis()/1000);
		this.encType=0; 
		this.cookieLen=(short) cookie.length();
		this.reserve=0;
		this.cookie=cookie;
		this.json=json;
	}
	

    
    public Message(short commandID, String cookie, String str) throws JSONException {
		this.headTag="#XRPC#";
		this.mainVersion=1;
		this.subVersion=1;
		//this.msgLen=(short) (json.toString().length()+cookie.length());
		//2015-05-25 richard备注：如果json字符串含有中文会导致计算长度错误；
		int a=getWordCount(str.toString());
		int b= getWordCount(cookie) ;
		this.msgLen=(short) (a+b);
		this.commandID=commandID;
		this.sequeeceNo=(int) (System.currentTimeMillis()/1000);
		this.encType=0; 
		this.cookieLen=(short) cookie.length();
		this.reserve=0;
		this.cookie=cookie;
		this.json=new JSONObject(str);
 	   
 	}
    
    public Message(Header head, String cookie, JSONObject json2) {
    	super(head);
    	this.cookie=cookie;
    	this.json=json2;
    	super.cookieLen=(short) cookie.length();
    	//super.msgLen=(short) ((short) cookie.length()+json2.toString().length());
    	super.msgLen=(short) (getWordCount(json.toString())+ getWordCount(cookie) );
	}
	
	public Message(Message msg){
		this.headTag=msg.headTag;
		this.mainVersion=msg.mainVersion;
		this.subVersion=msg.subVersion;
		this.msgLen=msg.msgLen;
		this.commandID=msg.commandID;
		this.sequeeceNo=msg.sequeeceNo;
		this.encType=msg.encType; 
		this.cookieLen=msg.cookieLen;
		this.reserve=msg.reserve;
		this.cookie=msg.cookie;
		this.json=msg.json;
		this.serverID=msg.serverID;
		this.createTime=msg.createTime;
	}
	
	Message(Header header,String cookie,  byte[] command) {
		super(header);	
		this.cookie=cookie;
		String jsonStr;
		try {
			jsonStr = new String(command,"utf-8");
			this.json=new JSONObject(jsonStr);
			this.msgLen=(short) (cookie.length()+command.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Message(Header header,String cookie, String command) throws JSONException {
		super(header);	
		this.cookie=cookie;

	    this.json=new JSONObject(command);

    	super.cookieLen=(short) cookie.length();
		//2015-05-25 richard备注：如果json字符串含有中文会导致计算长度错误；
    	//super.msgLen=(short) ((short) cookie.length()+command.length());
    	super.msgLen=(short) (getWordCount(json.toString())+ getWordCount(cookie) );
	}
	
    
	/***23 bytes of header
	 *  of cookie
	 * @throws UnsupportedEncodingException 
	 *  */
    public Message(byte[] msg) {    
	     byte[] headTag     ={msg[0],msg[1],msg[2],msg[3],msg[4],msg[5]};	
		 byte mainVersion	=msg[6];
		 byte subVersion	=msg[7];
		 byte[] msgLen		= {msg[8],msg[9]};	
		 byte[] commandID	= {msg[10],msg[11]};
		 byte[] sequeeceNo	={msg[12],msg[13],msg[14],msg[15]} ;
		 byte   encType     =msg[16];
		 byte[] cookieLen	= {msg[17],msg[18]};
		 byte[] reserve	    ={msg[19],msg[20],msg[21],msg[22]} ;

		 
		try {
			this.headTag=new String(headTag,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		};			
		this.mainVersion=mainVersion;
		this.subVersion=subVersion;
		this.msgLen=BytesUtil.bytesToShort(msgLen);
		this.commandID=BytesUtil.bytesToShort(commandID);
		this.sequeeceNo=BytesUtil.bytesToInt(sequeeceNo);
		this.encType=encType; 
		this.cookieLen=BytesUtil.bytesToShort(cookieLen);
		this.reserve=BytesUtil.bytesToInt(reserve);
		/*System.out.println("mainVersion="+this.mainVersion+",subVersion="+this.subVersion+",msgLen="+this.msgLen+",commandID="+this.commandID+
				",sequeeceNo="+this.sequeeceNo+
				",encType="+this.encType+
				",cookieLen="+this.cookieLen+
				",reserve="+this.reserve
				);*/
		
		 byte[] cookieByte=new byte[this.cookieLen];
		 for(int i=0;i<this.cookieLen;i++){
			 cookieByte[i]	    = msg[23+i] ;
		 }
		 this.cookie=new String(cookieByte);
		 
		String str=new String(msg);		
		String jsonStr=str.substring(23+this.cookieLen, str.length());
		try {
			this.json=new JSONObject(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
    


	/*public boolean isValid() {   
		return this.isValid();
	}*/
    

public String toString(){
	   String out=new String();
	   DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   out+=  		this.headTag		+",";
	   out+=		this.mainVersion	+",";
	   out+=		this.subVersion	+",";
	   out+=		this.msgLen		+",";
	   out+=		Integer.toHexString(this.commandID) 	+",";
	   out+=		this.sequeeceNo	+",";
	   out+=		this.encType		+",";
	   out+=		this.cookieLen	+",";
	   out+=		this.reserve		+",";  
	   out+=		this.cookie		+","; 
	   if(this.json!=null){
		   out+=		this.json.toString()	; 
	   }

	   return out;
    }
   
   public byte[] toBytes(){
	   
	   byte[] b1=	BytesUtil.mergeBytes(BytesUtil.getBytes(this.headTag, "UTF-8"), BytesUtil.getBytes(this.mainVersion));
	   byte[] b3=	BytesUtil.mergeBytes(b1, BytesUtil.getBytes(this.subVersion));
	   byte[] b4=	BytesUtil.mergeBytes(b3, BytesUtil.getBytes(this.msgLen));
	   byte[] b5=	BytesUtil.mergeBytes(b4, BytesUtil.getBytes(this.commandID));
	   byte[] b6=	BytesUtil.mergeBytes(b5, BytesUtil.getBytes(this.sequeeceNo));
	   byte[] b7=	BytesUtil.mergeBytes(b6, BytesUtil.getBytes(this.encType));
	   byte[] b8=	BytesUtil.mergeBytes(b7, BytesUtil.getBytes(this.cookieLen));
	   byte[] b9=	BytesUtil.mergeBytes(b8, BytesUtil.getBytes(this.reserve));
	   byte[] b10=	BytesUtil.mergeBytes(b9, BytesUtil.getBytes(this.cookie, "UTF-8"));
	   byte[] b11=	BytesUtil.mergeBytes(b10,BytesUtil.getBytes(this.json.toString(), "UTF-8"));
	   return b11;
    }
   
   public byte[] toBytesSmallEnd(){
	   
	   byte[] b1=	BytesUtil.mergeBytes(BytesUtil.toBytes(this.headTag, "UTF-8"), BytesUtil.toBytes(this.mainVersion));
	   byte[] b3=	BytesUtil.mergeBytes(b1, BytesUtil.toBytes(this.subVersion));
	   byte[] b4=	BytesUtil.mergeBytes(b3, BytesUtil.toBytes(this.msgLen));
	   byte[] b5=	BytesUtil.mergeBytes(b4, BytesUtil.toBytes(this.commandID));
	   byte[] b6=	BytesUtil.mergeBytes(b5, BytesUtil.toBytes(this.sequeeceNo));
	   byte[] b7=	BytesUtil.mergeBytes(b6, BytesUtil.toBytes(this.encType));
	   byte[] b8=	BytesUtil.mergeBytes(b7, BytesUtil.toBytes(this.cookieLen));
	   byte[] b9=	BytesUtil.mergeBytes(b8, BytesUtil.toBytes(this.reserve));
	   byte[] b10=	BytesUtil.mergeBytes(b9, BytesUtil.toBytes(this.cookie, "UTF-8"));
	   byte[] b11=	BytesUtil.mergeBytes(b10,BytesUtil.toBytes(this.json.toString(), "UTF-8"));
	   return b11;
    }
   
   
   public int getSequenceNO() {
	   int SequenceNO=-1;
	   if(this.cookie==null){
         return -1;
	   }else{
		   SequenceNO=Integer.parseInt(this.cookie.split("_")[0]);
	   }	   
	return SequenceNO;	
   }
   
 /*   public void writeBytesToSock(Socket sock){
    	try {
			DataOutputStream dataout= new DataOutputStream(sock.getOutputStream());
			   dataout.write(  	BytesUtil.getBytes(this.headTag, "UTF-8")  );
			   dataout.write(	BytesUtil.getBytes(	this.mainVersion)	  );
			   dataout.write(	BytesUtil.getBytes(	this.subVersion)       );
			   dataout.write(	BytesUtil.getBytes(	this.msgLen)		      );
			   dataout.write(	BytesUtil.getBytes(	this.commandID)	      );
			   dataout.write(	BytesUtil.getBytes(	this.sequeeceNo)	      );
			   dataout.write(	BytesUtil.getBytes(	this.encType)		  );
			   dataout.write(	BytesUtil.getBytes(	this.cookieLen)	      );
			   dataout.write(	BytesUtil.getBytes(	this.reserve	)	      ) ;  
			   dataout.write(	BytesUtil.getBytes(	this.cookie,"UTF-8")		          )   ; 
			   dataout.write(	BytesUtil.getBytes(	this.json.toString(),"UTF-8") )	; 
			   dataout.flush();
			   dataout.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }*/
    
    public  void writeBytesToSock2(Socket sock) throws IOException{
    	synchronized (sock) {
			DataOutputStream dataout= new DataOutputStream(sock.getOutputStream());
			dataout.write(this.toBytes());
			dataout.flush();
		}
			// 2015-05-04
			//dataout.close();    	
    }
    
   /*public void writeToSock(Socket sock){
    	try {
			DataOutputStream dataout= new DataOutputStream(sock.getOutputStream());
			   dataout.writeBytes(  	this.headTag            )  ;
			   dataout.writeByte(		this.mainVersion)	  ;
			   dataout.writeByte(		this.subVersion)       ;
			   dataout.writeShort(		this.msgLen)		      ;
			   dataout.writeShort(		this.commandID)	      ;
			   dataout.writeInt(		this.sequeeceNo)	      ;
			   dataout.writeByte(		this.encType)		  ;
			   dataout.writeByte(		this.cookieLen)	      ;	   
			   dataout.writeInt(		this.reserve	)	       ;  
			   dataout.writeBytes(		this.cookie)		             ; 
			   dataout.writeBytes(		this.json.toString()   ) 	;
			   dataout.flush();	
			   dataout.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }*/
    

	
	public short getMsgLength(){
		//return (short)(this.cookie.length()+this.json.toString().length());
		//2015-05-25 json含有中文会导致长度计算错误
		return (short) (getWordCount(json.toString())+ getWordCount(cookie) );
	}
    
	public static Header getOneHeaer(short commandID){
		String headTag="#XRPC#";			
		byte mainVersion=1;
		byte subVersion=1;
		int sequeeceNo=(int) (System.currentTimeMillis()/1000);
		byte encType=0; 
		short cookieLen=0;
		int reserve=0;
		short msgLen=cookieLen;
		Header head=new Header(headTag, mainVersion, subVersion, msgLen, commandID, sequeeceNo, encType, cookieLen, reserve);
		return head;
	}
    
   
    public static Message getOneMsg() {    	
    	String headTag="#XRPC#";			
    	byte mainVersion=1;
    	byte subVersion=2;
    	short msgLen=15;
    	short commandID=0x1601;
    	int sequeeceNo=(int) (System.currentTimeMillis()/1000);
    	byte encType=0;
    	
    	int reserve=0;
    	int timeStamp=(int) (System.currentTimeMillis()/1000);
    	short cookieLen=(short) String.valueOf(timeStamp).length();
    	
    	JSONObject json=new JSONObject();
    	try {
			json.put("ctrolID", 1234567);
	    	json.put("sender", 1);
	    	json.put("roomID", 103);
	    	json.put("errorCode", -12);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	Header head= new Header(headTag, mainVersion, subVersion, msgLen, commandID, sequeeceNo, encType, cookieLen, reserve);

    	Message msg= new Message(head,String.valueOf(timeStamp),json);
    	return msg;
	}
    
    public static Message getEmptyMsg() {    	
    	String headTag="#XRPC#";			
    	byte mainVersion=1;
    	byte subVersion=1;
    	short msgLen=15;
    	short commandID=LogicControl.DO_NOTHING;
    	int sequeeceNo=(int) (System.currentTimeMillis()/1000);
    	byte encType=0;
    	
    	int reserve=0;
    	int timeStamp=(int) (System.currentTimeMillis()/1000);
    	short cookieLen=(short) String.valueOf(timeStamp).length();
    	
    	JSONObject json=new JSONObject();
    	try {
	    	json.put("errorCode", 0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	Header head= new Header(headTag, mainVersion, subVersion, msgLen, commandID, sequeeceNo, encType, cookieLen, reserve);
    	Message msg= new Message(head,String.valueOf(timeStamp),json);
    	return msg;
	}
    
    public static int getWordCount(String s)  
    {  
        int length = 0;  
        int a=s.length();
        for(int i = 0; i < s.length(); i++)  
        {  
            int ascii = Character.codePointAt(s, i);  
            if(ascii >= 0 && ascii <=255)  
                length++;  
            else  
                length += 3;  
                  
        }  
        return length;            
    }  
    
    @Override  
    public Object clone(){ 
    	return this.clone();
		
    }
    
	    
	public static void main(String[] args)   {
//		JSONObject jo;
//		try {
//			jo = new JSONObject("{\"key\":\"value\"}");
//			System.out.println(jo.toString());
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
		
		Message msg= Message.getOneMsg();
//        byte[] x=msg.toBytesSmallEnd();
//        byte[] y=BytesUtil.subByte(x, 23+msg.cookieLen, msg.msgLen-msg.cookieLen);
//        
//		System.out.println("jspn="+new String(y));
//		Message msg2=new Message(msg.toBytesSmallEnd());
//		System.out.println(msg2.toString());
		
		/*String str="{\"profileName\":\"未知情景1\",\"factorList\":{\"factorID\":20,\"minValue\":25,\"modifyTime\":\"2014-12-13 14:15:16\","
				+ "\"validFlag\":1,\"createTime\":\"2014-12-13 14:15:16\",\"maxValue\":28,\"roomID\":203,\"roomType\":2,\"operator\":0},\"profileTemplateID\":0,"
				+ "\"modifyTime\":\"2014-12-13 14:15:17\",\"createTime\":\"2014-12-13 14:15:16\",\"profileID\":123456789,\"profileSetID\":12345,\"ctrolID\":12345677,\"roomID\":203,\"roomType\":2}";
		System.out.println(getWordCount(str));*/

		
		
	}
}
