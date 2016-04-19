package com.jiuyi.qujiuyi.service.guide.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.dao.guide.GuidingPatientDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.guide.GuidPatientDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.guide.GuidingPatientService;

/**
 * @author superb    @Date 2016年1月21日
 * 
 * @Description 智能导诊
 *
 * @Copyright 2016 重庆柒玖壹健康管理有限公司
 */
@Service
public class GuidingPatientServiceImpl implements GuidingPatientService {
	@Autowired
	private GuidingPatientDao guidingPatientDao;

	/**
	 * 
	 * @number	1		@description	查询身体部位
	 * 
	 * @param guidPatientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月21日
	 */
	@Override
	public ResponseDto queryBodyPart(GuidPatientDto guidPatientDto) throws Exception {
		List<GuidPatientDto> parts = guidingPatientDao.queryBodyPart(guidPatientDto);
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", parts);
		responseDto.setDetail(map);
		responseDto.setResultDesc("身体部位列表");
		return responseDto;
	}

	/**
	 * 
	 * @number	2		@description	查询疾病
	 * 
	 * @param guidPatientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月21日
	 */
	@Override
	public ResponseDto queryIllness(GuidPatientDto guidPatientDto) throws Exception {
		if (guidPatientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}
		if (guidPatientDto.getType() == null) {
			throw new BusinessException("请指定性别");
		}
		if (guidPatientDto.getBodyPartId() == null) {
			throw new BusinessException("请指定身体部位");
		}

		List<GuidPatientDto> ness = guidingPatientDao.queryIllness(guidPatientDto);
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", ness);
		responseDto.setDetail(map);
		responseDto.setResultDesc("疾病列表");
		return responseDto;
	}
}
