package com.jiuyi.qujiuyi.service.order;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.order.OrderDto;

/**
 * @description 订单业务层接口
 * @author zhb
 * @createTime 2015年5月9日
 */
public interface OrderService {
    /**
     * @description 创建订单
     * @param orderDto
     * @return
     * @throws Exception
     */
    public ResponseDto createOrder(OrderDto orderDto) throws Exception;

    /**
     * @description 删除订单
     * @param orderDto
     * @return
     * @throws Exception
     */
    public ResponseDto deleteOrder(OrderDto orderDto) throws Exception;

    /**
     * @description 查询订单列表
     * @param orderDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryOrderList(OrderDto orderDto) throws Exception;

    /**
     * @description 查询订单详情
     * @param orderDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryOrderDetail(OrderDto orderDto) throws Exception;

    /**
     * @description 判断订单是否有效
     * @param orderDto
     * @return
     * @throws Exception
     */
    public ResponseDto isValidOrder(OrderDto orderDto) throws Exception;
}