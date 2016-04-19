package com.jiuyi.qujiuyi.service.consult;

import com.jiuyi.qujiuyi.dto.chat.ChatDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.consult.ConsultDto;

/**
 * @description 患者咨询业务层接口
 * @author zhb
 * @createTime 2015年4月28日
 */
public interface ConsultService {
	/**
	 * @description 获取我的图文咨询
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto queryMyConsult(ConsultDto consultDto) throws Exception;

	/**
	 * @description 创建咨询
	 * @param consultDto
	 * @throws Exception
	 */
	public ResponseDto createMyConsult(ConsultDto consultDto) throws Exception;

	/**
	 * @description 创建免费咨询
	 * @param consultDto
	 * @throws Exception
	 */
	public ResponseDto createFreeConsult(ConsultDto consultDto) throws Exception;

	/**
	 * @description 评价咨询
	 * @param consultDto
	 * @throws Exception
	 */
	public ResponseDto evaluateMyConsult(ConsultDto consultDto) throws Exception;

	/**
	 * @description 判断是否正在咨询此医生
	 * @param consultDto
	 * @throws Exception
	 */
	public ResponseDto isConsultingWithTheDoctor(ConsultDto consultDto) throws Exception;

	/**
	 * @description 根据医生id获取评价列表
	 * @param consultDto
	 * @throws Exception
	 */
	public ResponseDto queryEvaluationsByDoctorId(ConsultDto consultDto) throws Exception;

	/**
	 * @description 获取咨询详情
	 * @param consultDto
	 * @throws Exception
	 */
	public ResponseDto queryConsultDetail(ConsultDto consultDto) throws Exception;

	/**
	 * @description 删除图文咨询
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto deleteConsult(ConsultDto consultDto) throws Exception;

	/**
	 * @description 取消图文咨询
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto cancelConsult(ConsultDto consultDto) throws Exception;

	/**
	 * @description 获取当前图文咨询
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto getCurrentConsult(ConsultDto consultDto) throws Exception;

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
	public ResponseDto queryNoReadMessageCount(ChatDto chatDto) throws Exception;

	/**
	 * @description 更新咨询状态
	 * @param consultDto
	 * @throws Exception
	 */
	public void updateConsultStatus(ConsultDto consultDto) throws Exception;

	/**
	 * 
	 * @param consultDto
	 * @throws Exception
	 */
	public void updateFreeConsultNoRecive(ConsultDto consultDto) throws Exception;
	
	/**
	 * 
	 * @number @description 查询免费咨询当天剩余次数
	 * 
	 * @param consultDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月17日
	 */
	public ResponseDto queryFreeConsultNoUseCount(ConsultDto consultDto) throws Exception;

	/**
	 * 
	 * @number			@description	处理12小时医生未接受，咨询未开始的咨询
	 * 
	 * @param consultDto
	 * @throws Exception
	 *
	 * @Date 2016年1月29日
	 */
	public void handleConsultDoctorNoAccept() throws Exception;
}