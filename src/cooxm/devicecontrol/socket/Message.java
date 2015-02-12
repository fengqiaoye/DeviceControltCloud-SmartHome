package cooxm.devicecontrol.socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.CookieList;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.log.Log;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.util.BytesUtil;

public class Message extends Header {
	public static final short COMMAND_ACK_OFFSET       		   =  0x4000;
	static Logger log= Logger.getLogger(LogicControl.class);
	 //public Header header;
	 private String cookie=null;
	 private JSONObject json;
	 int serverID=-1;


	 
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
		this.msgLen=(short) ((short) cookie.length()+this.json.toString().length());
	}
	public JSONObject getJson() {
		return json;
	}
	public void setJson(JSONObject json) {
		this.json = json;
		this.msgLen=(short) ((short) cookie.length()+json.toString().length());
	}
	public Message(){}
	
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
	}
	
	Message(Header header,String cookie,  byte[] command) {
		super(header);	
		this.cookie=cookie;
		String jsonStr;
		try {
			jsonStr = new String(command,"utf-8");
			this.json=new JSONObject(jsonStr);
			this.msgLen=(short) (cookie.length()+jsonStr.length());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Message(Header header,String cookie, String command) {
		super(header);	
		this.msgLen=(short) (cookie.length()+command.length());
		this.cookie=cookie;
		//String jsonStr=new String(command,"utf-8");
		try {
			this.json=new JSONObject(command);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	super.cookieLen=(short) cookie.length();
    	super.msgLen=(short) ((short) cookie.length()+command.length());
	}
	
    
	/***23 bytes of header
	 *  of cookie
	 * @throws UnsupportedEncodingException 
	 *  */
    Message(byte[] msg) {    
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
		
		 byte[] cookieByte=new byte[this.cookieLen];
		 for(int i=0;i<this.cookieLen;i++){
			 cookieByte[i]	    = msg[23+i] ;
		 }
		 this.cookie=new String(cookieByte);
		 
		String jsonStr=new String(msg);		
		try {
			this.json=new JSONObject(jsonStr.substring(23+this.cookieLen, -1));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
    
    public Message(Header head, String cookie, JSONObject json2) {
    	super(head);
    	this.cookie=cookie;
    	this.json=json2;
    	super.cookieLen=(short) cookie.length();
    	super.msgLen=(short) ((short) cookie.length()+json2.toString().length());
	}
	/*public boolean isValid() {   
		return this.isValid();
	}*/
    
   public String msgToString(){
	   String out=new String();
	   DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   out+=  		this.headTag		+",";
	   out+=		this.mainVersion	+",";
	   out+=		this.subVersion	+",";
	   out+=		this.msgLen		+",";
	   out+=		this.commandID	+",";
	   out+=		this.sequeeceNo	+",";
	   out+=		this.encType		+",";
	   out+=		this.cookieLen	+",";
	   out+=		this.reserve		+",";  
	   out+=		this.cookie		+","; 
	   out+=		this.json.toString()	+","; 

	   return out;
    }
   
   /*public String MessageToBytes(){
	   byte[] out =new byte[this.msgLen+23];
	   out=  	BytesUtil.getBytes(this.headTag, "UTF-8");
	   out+=	BytesUtil.getBytes(	this.mainVersion)	;
	   out+=	BytesUtil.getBytes(	this.subVersion)   ;
	   out+=	BytesUtil.getBytes(	this.msgLen)		;
	   out+=	BytesUtil.getBytes(	this.commandID)	;
	   out+=	BytesUtil.getBytes(	this.sequeeceNo)	;
	   out+=	BytesUtil.getBytes(	this.encType)		;
	   out+=	BytesUtil.getBytes(	this.cookieLen)	;
	   out+=	BytesUtil.getBytes(	this.reserve	)	;  
	   out+=	BytesUtil.getBytes(	this.cookie)		; 
	   out+=	BytesUtil.getBytes(	this.json.toString(),"UTF-8")	; 
	   return out;
    }*/
   
   /** <pre> 根据cookie获取发送命令的serverID
    * 如果cookie为空则返回 -1 */
   /*public int getServerID() {
	   int serverID=-1;
	   if(this.cookie==null){
         return -1;
	   }else{
		   serverID=Integer.parseInt(this.cookie.split("_")[1]);
	   }	   
	return serverID;	
   }*/
   
   public int getSequenceNO() {
	   int SequenceNO=-1;
	   if(this.cookie==null){
         return -1;
	   }else{
		   SequenceNO=Integer.parseInt(this.cookie.split("_")[0]);
	   }	   
	return SequenceNO;	
   }
   
    public void writeBytesToSock(Socket sock){
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
			
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }
    
 /*   public void writeToSock(Socket sock){
    	try {
			DataOutputStream dataout= new DataOutputStream(sock.getOutputStream());
			   dataout.writeUTF(  	this.headTag            )  ;
			   dataout.writeByte(		this.mainVersion)	  ;
			   dataout.writeByte(		this.subVersion)       ;
			   dataout.writeShort(		this.msgLen)		      ;
			   dataout.writeShort(		this.commandID)	      ;
			   dataout.writeInt(		this.sequeeceNo)	      ;
			   dataout.writeByte(		this.encType)		  ;
			   dataout.writeByte(		this.cookieLen)	      ;	   
			   dataout.writeInt(		this.reserve	)	       ;  
			   dataout.writeUTF(		this.cookie)		             ; 
			   dataout.writeUTF(		this.json.toString()   ) 	;
			   dataout.flush();		
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }*/
    

	
	public short getMsgLength(){
		return (short)(this.cookie.length()+this.json.toString().length());
	}
    
	public static Header getOneHeaer(short commandID){
		String headTag="#XRPC#";			
		byte mainVersion=1;
		byte subVersion=1;
		int sequeeceNo=(int) (System.currentTimeMillis()/1000);
		byte encType=1; 
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
    	byte encType=1; 
    	short cookieLen=4;
    	int reserve=0;
    	
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
    	long timeStamp=System.currentTimeMillis()/1000;
    	Message msg= new Message(head,String.valueOf(timeStamp),json);

    	
    	return msg;
	}
	    
	public static void main(String[] args)  {
//		JSONObject jo;
//		try {
//			jo = new JSONObject("{\"key\":\"value\"}");
//			System.out.println(jo.toString());
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
		
		Message msg= Message.getOneMsg();
		try {
			msg.getJson().put("key", "value");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(msg.getJson().toString());

	}
}
