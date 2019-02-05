package org.alentar.parallelportmon.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FileUtils {
    public static String readResourceAsString(String path) throws Exception {
        InputStream in = Thread.currentThread().getContextClassLoader().getClass().getResourceAsStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String data = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        bufferedReader.close();

        return data;
    }
}
