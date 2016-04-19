package com.jiuyi.qujiuyi.dao.medicine;

import java.util.List;

import com.jiuyi.qujiuyi.dto.medicine.MedicineTypeDto;

/**
 * @description 药品分类dao层接口
 * @author zhb
 * @createTime 2015年9月6日
 */
public interface MedicineTypeDao {
    /**
     * @description 获取药品分类列表
     * @return
     * @throws Exception
     */
    public List<MedicineTypeDto> queryMedicineTypeList() throws Exception;
}