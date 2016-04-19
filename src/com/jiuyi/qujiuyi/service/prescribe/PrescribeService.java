package com.jiuyi.qujiuyi.service.prescribe;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.prescribe.PrescribeDto;

/**
 * @description 配药业务层接口
 * @author zhb
 * @createTime 2015年7月9日
 */
public interface PrescribeService {
    /**
     * @description 创建配药
     * @param prescribeDto
     * @return
     * @throws Exception
     */
    public ResponseDto createPrescribe(PrescribeDto prescribeDto) throws Exception;

    /**
     * @description 获取配药详情
     * @param prescribeDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryPrescribeDetail(PrescribeDto prescribeDto) throws Exception;

    /**
     * @description 获取患者配药列表
     * @param prescribeDto
     * @return
     * @throws Exception
     */
    public ResponseDto getPrescribeListByPatientId(PrescribeDto prescribeDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 删除配药
	 * 
	 * @param prescribeDto
	 * @return
	 * @throws Exception
	 * @Date 2015年11月19日
	 */
	public ResponseDto delPrescribe(PrescribeDto prescribeDto) throws Exception;
}