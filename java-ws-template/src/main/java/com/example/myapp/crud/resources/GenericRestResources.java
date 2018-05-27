/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.crud.resources;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.example.myapp.crud.GenericManager;

import javassist.NotFoundException;

/**
 * This REST WS handles not just one entity, but all possible entities. Entites
 * can lie in different packages, however their names must be different (I argue
 * this is assumed by JPA too). This service assumes all entities have a primary
 * key of type Long, called Id.
 * 
 * REST syntax is inspired to OData. For handling parameters, we follow this
 * pattern: @see https://api.stackexchange.com/docs/users
 * 
 * @author Luca Vercelli 2017
 *
 */
@Stateless
@Path("/")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class GenericRestResources {

	public static final Integer ZERO = 0;

	@Inject
	GenericManager manager;

	/**
	 * Represent a JSON answer with a List inside the "data" field.
	 */
	public static class DataList {
		private GenericEntity<Object> data;

		public GenericEntity<Object> getData() {
			return data;
		}

		public void setData(GenericEntity<Object> data) {
			this.data = data;
		}
	}

	/**
	 * Represent a JSON answer with a List inside the "data" field, plus a
	 * number inside the "count" field.
	 */
	public static class DataListCount extends DataList {
		private Long count;

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}
	}

	/**
	 * Return the Entity Class with given name.
	 * 
	 * @param entity
	 * @return
	 * @throws NotFoundException
	 *             if such entity is not known.
	 */
	public Class<?> getEntityOrThrowException(String entity) throws NotFoundException {
		Class<?> clazz = manager.getEntityClass(entity);
		if (clazz == null)
			throw new NotFoundException("Unknown entity set: " + entity);
		return clazz;
	}

	/**
	 * Return a list of objects (via JSON). We don't know the type of returned
	 * objects, so we must return a generic "Response".
	 * 
	 * @param entity
	 * @return
	 * @throws NotFoundException
	 */
	@GET
	@Path("{entity}")
	public Response find(@PathParam("entity") String entity, @QueryParam("$skip") Integer skip,
			@QueryParam("$top") Integer top, @QueryParam("$filter") String filter,
			@QueryParam("$orderby") String orderby, @QueryParam("$count") Boolean count) throws NotFoundException {

		Class<?> clazz = getEntityOrThrowException(entity);

		List<?> list;

		// FIXME we don't parse $filter clause
		list = manager.find(clazz, top, skip, filter, parseOrderByClause(orderby));

		// ListType and GenericEntity are needed in order to handle generics
		Type genericType = new ListType(clazz);
		GenericEntity<Object> genericList = new GenericEntity<Object>(list, genericType);

		if (count != null && count) {
			Long numItems = manager.countEntities(clazz);
			DataListCount d = new DataListCount();
			d.setData(genericList);
			d.setCount(numItems);
			return Response.ok(d).build();
		} else {
			DataList d = new DataList();
			d.setData(genericList);
			return Response.ok(d).build();
		}

	}

	/**
	 * Parse $orderby clause
	 * 
	 * @param orderby
	 * @return
	 */
	private String parseOrderByClause(String orderby) {
		if (orderby == null)
			return null;
		StringBuilder orderbyCondition = new StringBuilder();
		if (orderby != null && !orderby.trim().isEmpty()) {
			orderbyCondition.append(" order by ");
			String comma = "";
			String[] orderbyPieces = orderby.split(",");
			for (String piece : orderbyPieces) {
				String[] attrAndAsc = piece.split(" ");
				if (attrAndAsc.length > 2 || attrAndAsc.length == 0 || attrAndAsc[0] == null)
					throw new IllegalArgumentException("Syntax error in $orderby condiction");
				String attr = attrAndAsc[0];
				String asc = (attrAndAsc.length == 2) ? attrAndAsc[1].toLowerCase() : "asc";
				orderbyCondition.append(comma).append(" ").append(attr).append(" ").append(asc);
				comma = ",";
			}
		}
		return orderbyCondition.toString();
	}

	@GET
	@Path("{entity}/$count")
	@Produces(MediaType.TEXT_PLAIN)
	public Long count(@PathParam("entity") String entity) throws NotFoundException {
		Class<?> clazz = getEntityOrThrowException(entity);
		return manager.countEntities(clazz);
	}

	/**
	 * Retreive and return (via JSON) a single object by id. We don't know the
	 * type of returned objects, so we must return a generic "Response".
	 * 
	 * @param entity
	 * @return
	 * @throws NotFoundException
	 */
	@GET
	@Path("{entity}({id})")
	public Response findById(@PathParam("entity") String entity, @PathParam("id") Long id) throws NotFoundException {

		Class<?> clazz = getEntityOrThrowException(entity);

		if (id == null)
			throw new BadRequestException("Missing id");

		Object obj = manager.findById(clazz, id);
		if (obj == null)
			throw new NotFoundException("");

		return Response.ok(obj).build();
	}

	@GET
	@Path("{entity}({id})/{property}/$value")
	@Produces(MediaType.TEXT_PLAIN)
	public String rawProperty(@PathParam("entity") String entity, @PathParam("id") Long id,
			@PathParam("property") String property) throws NotFoundException {

		Class<?> clazz = getEntityOrThrowException(entity);

		if (id == null)
			throw new BadRequestException("Missing id");

		Object obj = manager.findById(clazz, id);
		if (obj == null)
			throw new NotFoundException("");

		// TODO
		return null;
	}

	@GET
	@Path("{entity}({id})/{property}")
	public Response getProperty(@PathParam("entity") String entity, @PathParam("id") Long id,
			@PathParam("property") String property) throws NotFoundException {

		Class<?> clazz = getEntityOrThrowException(entity);

		if (id == null)
			throw new BadRequestException("Missing id");

		Object obj = manager.findById(clazz, id);
		if (obj == null)
			throw new NotFoundException("");

		// TODO
		return null;
	}

	// TODO can @POST single property?

	/**
	 * Create and return a single object (via JSON). We don't know the type of
	 * returned objects, so we must return a generic "Response".
	 * 
	 * @param entity
	 * @return
	 * @throws NotFoundException
	 */
	@POST
	@Path("{entity}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("entity") String entity, Map<String, String> attributes)
			throws NotFoundException {

		Class<?> clazz = getEntityOrThrowException(entity);

		Object obj = manager.bean2object(clazz, attributes);
		obj = manager.save(obj);

		return Response.ok(obj).build();
	}

	/**
	 * Delete a single object by id.
	 * 
	 * @param entity
	 * @return
	 * @throws NotFoundException
	 */
	@DELETE
	@Path("{entity}({id})")
	public void delete(@PathParam("entity") String entity, @PathParam("id") Long id) throws NotFoundException {

		Class<?> clazz = getEntityOrThrowException(entity);

		manager.remove(clazz, id);
	}

	/**
	 * Update and return a single object by id.
	 * 
	 * @param entity
	 * @return
	 * @throws NotFoundException
	 */
	@PUT
	@Path("{entity}({id})")
	public Response update(@PathParam("entity") String entity, @PathParam("id") Long id, Map<String, String> attributes)
			throws NotFoundException {

		// FIXME: right way?

		Class<?> clazz = getEntityOrThrowException(entity);

		if (id == null)
			throw new BadRequestException("Missing id");

		attributes.put("id", id.toString());
		Object obj = manager.bean2object(clazz, attributes);
		obj = manager.save(obj);
		return Response.ok(obj).build();
	}

	/**
	 * Duplicate and return (via JSON) a single object by id.
	 * 
	 * @param entity
	 * @return
	 * @throws NotFoundException
	 */
	@GET
	@Path("{entity}({id})/Clone")
	public Response duplicate(@PathParam("entity") String entity, @PathParam("id") Long id) throws NotFoundException {

		// FIXME: right way?

		Class<?> clazz = getEntityOrThrowException(entity);

		if (id == null)
			throw new BadRequestException("Missing id");

		Object obj = manager.findById(clazz, id);

		Method setter;
		try {
			setter = obj.getClass().getMethod("setId", Long.class);
			setter.invoke(obj, (Long) null);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		return Response.ok(obj).build();

	}

}
