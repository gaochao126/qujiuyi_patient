package com.jiuyi.qujiuyi.service.detail.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.dao.detail.PatientAccountDetailDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.detail.PatientAccountDetailDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.detail.PatientAccountDetailService;

/**
 * @description 患者收支明细业务层实现
 * @author zhb
 * @createTime 2015年5月26日
 */
@Service
public class PatientAccountDetailServiceImpl implements PatientAccountDetailService {
    @Autowired
    private PatientAccountDetailDao patientAccountDetailDao;

    /**
     * @description 获取患者收支明细
     * @param patientAccountDetailDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryPatientAccountDetail(PatientAccountDetailDto patientAccountDetailDto) throws Exception {
        /** step1:空异常处理. */
        if (patientAccountDetailDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }
        
        /** step2:查询数据. */
        TokenDto token = CacheContainer.getToken(patientAccountDetailDto.getToken());
        PatientDto patientDto = token != null ? token.getPatient() : null;
        Integer patientId = patientDto != null ? patientDto.getId() : null;
        patientAccountDetailDto.setPatientId(patientId);
        List<PatientAccountDetailDto> list = patientAccountDetailDao.queryPatientAccountDetail(patientAccountDetailDto);

        /** step3:返回结果. */
        list = list == null ? new ArrayList<PatientAccountDetailDto>() : list;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", list);
        map.put("page", patientAccountDetailDto.getPage());
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("获取成功");
        responseDto.setDetail(map);
        return responseDto;
    }
}