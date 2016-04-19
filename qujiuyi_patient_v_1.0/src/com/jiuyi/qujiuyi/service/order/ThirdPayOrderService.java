package com.jiuyi.qujiuyi.service.order;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.order.ThirdPayOrderDto;

/**
 * @description 第三方支付订单业务层接口
 * @author zhb
 * @createTime 2015年8月21日
 */
public interface ThirdPayOrderService {
	/**
	 * @description 创建微信支付订单
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto createWeixinPayOrder(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * @description 根据微信支付返回的XML更新微信订单状态
	 * @param respXml
	 * @throws Exception
	 */
	public void updateWeixinOrderByRespXml(String respXml) throws Exception;

	/**
	 * @description 根据订单号查询订单详情
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto queryOrderDetailByOutTradeNo(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 处理过期的挂号订单
	 * 
	 * @throws Exception
	 * @Date 2015年12月1日
	 */
	public void handleThirdPayOrder() throws Exception;

	/**
	 * 
	 * @number @description 退款申请
	 * 
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月15日
	 */
	public ResponseDto requestRefund(ThirdPayOrderDto thirdPayOrderDto) throws Exception;

	/**
	 * 
	 * @number @description 退款处理
	 * 
	 * @param thirdPayOrderDto
	 * @throws Exception
	 *
	 * @Date 2015年12月15日
	 */
	public void refund(ThirdPayOrderDto thirdPayOrderDto) throws Exception;
	
	/**
	 * 
	 * @number			@description	定时请求15分钟内订单支付情况
	 * 
	 * @throws Exception
	 *
	 * @Date 2016年1月28日
	 */
	public void reqWinxinOrderQuery() throws Exception;
	
	/**
	 * 
	 * @number			@description	删除未付款的订单（脏数据）
	 * 
	 * @throws Exception
	 *
	 * @Date 2016年3月2日
	 */
	public void deleteNoPayOrder() throws Exception;

}