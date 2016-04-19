package com.jiuyi.qujiuyi.service.invitation.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.dao.coupon.CouponDao;
import com.jiuyi.qujiuyi.dao.invitation.InvitationVerificationDao;
import com.jiuyi.qujiuyi.dao.patient.PatientDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.coupon.CouponDto;
import com.jiuyi.qujiuyi.dto.invitation.InvitationVerificationDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.invitation.InvitationVerificationService;

/**
 * @description 邀请验证业务层实现
 * @author zhb
 * @createTime 2015年5月27日
 */
@Service
public class InvitationVerificationServiceImpl implements InvitationVerificationService {
    @Autowired
    private InvitationVerificationDao invitationVerificationDao;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private CouponDao couponDao;

    /**
     * @description 邀请验证
     * @param patientDto
     * @throws Exception
     */
    public ResponseDto invitationVerification(InvitationVerificationDto invitationVerificationDto) throws Exception {
        /** step1:空异常处理. */
        if (invitationVerificationDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:判断邀请验证活动是否已结束. */
        if (SysCfg.getInt("patient.invitationVerificationSwitch") == 0) {
            throw new BusinessException("邀请验证活动已结束");
        }

        /** step3:校验患者是否为第一次邀请码验证. */
        TokenDto token = CacheContainer.getToken(invitationVerificationDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : null;
        invitationVerificationDto.setPatientId(patient != null ? patient.getId() : null);
        List<InvitationVerificationDto> list = invitationVerificationDao.queryInvitationVerificationsByPatientId(invitationVerificationDto);
        if (list != null && !list.isEmpty()) {
            throw new BusinessException("该操作每个人只有一次机会");
        }
        
        /** step4:校验邀请码是否存在. */
        PatientDto queryPatientDto = new PatientDto();
        queryPatientDto.setInvitationCode(invitationVerificationDto.getInvitationCode());
        List<PatientDto> patientList = patientDao.queryPatientByInvitationCode(queryPatientDto);
        if (patientList == null || patientList.isEmpty()) {
            throw new BusinessException("邀请码有误");
        }

        /** step5:赠送礼卷. */
        Random r = new Random();
        CouponDto couponDto = new CouponDto();
        couponDto.setPatientId(patientList.get(0).getId());
        couponDto.setAmount(new BigDecimal(r.nextInt(4) + 2));
        couponDto.setCreateTime(new Date());
        couponDto.setExpireTime(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
        couponDao.createCoupon(couponDto);

        /** step6:保存验证记录. */
        invitationVerificationDto.setInsertTime(new Date());
        invitationVerificationDao.saveInvitationVerification(invitationVerificationDto);

        /** step6:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("邀请验证成功");
        return responseDto;
    }
}