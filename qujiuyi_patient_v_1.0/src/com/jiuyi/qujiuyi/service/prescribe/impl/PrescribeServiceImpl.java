package com.jiuyi.qujiuyi.service.prescribe.impl;

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
import com.jiuyi.qujiuyi.dao.medicine.MedicineDao;
import com.jiuyi.qujiuyi.dao.prescribe.PrescribeDao;
import com.jiuyi.qujiuyi.dao.prescribe.PrescribeDetailDao;
import com.jiuyi.qujiuyi.dto.common.RequestDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.medicine.MedicineDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.dto.prescribe.PrescribeDetailDto;
import com.jiuyi.qujiuyi.dto.prescribe.PrescribeDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.prescribe.PrescribeService;

/**
 * @description 配药业务层实现
 * @author zhb
 * @createTime 2015年7月9日
 */
@Service
public class PrescribeServiceImpl implements PrescribeService {
    @Autowired
    private PrescribeDao prescribeDao;

    @Autowired
    private PrescribeDetailDao prescribeDetailDao;

    @Autowired
    private MedicineDao medicineDao;

    /**
     * @description 创建配药
     * @param prescribeDto
     * @return
     * @throws Exception
     */
    @Override
	public ResponseDto createPrescribe(PrescribeDto prescribeDto) throws Exception {
        if (prescribeDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }
        TokenDto token = CacheContainer.getToken(prescribeDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : null;
        if (patient == null || patient.getId() == null) {
            throw new BusinessException("患者不存在");
        } else {
            prescribeDto.setPatientId(patient.getId());
        }
        if (prescribeDto.getDoctorId() == null) {
            throw new BusinessException("医生id不能为空");
        }
        if (!Util.isNotEmpty(prescribeDto.getSymptoms())) {
            throw new BusinessException("症状描述不能为空");
        }
        if (prescribeDto.getAge() == null) {
            throw new BusinessException("患者年龄必填");
        }
        if (prescribeDto.getGender() == null) {
            throw new BusinessException("患者性别必填");
        }
        if (prescribeDto.getReceiveType() == null) {
            throw new BusinessException("收药方式必填");
        }
        if (prescribeDto.getReceiveType() == 2 && prescribeDto.getReceiveAddress() == null) {
            throw new BusinessException("收药地址必填");
        }
        if (prescribeDto.getPrescribeDetailList() == null || prescribeDto.getPrescribeDetailList().isEmpty()) {
            throw new BusinessException("至少选择一样药品");
        }

        // 保存配药记录
        prescribeDto.setStatus(0);
        prescribeDto.setDistributionStatus(0);
        prescribeDto.setCreateTime(new Date());
        prescribeDao.createPrescribe(prescribeDto);

        // 保存配药明细
        for (PrescribeDetailDto prescribeDetailDto : prescribeDto.getPrescribeDetailList()) {
            prescribeDetailDto.setPrescribeId(prescribeDto.getId());
            prescribeDetailDao.createPrescribeDetail(prescribeDetailDto);
        }

		// 患者向医生发起配药请求
		RequestDto requestDto = new RequestDto();
		requestDto.setCmd("prescribeRequest");
		requestDto.setToken(prescribeDto.getToken());
		Map<String, Object> params = new HashMap<String, Object>();
		requestDto.setParams(params);
		params.put("sender", patient.getId());
		params.put("receiver", prescribeDto.getDoctorId());
		params.put("serviceId", prescribeDto.getId());
		URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

        ResponseDto responseDto = new ResponseDto();
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("id", prescribeDto.getId());
        responseDto.setDetail(detail);
        responseDto.setResultDesc("配药成功");
        return responseDto;
    }

    /**
     * @description 获取配药详情
     * @param prescribeDto
     * @return
     * @throws Exception
     */
    @Override
	public ResponseDto queryPrescribeDetail(PrescribeDto prescribeDto) throws Exception {
        if (prescribeDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        TokenDto token = CacheContainer.getToken(prescribeDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : new PatientDto();
        prescribeDto.setPatientId(patient.getId());

        prescribeDto = prescribeDao.queryPrescribeDetail(prescribeDto);
        if (prescribeDto == null) {
            throw new BusinessException("配药记录不存在");
        }
        prescribeDto.setDoctorHead(SysCfg.getString("doctor.headPortraitPath") + prescribeDto.getDoctorHead());

        if (Util.isNotEmpty(prescribeDto.getPresListImage())) {
            prescribeDto.setPresListImage(SysCfg.getString("preslist.path") + prescribeDto.getPresListImage());
        }

        MedicineDto medicineDto = new MedicineDto();
        medicineDto.setPrescribeId(prescribeDto.getId());
        List<MedicineDto> list = medicineDao.queryMedicineListByPrescribeId(medicineDto);
        prescribeDto.setMedicineList(list);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("获取成功");
        responseDto.setDetail(prescribeDto);
        return responseDto;
    }

    /**
     * @description 获取患者配药列表
     * @param prescribeDto
     * @return
     * @throws Exception
     */
    @Override
	public ResponseDto getPrescribeListByPatientId(PrescribeDto prescribeDto) throws Exception {
        if (prescribeDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        TokenDto token = CacheContainer.getToken(prescribeDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : new PatientDto();
        prescribeDto.setPatientId(patient.getId());

        List<PrescribeDto> list = prescribeDao.getPrescribeListByPatientId(prescribeDto);
        if (list != null && !list.isEmpty()) {
            for (PrescribeDto dto : list) {
                if (Util.isNotEmpty(dto.getPresListImage())) {
                    dto.setPresListImage(SysCfg.getString("preslist.path") + dto.getPresListImage());
                }
            }
        }

        ResponseDto responseDto = new ResponseDto();
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("list", list);
        detail.put("page", prescribeDto.getPage());
        responseDto.setResultDesc("获取成功");
        responseDto.setDetail(detail);
        return responseDto;
    }

	/**
	 * 
	 * @number
	 * @description 删除配药
	 * 
	 * @param prescribeDto
	 * @return
	 * @throws Exception
	 * @Date 2015年11月19日
	 */
	@Override
	public ResponseDto delPrescribe(PrescribeDto prescribeDto) throws Exception {
		/** step1: 校验空. */
		if (prescribeDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2: 校验serviceID. */
		if (prescribeDto.getId() == null) {
			throw new BusinessException("ID不能为空");
		}
		prescribeDao.delPrescribe(prescribeDto);
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("删除成功");
		return responseDto;
	}
}