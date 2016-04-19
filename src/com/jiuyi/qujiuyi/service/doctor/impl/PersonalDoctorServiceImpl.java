package com.jiuyi.qujiuyi.service.doctor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.dao.doctor.PersonalDoctorDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.doctor.DoctorDto;
import com.jiuyi.qujiuyi.dto.doctor.PersonalDoctorDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.doctor.PersonalDoctorService;

/**
 * @description 私人医生业务接口实现
 * @author zhb
 * @createTime 2015年4月24日
 */
@Service
public class PersonalDoctorServiceImpl implements PersonalDoctorService {
    @Autowired
    private PersonalDoctorDao personalDoctorDao;

    /**
     * @description 获取私人医生
     * @param personalDoctorDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryPersonalDoctors(PersonalDoctorDto personalDoctorDto) throws Exception {
        if (personalDoctorDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        TokenDto token = CacheContainer.getToken(personalDoctorDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : new PatientDto();
        personalDoctorDto.setPatientId(patient.getId());
        List<DoctorDto> list = personalDoctorDao.queryPersonalDoctors(personalDoctorDto);

        list = list == null ? new ArrayList<DoctorDto>() : list;
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("page", personalDoctorDto.getPage());
        detail.put("list", list);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("获取私人医生成功");
        responseDto.setDetail(detail);
        return responseDto;
    }

    /**
     * @description 判断是否为自己的私人医生
     * @param personalDoctorDto
     * @return
     * @throws Exception
     */
    public ResponseDto isMyPersonalDoctor(PersonalDoctorDto personalDoctorDto) throws Exception {
        /** step1:空异常处理. */
        if (personalDoctorDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验是否为本人操作. */
        TokenDto token = CacheContainer.getToken(personalDoctorDto.getToken());
        if (personalDoctorDto.getPatientId() == null
                || (token != null && token.getPatient() != null && !personalDoctorDto.getPatientId().equals(token.getPatient().getId()))) {
            throw new BusinessException("非本人操作");
        }

        /** step3:校验医生id. */
        if (personalDoctorDto.getPatientId() == null) {
            throw new BusinessException("医生id不能为空");
        }

        /** step4:查询数据. */
        List<PersonalDoctorDto> list = personalDoctorDao.queryPersonalDoctorByPatientIdAndDoctorId(personalDoctorDto);

        /** step5:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("isMyPersonalDoctor", list != null && !list.isEmpty());
        responseDto.setDetail(detail);
        return responseDto;
    }
}