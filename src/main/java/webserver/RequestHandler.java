package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        	String line = br.readLine();
        	log.debug("RequestLine : "+line);
        	if(line == null) {
        		return;
        	}
        	
        	String[] headerInfo = line.split(" ");
        	String contentType = "text/html";
        	
        	while(!line.equals("")) {
        		line = br.readLine();
        		log.debug("RequestLine : "+line);
        		if(line.contains("text/html"))
        			contentType = "text/html";
        		else if(line.contains("text/css"))
        			contentType = "text/css";
        	}
        	
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp"+returnUrl(headerInfo[1])).toPath());
            response200Header(dos, body.length, contentType);
            responseBody(dos, body);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String type) {
    	
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+type+";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private String returnUrl(String inputInfo) {
    	log.debug("Url : "+inputInfo);
    	if(inputInfo.equals("/")) {
    		return "/index.html";
    	}
    	
    	if(inputInfo.contains("?")) {
    		String[] tokenUrl = inputInfo.split("?");
    		
    		switch(tokenUrl[0]) {
    		case "/user/create":
    			User user = null;
    			String[] tokenData = tokenUrl[1].split("&");
    			matchingData(tokenUrl[0], tokenData);
    			break;
    		}
    		return tokenUrl[0];
    	}else {
    		return inputInfo;
    	}
    }
    
    private boolean matchingData(String url, String[] tokenData) {
    	switch(url) {
    	case "":
    		break;
    	}
    	
    	for(String data : tokenData) {
    		if(data.contains("&")){
    			
    		}
    	}
    	return false;
    }
}
