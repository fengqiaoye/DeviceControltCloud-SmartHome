package control;

import org.json.JSONException;
import org.json.JSONObject;

import socket.Message;
import socket.ServerThread;

public class LogicControl {	

	private static final short SYN_ROOM_PROFILE			=	0x1601;
	private static final short SWITCH_ROOM_PROFILE		=	0x1602;
	
	private static final short SYN_COM_RROFILE			=	0x1701;
	private static final short SWITCH_COM_RROFILE		=	0x1702;	
	
	private static final short SYN_APP_LIST				=	0x1801;
	private static final short SWITCH_APP_S_TATE		=	0x1802;
	
	private static final short WARNING_MSG			=	0x1901;
	private static final short EMERGENCY			=	0x1902;



	
	public void decodeCommand(Message msg){		
		int commandID=msg.header.commandID;
		//int len=msg.header.msgLen-msg.header.cookieLen;		
		//JSONObject jsonComm=getJason(inCommand, len);
		
		switch (commandID)
		{
		case SYN_ROOM_PROFILE:	
			String plate=new String("utf-8");
		case SWITCH_ROOM_PROFILE:	
			String plate2=new String("utf-8");
		case SYN_COM_RROFILE:	
			String plate3=new String("utf-8");
		case SWITCH_COM_RROFILE:	
			String plate4=new String("utf-8");
		case SYN_APP_LIST:	
			String plate5=new String("utf-8");
		case SWITCH_APP_S_TATE:	
			String plate6=new String("utf-8");
		case WARNING_MSG:	
			String plate7=new String("utf-8");
		case EMERGENCY:	
			String plate8=new String("utf-8");
			
		}		
		
	}
	

	
	public JSONObject getJason(String inComm,int len){			
		if(inComm==null) return null;
		String msgBody=inComm.substring(len);
		JSONObject jo;
		try {
			jo = new JSONObject(msgBody);
			return jo;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}
	


	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}	
	
}
