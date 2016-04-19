package com.jiuyi.qujiuyi.service.address.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.address.PatientAddressDao;
import com.jiuyi.qujiuyi.dto.address.PatientAddressDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.address.PatientAddressService;

/**
 * @description 患者地址业务层实现
 * @author zhb
 * @createTime 2015年9月2日
 */
@Service
public class PatientAddressServiceImpl implements PatientAddressService {
    @Autowired
    private PatientAddressDao patientAdressDao;

    /**
     * @description 新增患者地址
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public ResponseDto createPatientAddr(PatientAddressDto patientAddressDto) throws Exception {
        if (patientAddressDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        if (!Util.isNotEmpty(patientAddressDto.getAddr())) {
            throw new BusinessException("地址不能为空");
        }

        if (!Util.isNotEmpty(patientAddressDto.getName())) {
            throw new BusinessException("姓名不能为空");
        }

        if (!Util.isNotEmpty(patientAddressDto.getPhone())) {
            throw new BusinessException("电话不能为空");
        }

        if (patientAddressDto.getProvinceId() == null) {
            throw new BusinessException("省份id不能为空");
        }

        if (patientAddressDto.getCityId() == null) {
            throw new BusinessException("城市id不能为空");
        }

        TokenDto token = CacheContainer.getToken(patientAddressDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : new PatientDto();
        patientAddressDto.setPatientId(patient.getId());
        if (patientAddressDto.getIsDefault() != null && patientAddressDto.getIsDefault() == 1) {
            patientAdressDao.updateDefaultValByPatientId(patientAddressDto);
        }

        patientAdressDao.createPatientAddr(patientAddressDto);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("创建成功");
        return responseDto;
    }

    /**
     * @description 删除患者地址
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public ResponseDto deletePatientAddr(PatientAddressDto patientAddressDto) throws Exception {
        if (patientAddressDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        TokenDto token = CacheContainer.getToken(patientAddressDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : new PatientDto();
        patientAddressDto.setPatientId(patient.getId());
        patientAdressDao.deletePatientAddr(patientAddressDto);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("删除成功");
        return responseDto;
    }

    /**
     * @description 更新患者地址
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public ResponseDto updatePatientAddr(PatientAddressDto patientAddressDto) throws Exception {
        if (patientAddressDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        if (patientAddressDto.getProvinceId() == null) {
            throw new BusinessException("省份id不能为空");
        }

        if (patientAddressDto.getCityId() == null) {
            throw new BusinessException("城市id不能为空");
        }

        if (patientAddressDto.getTownId() == null) {
            throw new BusinessException("乡镇id不能为空");
        }

        TokenDto token = CacheContainer.getToken(patientAddressDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : new PatientDto();
        patientAddressDto.setPatientId(patient.getId());
        if (patientAddressDto.getIsDefault() != null && patientAddressDto.getIsDefault() == 1) {
            patientAdressDao.updateDefaultValByPatientId(patientAddressDto);
        }
        patientAdressDao.updatePatientAddr(patientAddressDto);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("更新成功");
        return responseDto;
    }

    /**
     * @description 查询患者地址列表
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryPatientAddrList(PatientAddressDto patientAddressDto) throws Exception {
        if (patientAddressDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        TokenDto token = CacheContainer.getToken(patientAddressDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : new PatientDto();
        patientAddressDto.setPatientId(patient.getId());
        List<PatientAddressDto> list = patientAdressDao.queryPatientAddrList(patientAddressDto);

        list = list == null ? new ArrayList<PatientAddressDto>() : list;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", list);
        map.put("page", patientAddressDto.getPage());
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("查询成功");
        responseDto.setDetail(map);
        return responseDto;
    }

    /**
     * @description 根据地址id查询地址信息
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryPatientAddr(PatientAddressDto patientAddressDto) throws Exception {
        if (patientAddressDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }
        TokenDto token = CacheContainer.getToken(patientAddressDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : new PatientDto();
        patientAddressDto.setPatientId(patient.getId());
        patientAddressDto.setId(patientAddressDto.getId());
        List<PatientAddressDto> list = patientAdressDao.queryPatientAddr(patientAddressDto);

        list = list == null ? new ArrayList<PatientAddressDto>() : list;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", list);
        map.put("page", patientAddressDto.getPage());
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("查询成功");
        responseDto.setDetail(map);
        return responseDto;
    }
}