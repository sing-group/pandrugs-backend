package es.uvigo.ei.sing.pandrugsdb.util;

import java.io.IOException;
import java.io.InputStream;

public class EmptyInputStream extends InputStream {
	public final static EmptyInputStream EMPTY_INPUT_STREAM = new EmptyInputStream();
	
	public static EmptyInputStream emptyInputStream() {
		return EMPTY_INPUT_STREAM;
	}
	
	@Override
	public int read() throws IOException {
		return -1;
	}
}
