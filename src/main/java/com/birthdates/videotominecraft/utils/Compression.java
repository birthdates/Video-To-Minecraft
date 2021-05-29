package com.birthdates.videotominecraft.utils;

import lombok.experimental.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

/**
 * Class used to compress & decompress large files
 */
@UtilityClass
public class Compression {

    public byte[] compress(byte[] bytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream compress = new DeflaterOutputStream(out);
        return getBytes(bytes, out, compress);
    }

    public byte[] decompress(byte[] bytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InflaterOutputStream compress = new InflaterOutputStream(out);
        return getBytes(bytes, out, compress);
    }

    private byte[] getBytes(byte[] toWrite, ByteArrayOutputStream out, FilterOutputStream stream) {
        try {
            stream.write(toWrite);
            stream.flush();
            stream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return out.toByteArray();
    }

}
