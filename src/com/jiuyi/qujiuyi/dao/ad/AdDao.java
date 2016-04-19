package com.jiuyi.qujiuyi.dao.ad;

import java.util.List;

import com.jiuyi.qujiuyi.dto.ad.AdDto;

/**
 * @description 广告dao层接口
 * @author zhb
 * @createTime 2015年5月15日
 */
public interface AdDao {
    /**
     * @description 获取广告
     * @return
     * @throws Exception
     */
    public List<AdDto> queryAds() throws Exception;
}