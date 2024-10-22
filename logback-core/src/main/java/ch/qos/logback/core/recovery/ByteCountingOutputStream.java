package ch.qos.logback.core.recovery;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteCountingOutputStream extends FilterOutputStream {

    private long count;

    public ByteCountingOutputStream(OutputStream out) {
        super(out);
    }

    public long getByteCount() {
        return count;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        count += len;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        count++;
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
