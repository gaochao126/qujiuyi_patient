package com.jiuyi.qujiuyi.dao.prescription;

import java.util.List;

import com.jiuyi.qujiuyi.dto.perscription.PrescriptionDto;

/**
 * @author superb @Date 2015年12月15日
 * 
 * @Description
 *
 * @Copyright 2015 重庆柒玖壹健康管理有限公司
 */
public interface PrescriptionDao {
	/**
	 * 
	 * @number 1 @description 更改处方信息
	 * 
	 * @param prescriptionDto
	 * @throws Exception
	 *
	 * @Date 2015年12月15日
	 */
	public void updatePrescription(PrescriptionDto prescriptionDto) throws Exception;

	/**
	 * 
	 * @number 2 @description 根据处方ID查询处方
	 * 
	 * @param prescriptionDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月16日
	 */
	public PrescriptionDto queryPrescriptionById(PrescriptionDto prescriptionDto) throws Exception;

	/**
	 * 
	 * @number 3 @description 查询用户处方列表
	 * 
	 * @param prescriptionDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月17日
	 */
	public List<PrescriptionDto> queryPrescriptionListByPaitnetId(PrescriptionDto prescriptionDto) throws Exception;

	/**
	 * 
	 * @number 4 @description 删除处方
	 * 
	 * @param prescriptionDto
	 * @throws Exception
	 *
	 * @Date 2015年12月18日
	 */
	public void deletePrescription(PrescriptionDto prescriptionDto) throws Exception;
}
