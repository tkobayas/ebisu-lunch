package com.github.tkobayas.ebisu.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scraper2 {

    public static void main(String[] args) throws Exception {
        String regex = ".*values \\((\\d+).*";
        Pattern p = Pattern.compile(regex);

        boolean start = false;
        int tagId = 0;

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("work/import.sql"), "UTF-8"));
        while (br.ready()) {

            if (!start) {
                String line = br.readLine();
                if (line.contains("和心庵のら")) {
                    start = true;
                } else {
                    continue;
                }
            }

            String line2 = br.readLine().trim();
            if (line2.equals("")) {
                tagId++;
                continue;
            }

            // System.out.println(line2);

            int restaurantId = -1;
            Matcher m = p.matcher(line2);
            if (m.find()) {
                restaurantId = Integer.parseInt(m.group(1));
            } else {
                continue;
            }

            System.out.println("insert into Restaurant_Tag (restaurant_id, tags_id) values (" + restaurantId + ", "
                    + tagId + ")");

            if (line2.contains("野郎ラーメン")) {
                break;
            }
        }
        br.close();
    }
}
