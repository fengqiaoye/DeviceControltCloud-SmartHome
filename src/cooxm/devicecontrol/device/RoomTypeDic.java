package cooxm.devicecontrol.device;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Sep 14, 2015 5:14:56 PM 
 */

public final class  RoomTypeDic {
	
	public static final int LivingRoomID=1;
	public static final String LivingRoomName="客厅";
	
	public static final int BedRoomID=2;
	public static final String BedRoomName="卧室";
	
	public static final int KitchenRoomID=3;
	public static final String KitchenRoomName="厨房";
	
	public static final int BathRoomID=4;
	public static final String BathRoomName="卫生间";
	
	public static final int BalconyID=5;
	public static final String BalconyName="阳台";
	
	public static final int DinningRoomID=6;
	public static final String DinningRoomName="餐厅";
	
	public static final int ReadingRoomID=7;
	public static final String ReadingRoomName="书房";
	
	public static final int StoreRoomID=8;
	public static final String StoreRoomName="储物间";
	
	public static final int EntertainmentRoomID=9;
	public static final String EntertainmentName="娱乐室";
	
	public static final int FitRoomID=10;
	public static final String FitName="健身房";
	
	public static int getRoomType(int roomID){
		return roomID/1000;
	}
	
	
	public static String getRoomTypeName(int roomType){
		switch (roomType) {
		case 1:
			return LivingRoomName;
		case 2:
			return BedRoomName;
		case 3:
			return KitchenRoomName;
		case 4:
			return BathRoomName;
		case 5:
			return BalconyName;
		case 6:
			return DinningRoomName;
		case 7:
			return ReadingRoomName;
		case 8:
			return StoreRoomName;
		case 9:
			return EntertainmentName;
		case 10:
			return FitName;	

		default:
			return "未知房间";
		}
		
	}
	

	public static void main(String[] args) {

	}

}
