package com.jiuyi.qujiuyi.service.syscfg;

import java.util.List;

import com.jiuyi.qujiuyi.dto.BaseDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.syscfg.SysCfgDto;

/**
 * @description 系统配置业务层接口
 * @author zhb
 * @createTime 2015年5月6日
 */
public interface SysCfgService {
    /**
     * @description 获取所有系统配置
     * @return
     * @throws Exception
     */
    public List<SysCfgDto> getAllSysCfg() throws Exception;

    /**
     * @description 获取APP版本信息
     * @param dto
     * @return
     * @throws Exception
     */
    public ResponseDto queryVersion(BaseDto dto) throws Exception;

    /**
     * @description 关于我们
     * @param dto
     * @return
     * @throws Exception
     */
    public ResponseDto queryAboutMe(BaseDto dto) throws Exception;

    /**
     * @description 获取帮助页面地址
     * @param dto
     * @return
     * @throws Exception
     */
    public ResponseDto queryHelpUrl(BaseDto dto) throws Exception;

    /**
     * @description 获取服务开关状态
     * @param dto
     * @return
     * @throws Exception
     */
    public ResponseDto getServiceSwitchStatus(BaseDto dto) throws Exception;
}