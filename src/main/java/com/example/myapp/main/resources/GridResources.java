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
import com.example.myapp.crud.GridManager;
import com.example.myapp.crud.entity.Grid;
import com.example.myapp.crud.entity.GridColumn;
import com.example.myapp.crud.resources.ListType;
import com.sun.messaging.jmq.io.Status;

@Stateless
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class GridResources {

	@Inject
	GenericManager genericManager;

	@Inject
	GridManager gridManager;

	@Inject
	Logger LOG;

	@GET
	@Path("{entity}/GridMetaData")
	public Response findMetaData(@PathParam("entity") String entity) {

		// TODO better to return a Grid with all its columns, instead of a
		// List<GridColumn>

		Class<?> clazz = genericManager.getEntityClass(entity);
		if (clazz == null)
			return Response.status(Status.NOT_FOUND).build();

		List<Grid> grids = gridManager.findGridsForEntity(entity);

		Grid grid = null;

		if (grids.isEmpty())
			grid = gridManager.createDefaultGrid(entity);
		else {
			if (grids.size() > 1)
				LOG.warn("More grids found for entity " + entity
						+ ". This is not supported yet. We take the first one.");
			grid = grids.get(0);
		}

		List<GridColumn> columns = grid.getColumns();

		// ListType and GenericEntity are needed in order to handle generics
		Type genericType = new ListType(GridColumn.class);
		GenericEntity<Object> genericList = new GenericEntity<Object>(columns, genericType);

		return Response.ok(genericList).build();

	}

	/**
	 * Similar to GET /{entity}, retrieve only selected columns
	 * 
	 * @param entity
	 * @return
	 */
	@GET
	@Path("{entity}/Grid")
	public Response find(@PathParam("entity") String entity) {

		// FIXME duplicated code

		Class<?> clazz = genericManager.getEntityClass(entity);
		if (clazz == null)
			return Response.status(Status.NOT_FOUND).build();

		List<Grid> grids = genericManager.findByProperty(Grid.class, "entity", entity);

		if (grids.isEmpty())
			return Response.status(Status.NOT_FOUND).build();
		// TODO create default grid

		if (grids.size() > 1)
			LOG.warn("More grids found for entity " + entity + ". This is not supported yet. We take the first one.");

		List<?> entities = gridManager.find(grids.get(0));

		// ListType and GenericEntity are needed in order to handle generics
		Type genericType = new ListType(clazz);
		GenericEntity<Object> genericList = new GenericEntity<Object>(entities, genericType);

		return Response.ok(genericList).build();

	}

}
