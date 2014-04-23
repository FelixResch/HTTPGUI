package at.resch.html.extensions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.reflections.Reflections;
import org.tukaani.xz.XZInputStream;

import at.resch.html.annotations.CustomHandler;
import at.resch.html.annotations.ResourcePack;
import at.resch.html.server.Session;

@CustomHandler("/res/*")
public class ResourceHandler implements HttpRequestHandler {

	private HashMap<String, byte[]> contents = new HashMap<>();

	public ResourceHandler() {
		Reflections reflections = new Reflections();
		Set<Class<?>> classes = reflections
				.getTypesAnnotatedWith(ResourcePack.class);
		for (Class<?> c : classes) {
			ResourcePack pack = c.getAnnotation(ResourcePack.class);
			try {
				InputStream in = new FileInputStream(pack.value());
				XZInputStream xzin = new XZInputStream(in);
				TarArchiveInputStream tin = new TarArchiveInputStream(xzin);
				TarArchiveEntry entry = null;
				HashMap<String, String> mappings = new HashMap<>();
				String prepend = "";
				while ((entry = (TarArchiveEntry) tin.getNextEntry()) != null) {
					Session.logger.debug(entry.getName());
					if (entry.getName().equalsIgnoreCase("_resource.pack")) {
						byte[] content = new byte[(int) entry.getSize()];
						tin.read(content, 0, content.length);
						String s = new String(content).replace("\r", "");
						String[] lines = s.split("\n");
						for (String line : lines) {
							Session.logger.debug("Line: "  + line);
							if (line.startsWith("#")) {
								Session.logger.info("Loading Resource Pack: "
										+ line.substring(1));
							} else if (line.startsWith("@")) {
								prepend = line.substring(1);
								prepend = prepend.endsWith("/") ? prepend
										.substring(0, prepend.length() - 1)
										: prepend;
							} else {
								if (line.contains(":")) {
									String from, to;
									from = line.split(":")[0];
									to = line.split(":")[1];
									Session.logger.debug("Linking: " + from  + " => " + to);
									mappings.put(from, to);
								} else {
									Session.logger.debug("Line " + line + " doesn't contain ':'");
								}
							}
						}
						Session.logger.debug(mappings);
					} else {
						if(!mappings.containsKey(entry.getName())) {
							Session.logger.warn("Omitting orphaned Archive Entry: " + entry.getName());
							continue;
						}
						int entry_len = (int) entry.getSize();
						byte[] store = new byte[entry_len];
						tin.read(store, 0, entry_len);
						contents.put(prepend
								+ mappings.get(entry.getName()).substring(1),
								store);
					}
				}
				tin.close();
			} catch (IOException e) {
				Session.logger.fatal(
						"Couldn't read Resource Pack: " + pack.value(), e);
			}

		}
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		String method = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);
		if (!method.equals("GET") && !method.equals("HEAD")
				&& !method.equals("POST")) {
			throw new MethodNotSupportedException(method
					+ " method not supported");
		}
		String target = request.getRequestLine().getUri();
		String key = target.substring(5);
		if(contents.containsKey(key)) {
			String contentType = "text";
			if(key.endsWith("css"))
				contentType = "text/css";
			else if (key.endsWith("js"))
				contentType = "text/javascript";
			else if (key.endsWith("woff"))
				contentType = "application/x-woff";
			StringEntity entity = new StringEntity(new String(contents.get(key)), ContentType.parse(contentType));
			response.setEntity(entity);
			response.setStatusCode(200);
		} else {
			response.setStatusCode(404);
		}
	}

}
