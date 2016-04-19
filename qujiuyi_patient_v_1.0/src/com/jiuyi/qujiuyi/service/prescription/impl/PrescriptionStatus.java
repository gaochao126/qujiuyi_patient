/**
 * 
 */
package com.jiuyi.qujiuyi.service.prescription.impl;

/**
 * 
 * @author xutaoyang
 *
 */
public enum PrescriptionStatus {

	/** 医生发起/等待患者回应0 */
	CREATED,

	/** 等待医生修改/审核未通过 1 */
	NEED_EDIT(true),

	/** 医生完成/患者取消 2 */
	PATIENT_CANCEL,

	/** 医生开方完成/待审核3 */
	PRESCRIBED(true),

	/** 医生取消开方/患者完成 4 */
	CANCEL_PRESCRIBE,

	/** 医生完成/患者取消支付 5 */
	CANCEL_PAY,

	/** 医生完成/患者完成支付6 */
	PAYEDM,

	/** 过期 7 */
	EXPIRED,

	/** 审核成功8 */
	REVIEW_SUCCESS,

	/** 已经配药 9 */
	SENDED,

	/** 再次申请该处方. */
	AGAIN(12);

	/** 该状态下是否允许医生配药 */
	private boolean canPrescribe;

	private Integer intValue;

	private PrescriptionStatus() {
		this(false);
	}

	private PrescriptionStatus(Integer intValue) {
		this.intValue = intValue;
	}

	private PrescriptionStatus(boolean canPrescribe) {
		this.canPrescribe = canPrescribe;
	}

	/**
	 * 该状态是否允许医生配药
	 * 
	 * @param status
	 * @return
	 */
	public static boolean statusCanPrescribe(int status) {
		return values()[status] != null && values()[status].canPrescribe;
	}

	public Integer getIntValue() {
		return intValue;
	}

	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}

}
