package com.jiuyi.qujiuyi.service.coupon.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.dao.coupon.CouponDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.coupon.CouponDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.coupon.CouponService;

/**
 * @description 礼券业务层实现
 * @author zhb
 * @createTime 2015年5月11日
 */
@Service
public class CouponServiceImpl implements CouponService {
	@Autowired
	private CouponDao couponDao;

	/**
	 * @description 获取礼券
	 * @param couponDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryCouponsByPatientId(CouponDto couponDto) throws Exception {
		if (couponDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		TokenDto token = CacheContainer.getToken(couponDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		if (patient.getId() == null) {
			throw new BusinessException("登录异常");
		}

		couponDto.setPatientId(patient.getId());
		List<CouponDto> list = couponDao.queryCouponsByPatientId(couponDto);

		list = list == null ? new ArrayList<CouponDto>() : list;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("page", couponDto.getPage());
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("获取礼券列表成功");
		responseDto.setDetail(map);
		return responseDto;
	}

	/**
	 * 
	 * @number			@description	查询优惠券
	 * 
	 * @param couponDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月5日
	 */
	@Override
	public ResponseDto queryCoupon(CouponDto couponDto) throws Exception {
		if (couponDto == null) {
			return new ResponseDto(1, "数据异常", null);
		}

		TokenDto token = CacheContainer.getToken(couponDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		if (patient.getId() == null) {
			return new ResponseDto(1, "登录异常", null);
		}

		if (!patient.getId().equals(couponDto.getPatientId())) {
			return new ResponseDto(2, "非本人操作", null);
		}

		List<CouponDto> list = couponDao.queryCoupons(couponDto);

		list = list == null ? new ArrayList<CouponDto>() : list;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("page", couponDto.getPage());
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("获取礼券列表成功");
		responseDto.setDetail(map);
		return responseDto;
	}

	/**
	 * 
	 * @number			@description	处理过期优惠券
	 * 
	 * @param couponDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月5日
	 */
	@Override
	public void CouponExpired() throws Exception {
		CouponDto couponDto = new CouponDto();
		couponDao.CouponExpired(couponDto);
	}
}