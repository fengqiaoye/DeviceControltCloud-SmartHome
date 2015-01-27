package cooxm.devicecontrol.unused;

import java.util.*;

/*** 情景模式所包含的情景因素*/
public class Factor3 {
 

	protected int factorID;
//	int factorType;
//	String factorName;
	int minValue;
	int maxValue;
	int compareWay;
	int validFlag;
	protected Date createTime;
	protected Date modifyTime;
	
	public Factor3() {
		// TODO Auto-generated constructor stub
	}

	
	Factor3(	int factorID,	
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
	Factor3(	int factorID,	
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

