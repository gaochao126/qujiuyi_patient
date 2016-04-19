package com.jiuyi.qujiuyi.service.ad;

import com.jiuyi.qujiuyi.dto.ad.AdDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;

/**
 * @description 广告业务层接口
 * @author zhb
 * @createTime 2015年5月15日
 */
public interface AdService {
    /**
     * @description 获取广告
     * @return
     * @throws Exception
     */
    public ResponseDto queryAds(AdDto adDto) throws Exception;
}