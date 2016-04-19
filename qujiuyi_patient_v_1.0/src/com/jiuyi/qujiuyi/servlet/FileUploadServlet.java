package com.jiuyi.qujiuyi.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jspsmart.upload.File;
import com.jspsmart.upload.Files;
import com.jspsmart.upload.SmartUpload;

/**
 * @description 文件上传
 * @author zhb
 * @createTime 2015年5月26日
 */
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = -7020454770786546065L;
    private final static Logger logger = Logger.getLogger(FileUploadServlet.class);

    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = null;
        try {
            req.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");

            // 登录验证
            ResponseDto responseDto = new ResponseDto();
            String token = req.getParameter("token");
            TokenDto tokenDto = Util.isNotEmpty(token) ? CacheContainer.getToken(token) : null;
            if (tokenDto != null && tokenDto.getPatient() != null) {
                tokenDto.setUpdateTime(System.currentTimeMillis());
            } else {
                responseDto.setResultCode(2);
                responseDto.setResultDesc("未登录");
                out = resp.getWriter();
                out.print(Constants.gson.toJson(responseDto));
                logger.info("FileUploadServlet.service#response data:" + Constants.gson.toJson(responseDto));
                out.flush();
                out.close();
                return;
            }

            // 新建一个SmartUpload对象
            SmartUpload su = new SmartUpload();

			PageContext pageContext = JspFactory.getDefaultFactory().getPageContext(this, req, resp, "", true, 8192, true);

            // 上传初始化
            su.initialize(pageContext);

            // 限制每个上传文件上传限制
            su.setMaxFileSize(SysCfg.getInt("fileUpload.maxFileSize"));

            // 限制总上传文件的长度
            su.setTotalMaxFileSize(SysCfg.getInt("fileUpload.totalMaxFileSize"));

            // 设定允许上传的文件
            su.setAllowedFilesList(SysCfg.getString("fileUpload.allowedFilesList"));

            // 设定禁止上传的文件
            su.setDeniedFilesList(SysCfg.getString("fileUpload.deniedFilesList"));

            // 上传文件
            su.upload();
            List<String> list = new ArrayList<String>();
            String type = req.getParameter("type");
            String filePath = null;
            if ("1".equals(type)) {
                filePath = SysCfg.getString("head.fileUploadPath") + "/";
            } else if ("2".equals(type)) {
                filePath = SysCfg.getString("auth.fileUploadPath") + "/";
            } else if ("3".equals(type)) {
                filePath = SysCfg.getString("symptoms.fileUploadPath") + "/";
            }
            Files files = su.getFiles();
            if (files == null || files.getCount() == 0) {
                responseDto.setResultDesc("上件文件不能为空");
                responseDto.setResultCode(1);
                Map<String, Object> detail = new HashMap<String, Object>();
                detail.put("list", list);
                responseDto.setDetail(detail);
                out = resp.getWriter();
                out.print(Constants.gson.toJson(responseDto));
                logger.info("FileUploadServlet.service#response data:" + Constants.gson.toJson(responseDto));
                out.flush();
                out.close();
                return;
            }
            for (int i = 0; i < files.getCount(); i++) {
                File file = files.getFile(i);
                String fileName = null;
                PatientDto patient = tokenDto.getPatient();
                if ("1".equals(type)) {
                    fileName = "head_" + patient.getId() + "." + file.getFileExt();
                } else if ("2".equals(type)) {
                    fileName = "auth_" + patient.getId() + "_" + Util.getUniqueSn() + "." + file.getFileExt();
                } else if ("3".equals(type)) {
                    fileName = "symptoms_" + patient.getId() + "_" + Util.getUniqueSn() + "." + file.getFileExt();
                }
                file.saveAs(filePath + fileName, SmartUpload.SAVE_AUTO);
                list.add(fileName);
            }
            responseDto.setResultDesc("上传成功");
            Map<String, Object> detail = new HashMap<String, Object>();
            detail.put("list", list);
            responseDto.setDetail(detail);

            out = resp.getWriter();
            out.print(Constants.gson.toJson(responseDto));
			out.flush();
			out.close();
        } catch (Exception e) {
            ResponseDto responseDto = new ResponseDto();
            responseDto.setResultCode(1);
            responseDto.setResultDesc("上传失败");
            out = resp.getWriter();
            out.print(Constants.gson.toJson(responseDto));
            logger.info("FileUploadServlet.service#response data:" + Constants.gson.toJson(responseDto));
            out.flush();
            out.close();
        }
    }
}