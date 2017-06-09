package com.example.myapp.crud;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This REST WS handles not just one entity, but all possible entities. Entites
 * can lie in different packages, however their nemaes must be different (I
 * argue this is assumed by JPA too). This service assumes all entities have a
 * primary key of type Long, called Id.
 * 
 * @author Luca Vercelli 2017
 *
 */
@Stateless
@Path("rsr")
@Produces(MediaType.APPLICATION_JSON)
public class CommonResources {

	@Inject
	CommonManager manager;

	@GET
	@Path("{entity}")
	public List<?> findAll(@PathParam("entity") String entity) {

		Class<?> clazz = manager.getEntityClass(entity);
		if (clazz == null)
			return Collections.emptyList();
		else
			return manager.findAll(clazz);
	}

	@GET
	@Path("{entity}/{id}")
	public Object findById(@PathParam("entity") String entity, @PathParam("id") Long id) {

		Class<?> clazz = manager.getEntityClass(entity);
		if (clazz == null)
			return null;
		else
			return manager.findById(clazz, id);
	}

	@POST
	@Path("{entity}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Object create(@PathParam("entity") String entity, Map<String, String> attributes) {

		Class<?> clazz = manager.getEntityClass(entity);
		if (clazz == null)
			return null; // FIXME give some error
		Object x = manager.bean2object(clazz, attributes);
		return manager.save(x);
	}

	@DELETE
	@Path("{entity}/{id}")
	public void delete(@PathParam("entity") String entity, @PathParam("id") Long id) {

		Class<?> clazz = manager.getEntityClass(entity);
		if (clazz == null)
			return;
		else
			manager.remove(clazz, id);
	}

	@PUT
	@Path("{entity}/{id}") // richiede di inserire (in json) tutti i campi
							// obbligatori
	public void update(@PathParam("entity") String entity, @PathParam("id") Long id, Map<String, String> attributes) {

		if (id == null)
			return; // FIXME give some error

		Class<?> clazz = manager.getEntityClass(entity);
		if (clazz == null)
			return; // FIXME give some error

		attributes.put("id", id.toString());
		Object x = manager.bean2object(clazz, attributes);

		manager.save(x);
	}

	/* ---- TEST RESOURCES ---- */
	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public String prova() {
		System.out.println("hello world");
		return "hello world";
	}

	/* ---- OTHER NON-REST METHODS---- */

	@GET
	@Path("{entity}/{id}/clone")
	public Object duplicate(@PathParam("entity") String entity, @PathParam("id") Long id) {

		Class<?> clazz = manager.getEntityClass(entity);
		if (clazz == null)
			return null;
		else {
			Object obj = manager.findById(clazz, id);

			Method setter;
			try {
				setter = obj.getClass().getMethod("setId", Long.class);
				setter.invoke(obj, id);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(e);
			}

			return obj;
		}
	}
	
}
