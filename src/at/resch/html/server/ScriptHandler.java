package at.resch.html.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

public class ScriptHandler implements HttpRequestHandler {

	private static String SCRIPT = "";

	public void handle(final HttpRequest request, final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {
		response.setStatusCode(200);
		StringEntity entity = new StringEntity(SCRIPT,
				ContentType.create("text/javascript"));
		response.setEntity(entity);
	}
	
	static {
		try {
			BufferedReader din = new BufferedReader(new InputStreamReader(new FileInputStream("script.js")));
			String l;
			while((l = din.readLine()) != null) {
				SCRIPT += l + "\n";
			}
			din.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
