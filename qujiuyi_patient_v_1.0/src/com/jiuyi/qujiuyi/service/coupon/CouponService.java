package com.jiuyi.qujiuyi.service.coupon;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.coupon.CouponDto;

/**
 * @description 礼券service层接口
 * @author zhb
 * @createTime 2015年5月11日
 */
public interface CouponService {
	/**
	 * @description 获取礼券
	 * @param couponDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto queryCouponsByPatientId(CouponDto couponDto) throws Exception;

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
	public ResponseDto queryCoupon(CouponDto couponDto) throws Exception;

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
	public void CouponExpired() throws Exception;
}