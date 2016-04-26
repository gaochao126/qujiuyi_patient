package com.jiuyi.qujiuyi.service.scancode.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.patient.PatientDao;
import com.jiuyi.qujiuyi.dao.scancode.ScanCodeDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.dto.scancode.ScanCodeDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.scancode.ScanCodeService;

@Service
public class ScanCodeServiceImpl implements ScanCodeService {
	@Autowired
	private ScanCodeDao scanCodeDao;

	@Autowired
	private PatientDao patientDao;

	/**
	 * 1.扫码，运营人员工作量记录
	 * 
	 * @param scanCodeDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto scanCode(ScanCodeDto scanCodeDto) throws Exception {
		if (scanCodeDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		if (scanCodeDto.getAdminId() == null) {
			throw new BusinessException("运营人员id不能为空");
		}

		if (!Util.isNotEmpty(scanCodeDto.getWeixinOpenId())) {
			throw new BusinessException("weixinOpenId不能为空");
		}

		// 查询该微信是否已注册
		PatientDto patientDto = new PatientDto();
		patientDto.setWeixinOpenId(scanCodeDto.getWeixinOpenId());
		PatientDto patient = patientDao.queryPatientByWeixinOpenId(patientDto);
		if (patient == null) {
			scanCodeDto.setType(0);
			List<ScanCodeDto> works = scanCodeDao.queryWorkLoad(scanCodeDto);
			if (works == null || works.isEmpty()) {
				scanCodeDto.setType(0);
				scanCodeDao.insertWorkLoad(scanCodeDto);
			}
		}
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("成功");
		return responseDto;
	}

	/**
	 * 2.医院扫码
	 * 
	 * @param scanCodeDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto hospitalScanCode(ScanCodeDto scanCodeDto) throws Exception {
		if (scanCodeDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		if (!Util.isNotEmpty(scanCodeDto.getWeixinOpenId())) {
			throw new BusinessException("weixinOpenId不能为空");
		}

		if (scanCodeDto.getHospitalId() == null) {
			throw new BusinessException("医院id不能为空");
		}
		// 查询该微信是否已注册
		PatientDto patientDto = new PatientDto();
		patientDto.setWeixinOpenId(scanCodeDto.getWeixinOpenId());
		PatientDto patient = patientDao.queryPatientByWeixinOpenId(patientDto);
		if (patient == null) {
			scanCodeDto.setType(1);
			List<ScanCodeDto> works = scanCodeDao.queryWorkLoad(scanCodeDto);
			if (works == null || works.isEmpty()) {
				scanCodeDao.insertWorkLoad(scanCodeDto);
			}
		}
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("成功");
		return responseDto;

	}
}
