package socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import util.BytesUtil;

public class Message  {
	

	 public Header header;
	 public String cookie;
	 public JSONObject json;
	 public Date receiveTime;
	 public Date replyTime;

	public Message(){}
	public Message(Message msg){	
		//Message copyMsg=new  Message();
		this.header=msg.header; 
		this.cookie=msg.cookie;
		this.json=msg.json;
	}
	
	Message(Header header){
		this.header=header;
	}
	
	Message(Header header,String cookie,  byte[] command) throws UnsupportedEncodingException, JSONException{
		this.header=header;	
		this.cookie=cookie;
		String jsonStr=new String(command,"utf-8");
		this.json=new JSONObject(jsonStr);
		this.header.msgLen=(short) (cookie.length()+jsonStr.length());
	}
	
	Message(Header header,String cookie, String command) throws UnsupportedEncodingException, JSONException{
		this.header=header;	
		this.header.msgLen=(short) (cookie.length()+command.length());
		this.cookie=cookie;
		//String jsonStr=new String(command,"utf-8");
		this.json=new JSONObject(command);
	}
    
	/***23 bytes of header
	 *  4 bytes of cookie
	 * @throws UnsupportedEncodingException 
	 *  */
    Message(byte[] msg) throws JSONException, UnsupportedEncodingException{    
	     byte[] headTag     ={msg[0],msg[1],msg[2],msg[3],msg[4],msg[5]};	
		 byte mainVersion	=msg[6];
		 byte subVersion	=msg[7];
		 byte[] msgLen		= {msg[8],msg[9]};	
		 byte[] commandID	= {msg[10],msg[11]};
		 byte[] sequeeceNo	={msg[12],msg[13],msg[14],msg[15]} ;
		 byte   encType     =msg[16];
		 byte[] cookieLen	= {msg[17],msg[18]};
		 byte[] reserve	    ={msg[19],msg[20],msg[21],msg[22]} ;

		 
		this.header.headTag=new String(headTag,"UTF-8");;			
		this.header.mainVersion=mainVersion;
		this.header.subVersion=subVersion;
		this.header.msgLen=BytesUtil.bytesToShort(msgLen);
		this.header.commandID=BytesUtil.bytesToShort(commandID);
		this.header.sequeeceNo=BytesUtil.bytesToInt(sequeeceNo);
		this.header.encType=encType; 
		this.header.cookieLen=BytesUtil.bytesToShort(cookieLen);
		this.header.reserve=BytesUtil.bytesToInt(reserve);
		
		 byte[] cookieByte=new byte[this.header.cookieLen];
		 for(int i=0;i<this.header.cookieLen;i++){
			 cookieByte[i]	    = msg[23+i] ;
		 }
		 this.cookie=new String(cookieByte);

		 
		String jsonStr=new String();
		
		this.json=new JSONObject(jsonStr.substring(16, -1));
	}
    
    public boolean isValid() {   
		return this.header.isValid();
	}
    
   public String msgToString(){
	   String out=new String();
	   out+=  		this.header.headTag		+",";
	   out+=		this.header.mainVersion	+",";
	   out+=		this.header.subVersion	+",";
	   out+=		this.header.msgLen		+",";
	   out+=		this.header.commandID	+",";
	   out+=		this.header.sequeeceNo	+",";
	   out+=		this.header.encType		+",";
	   out+=		this.header.cookieLen	+",";
	   out+=		this.header.reserve		+",";  
	   out+=		this.cookie		+","; 
	   out+=		this.json.toString()	+","; 
	   return out;
    }
   
   /*public String MessageToBytes(){
	   byte[] out =new byte[this.header.msgLen+23];
	   out=  	BytesUtil.getBytes(this.header.headTag, "UTF-8");
	   out+=	BytesUtil.getBytes(	this.header.mainVersion)	;
	   out+=	BytesUtil.getBytes(	this.header.subVersion)   ;
	   out+=	BytesUtil.getBytes(	this.header.msgLen)		;
	   out+=	BytesUtil.getBytes(	this.header.commandID)	;
	   out+=	BytesUtil.getBytes(	this.header.sequeeceNo)	;
	   out+=	BytesUtil.getBytes(	this.header.encType)		;
	   out+=	BytesUtil.getBytes(	this.header.cookieLen)	;
	   out+=	BytesUtil.getBytes(	this.header.reserve	)	;  
	   out+=	BytesUtil.getBytes(	this.cookie)		; 
	   out+=	BytesUtil.getBytes(	this.json.toString(),"UTF-8")	; 
	   return out;
    }*/
   
   /** <pre> 根据cookie获取发送命令的serverID
    * 如果cookie为空则返回 -1 */
   public int getServerID() {
	   int serverID=-1;
	   if(this.cookie==null){
         return -1;
	   }else{
		   serverID=Integer.parseInt(this.cookie.split("_")[1]);
	   }	   
	return serverID;	
   }
   
    public void writeBytesToSock(Socket sock){
    	try {
			DataOutputStream dataout= new DataOutputStream(sock.getOutputStream());
			   dataout.write(  	BytesUtil.getBytes(this.header.headTag, "UTF-8")  );
			   dataout.write(	BytesUtil.getBytes(	this.header.mainVersion)	  );
			   dataout.write(	BytesUtil.getBytes(	this.header.subVersion)       );
			   dataout.write(	BytesUtil.getBytes(	this.header.msgLen)		      );
			   dataout.write(	BytesUtil.getBytes(	this.header.commandID)	      );
			   dataout.write(	BytesUtil.getBytes(	this.header.sequeeceNo)	      );
			   dataout.write(	BytesUtil.getBytes(	this.header.encType)		  );
			   dataout.write(	BytesUtil.getBytes(	this.header.cookieLen)	      );
			   dataout.write(	BytesUtil.getBytes(	this.header.reserve	)	      ) ;  
			   dataout.write(	BytesUtil.getBytes(	this.cookie,"UTF-8")		          )   ; 
			   dataout.write(	BytesUtil.getBytes(	this.json.toString(),"UTF-8") )	; 
			   dataout.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }
    
    public void writeToSock(Socket sock){
    	try {
			DataOutputStream dataout= new DataOutputStream(sock.getOutputStream());
			   dataout.writeUTF(  	this.header.headTag            )  ;
			   dataout.writeByte(		this.header.mainVersion)	  ;
			   dataout.writeByte(		this.header.subVersion)       ;
			   dataout.writeShort(		this.header.msgLen)		      ;
			   dataout.writeShort(		this.header.commandID)	      ;
			   dataout.writeInt(		this.header.sequeeceNo)	      ;
			   dataout.writeByte(		this.header.encType)		  ;
			   dataout.writeByte(		this.header.cookieLen)	      ;	   
			   dataout.writeInt(		this.header.reserve	)	       ;  
			   dataout.writeUTF(		this.cookie)		             ; 
			   dataout.writeUTF(		this.json.toString()   ) 	;
			   dataout.flush();		
		} catch (IOException e) {
			e.printStackTrace();
		}    	
    }
    
    public Message getOneMsg() {
    	Message msg= new Message();
    	
    	String headTag="#XRPC#";			
    	byte mainVersion=1;
    	byte subVersion=2;
    	short msgLen=15;
    	short commandID=0x1601;
    	int sequeeceNo=123456;
    	byte encType=1; 
    	short cookieLen=4;
    	int reserve=0;
    	
    	JSONObject json=new JSONObject();
    	try {
			json.put("CtrolID", 1234567);
	    	json.put("sender", 1);
	    	json.put("roomID", 103);
	    	json.put("errorCode", -12);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	Header head= new Header(headTag, mainVersion, subVersion, msgLen, commandID, sequeeceNo, encType, cookieLen, reserve);
    	msg.header=head;
    	msg.cookie="87654321";
    	msg.json=json;
    	msg.receiveTime=new Date();
    	msg.replyTime=new Date();
    	
    	return msg;
	}
	    
	public static void main(String[] args) throws JSONException {
		JSONObject jo= new JSONObject("{\"key\":\"value\"}");
		
		System.out.println(jo.toString());
	}
}
