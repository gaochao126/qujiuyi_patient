package com.jiuyi.qujiuyi.service.auth.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.IDCard;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.auth.RealNameAuthDao;
import com.jiuyi.qujiuyi.dto.auth.RealNameAuthDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.auth.RealNameAuthService;

/**
 * @description 实名认证业务层实现
 * @author zhb
 * @createTime 2015年4月21日
 */
@Service
public class RealNameAuthServiceImpl implements RealNameAuthService {
    @Autowired
    private RealNameAuthDao realNameAuthDao;

    /**
     * @description 实名认证提交
     * @param realNameAuthDto
     * @return
     * @throws Exception
     */
    @Override
    public ResponseDto createRealNameAuth(RealNameAuthDto realNameAuthDto) throws Exception {
        /** setep1:空异常处理. */
        if (realNameAuthDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验是否为本人操作. */
        TokenDto token = CacheContainer.getToken(realNameAuthDto.getToken());
        if (realNameAuthDto.getUserId() == null
                || (token != null && token.getPatient() != null && !realNameAuthDto.getUserId().equals(token.getPatient().getId()))) {
            throw new BusinessException("非本人操作");
        }

        /** step3:校验真实姓名. */
        if (!Util.isNotEmpty(realNameAuthDto.getRealName())) {
            throw new BusinessException("真实姓名不能为空");
        }

        /** step4:校验身份证. */
        try {
            IDCard.IDCardValidate(realNameAuthDto.getUid());
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

        /** step5:校验身份证照. */
        if (!Util.isNotEmpty(realNameAuthDto.getAuthImage())) {
            throw new BusinessException("身份证照不能为空");
        }

        /** step6:校验是否已在核审中. */
        List<RealNameAuthDto> list = realNameAuthDao.queryRealNameAuthingByUserId(realNameAuthDto);
        if (list != null && !list.isEmpty()) {
            throw new BusinessException("已在核审中,请勿多次提交");
        }

        /** setep7:入库. */
        realNameAuthDto.setCreateTime(new Date());
        realNameAuthDao.createRealNameAuth(realNameAuthDto);

        /** step8:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("实名认证提交成功");
        return responseDto;
    }

}