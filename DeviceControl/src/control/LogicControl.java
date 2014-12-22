package control;

import org.json.JSONException;
import org.json.JSONObject;

import socket.ServerThread;

public class LogicControl {
	
	private static final short  CMDID_MAIN_CONTROL = 5000;

	private static final short SYN_ROOM_PROFILE			=	5101;
	private static final short SWITCH_ROOM_PROFILE		=	5102;
	
	private static final short SYN_COM_RROFILE			=	5201;
	private static final short SWITCH_COM_RROFILE		=	5202;	
	
	private static final short SYN_APP_LIST				=	5301;
	private static final short SWITCH_APP_S_TATE		=	5302;
	
	private static final short WARNING_MSG			=	5401;
	private static final short EMERGENCY			=	5402;
	
	private static final int headerLength=16;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void decodeCommand(String inCommand){
		
		//while(!ServerThread.receiveCommandQueue.isEmpty()){		
		//}
		
		int subCommand=getSubCommand(inCommand);
		int len=getCommandLength(inCommand);
		JSONObject jsonComm=getJason(inCommand, len);
		
		switch (subCommand)
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
	
	public String getCommandHeader(String inComm){		
		return inComm.substring(0, headerLength);		
	}
	
	
	public String getCommand(String inComm){		
		return null;		
	}
	
	public int getSubCommand(String inComm){		
		return 0;		
	}
	
	public int getCommandLength(String inComm){		
		return 0;		
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
	


	
	
}
