package com.jiuyi.qujiuyi.service.medicine;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.medicine.MedicineTypeDto;

/**
 * @description 药物分类业务层接口
 * @author zhb
 * @createTime 2015年7月8日
 */
public interface MedicineTypeService {
    /**
     * @description 查询药品分类列表
     * @param medicineTypeDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryMedicineTypeList(MedicineTypeDto medicineTypeDto) throws Exception;
}