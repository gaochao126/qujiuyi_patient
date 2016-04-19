package com.jiuyi.qujiuyi.dao.prescribe;

import com.jiuyi.qujiuyi.dto.prescribe.PrescribeDetailDto;

/**
 * @description 配药明细dao层接口
 * @author zhb
 * @createTime 2015年7月9日
 */
public interface PrescribeDetailDao {
    /**
     * @description 创建配药明细
     * @param prescribeDetailDto
     * @throws Exception
     */
    public void createPrescribeDetail(PrescribeDetailDto prescribeDetailDto) throws Exception;
}