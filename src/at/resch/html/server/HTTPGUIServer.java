package at.resch.html.server;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Field;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.apache.http.util.EntityUtils;
import org.reflections.Reflections;

import at.resch.html.HTMLCompiler;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.annotations.Partial;
import at.resch.html.components.StandardErrorPage;
import at.resch.html.elements.HTMLElement;
import at.resch.html.enums.Browsers;
import at.resch.html.server.util.UserAgentParser;
import at.resch.html.test.TestPage;
import at.resch.html.test.TestServerPage;

public class HTTPGUIServer {

	private static Session session;
	
	private static final String SECURITY_TOKEN = "ASuas55f3edfjfg55hert4ykzt5b4df54hdf";
	
	static class MainHandler implements HttpRequestHandler {

		public void handle(final HttpRequest request,
				final HttpResponse response, final HttpContext context)
				throws HttpException, IOException {

			String method = request.getRequestLine().getMethod()
					.toUpperCase(Locale.ENGLISH);
			if (!method.equals("GET") && !method.equals("HEAD")
					&& !method.equals("POST")) {
				throw new MethodNotSupportedException(method
						+ " method not supported");
			}
			String target = request.getRequestLine().getUri();
			String userAgent = request.getFirstHeader("User-Agent").getValue();
			Browsers browser = UserAgentParser.parseUserAgent(userAgent);
			if (request instanceof HttpEntityEnclosingRequest) {
				HttpEntity entity = ((HttpEntityEnclosingRequest) request)
						.getEntity();
				byte[] entityContent = EntityUtils.toByteArray(entity);
				System.out.println("Incoming entity content (bytes): "
						+ entityContent.length);
			}

			String html = session.getPage(target, browser);
			response.setEntity(new StringEntity(html, ContentType.TEXT_HTML));

			response.setStatusCode(200);
		}
	}
	
	static class ControlHandler implements HttpRequestHandler {

		public void handle(final HttpRequest request,
				final HttpResponse response, final HttpContext context)
				throws HttpException, IOException {

			String method = request.getRequestLine().getMethod()
					.toUpperCase(Locale.ENGLISH);
			if (!method.equals("GET") && !method.equals("HEAD")
					&& !method.equals("POST")) {
				throw new MethodNotSupportedException(method
						+ " method not supported");
			}
			String target = request.getRequestLine().getUri();
			if (request instanceof HttpEntityEnclosingRequest) {
				HttpEntity entity = ((HttpEntityEnclosingRequest) request)
						.getEntity();
				byte[] entityContent = EntityUtils.toByteArray(entity);
				System.out.println("Incoming entity content (bytes): "
						+ entityContent.length);
			}
			System.out.println(target);
			if(target.equals("/control/shutdown/" + SECURITY_TOKEN)) {
				System.out.println("Server is going down for reboot!");
				System.exit(0);
			}
		}
	}

	private static void listAllClasses() {
		try {
			Reflections reflections = new Reflections();
			System.out.println("Scanning for Pages");
			Set<Class<? extends Object>> subTypes = reflections.getTypesAnnotatedWith(Page.class);
			for (Class<? extends Object> class1 : subTypes) {
				if(class1.isAnnotationPresent(Identifier.class)) {
					Identifier i = class1.getAnnotation(Identifier.class);
					String id = i.value();
					Session.addPage(id, class1);
				}
			}
			System.out.println(subTypes.size() + " found");
			System.out.println("Scanning for Partials");
			Set<Class<? extends Object>> subTypes1 = reflections.getTypesAnnotatedWith(Partial.class);
			for (Class<? extends Object> class1 : subTypes1) {
				if(class1.isAnnotationPresent(Identifier.class)) {
					Identifier i = class1.getAnnotation(Identifier.class);
					String id = i.value();
					Session.addPartial(id, class1);
				}
			}
			System.out.println(subTypes1.size() + " found");
			System.out.println("Scanning for Locations");
			Set<Class<? extends Object>> subTypes2 = reflections.getTypesAnnotatedWith(Location.class);
			for (Class<? extends Object> class1 : subTypes2) {
				if(class1.isAnnotationPresent(Identifier.class)) {
					Identifier i = class1.getAnnotation(Identifier.class);
					String id = i.value();
					Session.addLocatable(id, class1);
				}
			}
			System.out.println(subTypes2.size() + " found");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void start() throws Exception {
		int port = 8080;

		listAllClasses();
		
		session = Session.create();
		session.addLocatable(new TestPage());

		HttpProcessor httpproc = HttpProcessorBuilder.create()
				.add(new ResponseDate()).add(new ResponseServer("Test/1.1"))
				.add(new ResponseContent()).add(new ResponseConnControl())
				.build();

		// Set up request handlers
		UriHttpRequestHandlerMapper registry = new UriHttpRequestHandlerMapper();
		registry.register("/css/IVORY", new StyleHandler());
		registry.register("/control/*", new ControlHandler());
		registry.register("/*", new MainHandler());

		// Set up the HTTP service
		HttpService httpService = new HttpService(httpproc, registry);

		SSLServerSocketFactory sf = null;
		if (port == 8443) {
			// Initialize SSL context
			ClassLoader cl = HTTPGUIServer.class.getClassLoader();
			URL url = cl.getResource("my.keystore");
			if (url == null) {
				System.out.println("Keystore not found");
				System.exit(1);
			}
			KeyStore keystore = KeyStore.getInstance("jks");
			keystore.load(url.openStream(), "secret".toCharArray());
			KeyManagerFactory kmfactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmfactory.init(keystore, "secret".toCharArray());
			KeyManager[] keymanagers = kmfactory.getKeyManagers();
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(keymanagers, null, null);
			sf = sslcontext.getServerSocketFactory();
		}

		Thread t = new RequestListenerThread(port, httpService, sf);
		t.setDaemon(false);
		t.start();
	}

	static class RequestListenerThread extends Thread {

		private final HttpConnectionFactory<DefaultBHttpServerConnection> connFactory;
		private final ServerSocket serversocket;
		private final HttpService httpService;

		public RequestListenerThread(final int port,
				final HttpService httpService, final SSLServerSocketFactory sf)
				throws IOException {
			this.connFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
			this.serversocket = sf != null ? sf.createServerSocket(port)
					: new ServerSocket(port);
			this.httpService = httpService;
		}

		@Override
		public void run() {
			System.out.println("Listening on port "
					+ this.serversocket.getLocalPort());
			while (!Thread.interrupted()) {
				try {
					// Set up HTTP connection
					Socket socket = this.serversocket.accept();
					System.out.println("Incoming connection from "
							+ socket.getInetAddress());
					HttpServerConnection conn = this.connFactory
							.createConnection(socket);

					// Start worker thread
					Thread t = new WorkerThread(this.httpService, conn);
					t.setDaemon(true);
					t.start();
				} catch (InterruptedIOException ex) {
					break;
				} catch (IOException e) {
					System.err
							.println("I/O error initialising connection thread: "
									+ e.getMessage());
					break;
				}
			}
		}
	}

	static class WorkerThread extends Thread {

		private final HttpService httpservice;
		private final HttpServerConnection conn;

		public WorkerThread(final HttpService httpservice,
				final HttpServerConnection conn) {
			super();
			this.httpservice = httpservice;
			this.conn = conn;
		}

		@Override
		public void run() {
			// System.out.println("New connection thread");
			HttpContext context = new BasicHttpContext(null);
			try {
				while (!Thread.interrupted() && this.conn.isOpen()) {
					this.httpservice.handleRequest(this.conn, context);
				}
			} catch (ConnectionClosedException ex) {
				// System.err.println("Client closed connection");
			} catch (IOException ex) {
				System.err.println("I/O error: " + ex.getMessage());
			} catch (HttpException ex) {
				System.err.println("Unrecoverable HTTP protocol violation: "
						+ ex.getMessage());
			} finally {
				try {
					this.conn.shutdown();
				} catch (IOException ignore) {
				}
			}
		}

	}

	public static void main(String[] args) {
		try {
			start();
		} catch (BindException e) {
			try {
				CloseableHttpClient httpclient = HttpClients.createDefault();
				URI uri = new URIBuilder().setScheme("http").setHost("127.0.0.1:8080")
						.setPath("/control/shutdown/" + SECURITY_TOKEN).build();
				HttpGet httpget = new HttpGet(uri);
				httpclient.execute(httpget);
			} catch (HttpHostConnectException e1) {
				try {
					start();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			} catch (URISyntaxException | IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
