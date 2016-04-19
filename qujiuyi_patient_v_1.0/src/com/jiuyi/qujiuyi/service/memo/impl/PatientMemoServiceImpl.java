package com.jiuyi.qujiuyi.service.memo.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.dao.memo.PatientMemoDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.memo.PatientMemoDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.memo.PatientMemoService;

/**
 * @description 常用就诊人业务层实现
 * @author zhb
 * @createTime 2015年4月29日
 */
@Service
public class PatientMemoServiceImpl implements PatientMemoService {
    @Autowired
    private PatientMemoDao patientMemoDao;

    @Override
    public ResponseDto addMemo(PatientMemoDto ratientMemoDto) throws Exception {
        /** step1:空异常处理. */
        if (ratientMemoDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验是否为本人操作. */
        TokenDto token = CacheContainer.getToken(ratientMemoDto.getToken());
        if (ratientMemoDto.getPatientId() == null
                || (token != null && token.getPatient() != null && !ratientMemoDto.getPatientId().equals(token.getPatient().getId()))) {
            throw new BusinessException("非本人操作");
        }

        /** step3:入库. */
        Date time = new Date();
        ratientMemoDto.setCreateTime(time);
        ratientMemoDto.setUpdateTime(time);
        patientMemoDao.addMemo(ratientMemoDto);

        /** step4:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("新增备忘录成功");
        return responseDto;
    }

    @Override
    public ResponseDto delMemo(PatientMemoDto ratientMemoDto) throws Exception {
        /** step1:空异常处理. */
        if (ratientMemoDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验是否为本人操作. */
        TokenDto token = CacheContainer.getToken(ratientMemoDto.getToken());
        if (ratientMemoDto.getPatientId() == null
                || (token != null && token.getPatient() != null && !ratientMemoDto.getPatientId().equals(token.getPatient().getId()))) {
            throw new BusinessException("非本人操作");
        }

        /** step3:更新数据库. */
        patientMemoDao.delMemo(ratientMemoDto);

        /** step4:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("删除备忘录成功");
        return responseDto;
    }

    @Override
    public ResponseDto modMemo(PatientMemoDto ratientMemoDto) throws Exception {
        /** step1:空异常处理. */
        if (ratientMemoDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验是否为本人操作. */
        TokenDto token = CacheContainer.getToken(ratientMemoDto.getToken());
        if (ratientMemoDto.getPatientId() == null
                || (token != null && token.getPatient() != null && !ratientMemoDto.getPatientId().equals(token.getPatient().getId()))) {
            throw new BusinessException("非本人操作");
        }

        /** step3:更新数据库. */
        ratientMemoDto.setUpdateTime(new Date());
        patientMemoDao.modMemo(ratientMemoDto);

        /** step4:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("更新备忘录成功");
        return responseDto;
    }

    @Override
    public ResponseDto queryMemos(PatientMemoDto ratientMemoDto) throws Exception {
        /** step1:空异常处理. */
        if (ratientMemoDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验是否为本人操作. */
        TokenDto token = CacheContainer.getToken(ratientMemoDto.getToken());
        if (ratientMemoDto.getPatientId() == null
                || (token != null && token.getPatient() != null && !ratientMemoDto.getPatientId().equals(token.getPatient().getId()))) {
            throw new BusinessException("非本人操作");
        }

        /** step3:查询数据. */
        List<PatientMemoDto> list = patientMemoDao.queryMemos(ratientMemoDto);

        /** step4:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        list = list == null ? new ArrayList<PatientMemoDto>() : list;
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("page", ratientMemoDto.getPage());
        detail.put("list", list);
        responseDto.setDetail(detail);
        responseDto.setResultDesc("获取备忘录成功");
        return responseDto;
    }
}