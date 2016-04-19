package com.jiuyi.qujiuyi.common.dict;

import org.springframework.context.ApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class Constants {
    /** 常用常量. */
	public static ApplicationContext applicationContext;
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    public static final JsonParser jsonParser = new JsonParser();
    public static final int SUCCEED = 0;
    public static final String SUCCEED_DESC = "成功";
    public static final int FAILED = 1;
    public static final String FAILED_DESC = "失败";
    public static final int NOT_SIGN_IN = 2;
    public static final String NOT_SIGN_IN_DESC = "未登录";
    public static final String CMD_IS_NOT_EXIST = "接口不存在";
    public static final String DATA_ERROR = "数据异常";

    /** 订单状态. */
    public class OrderSatus {
        // 未支付
        public static final int ORDER_STATUS_0 = 0;
        // 已支付
        public static final int ORDER_STATUS_1 = 1;
        // 已过期
        public static final int ORDER_STATUS_2 = 2;
    }

    /** 订单类型. */
    public class OrderType {
        // 购买服务
        public static final int ORDER_TYPE_1 = 1;
        // 余额充值
        public static final int ORDER_TYPE_2 = 2;
    }
}