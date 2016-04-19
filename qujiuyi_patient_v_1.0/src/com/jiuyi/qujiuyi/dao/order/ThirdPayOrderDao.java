package com.jiuyi.qujiuyi.dao.order;

import java.util.List;

import com.jiuyi.qujiuyi.dto.order.ThirdPayOrderDto;

/**
 * @description 第三方支付订单dao层
 * @author zhb
 * @createTime 2015年8月25日
 */
public interface ThirdPayOrderDao {
	/**
	 * @description 创建微信支付订单
	 * @param thirdPayOrderDto
	 * @throws Exception
	 */
	public void createWeixinPayOrder(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * @description 更新微信订单
	 * @param thirdPayOrderDto
	 * @throws Exception
	 */
	public void updateWeixinOrder(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * @description 根据订单编号获取微信订单
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 */
	public ThirdPayOrderDto getWeixinOrderByOutTradeNo(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * @description 根据订单号查询订单详情
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 */
	public ThirdPayOrderDto queryOrderDetailByOutTradeNo(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 查询过期订单
	 * 
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 * @Date 2015年12月1日
	 */
	public List<ThirdPayOrderDto> queryThirdPayOrderExpired(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 更改过期的订单displayStatus状态
	 * 
	 * @throws Exception
	 * @Date 2015年12月1日
	 */
	public void updateThirdPayOrderDisplayStatus(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description
	 * 
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 * @Date 2015年12月9日
	 */
	public List<ThirdPayOrderDto> queryThirdPayOrderByPlanIdAndRelativeId(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * 
	 * @number @description 更改订单隐藏状态，根据订单编号
	 * 
	 * @param thirdPayOrderDto
	 * @throws Exception
	 *
	 * @Date 2015年12月11日
	 */
	public void updateThirdPayOrderDisplayStatusByOrderNo(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * 
	 * @number @description 根据订单编号和用户ID更改付款状态
	 * 
	 * @param thirdPayOrderDto
	 * @throws Exception
	 *
	 * @Date 2015年12月15日
	 */
	public void updatePayStatusByOutTradeNoAndPatientId(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * 
	 * @number @description 根据serviceId查询订单
	 * 
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月15日
	 */
	public ThirdPayOrderDto queryThirdPayOrderByServiceId(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * 
	 * @number @description 查询未过期并且未付款的订单
	 * 
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月28日
	 */
	public List<ThirdPayOrderDto> queryThirdOrder(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * 
	 * @number @description 1小时后删除未付款的订单
	 * 
	 * @throws Exception
	 *
	 * @Date 2016年3月2日
	 */
	public void deleteNoPayOrder() throws Exception;
}