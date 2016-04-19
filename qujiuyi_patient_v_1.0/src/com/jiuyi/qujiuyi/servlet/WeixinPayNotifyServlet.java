package com.jiuyi.qujiuyi.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.service.order.ThirdPayOrderService;

public class WeixinPayNotifyServlet extends HttpServlet {
    /** serialVersionUID. */
    private static final long serialVersionUID = -2747509409036263709L;

    private final static Logger logger = Logger.getLogger(WeixinPayNotifyServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String respXml = getRequestContent(request);
        try {
            Constants.applicationContext.getBean(ThirdPayOrderService.class).updateWeixinOrderByRespXml(respXml);
            logger.info("----------weixin pay success------------");
            logger.info(respXml);
        } catch (Exception e) {
            logger.error("", e);
        }
        print(response, "success");
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
            return;
        }
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(content);
        } catch (IOException e) {

        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}