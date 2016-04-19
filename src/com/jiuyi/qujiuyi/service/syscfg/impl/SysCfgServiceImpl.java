package com.jiuyi.qujiuyi.service.syscfg.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.dao.syscfg.SysCfgDao;
import com.jiuyi.qujiuyi.dto.BaseDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.syscfg.SysCfgDto;
import com.jiuyi.qujiuyi.service.syscfg.SysCfgService;

/**
 * @description 系统配置业务层实现
 * @author zhb
 * @createTime 2015年5月6日
 */
@Service
public class SysCfgServiceImpl implements SysCfgService {
    @Autowired
    private SysCfgDao sysCfgDao;

    /**
     * @description 获取所有系统配置
     * @return
     * @throws Exception
     */
    @Override
	public List<SysCfgDto> getAllSysCfg() throws Exception {
        return sysCfgDao.getAllSysCfg();
    }

    /**
     * @description 获取APP版本信息
     * @param dto
     * @return
     * @throws Exception
     */
    @Override
	public ResponseDto queryVersion(BaseDto dto) throws Exception {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("成功获取版本信息");
        Map<String, Object> detail = new HashMap<String, Object>();
        responseDto.setDetail(detail);
        if (dto.getDeviceType() == 3) {
            detail.put("version", SysCfg.getInt("patient.android.version"));
			detail.put("forceUpdate", SysCfg.getString("patient.app.android.forceUpdate"));
        } else if (dto.getDeviceType() == 4) {
            detail.put("version", SysCfg.getInt("patient.ios.version"));
        }
        detail.put("downloadUrl", SysCfg.getString("patient.app.downloadUrl"));
        detail.put("twoDimensionCode", SysCfg.getString("patient.app.twoDimensionCode"));
        return responseDto;
    }

    /**
     * @description 关于我们
     * @param dto
     * @return
     * @throws Exception
     */
    @Override
	public ResponseDto queryAboutMe(BaseDto dto) throws Exception {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("获取成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        responseDto.setDetail(detail);
        detail.put("logoUrl", SysCfg.getString("patient.qujiuyi.logoUrl"));
        if (dto.getDeviceType() == 3) {
            detail.put("version", SysCfg.getInt("patient.android.version"));
        } else if (dto.getDeviceType() == 4) {
            detail.put("version", SysCfg.getInt("patient.ios.version"));
        }
        detail.put("copyright", "Copyright © 2015");
        detail.put("company", "重庆玖壹健康管理有限公司");
        detail.put("userProtocol", SysCfg.getString("patient.protocol"));
        detail.put("officialWebsite", "http://www.51791.com");
        return responseDto;
    }

    /**
     * @description 获取帮助页面地址
     * @param dto
     * @return
     * @throws Exception
     */
    @Override
	public ResponseDto queryHelpUrl(BaseDto dto) throws Exception {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("获取成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        responseDto.setDetail(detail);
        detail.put("helpUrl", SysCfg.getString("patient.helpUrl"));
        return responseDto;
    }

    /**
     * @description 获取服务开关状态
     * @param dto
     * @return
     * @throws Exception
     */
    @Override
	public ResponseDto getServiceSwitchStatus(BaseDto dto) throws Exception {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("获取成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        responseDto.setDetail(detail);
        detail.put("consultSwitchStatus", SysCfg.getString("consult.switch.status"));
        detail.put("pushSwitchStatus", SysCfg.getString("push.switch.status"));
        return responseDto;
    }
}