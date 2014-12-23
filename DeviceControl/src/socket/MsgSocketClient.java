package socket;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午3:03:30 
 */

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class MsgSocketClient {
	
	private static final short POSINFO_PLATE_NUMBER			=	0x0001;
	private static final short POSINFO_LONGITUDE			=	0x0002;
	private static final short POSINFO_LATITUDE				=	0x0003;
	private static final short POSINFO_REPORT_TIME			=	0x0004;
	private static final short POSINFO_DEV_ID				=	0x0005;
	private static final short POSINFO_SPEED				=	0x0006;
	private static final short POSINFO_DIRECTION			=	0x0007;
	private static final short	POSINFO_LOCATION_STATUS		=	0x0008;

	private static final short ALARMINFO_SIM_NUMBER			=	0x0010;
	private static final short ALARMINFO_CAR_STATUS			=	0x0011;
	private static final short ALARMINFO_CAR_COLOUR			=	0x0012;
	
    static String GPSline=new String();
	public static Socket msgSk =null;
	PrintWriter pw;
	static OutputStream os = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		MsgSocketClient msc=new MsgSocketClient("127.0.0.1",64415);

	}

	
	MsgSocketClient(String serverIP,int serverpot)
	{
        OutputStreamWriter writer;  
			if(msgSk==null){
				try {
					msgSk=new Socket(serverIP,serverpot);
		            writer = new OutputStreamWriter(msgSk.getOutputStream()); 
		            pw = new PrintWriter(writer, true); 
			        pw.println("Connect from DeviceControl client 1 !\n");  
			        pw.println("Connect from DeviceControl client 2 !\n"); 
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			


	        try {
				String msg=CtrolSocketServer.readMsgFromScok(msgSk);
				System.out.println(msg); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	 
	}
	

	
	@SuppressWarnings("unused")
	public void getheader(Socket sock)
	{
		int ch=0;
		while(true){
			byte[] b3= new byte[3];

				try{
					sock.getInputStream().read(b3,0,3);
					ch=b3[0];
				}catch ( Exception e){
					System.out.println("connection reset, reconnecting ...");
					//sock.close();					
				}		

			int len=bytesToShort(b3, 1);
			if(len<0) break;
			byte[] bytelen= new byte[len];
			try {
				sock.getInputStream().read(bytelen);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(bytelen==null){
				System.out.println("read the second part from byte from socket failed ! ");
				break ;
			}
			
			try {
				decodeMsg(ch,bytelen);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   // System.out.println(count++ +":\n");
		}		
	}
	
	
	public static String decodeMsg( int len,byte[] msg) throws Exception
	{
		short offset=0;
		short unit_id = -1;
		short unit_len = 0;
		byte[] unit_value = null;

		String plate=null;
		short tempshort1=0,tempshort2=0,tempshort3=0,tempshort4=0,tempshort5=0,tempshort6=0;

		char tempchar=0;

		while (offset<=len-2)
		{
			unit_id =bytesToShort(msg,offset);

			offset = (short) (offset +2);

			unit_len = (short) bytesToShort(msg, offset);
			//System.out.println("	Unit len=: "+unit_len);
			
			if(unit_len+offset<=len){
				offset = (short) (offset + 2);

				unit_value=new byte[unit_len];
				for(int i=0;i<unit_len;i++) {

					unit_value[i]=msg[offset+i];
				}
				offset = (short) (offset + unit_len);
			}else {
				break;
			}
			
			DecimalFormat df2=(DecimalFormat) DecimalFormat.getInstance(); 			

			switch (unit_id)
			{
			case POSINFO_PLATE_NUMBER:				
				plate=new String(unit_value,"gbk");
				//System.out.println("	Plate number:"+plate+"\t");
				GPSline=plate+",";
				plate=null;
				break;
			case POSINFO_LONGITUDE:
				long lon=0;
				if(unit_len==2) {
				} else if(unit_len==4)
					lon = bytesToInt(unit_value);

				double dLon=lon/1000000.0;
				df2.applyPattern("0.000000"); 
				GPSline=GPSline+df2.format(dLon)+",";
				break;
			case POSINFO_LATITUDE:
				long lan=0;
				if(unit_len==2)
					lan = bytesToShort(unit_value);
				else if(unit_len==4)
					lan = bytesToInt(unit_value);
				else if(unit_len==8)
					lan = bytesToLong(unit_value);

				double dLan=lan/1000000.0;
				df2.applyPattern("0.000000");
				GPSline=GPSline+df2.format(dLan)+",";
				break;
			case POSINFO_REPORT_TIME:				

				df2.applyPattern("00"); 
				//System.out.println(df2.format(1.2));
				
				tempshort1 =(short) bytesToShort(unit_value);

				tempshort2 = (short)unit_value[2] ;

				tempshort3 = (short) unit_value[3] ;

				tempshort4 =  (short) unit_value[4] ;

				tempshort5 =  (short) unit_value[5] ;

				tempshort6 = (short) unit_value[6] ;
				//System.out.println("	Date: "+tempshort1+"-"+tempshort2+"-"+tempshort3+"-"+tempshort4+"-"+tempshort5+"-"+tempshort6+"\t");
				GPSline=GPSline+tempshort1+"-"+df2.format(tempshort2)+"-"+df2.format(tempshort3)+
						" "+df2.format(tempshort4)+":"+df2.format(tempshort5)+":"+df2.format(tempshort6)+",";
				break;
			case POSINFO_DEV_ID:
				long sim =0;
				if(unit_len==2)
					sim = bytesToShort(unit_value);
				else if(unit_len==4)
					sim = bytesToInt(unit_value);
				else if(unit_len==8)
					sim= bytesToLong(unit_value);

				//System.out.println("	Device ID:"+tempint+"\t");
				GPSline=GPSline+sim+",";
				break;
			case POSINFO_SPEED:	

				tempshort1 = (short) bytesToShort(unit_value);
				//System.out.println("	Speed:"+tempshort1+"\t");
				GPSline=GPSline+df2.format(tempshort1) +",";
				break;
			case POSINFO_DIRECTION:	
				if(unit_len>=2){
				tempshort1 = (short) bytesToShort(unit_value);
				//System.out.println("	Bearing:"+tempshort1+"\t");
				//df2.applyPattern("000"); 
				GPSline=GPSline+(short)(tempshort1/100) +",";
				}
				break;
			case POSINFO_LOCATION_STATUS:
				tempchar = (char) unit_value[0];
				tempshort1 = (short) tempchar;
				//System.out.println("	positioning status:"+tempshort1+"\t");
				//GPSline=GPSline+tempshort1 +",";
				break;
			case ALARMINFO_SIM_NUMBER:
				plate=new String(unit_value,"GBK");
				//System.out.println("	SIM NO.:"+plate+"\t");
				//GPSline=GPSline+plate+",";
				plate=null;
				break;
			case ALARMINFO_CAR_STATUS:
				tempchar = (char) unit_value[0];
				tempshort1 = (short) tempchar;
				//System.out.println("	With passenger:"+tempshort1+"\t");
				GPSline=GPSline+tempshort1 +",";
				break;
			case ALARMINFO_CAR_COLOUR:
				plate=new String(unit_value,"GBK");
				//System.out.println("	Car Color:"+plate+"\n");
				GPSline=GPSline+plate +"\n";
				plate=null;		
				
				new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				new SimpleDateFormat("yyyy-MM-dd");
				new SimpleDateFormat("yyyy-MM-dd-HH-");
				df2.applyPattern("00"); 
				
				Date nowDate=new Date();
				System.getProperty("user.dir");				 
				 int min=nowDate.getMinutes();
				nowDate.getSeconds();
				if(min<30 ){min=00;	}
				else if(min>=30){min=30;}
				return GPSline;
				
			default:
				System.out.println("\n	### 	Error: can't resort message info!   #### unit_id="+unit_id+"\n");
				Thread.sleep(100);	
				
				break;
			}
		}
		return null;
	}

	
	public static short bytesToShort(byte[] b, int offset) {  
		return (short)    (b[offset + 1] & 0xff <<8 | (b[offset] & 0xff) << 0)   ; 
	}  
	
	public static short bytesToShort(byte[] b) {  
		return (short)( (b[1] & 0xff)<<8 | (b[0] & 0xff) );// << 8);  
	} 
	
	public static long bytesToLong(byte[] array) {  
		return ((((long) array[0] & 0xff) << 0) | (((long) array[1] & 0xff) << 8) | (((long) array[2] & 0xff) << 16)  
				| (((long) array[3] & 0xff) << 24) | (((long) array[4] & 0xff) << 32)  
				| (((long) array[5] & 0xff) << 40) | (((long) array[6] & 0xff) << 48) | (((long) array[7] & 0xff) <<56));  
	}  



	public static int bytesToInt(byte b[]) {  
		return (b[3] & 0xff )<<24 | (b[2] & 0xff )<< 16 | (b[1] & 0xff) << 8 | (b[0] & 0xff) << 0;  
	}	

}
