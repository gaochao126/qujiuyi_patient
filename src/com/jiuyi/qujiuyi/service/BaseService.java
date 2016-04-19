package com.jiuyi.qujiuyi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.URLInvoke;
import com.jiuyi.qujiuyi.dto.Page;

/**
 * @author superb @Date 2016年1月22日
 * 
 * @Description
 *
 * @Copyright 2016 重庆柒玖壹健康管理有限公司
 */
public class BaseService {
	protected final static Logger logger = Logger.getLogger(BaseService.class);
	private final static String SUCCESS = "0";
	private final static String FAIL = "1";
	private final static String ERROR = "数据异常";

	private final static String KEY_CODE = "resultCode";
	private final static String KEY_DESC = "resultDesc";

	private List<?> dataList;

	private Object dataObj;

	private Page page;

	private String code;

	private String desc;

	private Map<String, String> params = new HashMap<String, String>();

	private int requestCount;

	public boolean isSuccess() {
		return SUCCESS.equals(this.getCode());
	}

	/**
	 * @description 组装数据
	 * @param cmd
	 * @param clazz
	 * @param url
	 * @return
	 */
	public void packageData(String cmd, Class<?> clazz, String url) throws Exception {
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("cmd", cmd);

		Map<String, Object> params = new HashMap<String, Object>();
		reqMap.put("params", params);
		params.put("page", this.getPage());
		params.putAll(this.getParams());

		String result = URLInvoke.post(url, Constants.gson.toJson(reqMap));
		logger.info("BaseService----->>>" + result);

		JsonObject json = Constants.jsonParser.parse(result).getAsJsonObject();
		this.code = json.has(KEY_CODE) ? json.get(KEY_CODE).getAsString() : FAIL;
		this.desc = json.has(KEY_DESC) ? json.get(KEY_DESC).getAsString() : ERROR;
		if (!isSuccess()) {
			return;
		}
		if (json.has("detail")) {
			JsonObject detail = json.getAsJsonObject("detail");
			if (detail.has("page")) {
				Page page = Constants.gson.fromJson(detail.get("page"), Page.class);
				this.setPage(page);
			}
			if (detail.has("list")) {
				JsonArray ja = detail.get("list").getAsJsonArray();
				if (!ja.isJsonNull() && ja.size() > 0) {
					List<Object> dataList = new ArrayList<Object>();
					for (int i = 0; i < ja.size(); i++) {
						dataList.add(Constants.gson.fromJson(ja.get(i), clazz));
					}
					this.setDataList(dataList);
				}
			}
			this.setDataObj(Constants.gson.fromJson(detail, clazz));
		}
	}

	public List<?> getDataList() {
		return dataList;
	}

	public void setDataList(List<?> dataList) {
		this.dataList = dataList;
	}

	public Object getDataObj() {
		return dataObj;
	}

	public void setDataObj(Object dataObj) {
		this.dataObj = dataObj;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public int getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(int requestCount) {
		this.requestCount = requestCount;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
