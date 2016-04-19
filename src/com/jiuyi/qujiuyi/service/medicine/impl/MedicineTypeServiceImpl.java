package com.jiuyi.qujiuyi.service.medicine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.dao.medicine.MedicineTypeDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.medicine.MedicineTypeDto;
import com.jiuyi.qujiuyi.service.medicine.MedicineTypeService;

/**
 * @description 药物业务层实现
 * @author zhb
 * @createTime 2015年7月8日
 */
@Service
public class MedicineTypeServiceImpl implements MedicineTypeService {
    @Autowired
    private MedicineTypeDao medicineTypeDao;

    /**
     * @description 查询药品分类列表
     * @param medicineTypeDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryMedicineTypeList(MedicineTypeDto medicineTypeDto) throws Exception {
        List<MedicineTypeDto> list = medicineTypeDao.queryMedicineTypeList();
        list = list != null ? list : new ArrayList<MedicineTypeDto>();
        for (MedicineTypeDto dto : list) {
            dto.setImage(SysCfg.getString("medicine.virtualUrl") + dto.getImage());
        }
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("获取成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("list", list);
        responseDto.setDetail(detail);
        return responseDto;
    }
}