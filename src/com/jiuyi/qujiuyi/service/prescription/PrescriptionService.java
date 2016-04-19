package com.jiuyi.qujiuyi.service.prescription;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.perscription.PrescriptionDto;

/**
 * @author superb @Date 2015年12月15日
 * 
 * @Description
 *
 * @Copyright 2015 重庆柒玖壹健康管理有限公司
 */
public interface PrescriptionService {
	/**
	 * 
	 * @number 1 @description 接收处方申请申请
	 * 
	 * @param prescriptionDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月15日
	 */
	public ResponseDto recivePrescription(PrescriptionDto prescriptionDto) throws Exception;

	/**
	 * 
	 * @number 2 @description 处方列表
	 * 
	 * @param prescriptionDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月17日
	 */
	public ResponseDto queryPrescriptionList(PrescriptionDto prescriptionDto) throws Exception;

	/**
	 * 
	 * @number 3 @description 处方详情
	 * 
	 * @param prescriptionDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月17日
	 */
	public ResponseDto queryPrescriptionDetail(PrescriptionDto prescriptionDto) throws Exception;

	/**
	 * 
	 * @number 4 @description 删除处方
	 * 
	 * @param prescriptionDto
	 * @throws Exception
	 *
	 * @Date 2015年12月18日
	 */
	public ResponseDto detelePrescription(PrescriptionDto prescriptionDto) throws Exception;

	/**
	 * 
	 * @number @description 确认处方已配药
	 * 
	 * @param prescriptionDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年2月1日
	 */
	public ResponseDto comfirmPrescription(PrescriptionDto prescriptionDto) throws Exception;

	/**
	 * 
	 * @param prescriptionDto
	 *            再次申请处方
	 * @return
	 * @throws Exception
	 */
	public ResponseDto onceAgain(PrescriptionDto prescriptionDto) throws Exception;
}
