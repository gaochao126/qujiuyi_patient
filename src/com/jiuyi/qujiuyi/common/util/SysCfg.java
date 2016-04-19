package com.jiuyi.qujiuyi.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.dto.syscfg.SysCfgDto;
import com.jiuyi.qujiuyi.service.syscfg.SysCfgService;

/**
 * @description 系统配置参数读取类
 * @author zhb
 * @createTime 2015年4月25日
 */
public class SysCfg {
	// 用于存于配置参数
    private static Map<String, String> map;

	// 静态块一次性加载系统配置参数到map中
    public static void init() {
        map = new ConcurrentHashMap<String, String>();
		Properties prop = new Properties();
		InputStream in = SysCfg.class.getResourceAsStream("/syscfg.properties");
		try {
			prop.load(in);
			for (Object key : prop.keySet()) {
				map.put(key.toString(), prop.getProperty(key.toString()).trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    List<SysCfgDto> list = Constants.applicationContext.getBean(SysCfgService.class).getAllSysCfg();
                    if (list != null && !list.isEmpty()) {
                        for (SysCfgDto dto : list) {
                            map.put(dto.getConfigName(), dto.getConfigValue());
                        }
                    }
					for (String key : map.keySet()) {
						map.put(key, map.get(key).replace("{IP}", map.get("access.url")));
					}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5 * 60 * 1000);
	}

    /**
     * @description 根据参数名获取字符串参数值
     * @param key
     * @return
     */
    public static String getString(String key) {
        String value = map.get(key);
        if (value == null) {
            return value;
        }
        return value.trim();
	}

    /**
     * @description 根据参数名获取整型参数值
     * @param key
     * @return
     */
    public static int getInt(String key) {
        String value = map.get(key);
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @description 根据参数名获取整型参数值
     * @param key
     * @return
     */
    public static long getLong(String key) {
        String value = map.get(key);
        if (value == null) {
            return 0;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}