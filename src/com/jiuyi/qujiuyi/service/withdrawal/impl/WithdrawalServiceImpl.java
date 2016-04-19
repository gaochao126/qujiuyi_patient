package com.jiuyi.qujiuyi.service.withdrawal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.patient.PatientDao;
import com.jiuyi.qujiuyi.dao.withdrawal.WithdrawalDao;
import com.jiuyi.qujiuyi.dao.withdrawal.WithdrawalRuleDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.dto.withdrawal.WithdrawalDto;
import com.jiuyi.qujiuyi.dto.withdrawal.WithdrawalRuleDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.withdrawal.WithdrawalService;

/**
 * @description 提现业务层实现
 * @author zhb
 * @createTime 2015年5月19日
 */
@Service
public class WithdrawalServiceImpl implements WithdrawalService {
    @Autowired
    private WithdrawalDao withdrawalDao;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private WithdrawalRuleDao withdrawalRuleDao;

    /**
     * @description 支付宝提现
     * @param alipayWithdrawalDto
     * @return
     * @throws Exception
     */
    public ResponseDto alipayWithdrawal(WithdrawalDto alipayWithdrawalDto) throws Exception {
        /** step1:空异常处理. */
        if (alipayWithdrawalDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验是否为本人操作. */
        TokenDto token = CacheContainer.getToken(alipayWithdrawalDto.getToken());
        if (alipayWithdrawalDto.getPatientId() == null
                || (token != null && token.getPatient() != null && !alipayWithdrawalDto.getPatientId().equals(token.getPatient().getId()))) {
            throw new BusinessException("非本人操作");
        }

        /** step3:校验账户. */
        if (!Util.isNotEmpty(alipayWithdrawalDto.getAccount())) {
            throw new BusinessException("账户必须填写");
        }

        /** step4:校验姓名. */
        if (!Util.isNotEmpty(alipayWithdrawalDto.getName())) {
            throw new BusinessException("姓名必须填写");
        }

        /** step5:校验金额不为空. */
        if (alipayWithdrawalDto.getAmount() == null) {
            throw new BusinessException("提现金额必须填写");
        }

        /** step6:校验金额不小于10. */
        if (alipayWithdrawalDto.getAmount() < 10) {
            throw new BusinessException("提现金额不能小于10");
        }
        
        /** step7:校验本次是否有提现申请. */
        WithdrawalDto queryWithdrawalDto = new WithdrawalDto();
        queryWithdrawalDto.setPatientId(alipayWithdrawalDto.getPatientId());
        queryWithdrawalDto.setStartTime(Util.getStartTimeOfWeek());
        queryWithdrawalDto.setEndTime(Util.getEndTimeOfWeek());
        List<WithdrawalDto> list = withdrawalDao.queryWithdrawalRecordsByCondition(queryWithdrawalDto);
        if (list != null && !list.isEmpty()) {
            throw new BusinessException("本周只能提现一次");
        }

        /** step8:校验提现密码是否正确. */
        PatientDto queryPatientDto = new PatientDto();
        queryPatientDto.setId(alipayWithdrawalDto.getPatientId());
        PatientDto patientDto = patientDao.queryPatientById(queryPatientDto);
        if (patientDto == null || patientDto.getWithdrawalPassword() == null
                || !patientDto.getWithdrawalPassword().equals(alipayWithdrawalDto.getWithdrawalPassword())) {
            throw new BusinessException("提现密码不正确");
        }

        /** step9:校验金额是否满足提现金额. */
        if (patientDto != null && (patientDto.getBalance() == null || patientDto.getBalance() < alipayWithdrawalDto.getAmount())) {
            throw new BusinessException("余额不足");
        }

        /** step10:更新用户余额. */
        if (patientDto != null) {
            patientDto.setBalance(patientDto.getBalance() - alipayWithdrawalDto.getAmount());
            patientDao.updateBalance(patientDto);
        }

        /** step11:保存提现记录. */
        alipayWithdrawalDto.setCreateTime(new Date());
        withdrawalDao.alipayWithdrawal(alipayWithdrawalDto);

        /** setp12:更新token. */
        token.getPatient().setBalance(patientDto.getBalance());

        /** step13:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("balance", patientDto != null && patientDto.getBalance() != null ? patientDto.getBalance() : 0);
        responseDto.setDetail(detail);
        responseDto.setResultDesc("提现成功");
        return responseDto;
    }

    /**
     * @description 银行卡提现
     * @param alipayWithdrawalDto
     * @return
     * @throws Exception
     */
    public ResponseDto bankWithdrawal(WithdrawalDto bankWithdrawalDto) throws Exception {
        /** step1:空异常处理. */
        if (bankWithdrawalDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验是否为本人操作. */
        TokenDto token = CacheContainer.getToken(bankWithdrawalDto.getToken());
        if (bankWithdrawalDto.getPatientId() == null
                || (token != null && token.getPatient() != null && !bankWithdrawalDto.getPatientId().equals(token.getPatient().getId()))) {
            throw new BusinessException("非本人操作");
        }

        /** step3:校验银行. */
        if (bankWithdrawalDto.getBankId() == null) {
            throw new BusinessException("银行必须填写");
        }

        /** step4:校验账户. */
        if (!Util.isNotEmpty(bankWithdrawalDto.getAccount())) {
            throw new BusinessException("账户必须填写");
        }

        /** step5:校验姓名. */
        if (!Util.isNotEmpty(bankWithdrawalDto.getName())) {
            throw new BusinessException("姓名必须填写");
        }

        /** step6:校验金额不为空. */
        if (bankWithdrawalDto.getAmount() == null) {
            throw new BusinessException("提现金额必须填写");
        }

        /** step7:校验金额不小于10. */
        if (bankWithdrawalDto.getAmount() < 10) {
            throw new BusinessException("提现金额不能小于10");
        }

        /** step8:校验本次是否有提现申请. */
        WithdrawalDto queryWithdrawalDto = new WithdrawalDto();
        queryWithdrawalDto.setPatientId(bankWithdrawalDto.getPatientId());
        queryWithdrawalDto.setStartTime(Util.getStartTimeOfWeek());
        queryWithdrawalDto.setEndTime(Util.getEndTimeOfWeek());
        List<WithdrawalDto> list = withdrawalDao.queryWithdrawalRecordsByCondition(queryWithdrawalDto);
        if (list != null && !list.isEmpty()) {
            throw new BusinessException("本周只能提现一次");
        }

        /** step9:校验提现密码是否正确. */
        PatientDto queryPatientDto = new PatientDto();
        queryPatientDto.setId(bankWithdrawalDto.getPatientId());
        PatientDto patientDto = patientDao.queryPatientById(queryPatientDto);
        if (patientDto == null || patientDto.getWithdrawalPassword() == null
                || !patientDto.getWithdrawalPassword().equals(bankWithdrawalDto.getWithdrawalPassword())) {
            throw new BusinessException("提现密码不正确");
        }

        /** step10:校验金额是否满足提现金额. */
        if (patientDto != null && (patientDto.getBalance() == null || patientDto.getBalance() < bankWithdrawalDto.getAmount())) {
            throw new BusinessException("余额不足");
        }

        /** step11:更新用户余额. */
        if (patientDto != null) {
            patientDto.setBalance(patientDto.getBalance() - bankWithdrawalDto.getAmount());
            patientDao.updateBalance(patientDto);
        }

        /** step12:保存提现记录. */
        bankWithdrawalDto.setCreateTime(new Date());
        withdrawalDao.bankWithdrawal(bankWithdrawalDto);

        /** setp13:更新token. */
        token.getPatient().setBalance(patientDto.getBalance());

        /** step14:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("提现成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("balance", patientDto != null && patientDto.getBalance() != null ? patientDto.getBalance() : 0);
        responseDto.setDetail(detail);
        return responseDto;
    }

    /**
     * @description 获取提现规则
     * @return
     * @throws Exception
     */
    public ResponseDto queryWithdrawalRules(WithdrawalRuleDto withdrawalRuleDto) throws Exception {
        List<WithdrawalRuleDto> list = withdrawalRuleDao.queryWithdrawalRules();
        list = list != null && !list.isEmpty() ? list : new ArrayList<WithdrawalRuleDto>();
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("获取成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("list", list);
        responseDto.setDetail(detail);
        return responseDto;
    }
}