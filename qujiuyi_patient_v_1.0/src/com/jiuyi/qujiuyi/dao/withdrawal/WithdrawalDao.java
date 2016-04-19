package com.jiuyi.qujiuyi.dao.withdrawal;

import java.util.List;

import com.jiuyi.qujiuyi.dto.withdrawal.WithdrawalDto;

/**
 * @description 银行卡提现dao层接口
 * @author zhb
 * @createTime 2015年5月19日
 */
public interface WithdrawalDao {
    /**
     * @description 银行卡提现
     * @param alipayWithdrawalDto
     * @throws Exception
     */
    public void bankWithdrawal(WithdrawalDto bankWithdrawalDto) throws Exception;

    /**
     * @description 支付宝提现
     * @param alipayWithdrawalDto
     * @throws Exception
     */
    public void alipayWithdrawal(WithdrawalDto alipayWithdrawalDto) throws Exception;

    /**
     * @description 根据条件筛选提现记录
     * @param withdrawalDto
     * @return
     * @throws Exception
     */
    public List<WithdrawalDto> queryWithdrawalRecordsByCondition(WithdrawalDto withdrawalDto) throws Exception;
}