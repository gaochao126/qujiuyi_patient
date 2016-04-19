package com.jiuyi.qujiuyi.dao.auth;

import java.util.List;

import com.jiuyi.qujiuyi.dto.auth.RealNameAuthDto;

/**
 * @description 实名认证dao层接口
 * @author zhb
 * @createTime 2015年4月21日
 */
public interface RealNameAuthDao {
    /**
     * @description 实名认证提交
     * @param realNameAuthDto
     * @return
     * @throws Exception
     */
    public void createRealNameAuth(RealNameAuthDto realNameAuthDto) throws Exception;

    /**
     * @description 根据用户id查询正在审核中的实名认证
     * @param realNameAuthDto
     * @return
     * @throws Exception
     */
    public List<RealNameAuthDto> queryRealNameAuthingByUserId(RealNameAuthDto realNameAuthDto) throws Exception;
}