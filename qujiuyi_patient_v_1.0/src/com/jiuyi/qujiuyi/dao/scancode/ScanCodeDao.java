package com.jiuyi.qujiuyi.dao.scancode;

import java.util.List;

import com.jiuyi.qujiuyi.dto.scancode.ScanCodeDto;

public interface ScanCodeDao {
	/**
	 * 1.添加运营人员工作量记录(关注微信)
	 * @param scanCodeDto
	 * @throws Exception
	 */
	public void insertWorkLoad(ScanCodeDto scanCodeDto) throws Exception;
	
	/**
	 * 2.删除运营人员工作量记录
	 * @param scanCodeDto
	 * @throws Exception
	 */
	public void deleteWorkLoad(ScanCodeDto scanCodeDto) throws Exception;
	
	/**
	 * 3.查询工作量
	 * @param scanCodeDto
	 * @return
	 * @throws Exception
	 */
	public List<ScanCodeDto> queryWorkLoad(ScanCodeDto scanCodeDto)throws Exception;
}
 