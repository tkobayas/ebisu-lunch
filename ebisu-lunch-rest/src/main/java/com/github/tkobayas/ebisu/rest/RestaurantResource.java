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
package com.github.tkobayas.ebisu.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tkobayas.ebisu.model.Restaurant;
import com.github.tkobayas.ebisu.service.RestaurantService;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the restaurants table.
 */
@Path("/restaurants")
@RequestScoped
public class RestaurantResource {

    private Logger log = LoggerFactory.getLogger(RestaurantResource.class);

    @Inject
    private Validator validator;

    @Inject
    private RestaurantService service;

    @Inject
    RestaurantService registration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Restaurant> listAllRestaurants() {
        return service.findAllOrderedByArea();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Restaurant lookupRestaurantById(@PathParam("id") long id) {
        Restaurant restaurant = service.findById(id);
        if (restaurant == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return restaurant;
    }

    /**
     * Creates a new restaurant from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRestaurant(Restaurant restaurant) {

        Response.ResponseBuilder builder = null;

        try {
            // Validates restaurant using bean validation
            validateRestaurant(restaurant);

            registration.register(restaurant);

            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("num", "Num taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /**
     * <p>
     * Validates the given Restaurant variable and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing b with the same name is registered it throws a regular validation
     * exception so that it can be interpreted separately.
     * </p>
     * 
     * @param restaurant Restaurant to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If restaurant with the same num already exists
     */
    private void validateRestaurant(Restaurant restaurant) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Restaurant>> violations = validator.validate(restaurant);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the name
        if (nameAlreadyExists(restaurant.getName())) {
            throw new ValidationException("Unique Name Violation");
        }
    }

    /**
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
     * by clients to show violations.
     * 
     * @param violations A set of violations that needs to be reported
     * @return JAX-RS response containing all violations
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.debug("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

    /**
     * Checks if a restaurant with the same name is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "name")" constraint from the Restaurant class.
     * 
     * @param num The name to check
     * @return True if the name already exists, and false otherwise
     */
    public boolean nameAlreadyExists(String name) {
        Restaurant restaurant = null;
        try {
            restaurant = service.findByName(name);
        } catch (NoResultException e) {
            // ignore
        }
        return restaurant != null;
    }
}
