package at.resch.html.test.auth;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import at.resch.html.annotations.CustomHandler;

@CustomHandler("/auth/*")
public class AuthHandler implements HttpRequestHandler {
	public void handle(final HttpRequest request, final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {

		String method = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);
		if (!method.equals("GET") && !method.equals("HEAD")
				&& !method.equals("POST")) {
			throw new MethodNotSupportedException(method
					+ " method not supported");
		}

		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntity entity = ((HttpEntityEnclosingRequest) request)
					.getEntity();
			byte[] entityContent = EntityUtils.toByteArray(entity);
			System.out.println("Incoming entity content (bytes): "
					+ entityContent.length);
		}
		String target = request.getRequestLine().getUri();
		//System.out.println(target);
		String content = "";

		if (target.contains("/")) {
			if (target.startsWith("/auth/t/")) {
				String uinfo = target.substring(target.lastIndexOf("/") + 1);
				String uname = uinfo.split(";")[0];
				String token = uinfo.split(";")[1];
				try {
					content = DBBackend.authenticateToken(uname, token);
				} catch (SQLException e) {
					try {
						DBBackend.init();
						content = DBBackend.authenticateToken(uname, token);
					} catch (SQLException e1) {
						e.printStackTrace();
						String content_ = "not found";
						StringEntity entity_ = new StringEntity(content_,
								ContentType.parse("text/json"));
						response.setEntity(entity_);
						return;
					}
				}
				StringEntity entity = new StringEntity(content,
						ContentType.TEXT_HTML);
				response.setEntity(entity);
				response.setStatusCode(200);
				return;
			} else {
				String uname = target.split("/")[2];
				BigInteger[] keys = null;
				try {
					DBBackend.init();
					keys = UserServices.generateKeys(DBBackend
							.getPassword(uname));
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				try {
					if (target.endsWith("1")) {
						content = UserServices.encrypt(
								DBBackend.getUserInfo(uname), keys);
						StringEntity entity = new StringEntity(content,
								ContentType.TEXT_HTML);
						response.setEntity(entity);
						response.setStatusCode(200);
						return;
					}
				} catch (SQLException e) {
				}
			}
		}

		String json = "not found";
		StringEntity entity = new StringEntity(json,
				ContentType.APPLICATION_JSON);
		response.setEntity(entity);
		response.setStatusCode(200);
	}

}
