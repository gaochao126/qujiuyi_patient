package com.jiuyi.qujiuyi.common.util;

import org.apache.log4j.Logger;

public class Loggers {

	private static final Logger logger = Logger.getLogger("qujiuyi_patient_log");

	public static Logger logger() {
		return logger;
	}

}
