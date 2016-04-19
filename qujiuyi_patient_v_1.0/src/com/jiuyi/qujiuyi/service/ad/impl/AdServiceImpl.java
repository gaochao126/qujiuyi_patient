package com.jiuyi.qujiuyi.service.ad.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.dao.ad.AdDao;
import com.jiuyi.qujiuyi.dto.ad.AdDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.service.ad.AdService;

/**
 * @description 广告业务层实现
 * @author zhb
 * @createTime 2015年5月15日
 */
@Service
public class AdServiceImpl implements AdService {
    private static List<AdDto> adList;
    private static long getAdsTime;

    @Autowired
    private AdDao adDao;

    /**
     * @description 获取广告
     * @param adDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryAds(AdDto adDto) throws Exception {
        if (adList == null || System.currentTimeMillis() - getAdsTime > 2 * 60 * 1000) {
            getAdsTime = System.currentTimeMillis();
            adList = adDao.queryAds();
        }
        ResponseDto responseDto = new ResponseDto();
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("list", adList);
        responseDto.setResultDesc("广告获取成功");
        responseDto.setDetail(detail);
        return responseDto;
    }
}