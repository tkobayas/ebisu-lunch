package com.github.tkobayas.ebisu.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Scraper {

    public static void main(String[] args) throws Exception {
    	
    	int id = 100;
        
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("work/fromMojo.xml"), "UTF-8"));
        while (br.ready()) {

            String line = br.readLine();
            if (!line.contains("<li>")) {
                continue;
            }
            
            String name = br.readLine().trim();
            if (name.equals("</ul>") || name.equals("</li>")) {
            	continue;
            }
            
            System.out.println("insert into Restaurant (id, area, name) values ("
                + (id++) + ", 'EAST', '" + name + "')");
        }
        br.close();
    }
}
