package com.jiuyi.qujiuyi.dao.medicine;

import java.util.List;

import com.jiuyi.qujiuyi.dto.medicine.MedicineDto;

/**
 * @description 药物dao层接口
 * @author zhb
 * @createTime 2015年7月8日
 */
public interface MedicineDao {
    /**
     * @description 查询药物列表
     * @param medicineDto
     * @return
     * @throws Exception
     */
    public List<MedicineDto> queryMedicineList(MedicineDto medicineDto) throws Exception;

    /**
     * @description 根据配药记录id查询药物列表
     * @param medicineDto
     * @return
     * @throws Exception
     */
    public List<MedicineDto> queryMedicineListByPrescribeId(MedicineDto medicineDto) throws Exception;
}