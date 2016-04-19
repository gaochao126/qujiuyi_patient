package com.jiuyi.qujiuyi.service.withdrawal;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.withdrawal.WithdrawalDto;
import com.jiuyi.qujiuyi.dto.withdrawal.WithdrawalRuleDto;

/**
 * @description 提现业务层接口
 * @author zhb
 * @createTime 2015年5月19日
 */
public interface WithdrawalService {
    /**
     * @description 支付宝提现
     * @param alipayWithdrawalDto
     * @return
     * @throws Exception
     */
    public ResponseDto alipayWithdrawal(WithdrawalDto alipayWithdrawalDto) throws Exception;

    /**
     * @description 银行卡提现
     * @param alipayWithdrawalDto
     * @return
     * @throws Exception
     */
    public ResponseDto bankWithdrawal(WithdrawalDto bankWithdrawalDto) throws Exception;

    /**
     * @description 获取提现规则
     * @return
     * @throws Exception
     */
    public ResponseDto queryWithdrawalRules(WithdrawalRuleDto withdrawalRuleDto) throws Exception;
}