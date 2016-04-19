package com.jiuyi.qujiuyi.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.service.BusinessException;

public class ApiServlet extends HttpServlet {
    /** serialVersionUID. */
    private static final long serialVersionUID = 9109406284366625075L;
    private final static Logger logger = Logger.getLogger(ApiServlet.class);

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        /** 解析数据. */
        String reqData = getRequestContent(request);
        if (!Util.isNotEmpty(reqData)) {
            return;
        }
        logger.info("ApiServlet.doPost#requst data : " + reqData);
        JsonObject jsonObject = Constants.jsonParser.parse(reqData).getAsJsonObject();
        String cmd = jsonObject != null && jsonObject.has("cmd") ? jsonObject.get("cmd").getAsString() : null;
        String token = jsonObject != null && jsonObject.has("token") ? jsonObject.get("token").getAsString() : null;
        String deviceType = jsonObject != null && jsonObject.has("deviceType") ? jsonObject.get("deviceType").getAsString() : null;
        JsonObject params = jsonObject != null && jsonObject.has("params") ? jsonObject.get("params").getAsJsonObject() : new JsonObject();
        params.addProperty("token", token);
        params.addProperty("ipAddr", this.getIpAddr(request));
        params.addProperty("deviceType", deviceType);

        /** 校验cmd不为空. */
        if (!Util.isNotEmpty(cmd)) {
            logger.info("ApiServlet.doPost#cmd is null");
            return;
        }

        /** token 鉴权. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setCmd(cmd);
        if ("queryPersonalInfo".equals(cmd) && "6".equals(deviceType)) {
            String askPassword = jsonObject != null && jsonObject.has("askPassword") ? jsonObject.get("askPassword").getAsString() : null;
            if (!SysCfg.getString("weixin.askPassword").equals(askPassword)) {
                responseDto.setResultCode(1);
                responseDto.setResultDesc("鉴权失败");
                print(response, Constants.gson.toJson(responseDto));
                return;
            }
        } else {
			if (!auth(cmd, token, deviceType)) {
                responseDto.setResultCode(Constants.NOT_SIGN_IN);
                responseDto.setResultDesc(Constants.NOT_SIGN_IN_DESC);
                print(response, Constants.gson.toJson(responseDto));
                return;
            }
        }

        /** 获取业务方法. */
        if (CacheContainer.serviceMap.get(cmd) == null) {
            responseDto.setResultCode(Constants.FAILED);
            responseDto.setResultDesc(Constants.CMD_IS_NOT_EXIST);
            print(response, Constants.gson.toJson(responseDto));
            return;
        }
        Object obj = Constants.applicationContext.getBean(CacheContainer.serviceMap.get(cmd));
        Method[] methods = CacheContainer.serviceMap.get(cmd).getMethods();
        Method serviceMethod = null;
        for (Method m : methods) {
            if (m.getName().equals(cmd) && m.getParameterTypes().length == 1) {
                serviceMethod = m;
                break;
            }
        }
        if (serviceMethod == null) {
            logger.info("ApiServlet.doPost#service method not found");
            responseDto.setResultCode(Constants.FAILED);
            responseDto.setResultDesc(Constants.CMD_IS_NOT_EXIST);
            print(response, Constants.gson.toJson(responseDto));
            return;
        }

        /** 调用业务方法，并返回结果. */
        try {
            responseDto = (ResponseDto) serviceMethod.invoke(obj, Constants.gson.fromJson(params, serviceMethod.getParameterTypes()[0]));
            if (responseDto != null) {
                responseDto.setCmd(cmd);
            }
        } catch (Exception e) {
            responseDto.setResultCode(Constants.FAILED);
            if (e instanceof InvocationTargetException) {
                InvocationTargetException e1 = (InvocationTargetException) e;
                if (e1.getTargetException() instanceof BusinessException) {
                    responseDto.setResultDesc(e1.getTargetException().getMessage());
                    logger.info("ApiServlet.doPost#business exception,{cmd : " + cmd + "}");
                } else {
                    responseDto.setResultDesc(Constants.FAILED_DESC);
                    logger.error("ApiServlet.doPost#service interface error,{cmd : " + cmd + ", message : " + e.getMessage() + "}", e1.getTargetException());
                }
            } else {
                responseDto.setResultDesc(Constants.FAILED_DESC);
                logger.error("ApiServlet.doPost#service interface error,{cmd : " + cmd + ", message : " + e.getMessage() + "}", e);
            }
        } finally {
            print(response, Constants.gson.toJson(responseDto));
        }
    }

    /**
     * @description 获取上报数据
     * @param request
     * @return
     */
    private static String getRequestContent(HttpServletRequest request) {
        String content = "";
        try {
            InputStream in = request.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                content += new String(buffer, 0, len, "UTF-8");
            }
            in.close();
        } catch (IOException e) {
            logger.error("ApiServlet.getRequestContent#get request content failed", e);
        }
        return content;
    }

    /**
     * @description 响应服务端
     * @param response
     * @param content
     */
    private static void print(HttpServletResponse response, String content) {
        if (response == null || !Util.isNotEmpty(content)) {
            logger.debug("ApiServlet.print#response or content is null");
            return;
        }
        PrintWriter out = null;
        try {
            logger.info("ApiServlet.print#response data : " + content);
            out = response.getWriter();
            out.print(content);
        } catch (IOException e) {
            logger.error("ApiServlet.print#response error", e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    /**
     * @description token鉴权
     * @param cmd
     * @param token
     * @return
     */
	private static boolean auth(String cmd, String token, String deviceType) {
        if (CacheContainer.notAuthMap.containsKey(cmd)) {
            return true;
        }

        if (!Util.isNotEmpty(token)) {
            return false;
        }

		TokenDto tokenDto = CacheContainer.getToken(token, deviceType, true);
        if (tokenDto != null && tokenDto.getPatient() != null) {
            tokenDto.setUpdateTime(System.currentTimeMillis());
            return true;
        }

        return false;
    }

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}