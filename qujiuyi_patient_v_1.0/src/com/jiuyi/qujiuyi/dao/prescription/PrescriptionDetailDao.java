package com.jiuyi.qujiuyi.dao.prescription;

import java.util.List;

import com.jiuyi.qujiuyi.dto.perscription.PrescriptionDetailDto;

/**
 * @author superb @Date 2015年12月15日
 * 
 * @Description
 *
 * @Copyright 2015 重庆柒玖壹健康管理有限公司
 */
public interface PrescriptionDetailDao {
	/**
	 * 
	 * @number 1 @description 根据处方ID查询处方清单
	 * 
	 * @param prescriptionDetailDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月16日
	 */
	public List<PrescriptionDetailDto> queryPrescriptionDetailByPrescriptionId(PrescriptionDetailDto prescriptionDetailDto) throws Exception;

}
