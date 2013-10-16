package tcss558.homework1.udp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class SpellingOutputStream extends OutputStream implements AutoCloseable {
	ByteArrayOutputStream byteArrayOutputStream;
	DataOutputStream dataOutputStream;

	public SpellingOutputStream() {
		byteArrayOutputStream = new ByteArrayOutputStream();
		dataOutputStream = new DataOutputStream(byteArrayOutputStream);
	}

	public void writeNullByte() {
		writeByte(0);
	}

	public void writeNullTerminatedString(String value) {
		try {
			dataOutputStream.write(value.getBytes(Charset.forName("US-ASCII")));
			writeNullByte();
		} catch (IOException e) {
		}
	}

	public void writeByte(int byteValue) {
		try {
			dataOutputStream.writeByte(byteValue);
		} catch (IOException e) {
		}
	}

	public byte[] toByteArray() {
		try {
			dataOutputStream.flush();
		} catch (IOException e) {
		}
		return byteArrayOutputStream.toByteArray();
	}

	public int size() {
		return byteArrayOutputStream.size();
	}

	@Override
	public void write(int b) {
		writeByte(b);
	}

	@Override
	public void close() {
		try {
			super.close();
			dataOutputStream.close();
			byteArrayOutputStream.close();
		} catch (IOException e) {
		}
	}
}
