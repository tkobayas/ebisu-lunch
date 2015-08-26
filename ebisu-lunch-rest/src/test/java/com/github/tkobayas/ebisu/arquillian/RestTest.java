package com.github.tkobayas.ebisu.arquillian;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tkobayas.ebisu.model.Restaurant;

@RunWith(Arquillian.class)
public class RestTest {

    private static final String BASE_URL = "http://localhost:8080/ebisu-lunch-rest/rest/restaurants/";

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "ebisu-lunch-rest.war").addPackages(true, "com.fasterxml.jackson")
                .addPackages(true, "com.github.tkobayas.ebisu").addAsResource("import.sql")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsWebInfResource("test-ds.xml");
    }

    @Test
    @Ignore
    public void testLookupById() throws Exception {

        System.out.println("=============================================================");

        String url = BASE_URL + "102";
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        assertEquals(200, conn.getResponseCode());

        InputStream inputStream = conn.getInputStream();

        ObjectMapper mapper = new ObjectMapper();
        Restaurant restaurant = mapper.readValue(inputStream, Restaurant.class);

        System.out.println("Found : " + restaurant);
    }

    @Test
    public void testListAllRestaurants() throws Exception {

        System.out.println("=============================================================");

        String url = BASE_URL;
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        assertEquals(200, conn.getResponseCode());

        InputStream inputStream = conn.getInputStream();

        ObjectMapper mapper = new ObjectMapper();
        List<Restaurant> restaurants = mapper.readValue(inputStream, List.class);

        System.out.println("Found : " + restaurants);
    }
}
