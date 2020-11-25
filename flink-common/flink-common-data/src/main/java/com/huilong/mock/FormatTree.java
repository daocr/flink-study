package com.huilong.mock;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author daocr
 * @date 2020/11/25
 */
public class FormatTree {

    public static void main(String[] args) {


        String dir = "/Users/daocr/IdeaProjects/study/flink-study/";

        List<String> strings = runShell("tree -N -d -L 5", dir);

        List<String> excludes = Arrays.asList("main", "src", "java", "test", "resources", "com", "target", "classes", "generated-sources", "annotations");

        for (String line : strings) {
            boolean check = check(excludes, line);
            if (check) {
                System.out.println(line);
            }
        }

    }

    private static boolean check(List<String> excludes, String line) {
        for (String exclude : excludes) {
            if (line.contains(exclude)) {
                return false;
            }
        }
        return true;
    }


    public static List<String> runShell(String shStr, String dir) {
        List<String> strList = new ArrayList<String>();
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", shStr}, null, new File(dir));
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            process.waitFor();
            while ((line = input.readLine()) != null) {
                strList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strList;
    }
}
