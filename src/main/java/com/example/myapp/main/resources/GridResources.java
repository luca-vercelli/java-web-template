package com.example.myapp.main.resources;

import java.lang.reflect.Type;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.example.myapp.crud.GenericManager;
import com.example.myapp.crud.entity.Grid;
import com.example.myapp.crud.entity.GridColumn;
import com.example.myapp.crud.resources.GenericRestResources.ListType;
import com.sun.messaging.jmq.io.Status;

@Stateless
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class GridResources {

	@Inject
	GenericManager manager;

	@Inject
	Logger LOG;

	@GET
	@Path("{entity}/Grid")
	public Response find(@PathParam("entity") String entity) {

		// TODO better to return a Grid with all its columns, instead of a
		// List<GridColumn>

		Class<?> clazz = manager.getEntityClass(entity);
		if (clazz == null)
			return Response.status(Status.NOT_FOUND).build();

		List<Grid> grids = manager.findByProperty(Grid.class, "entity", entity);

		if (grids.isEmpty())
			return Response.status(Status.NOT_FOUND).build();
		if (grids.size() > 1)
			LOG.warn("More grids found for entity " + entity + ". This is not supported yet. We take the first one.");

		List<GridColumn> columns = grids.get(0).getColumns();

		// ListType and GenericEntity are needed in order to handle generics
		Type genericType = new ListType(GridColumn.class);
		GenericEntity<Object> genericList = new GenericEntity<Object>(columns, genericType);

		return Response.ok(genericList).build();

	}
}
