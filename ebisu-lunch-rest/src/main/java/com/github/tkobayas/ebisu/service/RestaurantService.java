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
package com.github.tkobayas.ebisu.service;


import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tkobayas.ebisu.model.Restaurant;

@Stateless
public class RestaurantService {

    private Logger log = LoggerFactory.getLogger(RestaurantService.class);

    @PersistenceContext
    private EntityManager em;

    public void register(Restaurant restaurant) throws Exception {
        log.info("Registering " + restaurant);
        em.persist(restaurant);
    }
    
    public void update(Restaurant restaurant) throws Exception {
        log.info("Updating " + restaurant);
        em.merge(restaurant);
    }
    
    public Restaurant findById(Long id) {
        return em.find(Restaurant.class, id);
    }

    public Restaurant findByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Restaurant> criteria = cb.createQuery(Restaurant.class);
        Root<Restaurant> restaurant = criteria.from(Restaurant.class);
        criteria.select(restaurant).where(cb.equal(restaurant.get("name"), name));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Restaurant> findAllOrderedByArea() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Restaurant> criteria = cb.createQuery(Restaurant.class);
        Root<Restaurant> restaurant = criteria.from(Restaurant.class);
        criteria.select(restaurant).orderBy(cb.asc(restaurant.get("area")));
        return em.createQuery(criteria).getResultList();
    }
}
