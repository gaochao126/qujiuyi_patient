package com.jiuyi.qujiuyi.dao.withdrawal;

import java.util.List;

import com.jiuyi.qujiuyi.dto.withdrawal.WithdrawalRuleDto;

public interface WithdrawalRuleDao {
    /**
     * @description 获取提现规则
     * @return
     * @throws Exception
     */
    public List<WithdrawalRuleDto> queryWithdrawalRules() throws Exception;
}