package com.jiuyi.qujiuyi.service.relative.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.IDCard;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.relative.PatientRelativeDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.dto.relative.PatientRelativeDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.relative.PatientRelativeService;

/**
 * @description 常用就诊人业务层实现
 * @author zhb
 * @createTime 2015年4月29日
 */
@Service
public class PatientRelativeServiceImpl implements PatientRelativeService {
	@Autowired
	private PatientRelativeDao patientRelativeDao;

	/**
	 * @description 新增常用就诊人
	 * @param patientRelativeDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto addRelative(PatientRelativeDto patientRelativeDto) throws Exception {
		/** step1:空异常处理. */
		if (patientRelativeDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(patientRelativeDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();

		// 判断用户手机号
		if (Util.isNotEmpty(patientRelativeDto.getPhone())) {
			if (!Util.isMobile(patientRelativeDto.getPhone())) {
				throw new BusinessException("就诊人手机号格式不正确");
			}
		}

		if (!Util.isNotEmpty(patientRelativeDto.getNation())) {
			throw new BusinessException("请选择民族");
		}

		/** step3:如果新增的是默认就诊人,则更新历史默认就诊人为非默认就诊人. */
		patientRelativeDto.setPatientId(patient.getId());
		if (patientRelativeDto.getIsDefault() != null && patientRelativeDto.getIsDefault() == 1) {
			patientRelativeDao.updateAllDefultByPatientId(patientRelativeDto);
		}

		/** step4:新增. */
		if (Util.isNotEmpty(patientRelativeDto.getCertificateNumber())) {
			patientRelativeDto.setGender(IDCard.getGenderByCard(patientRelativeDto.getCertificateNumber()));
			patientRelativeDto.setBirthday(IDCard.getBirthdayByCard(patientRelativeDto.getCertificateNumber()));
		}
		Date time = new Date();
		patientRelativeDto.setCreateTime(time);
		patientRelativeDto.setUpdateTime(time);

		patientRelativeDao.addRelative(patientRelativeDto);

		/** step5:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("新增常用就诊人成功");
		return responseDto;
	}

	/**
	 * @description 删除常用就诊人
	 * @param patientRelativeDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto delRelative(PatientRelativeDto patientRelativeDto) throws Exception {
		/** step1:空异常处理. */
		if (patientRelativeDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(patientRelativeDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();

		/** step3:新增. */
		patientRelativeDto.setPatientId(patient.getId());
		patientRelativeDao.delRelative(patientRelativeDto);

		/** step4:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("删除常用就诊人成功");
		return responseDto;
	}

	/**
	 * @description 修改常用就诊人
	 * @param patientRelativeDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto modRelative(PatientRelativeDto patientRelativeDto) throws Exception {
		/** step1:空异常处理. */
		if (patientRelativeDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(patientRelativeDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();

		// 判断用户手机号
		if (Util.isNotEmpty(patientRelativeDto.getPhone())) {
			if (!Util.isMobile(patientRelativeDto.getPhone())) {
				throw new BusinessException("就诊人手机号格式不正确");
			}
		}

		/** step3:如果新增的是默认就诊人,则更新历史默认就诊人为非默认就诊人. */
		patientRelativeDto.setPatientId(patient.getId());
		if (patientRelativeDto.getIsDefault() != null && patientRelativeDto.getIsDefault() == 1) {
			patientRelativeDao.updateAllDefultByPatientId(patientRelativeDto);
		}

		/** step4:修改. */
		if (Util.isNotEmpty(patientRelativeDto.getCertificateNumber())) {
			patientRelativeDto.setGender(IDCard.getGenderByCard(patientRelativeDto.getCertificateNumber()));
			patientRelativeDto.setBirthday(IDCard.getBirthdayByCard(patientRelativeDto.getCertificateNumber()));
		}
		patientRelativeDto.setUpdateTime(new Date());
		patientRelativeDao.modRelative(patientRelativeDto);

		/** step5:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("修改常用就诊人成功");
		return responseDto;
	}

	/**
	 * @description 查询常用就诊人
	 * @param patientRelativeDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryRelatives(PatientRelativeDto patientRelativeDto) throws Exception {
		/** step1:空异常处理. */
		if (patientRelativeDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(patientRelativeDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		patientRelativeDto.setPatientId(patient.getId());

		/** step3:查询. */
		List<PatientRelativeDto> list = patientRelativeDao.queryRelatives(patientRelativeDto);

		/** step4:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		list = list == null ? new ArrayList<PatientRelativeDto>() : list;
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("page", patientRelativeDto.getPage());
		detail.put("list", list);
		responseDto.setDetail(detail);
		responseDto.setResultDesc("获取常用就诊人成功");
		return responseDto;
	}
}