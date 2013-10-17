package tcss558.homework1.tcp;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class TCPSpellingWriter implements AutoCloseable {
	OutputStreamWriter writer;

	public TCPSpellingWriter(OutputStream out) {
		writer = new OutputStreamWriter(new BufferedOutputStream(out), Charset.forName("US-ASCII"));
	}

	public void writeLine() throws IOException{
		writeLine("");
	}
	
	public void writeLine(String str) throws IOException {
		writer.write(str + "\n");
		writer.flush();
	}

	@Override
	public void close() {
		try {
			writer.close();
		} catch (Exception e) {
		}
	}

}
