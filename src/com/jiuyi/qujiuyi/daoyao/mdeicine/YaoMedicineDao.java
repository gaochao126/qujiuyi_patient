package com.jiuyi.qujiuyi.daoyao.mdeicine;

import java.util.List;

import com.jiuyi.qujiuyi.dto.medicine.YaoMedicineDto;
import com.jiuyi.qujiuyi.dto.medicine.YaoMedicineFormatDto;

/**
 * @author superb @Date 2015年12月16日
 * 
 * @Description
 *
 * @Copyright 2015 重庆柒玖壹健康管理有限公司
 */
public interface YaoMedicineDao {
	/**
	 * 
	 * @number 1 @description 根据药品ID查询药品详情
	 * 
	 * @param yaoMedicineDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月16日
	 */
	public List<YaoMedicineDto> queryMedicineById(YaoMedicineDto yaoMedicineDto) throws Exception;

	/**
	 * 
	 * @number 2 @description 根据规格ID查询规格对象
	 * 
	 * @param yaoMedicineFormatDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月16日
	 */
	public YaoMedicineFormatDto queryMedicineFormatById(YaoMedicineFormatDto yaoMedicineFormatDto) throws Exception;

	/**
	 * 
	 * @number 3 @description 根据规格ID集合查询规格集合
	 * 
	 * @param medicines
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月16日
	 */
	public List<YaoMedicineFormatDto> queryFormatListByFormatIds(List<String> formatIds) throws Exception;

	/**
	 * 
	 * @number 4 @description 根据规格ID集合查询药品信息集合
	 * 
	 * @param formatIds
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月17日
	 */
	public List<YaoMedicineFormatDto> queryMedicineListByFormatId(List<String> formatIds) throws Exception;

}
