package at.resch.html.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.util.Locale;
import java.util.Set;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.MethodNotSupportedException;
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
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.reflections.Reflections;

import at.resch.html.annotations.CustomHandler;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.annotations.Partial;
import at.resch.html.enums.Browsers;
import at.resch.html.server.util.UserAgentParser;
import at.resch.html.test.TestPage;

public class HTTPGUIServer {
	
	
	private static final Logger log = Logger.getLogger(HTTPGUIServer.class);
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

			String html = Session.create().getPage(target, browser);
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
			}
			if(target.equals("/control/shutdown/" + SECURITY_TOKEN)) {
				log.warn("Server is going down for reboot");
				System.exit(0);
			}
		}
	}
	
	static class ActionHandler implements HttpRequestHandler {

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
			StringEntity str_entity = null;
			if (request instanceof HttpEntityEnclosingRequest) {
				HttpEntity entity = ((HttpEntityEnclosingRequest) request)
						.getEntity();
				byte[] entityContent = EntityUtils.toByteArray(entity);
				str_entity = new StringEntity(new String(entityContent));
			}
			if(target.equals("/action/get/list/")) {
				System.out.println("Transmitting Action List for Session " + Session.getCurrent().get("client.address"));
				log.info("Transmitting Action List for Session " + Session.getCurrent().get("client.address"));
				String json = Session.getCurrent().getActionManager().getActions();
				response.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
				response.setStatusCode(200);
				return;
			} else if (target.startsWith("/action/invoke/")){
				BufferedReader in = new BufferedReader(new InputStreamReader(str_entity.getContent()));
				String action = in.readLine();
				String json = Session.getCurrent().getActionManager().invokeAction(action, browser).getJSON();
				response.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
				response.setStatusCode(200);
				return;
			}
		}
	}

	private static void listAllClasses() {
		try {
			Reflections reflections = new Reflections();
			log.info("Scanning for HTML pages");
			Set<Class<? extends Object>> subTypes = reflections.getTypesAnnotatedWith(Page.class);
			for (Class<? extends Object> class1 : subTypes) {
				if(class1.isAnnotationPresent(Identifier.class)) {
					Identifier i = class1.getAnnotation(Identifier.class);
					String id = i.value();
					Session.addPage(id, class1);
				}
			}
			log.info("Found " + subTypes.size() + " Pages");
			log.info("Scanning for HTML partials");
			Set<Class<? extends Object>> subTypes1 = reflections.getTypesAnnotatedWith(Partial.class);
			for (Class<? extends Object> class1 : subTypes1) {
				if(class1.isAnnotationPresent(Identifier.class)) {
					Identifier i = class1.getAnnotation(Identifier.class);
					String id = i.value();
					Session.addPartial(id, class1);
				}
			}
			log.info("Found " + subTypes1.size() + " Partials");
			log.info("Scanning HTML document tree");
			Set<Class<? extends Object>> subTypes2 = reflections.getTypesAnnotatedWith(Location.class);
			for (Class<? extends Object> class1 : subTypes2) {
				if(class1.isAnnotationPresent(Identifier.class)) {
					Identifier i = class1.getAnnotation(Identifier.class);
					String id = i.value();
					Session.addLocatable(id, class1);
				}
			}
			log.info("Found " + subTypes2.size() + " Locations");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void addCustomHandlers(UriHttpRequestHandlerMapper registry) {
		Reflections reflections = new Reflections();
		log.info("Scanning for Custom Handlers");
		Set<Class<? extends Object>> subTypes2 = reflections.getTypesAnnotatedWith(CustomHandler.class);
		for (Class<? extends Object> class1 : subTypes2) {
			if(class1.isAnnotationPresent(CustomHandler.class)) {
				try {
					CustomHandler i = class1.getAnnotation(CustomHandler.class);
					String id = i.value();
					Object o = class1.newInstance();
					if(o instanceof HttpRequestHandler)
						registry.register(id, (HttpRequestHandler) o);
				} catch (InstantiationException | IllegalAccessException e) {
					log.error("Error while adding Custom Handler", e);
				}
			}
		}
		log.info("Found " + subTypes2.size() + " CustomHandlers");
	}

	public static void start() throws Exception {
		int port = 8080;

		Session session = Session.getCurrent();
		session.addLocatable(new TestPage());

		HttpProcessor httpproc = HttpProcessorBuilder.create()
				.add(new ResponseDate()).add(new ResponseServer("Test/1.1"))
				.add(new ResponseContent()).add(new ResponseConnControl())
				.build();

		// Set up request handlers
		UriHttpRequestHandlerMapper registry = new UriHttpRequestHandlerMapper();
		registry.register("/css/IVORY", new StyleHandler());
		registry.register("/control/*", new ControlHandler());
		registry.register("/action/*", new ActionHandler());
		registry.register("/script/", new ScriptHandler());
		addCustomHandlers(registry);
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
			this.setName("RequestListener");
		}

		@Override
		public void run() {
			log.info("Server running on port: " + serversocket.getLocalPort());
			while (!Thread.interrupted()) {
				try {
					// Set up HTTP connection
					Socket socket = this.serversocket.accept();
					log.info("Incoming connection from: " + socket.getInetAddress());
					HttpServerConnection conn = this.connFactory
							.createConnection(socket);

					// Start worker thread
					Thread t = new WorkerThread(this.httpService, conn);
					t.setName(socket.getInetAddress().toString());
					t.setDaemon(true);
					t.start();
				} catch (InterruptedIOException ex) {
					break;
				} catch (IOException e) {
					log.error("I/O Error. Server couldn't be started", e);
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
			if(!Session.exists())
				Session.create();
			HttpContext context = new BasicHttpContext(null);
			try {
				while (!Thread.interrupted() && this.conn.isOpen()) {
					this.httpservice.handleRequest(this.conn, context);
				}
			} catch (ConnectionClosedException ex) {
			} catch (IOException ex) {
				log.error("I/O error", ex);
			} catch (HttpException ex) {
				log.error("Unrecoverable HTTP Protocol violation", ex);
			} finally {
				try {
					this.conn.shutdown();
				} catch (IOException ignore) {
				}
			}
		}

	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		try {
			listAllClasses();
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
