package com.jiuyi.qujiuyi.service.medicine.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.dao.medicine.MedicineDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.medicine.MedicineDto;
import com.jiuyi.qujiuyi.service.medicine.MedicineService;

/**
 * @description 药物业务层实现
 * @author zhb
 * @createTime 2015年7月8日
 */
@Service
public class MedicineServiceImpl implements MedicineService {
	@Autowired
	private MedicineDao medicineDao;

	/**
	 * @description 查询药物列表
	 * @param medicineDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryMedicineList(MedicineDto medicineDto) throws Exception {
		medicineDto = medicineDto != null ? medicineDto : new MedicineDto();
		List<MedicineDto> list = medicineDao.queryMedicineList(medicineDto);
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("获取成功");
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("page", medicineDto.getPage());
		detail.put("list", list);
		responseDto.setDetail(detail);
		return responseDto;
	}
}