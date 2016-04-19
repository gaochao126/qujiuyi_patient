package com.jiuyi.qujiuyi.service.medicine;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.medicine.MedicineDto;

/**
 * @description 药物业务层接口
 * @author zhb
 * @createTime 2015年7月8日
 */
public interface MedicineService {
	/**
	 * @description 查询药物列表
	 * @param medicineDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto queryMedicineList(MedicineDto medicineDto) throws Exception;

}