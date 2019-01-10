package com.example.myapp.crud.resources;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

import com.example.myapp.crud.DataManager;
import com.example.myapp.crud.GridManager;
import com.example.myapp.crud.entity.Grid;
import com.example.myapp.crud.entity.GridColumn;

@Stateless
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class GridResources {

	@Inject
	DataManager genericManager;
	@Inject
	GridManager gridManager;
	@Inject
	Logger LOG;

	@GET
	@Path("{entity}/gridMetadata")
	public Response findMetaData(@PathParam("entity") String entity) {

		// TODO better to return a Grid with all its columns, instead of a
		// List<GridColumn>

		Grid grid = gridManager.getGrid(entity);
		if (grid == null) {
			LOG.error("No grid found for " + entity);
			Response.status(Status.NOT_FOUND).build();
		}

		List<GridColumn> columns = grid.getColumns();
		if (columns == null || columns.isEmpty()) {
			LOG.error("No columns found for " + entity);
			Response.status(Status.NOT_FOUND).build();
		}

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
	@Path("{entity}/grid")
	public Response find(@PathParam("entity") String entity) {

		Grid grid = gridManager.getGrid(entity);
		if (grid == null) {
			LOG.error("No grid found for " + entity);
			Response.status(Status.NOT_FOUND).build();
		}

		List<Object[]> entities = gridManager.find(grid);

		LOG.info("QUA entities=" + entities.size());

		// ListType and GenericEntity are needed in order to handle generics
		Type genericType = new ListType(String.class);
		GenericEntity<Object> genericList = new GenericEntity<Object>(entities, genericType);

		return Response.ok(genericList).build(); // TEST

	}

	/**
	 * Export Grid to XLSX.
	 * 
	 * @param entity
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("{entity}/gridXLSX")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response exportXLSX(@PathParam("entity") String entity) throws IOException {

		Grid grid = gridManager.getGrid(entity);
		if (grid == null) {
			LOG.error("No grid found for " + entity);
			Response.status(Status.NOT_FOUND).build();
		}

		File f = gridManager.exportXLSX(grid);

		return Response.ok(f).header("Content-Disposition", "attachment; filename=\"Export-" + entity + ".xlsx\"")
				.header("Set-Cookie", "fileDownload=true; path=/").build();
	}

	/**
	 * Export Grid to CSV.
	 * 
	 * @param entity
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("{entity}/gridCSV")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response exportCSV(@PathParam("entity") String entity) throws IOException {

		Grid grid = gridManager.getGrid(entity);
		if (grid == null) {
			LOG.error("No grid found for " + entity);
			Response.status(Status.NOT_FOUND).build();
		}

		File f = gridManager.exportCSV(grid);

		return Response.ok(f).header("Content-Disposition", "attachment; filename=\"Export-" + entity + ".csv\"")
				.header("Set-Cookie", "fileDownload=true; path=/").build();
	}

	/**
	 * This is just a debug internal path
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("test/test")
	public Response test() {

		@SuppressWarnings("rawtypes")
		Map test = new HashMap();
		test.put("colonna1", "pippo");
		test.put("colonna2", "pluto");

		return Response.ok(test).build();

	}
}
