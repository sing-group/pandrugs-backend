package es.uvigo.ei.sing.pandrugsdb.service.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

@Consumes(MediaType.MULTIPART_FORM_DATA)
public abstract class MultipartMessageBodyReader<T> implements MessageBodyReader<T> {
	private String getName(BodyPart part) throws MessagingException {
		final String contentDisposition = part.getHeader("content-disposition")[0];

		final String[] tokens = contentDisposition.split(";");
		for (String token : tokens) {
			final String[] nameValues = token.trim().split("=");
			if ("name".equals(nameValues[0])) {
				return nameValues[1].replaceAll("\"", "");
			}
		}
		
		return null;
	}

	private byte[] toByteArray(InputStream stream) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		final byte[] buffer = new byte[1024];
		int readed;
		while ((readed = stream.read(buffer)) != -1) {
			baos.write(buffer, 0, readed);
		}
		
		return baos.toByteArray();
	}

	private MimeMultipart createMimeMultipart(
		MultivaluedMap<String, String> httpHeaders, InputStream entityStream
	) throws MessagingException, IOException {
		if (httpHeaders.containsKey("Content-type")) {
			final String contentType = httpHeaders.getFirst("Content-type");
			
			return new MimeMultipart(new ByteArrayDataSource(entityStream, contentType));
		} else {
			throw new IllegalArgumentException("No Content-type found");
			
		}
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		try {
			return Class.forName(genericType.getTypeName()).isAssignableFrom(type);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
		MultivaluedMap<String, String> httpHeaders, InputStream entityStream
	) throws IOException, WebApplicationException {
		try {
			this.init();
			
			final MimeMultipart mimemultipart = createMimeMultipart(httpHeaders, entityStream);
			for (int i = 0; i < mimemultipart.getCount(); i++) {
				final BodyPart p = mimemultipart.getBodyPart(i);
				final String name = getName(p);

				this.add(name, toByteArray(p.getInputStream()));
			}

			return this.build();
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract void init();

	protected abstract void add(String name, byte[] bs);

	protected abstract T build();
}
