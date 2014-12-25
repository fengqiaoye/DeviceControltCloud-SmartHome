package control;

import org.json.JSONException;
import org.json.JSONObject;

import device.Profile;
import socket.Message;
import socket.ServerThread;

public class LogicControl {	
    /*** 请求 情景模式命令
     *<pre>json格式为：
     * { 
     *   comand:GET_ROOM_PROFILE 
     *   CtrolID:1234567
     *   profileID:7654321
     * }
     *   */
	private static final short GET_ROOM_PROFILE			=	0x1601;
	
	
    /*** 设置 情景模式
     *<PRE>Json格式为所要上传或者保存的情景模式的 json格式：
     *  {
			"profileID":123456789,
			"CtrolID":12345677,
			"profileName":"未知情景",
			"profileSetID":12345,
			"profileTemplateID":0,
			"roomID":203,
			"roomType":2
			"factorList":
			[
				{"factorID":40,"minValue":20,"compareWay":0,"modifyTime":"Fri Dec 12 12:30:00 CST 2014","validFlag":false,
				"createTime":"Fri Dec 12 12:30:00 CST 2014","maxValue":30
				},
	
				{"factorID":60,"minValue":1,"compareWay":0,
				"modifyTime":"Fri Dec 12 12:30:00 CST 2014","validFlag":false,"createTime":"Fri Dec 12 12:30:00 CST 2014","maxValue":1
				}
			],
			"modifyTime":"Sat Dec 13 14:15:17 CST 2014",
			"createTime":"Sat Dec 13 14:15:16 CST 2014",
		}
     */
	private static final short SET_ROOM_PROFILE			=	0x1602;
	
	/*** 切换情景模式命令 
      <pre>json格式和请求为：
     * { 
     *   comand:SWITCH_ROOM_PROFILE 
     *   CtrolID:1234567
     *   profileID:7654321
     * }
	 * */
	private static final short SWITCH_ROOM_PROFILE		=	0x1603;
	
	/*** 请求 情景模式集 
     *json格式和请求为： <pre>
     * { 
     *   GET_RROFILE_SET 
     *   CtrolID:1234567
     *   profileSetID:7654321
     * }
	 * */	
	private static final short GET_RROFILE_SET			=	0x1701;
	
	/*** 设置 情景模式集
	 * <pre>Json格式和 设置情景模式 和 {@link control.LogicControl#SET_ROOM_RROFILE SET_ROOM_RROFILE} 类似：
     * {  
     *   [  
     *   情景模式集 的json格式 ：即多个情景模式组成的json数组    
     *   ]  
     * }
	 * */
	private static final short SET_RROFILE_SET			=	0x1702;
	
	/*** 情景模式集切换 
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     comand:SWITCH_RROFILE_SET 
	 *     CtrolID:1234567
	 *     profileSetID:7654321
     *   }*/
	private static final short SWITCH_RROFILE_SET		=	0x1703;	
	
	/*** 请求家电列表
	 * 	 <pre>对应json消息体为：
	 *   {
	 *     comand:GET_APP_LIST 
	 *     CtrolID:1234567
     *   }*/
	private static final short GET_APP_LIST				=	0x1801;
	
	
	/*** 设置 家电列表
	 * 	 <pre>对应的：
	 *   {
	 *     将每一个家电转一个json对象，将整个房屋多个家电组成一个json数组
     *   }*/
	private static final short SET_APP_LIST				=	0x1802;
	
	
	/*** 切换某个家电状态
	 * 	 <pre>例如对应json消息体如下格式 ：
	 *   {
	 *     comand:SWITCH_APP_STATE
	 *     CtrolID:1234567
	 *     deviceID:7654321
     *   }*/
	private static final short SWITCH_APP_STATE		    =	0x1803;
	
  /*** 告警消息
  <pre>例如对应json消息体如下格式 ：
  {
    "warnID",  123456      
    "warnName","厨房漏气"      
    "warnContent", "检测你的厨房可燃气体超标，已自动帮你打开厨房的窗户~"  
    "type",  1  	
    "channel",0      
    "createTime","2014-12-25 12:13:14"    
    "modifyTime","2014-12-25 12:13:14"  
  }
  */
	private static final short WARNING_MSG				=	0x1901;
	private static final short EMERGENCY				=	0x1902;
	

	
	public void decodeCommand(Message msg){		
		int commandID=msg.header.commandID;
		//JSONObject json=msg.json;
		
		switch (commandID)
		{
		case GET_ROOM_PROFILE:			
		
		case SET_ROOM_PROFILE:	
			
		case SWITCH_ROOM_PROFILE:	
			String plate2=new String("utf-8");
		case GET_RROFILE_SET:	
			String plate3=new String("utf-8");
		case SET_RROFILE_SET:	
			String plate12=new String("utf-8");			
		case SWITCH_RROFILE_SET:	
			String plate4=new String("utf-8");
		case GET_APP_LIST:	
			String plate5=new String("utf-8");
		case SET_APP_LIST:	
			String plate11=new String("utf-8");			
		case SWITCH_APP_STATE:	
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
		
		System.out.println(GET_APP_LIST+"!");
		
		if(GET_APP_LIST==6145){
			System.out.println("ture");
		}

	}	
	
}
