package tcss558.homework1;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class SpellingInputStream extends InputStream {

	DataInputStream dataInputStream;

	public SpellingInputStream(InputStream in) {
		dataInputStream = new DataInputStream(in);
	}

	@Override
	public int read() {
		try {
			return dataInputStream.read();
		} catch (IOException e) {
			return -1;
		}
	}
	
	public String readNullTerminatedString(){
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		int current;
		while((current = read()) > 0)
			output.write(current);
		if(current == 0)
			return new String(output.toByteArray(), Charset.forName("US-ASCII"));
		return null;
	}

}
