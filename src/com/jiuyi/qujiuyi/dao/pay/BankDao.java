package com.jiuyi.qujiuyi.dao.pay;

import java.util.List;

import com.jiuyi.qujiuyi.dto.pay.BankDto;

/**
 * @description 实名认证dao层接口
 * @author zhb
 * @createTime 2015年4月21日
 */
public interface BankDao {
    /**
     * @description 查询银行列表
     * @throws Exception
     */
    public List<BankDto> queryBanks() throws Exception;
}