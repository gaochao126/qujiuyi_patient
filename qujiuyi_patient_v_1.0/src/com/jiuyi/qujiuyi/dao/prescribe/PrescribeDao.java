package com.jiuyi.qujiuyi.dao.prescribe;

import java.util.List;

import com.jiuyi.qujiuyi.dto.prescribe.PrescribeDto;

/**
 * @description 配药dao层接口
 * @author zhb
 * @createTime 2015年7月9日
 */
public interface PrescribeDao {
    /**
     * @description 创建配药
     * @param prescribeDto
     * @throws Exception
     */
    public void createPrescribe(PrescribeDto prescribeDto) throws Exception;

    /**
     * @description 获取配药详情
     * @param prescribeDto
     * @return
     * @throws Exception
     */
    public PrescribeDto queryPrescribeDetail(PrescribeDto prescribeDto) throws Exception;

    /**
     * @description 获取患者配药列表
     * @param prescribeDto
     * @return
     * @throws Exception
     */
    public List<PrescribeDto> getPrescribeListByPatientId(PrescribeDto prescribeDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 删除配药
	 * 
	 * @param prescribeDto
	 * @throws Exception
	 * @Date 2015年11月19日
	 */
	public void delPrescribe(PrescribeDto prescribeDto) throws Exception;
}