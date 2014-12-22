package socket;

import java.io.UnsupportedEncodingException;
import org.json.JSONException;
import org.json.JSONObject;

import util.BytesUtil;

public class Message {
	

	 Header header;
	 int cookie;
	 JSONObject json;

	Message(){}
	Message(Message msg){
		this.header=msg.header;
		this.cookie=msg.cookie;
		this.json=msg.json;
	}
	
	Message(Header header){
		this.header=header;
	}
	
	Message(Header header,int cookie,  byte[] command) throws UnsupportedEncodingException, JSONException{
		this.header=header;	
		this.cookie=cookie;
		String jsonStr=new String(command,"utf-8");
		this.json=new JSONObject(jsonStr);
	}
	
	Message(Header header,int cookie, String command) throws UnsupportedEncodingException, JSONException{
		this.header=header;	
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
		 this.cookie=BytesUtil.bytesToInt(cookieByte);

		 
		String jsonStr=new String();
		
		this.json=new JSONObject(jsonStr.substring(16, -1));
	}
    
    public boolean isValid() {   
		return this.header.isValid();
	}
    
   public String MessageToString(){
	   String out=new String();
	   out+=  		this.header.headTag			;
	   out+=		this.header.mainVersion	;
	   out+=		this.header.subVersion	;
	   out+=		this.header.msgLen		;
	   out+=		this.header.commandID	;
	   out+=		this.header.sequeeceNo	;
	   out+=		this.header.encType		;
	   out+=		this.header.cookieLen	;
	   out+=		this.header.reserve		;    	
	   return out;
    }
	    
	

		

}
