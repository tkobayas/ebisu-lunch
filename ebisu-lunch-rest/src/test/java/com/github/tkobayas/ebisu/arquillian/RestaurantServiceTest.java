/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tkobayas.ebisu.arquillian;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.tkobayas.ebisu.model.Area;
import com.github.tkobayas.ebisu.model.Restaurant;
import com.github.tkobayas.ebisu.model.Tag;
import com.github.tkobayas.ebisu.service.RestaurantService;

@RunWith(Arquillian.class)
public class RestaurantServiceTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "ebisu-lunch-rest.war")
                .addClasses(Restaurant.class, RestaurantService.class, Tag.class, Area.class)
                // .addAsResource("import.sql")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                // Deploy our test datasource
                .addAsWebInfResource("test-ds.xml");
    }

    @Inject
    RestaurantService restaurantService;

    @Test
    @Ignore
    public void testRegister() throws Exception {
        Tag tag1 = new Tag("イタリアンX");
        restaurantService.register(tag1);
        Tag tag2 = new Tag("エスプレッソX");
        restaurantService.register(tag2);

        Restaurant newRestaurant = new Restaurant();
        newRestaurant.setName("ポカポカX");
        newRestaurant.setArea(Area.EAST);
        newRestaurant.addTag(tag1);
        newRestaurant.addTag(restaurantService.findTagByValue("エスプレッソX"));
        restaurantService.register(newRestaurant);
        assertNotNull(newRestaurant.getId());
        System.out.println(newRestaurant);
    }
}
