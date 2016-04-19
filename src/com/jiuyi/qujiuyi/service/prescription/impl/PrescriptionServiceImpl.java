package com.jiuyi.qujiuyi.service.prescription.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.URLInvoke;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.prescription.PrescriptionDao;
import com.jiuyi.qujiuyi.dao.prescription.PrescriptionDetailDao;
import com.jiuyi.qujiuyi.dao.relative.PatientRelativeDao;
import com.jiuyi.qujiuyi.daoyao.mdeicine.YaoMedicineDao;
import com.jiuyi.qujiuyi.dto.common.RequestDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.medicine.YaoMedicineFormatDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.dto.perscription.PrescriptionDetailDto;
import com.jiuyi.qujiuyi.dto.perscription.PrescriptionDto;
import com.jiuyi.qujiuyi.dto.relative.PatientRelativeDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.prescription.PrescriptionService;

/**
 * @author superb @Date 2015年12月15日
 * 
 * @Description
 *
 * @Copyright 2015 重庆柒玖壹健康管理有限公司
 */
@Service
public class PrescriptionServiceImpl implements PrescriptionService {
	@Autowired
	private PrescriptionDao prescriptionDao;

	@Autowired
	private PatientRelativeDao patientRelativeDao;

	@Autowired
	private PrescriptionDetailDao prescriptionDetailDao;

	@Autowired
	private YaoMedicineDao yaoMedicineDao;

	/**
	 * 
	 * @number 1 @description 接收处方申请
	 * 
	 * @param prescriptionDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月15日
	 */
	@Override
	public ResponseDto recivePrescription(PrescriptionDto prescriptionDto) throws Exception {
		/** step1:空校验. */
		if (prescriptionDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(prescriptionDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		prescriptionDto.setPatientId(patient.getId());

		/** step3: 校验常用就诊人ID. */
		if (prescriptionDto.getRelativeId() == null) {
			throw new BusinessException("常用就诊人ID不能为空");
		}

		// 查询常用就诊人
		PatientRelativeDto patientRelativeDto = new PatientRelativeDto();
		patientRelativeDto.setId(prescriptionDto.getRelativeId());
		PatientRelativeDto relative = patientRelativeDao.getPatientRelativeById(patientRelativeDto);
		if (relative == null) {
			throw new BusinessException("常用就诊人不存在");
		}
		prescriptionDto.setRelativeName(relative.getName());
		prescriptionDto.setRelativeUid(relative.getCertificateNumber());
		prescriptionDto.setRelativeAge(Util.getAge(relative.getBirthday()));
		prescriptionDto.setRelativeGender(relative.getGender());

		if (!Util.isNotEmpty(prescriptionDto.getId())) {
			throw new BusinessException("处方id不能为空");
		}

		/** step4: 设置状态为接收状态. */
		prescriptionDto.setStatus(1);

		/** step5: 执行. */
		prescriptionDao.updatePrescription(prescriptionDto);
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("接受成功");
		return responseDto;
	}

	/**
	 * 
	 * @number 2 @description 处方列表
	 * 
	 * @param prescriptionDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月17日
	 */
	@Override
	public ResponseDto queryPrescriptionList(PrescriptionDto prescriptionDto) throws Exception {
		/** step1:获取用户. */
		TokenDto token = CacheContainer.getToken(prescriptionDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();

		prescriptionDto.setPatientId(patient.getId());

		// 处方列表
		List<PrescriptionDto> prescriptions = prescriptionDao.queryPrescriptionListByPaitnetId(prescriptionDto);

		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("list", prescriptions);
		dataMap.put("page", prescriptionDto.getPage());
		responseDto.setDetail(dataMap);
		responseDto.setResultDesc("我的处方清单");
		return responseDto;
	}

	/**
	 * 
	 * @number 3 @description 处方详情
	 * 
	 * @param prescriptionDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月17日
	 */
	@Override
	public ResponseDto queryPrescriptionDetail(PrescriptionDto prescriptionDto) throws Exception {
		/** step1: 校验空. */
		if (prescriptionDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:校验处方ID. */
		if (!Util.isNotEmpty(prescriptionDto.getId())) {
			throw new BusinessException("处方ID不能为空");
		}

		// 获取用户
		TokenDto token = CacheContainer.getToken(prescriptionDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();

		prescriptionDto.setPatientId(patient.getId());
		/** step3:查询处方. */
		PrescriptionDto prescription = prescriptionDao.queryPrescriptionById(prescriptionDto);
		prescription.setValidity(new Date(prescription.getCreateTime().getTime() + 2 * 24 * 60 * 60 * 1000));// 有效期
		prescription.setTakeMedecine("在有效期内向药师出示你的处方号，并支付费用即可");
		/** step4:根据处方ID查询处方清单. */
		PrescriptionDetailDto prescriptionDetailDto = new PrescriptionDetailDto();
		prescriptionDetailDto.setPrescriptionId(prescriptionDto.getId());
		List<PrescriptionDetailDto> detail = prescriptionDetailDao.queryPrescriptionDetailByPrescriptionId(prescriptionDetailDto);

		// 获取规格ID集合
		List<String> formatIds = new ArrayList<String>();
		for (int i = 0; i < detail.size(); i++) {
			formatIds.add(detail.get(i).getFormatId());
		}

		List<YaoMedicineFormatDto> medicines = new ArrayList<YaoMedicineFormatDto>();
		/** step4:查询药品. */
		if (formatIds != null && !formatIds.isEmpty() && formatIds.size() > 0) {
			medicines = yaoMedicineDao.queryMedicineListByFormatId(formatIds);
		}

		for (int j = 0; j < detail.size(); j++) {
			for (int n = 0; n < medicines.size(); n++) {
				if (detail.get(j).getFormatId().equals(medicines.get(n).getFormat_id())) {
					detail.get(j).setMedicineFormat(medicines.get(n));
					break;
				}
			}
		}

		prescription.setPrescriptionDetail(detail);
		ResponseDto responseDto = new ResponseDto();
		responseDto.setDetail(prescription);
		responseDto.setResultDesc("处方详情");
		return responseDto;
	}

	/**
	 * 
	 * @number 4 @description 删除处方
	 * 
	 * @param prescriptionDto
	 * @throws Exception
	 *
	 * @Date 2015年12月18日
	 */
	@Override
	public ResponseDto detelePrescription(PrescriptionDto prescriptionDto) throws Exception {
		/** setp1:judge null. */
		if (prescriptionDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2: judge id. */
		if (!Util.isNotEmpty(prescriptionDto.getId())) {
			throw new BusinessException("处方ID不能为空");
		}

		/** step3: get patient. */
		TokenDto token = CacheContainer.getToken(prescriptionDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		prescriptionDto.setPatientId(patient.getId());

		/** step4: execute delete */
		prescriptionDao.deletePrescription(prescriptionDto);

		/** step5: return result. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("删除成功");
		return responseDto;
	}

	/**
	 * 
	 * @number @description 确认处方已配药
	 * 
	 * @param prescriptionDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年2月1日
	 */
	@Override
	public ResponseDto comfirmPrescription(PrescriptionDto prescriptionDto) throws Exception {
		if (prescriptionDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}
		if (prescriptionDto.getId() == null) {
			throw new BusinessException("处方id不能为空");
		}

		prescriptionDto.setStatus(9);
		prescriptionDao.updatePrescription(prescriptionDto);
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("成功");
		return responseDto;
	}

	/**
	 * 
	 * @param prescriptionDto
	 *            再次申请处方
	 * @return
	 * @throws Exception
	 */
	public ResponseDto onceAgain(PrescriptionDto prescriptionDto) throws Exception {
		if (prescriptionDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		if (!Util.isNotEmpty(prescriptionDto.getId())) {
			throw new BusinessException("处方id不能为空");
		}

		/** step3: get patient. */
		TokenDto token = CacheContainer.getToken(prescriptionDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		prescriptionDto.setPatientId(patient.getId());

		/** 获取处方 */
		PrescriptionDto pres = prescriptionDao.queryPrescriptionById(prescriptionDto);
		if (pres.getStatus() == 7 || pres.getStatus() == 9) {
			prescriptionDto.setStatus(PrescriptionStatus.AGAIN.getIntValue());
			prescriptionDao.updatePrescription(prescriptionDto);
			PrescriptionDto prescription = prescriptionDao.queryPrescriptionById(prescriptionDto);

			// 终端提示
			RequestDto requestDto = new RequestDto();
			requestDto.setCmd("sendSystemMsg");
			Map<String, Object> params = new HashMap<String, Object>();
			Map<String, String> content = new HashMap<String, String>();
			content.put("cmd", "onceAgain");
			content.put("message", "患者再次申请处方");
			requestDto.setParams(params);
			params.put("target", prescription.getDoctorId());
			params.put("targetType", 0);
			params.put("summary", "患者再次申请处方");
			params.put("content", Constants.gson.toJson(content));
			params.put("weixinMsg", "患者再次申请处方");
			URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
		} else if (pres.getStatus() == 12) {
			throw new BusinessException("您已申请过该处方，一天内只能申请一次，请耐心等待");
		} else {
			throw new BusinessException("该处方未完成，无法再次申请");
		}

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("您的处方申请已发送，请耐心等待医生回复");
		return responseDto;
	}
}
