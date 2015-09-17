package com.axmexa.gxtapp.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.mozilla.universalchardet.UniversalDetector;

import com.axmexa.gxtapp.client.GxtappEntryPoint;
import com.axmexa.gxtapp.client.service.FileUploadService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FileUploadServiceImpl extends RemoteServiceServlet implements
		FileUploadService {
	
	private static final Logger logger = Logger.getLogger(FileUploadServiceImpl.class.getSimpleName());
	private static final List<String> charsets = Arrays.asList("utf-8", "windows-1251");

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(ServletFileUpload.isMultipartContent(req)){
			File tempFile = null;
            try {
                FileItemFactory fileItemFactory = new DiskFileItemFactory();
                ServletFileUpload uploadHandlr = new ServletFileUpload(fileItemFactory);
                List<FileItem> uploadItems = uploadHandlr.parseRequest(req);

                for (FileItem fileItem : uploadItems) {
                    if(!fileItem.isFormField()){
                    	
                    	if (fileItem.getSize() > 500*1024)  throw new Exception(GxtappEntryPoint.FILE_IS_LARGE);
                    	
                    	
//                    	Charset detectCharset = detectCharset(tempFile);
                    	String detectCharset = detectCharset(fileItem);
						if (null == detectCharset || !charsets.contains(detectCharset.toLowerCase())){
                    		throw new Exception(GxtappEntryPoint.FILE_IS_WRONG_CHARSET);
                    	}
						tempFile = storeFile(fileItem);
                    	logger.warning("string: " + fileItem.getString(detectCharset));
						String storeFilePath = tempFile.getCanonicalPath();
                    	resp.setContentType("text/html");
                    	resp.getWriter().append(storeFilePath);
                    	resp.getWriter().append("=");  // to splitting params
                    	resp.getWriter().append(detectCharset.toString());
                    	resp.getWriter().close();
                    }
                }
            } catch (Exception ex){
            	if (null != tempFile){
            		if (tempFile.exists()) {
            			tempFile.delete();
            		}
            	}
				logger.log(Level.WARNING, ex.getMessage(), ex);
                resp.getWriter().write(ex.getMessage());
            }
        }
	}

	private String detectCharset(FileItem fileItem) throws IOException {

		InputStream inputStream = fileItem.getInputStream();
		byte[] buf = new byte[4096];
		UniversalDetector detector = new UniversalDetector(null);
		int nread;
		while ((nread = inputStream.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		
		detector.dataEnd();

		
		String encoding = detector.getDetectedCharset();
		detector.reset();
		
		return encoding;
	}

	private File storeFile(FileItem fileItem) throws IOException, Exception {
			File temp = File.createTempFile("temp", ".txt");
			fileItem.write(temp);
			return temp;
	}
	
			
}
