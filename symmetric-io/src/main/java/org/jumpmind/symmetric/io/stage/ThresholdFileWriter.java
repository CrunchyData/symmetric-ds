package org.jumpmind.symmetric.io.stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;

/**
 * Write to an internal buffer up until the threshold. When the threshold is
 * reached, flush the buffer to the file and write to the file from that point
 * forward.
 */
public class ThresholdFileWriter extends Writer {

    private File file;    

    private BufferedWriter fileWriter;

    private StringBuilder buffer;

    private long threshhold;

    /**
     * @param threshold The number of bytes at which to start writing to a file
     * @param file The file to write to after the threshold has been reached
     */
    public ThresholdFileWriter(long threshold, StringBuilder buffer, File file) {
        this.file = file;
        this.buffer = buffer;
        this.threshhold = threshold;
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void close() throws IOException {
        if (fileWriter != null) {
            fileWriter.close();
            fileWriter = null;            
        }
    }

    @Override
    public void flush() throws IOException {
        if (fileWriter != null) {
            fileWriter.flush();
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (fileWriter != null) {
            fileWriter.write(cbuf, off, len);
        } else if (len + buffer.length() > threshhold) {
            file.getParentFile().mkdirs();
            fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            fileWriter.write(buffer.toString());
            fileWriter.write(cbuf, off, len);
            fileWriter.flush();
        } else {
            buffer.append(new String(cbuf), off, len);
        }
    }

    public BufferedReader getReader() throws IOException {
        if (file != null && file.exists()) {
            return new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        } else {
            return new BufferedReader(new StringReader(buffer.toString()));
        }
    }
    
    public void delete() {
        if (file != null && file.exists()) {
            file.delete();
        }
        file = null;
        buffer.setLength(0);
    }

}