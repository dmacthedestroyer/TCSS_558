package tcss558.homework1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.nio.charset.Charset;
import java.util.Arrays;

public class SpellingInputStream extends InputStream implements AutoCloseable {

	DataInputStream dataInputStream;

	public SpellingInputStream(InputStream in) {
		dataInputStream = new DataInputStream(in);
	}

	public SpellingInputStream(DatagramPacket datagramPacket) {
		this(new ByteArrayInputStream(Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength())));
	}

	@Override
	public int read() {
		try {
			return dataInputStream.read();
		} catch (IOException e) {
			return -1;
		}
	}

	public String readNullTerminatedString() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		int current;
		while ((current = read()) > 0)
			output.write(current);
		if (current == 0)
			return new String(output.toByteArray(), Charset.forName("US-ASCII"));
		return null;
	}

	@Override
	public void close() {
		try {
			super.close();
			dataInputStream.close();
		} catch (IOException e) {
		}
	}
}
