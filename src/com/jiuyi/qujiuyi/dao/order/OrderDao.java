package com.jiuyi.qujiuyi.dao.order;

import java.util.List;

import com.jiuyi.qujiuyi.dto.order.OrderDto;

/**
 * @description 订单dao层接口
 * @author zhb
 * @createTime 2015年5月9日
 */
public interface OrderDao {
    /**
     * @description 创建订单
     * @param orderDto
     * @throws Exception
     */
    public void createOrder(OrderDto orderDto) throws Exception;

    /**
     * @description 查询订单列表
     * @param orderDto
     * @return
     * @throws Exception
     */
    public List<OrderDto> queryOrderList(OrderDto orderDto) throws Exception;
    
    /**
     * @description 查询订单详情
     * @param orderDto
     * @return
     * @throws Exception
     */
    public OrderDto queryOrderDetail(OrderDto orderDto) throws Exception;

    /**
     * @description 删除订单
     * @param orderDto
     * @throws Exception
     */
    public void deleteOrder(OrderDto orderDto) throws Exception;

    /**
     * @description 根据订单id查询订单
     * @param orderDto
     * @throws Exception
     */
    public OrderDto queryOrderById(OrderDto orderDto) throws Exception;

    /**
     * @description 根据订单号查询订单
     * @param orderDto
     * @throws Exception
     */
    public OrderDto queryOrderByOrderNumber(OrderDto orderDto) throws Exception;

    /**
     * @description 根据订单号查询订单
     * @param orderDto
     * @throws Exception
     */
    public void updateOrder(OrderDto orderDto) throws Exception;

    /**
     * @description 根据图文咨询id获取订单
     * @param orderDto
     * @return
     * @throws Exception
     */
    public OrderDto queryOrderByServiceId(OrderDto orderDto) throws Exception;
}