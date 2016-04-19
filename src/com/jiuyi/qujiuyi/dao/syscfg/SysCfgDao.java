package com.jiuyi.qujiuyi.dao.syscfg;

import java.util.List;

import com.jiuyi.qujiuyi.dto.syscfg.SysCfgDto;

/**
 * @description 系统配置dao层接口
 * @author zhb
 * @createTime 2015年5月6日
 */
public interface SysCfgDao {
    /**
     * @description 获取所有系统配置
     * @return
     * @throws Exception
     */
    public List<SysCfgDto> getAllSysCfg() throws Exception;
}