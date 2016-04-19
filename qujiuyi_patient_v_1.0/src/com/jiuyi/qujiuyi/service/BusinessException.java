package com.jiuyi.qujiuyi.service;

/**
 * @author zhb
 * @date 2015年3月22日
 */
public class BusinessException extends Exception {
	private static final long serialVersionUID = 7524075320515958506L;

	public BusinessException(String message) {
        super(message);
	}
}