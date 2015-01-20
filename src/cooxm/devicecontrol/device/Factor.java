package cooxm.devicecontrol.device;

import java.util.*;

/*** 情景模式所包含的情景因素*/
public class Factor {
 
	/***<pre>0-10：保留
	10：灯
	20：电视
	40: 空调

	41: 空调开关
	42：空调温度
	43：空调风速

	60：窗户
	80：窗帘
	90：暖器

	201：光
	301：PM2.5 
	401：有害气体
	501：湿度
	601：温度
	701：天气（预报）
	901：声音*/
	protected int factorID;
	//int factorType;
	//String factorName;
	int minValue;
	int maxValue;
	int compareWay;
	int validFlag;
	protected Date createTime;
	protected Date modifyTime;
	
	public Factor() {
		// TODO Auto-generated constructor stub
	}

	
	Factor(	int factorID,	
			//int factorType,	
			//String factorName,
			int minValue,
			int maxValue,
			int compareWay,
			int validFlag,
			Date createTime,
			Date modifyTime )
	{
		this.factorID=factorID;
		//this.factorType=factorType;
		//this.factorName=factorName;
		this.minValue=minValue;
		this.maxValue=maxValue;
		this.compareWay=compareWay;
		this.validFlag=validFlag;
		this.createTime=createTime;
		this.modifyTime=modifyTime;		
	}
	
	/*** 不含创建时间,不含因素名称的初始化方法*/
	Factor(	int factorID,	
			int factorType,	
			int minValue,
			int maxValue,
			int compareWay,
			int validFlag
			)
	{
		this.factorID=factorID;
		//this.factorType=factorType;
		this.minValue=minValue;
		this.maxValue=maxValue;
		this.compareWay=compareWay;
		this.validFlag=validFlag;	
		
		
	}
	

}

