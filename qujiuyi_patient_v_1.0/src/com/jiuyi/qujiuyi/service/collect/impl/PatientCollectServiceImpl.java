package com.jiuyi.qujiuyi.service.collect.impl;

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
import com.jiuyi.qujiuyi.dao.collect.PatientCollectDao;
import com.jiuyi.qujiuyi.dao.doctor.DoctorDao;
import com.jiuyi.qujiuyi.dto.collect.PatientCollectDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.doctor.DoctorDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.collect.PatientCollectService;

@Service
public class PatientCollectServiceImpl implements PatientCollectService {
    @Autowired
    private PatientCollectDao patientCollectDao;

    @Autowired
    private DoctorDao doctorDao;

    /**
     * @description 收藏医生
     * @param patientDto
     * @throws Exception
     */
    @Override
    public ResponseDto collectDoctor(PatientCollectDto patientCollectDto) throws Exception {
        /** setep1:空异常处理. */
        if (patientCollectDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:设置用户id. */
        TokenDto token = CacheContainer.getToken(patientCollectDto.getToken());
        PatientDto patient = token != null && token.getPatient() != null ? token.getPatient() : new PatientDto();
        patientCollectDto.setPatientId(patient.getId());


        /** step3:校验上报数据是否完整. */
        if (patientCollectDto.getPatientId() == null || patientCollectDto.getDoctorId() == null) {
            throw new BusinessException("上报数据不完整");
        }

        /** step4:判断医生类型是否有效. */
        patientCollectDto.setDoctorType(patientCollectDto.getDoctorType() != null ? patientCollectDto.getDoctorType() : 1);
        if (patientCollectDto.getDoctorType() != 0 && patientCollectDto.getDoctorType() != 1) {
            throw new BusinessException("医生类型不对");
        }

        /** step5:判断医生是否存在. */
        DoctorDto doctorDto = new DoctorDto();
        doctorDto.setId(patientCollectDto.getDoctorId());
        if(patientCollectDto.getDoctorType() ==1){
            doctorDto = doctorDao.queryDoctorById(doctorDto);
        } else {
            doctorDto = doctorDao.getHospitalDoctorDetail(doctorDto);
        }
        if (doctorDto == null) {
            throw new BusinessException("医生不存在");
        }

        /** step6:判断医生是否已被收藏. */
        List<PatientCollectDto> list = patientCollectDao.queryCollectByPatientIdAndDoctorId(patientCollectDto);
        if (list != null && !list.isEmpty()) {
            throw new BusinessException("医生已被收藏");
        }

        /** step7:保存数据. */
        patientCollectDto.setCollectTime(new Date());
        patientCollectDao.collectDoctor(patientCollectDto);

        /** step8:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("收藏成功");
        return responseDto;
    }

    /**
     * @description 删除收藏医生
     * @param patientCollectDto
     * @throws Exception
     */
    @Override
	public ResponseDto deleteCollectDoctor(PatientCollectDto patientCollectDto) throws Exception {
        /** setep1:空异常处理. */
        if (patientCollectDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** setep2:空异常处理. */
        if (patientCollectDto.getDoctorId() == null) {
            throw new BusinessException("医生id不能为空");
        }

        /** step3:设置用户id. */
        TokenDto token = CacheContainer.getToken(patientCollectDto.getToken());
        PatientDto patient = token != null && token.getPatient() != null ? token.getPatient() : new PatientDto();
        patientCollectDto.setPatientId(patient.getId());

        /** step4:判断医生类型是否有效. */
        patientCollectDto.setDoctorType(patientCollectDto.getDoctorType() != null ? patientCollectDto.getDoctorType() : 1);
        if (patientCollectDto.getDoctorType() != 0 && patientCollectDto.getDoctorType() != 1) {
            throw new BusinessException("医生类型不对");
        }

        /** step5:删除医生. */
        patientCollectDao.deleteCollectDoctor(patientCollectDto);

        /** step6:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("取消收藏成功");
        return responseDto;
    }

    /**
     * @description 获取收藏医生列表
     * @param patientCollectDto
     * @throws Exception
     */
    @Override
	public ResponseDto queryCollectDoctorList(PatientCollectDto patientCollectDto) throws Exception {
        /** setep1:空异常处理. */
        if (patientCollectDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:设置用户id. */
        TokenDto token = CacheContainer.getToken(patientCollectDto.getToken());
        PatientDto patient = token != null && token.getPatient() != null ? token.getPatient() : new PatientDto();
        patientCollectDto.setPatientId(patient.getId());

        /** step3:查询收藏列表. */
        List<PatientCollectDto> list = patientCollectDao.queryCollectDoctorList(patientCollectDto);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getDoctorHeadPortrait() != null && list.get(i).getDoctorHeadPortrait().startsWith("http") == false) {
				list.get(i).setDoctorHeadPortrait(SysCfg.getString("doctor.head.path") + list.get(i).getDoctorHeadPortrait());
			}
		}

        /** step4:返回结果. */
        list = list == null ? new ArrayList<PatientCollectDto>() : list;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", list);
        map.put("page", patientCollectDto.getPage());
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("收藏列表查询成功");
        responseDto.setDetail(map);
        return responseDto;
    }

    /**
     * @description 判断医生是否已被收藏
     * @param patientCollectDto
     * @throws Exception
     */
    @Override
	public ResponseDto isCollectedDoctor(PatientCollectDto patientCollectDto) throws Exception {
        /** setep1:空异常处理. */
        if (patientCollectDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:设置用户id. */
        TokenDto token = CacheContainer.getToken(patientCollectDto.getToken());
        PatientDto patient = token != null && token.getPatient() != null ? token.getPatient() : new PatientDto();
        patientCollectDto.setPatientId(patient.getId());

        /** setep3:空异常处理. */
        if (patientCollectDto.getDoctorId() == null) {
            throw new BusinessException("医生id不能为空");
        }

        /** step4:判断医生类型是否有效. */
        patientCollectDto.setDoctorType(patientCollectDto.getDoctorType() != null ? patientCollectDto.getDoctorType() : 1);
        if (patientCollectDto.getDoctorType() != 0 && patientCollectDto.getDoctorType() != 1) {
            throw new BusinessException("医生类型不对");
        }

        /** setep5:查询数据库. */
        List<PatientCollectDto> list = patientCollectDao.queryCollectByPatientIdAndDoctorId(patientCollectDto);

        /** setep6:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("collected", list != null && !list.isEmpty());
        responseDto.setDetail(detail);
        responseDto.setResultDesc("成功");
        return responseDto;
    }

	/**
	 * 
	 * @number			@description	收藏医院
	 * 
	 * @param patientCollectDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月9日
	 */
	@Override
	public ResponseDto collectHospital(PatientCollectDto patientCollectDto) throws Exception {
		/** setep1:空异常处理. */
		if (patientCollectDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:设置用户id. */
		TokenDto token = CacheContainer.getToken(patientCollectDto.getToken());
		PatientDto patient = token != null && token.getPatient() != null ? token.getPatient() : new PatientDto();
		patientCollectDto.setPatientId(patient.getId());

		/** step3:判断医院ID. */
		if (patientCollectDto.getHospitalId() == null) {
			throw new BusinessException("医院id不能为空");
		}
		/** step4:查询医院是否被收藏. */
		List<PatientCollectDto> list = patientCollectDao.queryCollectHospitalList(patientCollectDto);
		if (list != null && !list.isEmpty()) {
			throw new BusinessException("您已收藏该医院");
		}

		patientCollectDto.setCollectTime(new Date());
		patientCollectDao.collectHospital(patientCollectDto);

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("收藏成功");
		return responseDto;
	}

	/**
	 * 
	 * @number			@description	删除收藏医院
	 * 
	 * @param patientCollectDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月9日
	 */
	@Override
	public ResponseDto deleteCollectHospital(PatientCollectDto patientCollectDto) throws Exception {
		/** step1:judge null */
		if (patientCollectDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:设置用户id. */
		TokenDto token = CacheContainer.getToken(patientCollectDto.getToken());
		PatientDto patient = token != null && token.getPatient() != null ? token.getPatient() : new PatientDto();
		patientCollectDto.setPatientId(patient.getId());
		patientCollectDao.deleteColectHospital(patientCollectDto);

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("删除成功");
		return responseDto;
	}
	

	/**
	 * 
	 * @number			@description	
	 * 
	 * @param patientCollectDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月9日
	 */
	@Override
	public ResponseDto queryCollectHospitalList(PatientCollectDto patientCollectDto) throws Exception {
		/** step1: judge null */
		if (patientCollectDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:设置用户id. */
		TokenDto token = CacheContainer.getToken(patientCollectDto.getToken());
		PatientDto patient = token != null && token.getPatient() != null ? token.getPatient() : new PatientDto();
		patientCollectDto.setPatientId(patient.getId());

		List<PatientCollectDto> list = patientCollectDao.queryCollectHospitalList(patientCollectDto);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getHead() != null) {
				list.get(i).setHead(SysCfg.getString("hospital.head.path") + list.get(i).getHead());
			}
		}
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("查询成功");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		responseDto.setDetail(map);
		return responseDto;
	}

	/**
	 * 
	 * @number			@description	判断医院是否被收藏
	 * 
	 * @param patientCollectDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月9日
	 */
	@Override
	public ResponseDto checkHospitalIsCollect(PatientCollectDto patientCollectDto) throws Exception {
		if (patientCollectDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}
		if (patientCollectDto.getHospitalId() == null) {
			throw new BusinessException("医院id不能为空");
		}
		TokenDto token = CacheContainer.getToken(patientCollectDto.getToken());
		PatientDto patient = token != null && token.getPatient() != null ? token.getPatient() : new PatientDto();
		patientCollectDto.setPatientId(patient.getId());

		List<PatientCollectDto> list = patientCollectDao.queryCollectHospitalList(patientCollectDto);
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> map = new HashMap<String, Object>();
		if (list == null || list.isEmpty()) {
			map.put("isCollect", "false");
		} else {
			map.put("isCollect", "true");
		}
		responseDto.setDetail(map);
		responseDto.setResultDesc("成功");
		return responseDto;
	}
	
}