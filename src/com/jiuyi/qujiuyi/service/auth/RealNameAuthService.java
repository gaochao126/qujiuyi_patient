package com.jiuyi.qujiuyi.service.auth;

import com.jiuyi.qujiuyi.dto.auth.RealNameAuthDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;

/**
 * @description 实名认证业务层接口
 * @author zhb
 * @createTime 2015年4月21日
 */
public interface RealNameAuthService {
    /**
     * @description 实名认证提交
     * @param realNameAuthDto
     * @return
     * @throws Exception
     */
    public ResponseDto createRealNameAuth(RealNameAuthDto realNameAuthDto) throws Exception;
}