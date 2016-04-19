package com.jiuyi.qujiuyi.dao.consult;

import java.util.List;

import com.jiuyi.qujiuyi.dto.chat.ChatDto;
import com.jiuyi.qujiuyi.dto.consult.ConsultDto;

/**
 * @description 患者咨询dao层接口
 * @author zhb
 * @createTime 2015年4月28日
 */
public interface ConsultDao {
	/**
	 * @description 获取我的图文咨询
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	public List<ConsultDto> queryMyConsult(ConsultDto consultDto) throws Exception;

	/**
	 * @description 获取我的免费图文咨询
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	public List<ConsultDto> queryMyFreeConsult(ConsultDto consultDto) throws Exception;

	/**
	 * @description 获取进行中的问医生
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	public List<ConsultDto> queryMyConsulting(ConsultDto consultDto) throws Exception;

	/**
	 * @description 创建咨询
	 * @param consultDto
	 * @throws Exception
	 */
	public void createMyConsult(ConsultDto consultDto) throws Exception;

	/**
	 * @description 根据id查询咨询
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	public ConsultDto queryConsultById(ConsultDto consultDto) throws Exception;

	/**
	 * @description 评价咨询
	 * @param consultDto
	 * @throws Exception
	 */
	public void evaluateMyConsult(ConsultDto consultDto) throws Exception;

	/**
	 * @description 评价咨询
	 * @param consultDto
	 * @throws Exception
	 */
	public void updatePayStatus(ConsultDto consultDto) throws Exception;

	/**
	 * @description 根据医生获取医生最好的那条评价
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	public ConsultDto queryBestEvaluationByDoctorId(ConsultDto consultDto) throws Exception;

	/**
	 * @description 根据医生id获取评价列表
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	public List<ConsultDto> queryEvaluationsByDoctorId(ConsultDto consultDto) throws Exception;

	/**
	 * @description 删除图文咨询
	 * @param consultDto
	 * @throws Exception
	 */
	public void deleteConsult(ConsultDto consultDto) throws Exception;
	
	/**
	 * 删除医生为接收的免费图文咨询
	 * @param consultDto
	 * @throws Exception
	 */
	public void deleteFreeConsult(ConsultDto consultDto) throws Exception;

	/**
	 * @description 取消图文咨询
	 * @param consultDto
	 * @throws Exception
	 */
	public void cancelConsult(ConsultDto consultDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 根据服务ID查询离线消息数量
	 * 
	 * @param chatDto
	 * @return
	 * @throws Exception
	 * @Date 2015年11月12日
	 */
	public Integer queryOfflineMessageCountBySrviceId(ChatDto chatDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 查询用户离线消息条数
	 * 
	 * @param chatDto
	 * @return
	 * @throws Exception
	 * @Date 2015年11月12日
	 */
	public List<ChatDto> queryNoReadMessageCount(ChatDto chatDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 查询未结束的咨询
	 * 
	 * @param consultDto
	 * @return
	 * @throws Exception
	 * @Date 2015年11月26日
	 */
	public List<ConsultDto> queryConsultNoEnd(ConsultDto consultDto) throws Exception;

	/**
	 * @description 更新咨询状态
	 * @param consultDto
	 * @throws Exception
	 */
	public void updateConsultStatus(ConsultDto consultDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 查询指定ServiceId的医生聊天记录
	 * 
	 * @param chatDto
	 * @return
	 * @throws Exception
	 * @Date 2015年12月1日
	 */
	public List<ChatDto> queryChatBySendTypeAndServiceId(ChatDto chatDto) throws Exception;

	/**
	 * @description 获取未完成的问医生
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	public List<ConsultDto> queryMyConsultNoEnd(ConsultDto consultDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 查询已经过期的咨询
	 * 
	 * @param consultDto
	 * @return
	 * @throws Exception
	 * @Date 2015年12月9日
	 */
	public List<ConsultDto> queryConsultAlreadyExpired(ConsultDto consultDto) throws Exception;

	/**
	 * 
	 * @number @description 更改指定服务ID的未读消息为已读
	 * 
	 *
	 * @Date 2015年12月15日
	 */
	public void updateReadStatusByServiceId(ChatDto chatDto) throws Exception;

	/**
	 * 
	 * @number @description 更新过期咨询服务的未读消息为已读
	 * 
	 * @throws Exception
	 *
	 * @Date 2015年12月15日
	 */
	public void updateReadStatusByBatchServiceId(ConsultDto consultDto) throws Exception;

	/**
	 * 
	 * @number			@description	查询医生12小时后未接受的付费咨询
	 * 
	 * @param consultDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月29日
	 */
	public List<ConsultDto> queryConsultDoctorNoAccept(ConsultDto consultDto) throws Exception;

	/**
	 * 
	 * @number			@description	退款成功后修改咨询
	 * 
	 * @param consultDto
	 * @throws Exception
	 *
	 * @Date 2016年1月29日
	 */
	public void updateConsultRefundSuccess(ConsultDto consultDto) throws Exception;
}