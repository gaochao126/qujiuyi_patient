package com.jiuyi.qujiuyi.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * 下载工具类
 * 
 * @author zhb
 * 
 */
public class DownloadUtils {

	/**
	 * 服务端向客户端响应文件
	 * 
	 * @param resp
	 *            the resp
	 * @param filePath
	 *            文件所在目录
	 * @param fileName
	 *            文件名
	 */
	public static void sendFile(HttpServletResponse resp, String filePath, String fileName) {

		resp.reset();
		resp.setContentType("application/force-download;charset=utf-8");
		resp.addHeader("Content-Disposition", "attachment;filename=" + fileName);

		OutputStream out = null;
		FileInputStream in = null;

		try {
			File f = new File(filePath, fileName);
			out = resp.getOutputStream();
			in = new FileInputStream(f);

			resp.addHeader("Content-Length", f.length() + "");

			byte[] b = new byte[1024];
			int i = 0;
			while ((i = in.read(b)) != -1) {
				out.write(b, 0, i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}