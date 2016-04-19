package com.jiuyi.qujiuyi.dao.coupon;

import java.util.List;

import com.jiuyi.qujiuyi.dto.coupon.CouponDto;

/**
 * @description 礼券dao层接口
 * @author zhb
 * @createTime 2015年5月11日
 */
public interface CouponDao {
	/**
	 * @description 获取礼券
	 * @param couponDto
	 * @return
	 * @throws Exception
	 */
	public List<CouponDto> queryCouponsByPatientId(CouponDto couponDto) throws Exception;

	/**
	 * 
	 * @number			@description 查询优惠券
	 * 
	 * @param couponDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月5日
	 */
	public List<CouponDto> queryCoupons(CouponDto couponDto) throws Exception;

	/**
	 * @description 根据礼券id获取礼券
	 * @param couponDto
	 * @return
	 * @throws Exception
	 */
	public CouponDto queryCouponsById(CouponDto couponDto) throws Exception;

	/**
	 * @description 根据礼券使用状态
	 * @param couponDto
	 * @throws Exception
	 */
	public void updateCouponStatus(CouponDto couponDto) throws Exception;

	/**
	 * @description 创建礼卷
	 * @param couponDto
	 * @throws Exception
	 */
	public void createCoupon(CouponDto couponDto) throws Exception;

	/**
	 * 
	 * @number			@description	过期优惠券处理
	 * 
	 * @param couponDto
	 * @throws Exception
	 *
	 * @Date 2016年1月5日
	 */
	public void CouponExpired(CouponDto couponDto) throws Exception;
}