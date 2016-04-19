package com.jiuyi.qujiuyi.service.register.impl;

public enum RegisterStatus {
	/** 带就诊0*/
	NOREGISTER(0),	
	
	/** 已就诊1*/
	REGISTED(1),
	
	/** 已取消2*/
	CANCLE(2),
	
	/** 已过时3*/
	OUTDATE(3),
	
	/** 挂号失败4*/
	REGISTERFILE(4),
	
	/** 停诊取消5*/
	STOPREGISTER(5),
	
	/** 在线支付1*/
	ONLINEPAY(1),
	
	/** 到院支付2*/
	TOHOSPITAL(2),
	
	/** 不可以取号0*/
	NOFETCHNUMBER(0),
	
	/** 可以取号1*/
	YESFETCHNUMBER(1),
	
	/** 不可以取消挂号0*/
	NOCANCLE(0),
	
	/** 可以取消挂号1*/
	YESCANCLE(1),
	
	/** 未取号0*/
	NOFETCHSTATUS(0),
	
	/** 已取号*/
	YESFETCHSTATUS(1);
	
	/** */
	private Integer intValue;
	
	private String strValue;

	private RegisterStatus(int intValue){
		this.intValue = intValue;
	}
	
	private RegisterStatus(String strValue){
		this.strValue = strValue;
	}
	public Integer getIntValue() {
		return intValue;
	}

	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	
}
