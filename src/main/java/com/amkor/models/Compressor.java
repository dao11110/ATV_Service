package com.amkor.models;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Compressor {
    public static byte[] compress(String data) {
        try {
            byte[] input = data.getBytes();
            Deflater deflater = new Deflater();
            deflater.setInput(input);
            deflater.finish();
            byte[] output = new byte[100];
            int compressedDataLength = deflater.deflate(output);
            return Arrays.copyOf(output, compressedDataLength);
        } catch (Exception ex) {
            return null;
        }

    }

    public static String decompress(byte[] data) {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(data);
            byte[] output = new byte[100];
            int decompressedDataLength = inflater.inflate(output);
            return new String(output, 0, decompressedDataLength, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }

    }
}
