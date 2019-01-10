package com.example.myapp.crud;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.slf4j.Logger;

import com.example.myapp.crud.entity.Grid;
import com.example.myapp.crud.entity.GridColumn;
import com.example.myapp.main.enums.BooleanYN;
import com.example.myapp.main.util.Exporter;

@Stateless
public class GridManager {

	@PersistenceContext(unitName = "MyPersistenceUnit")
	private EntityManager em;

	@Inject
	DataManager genericManager;

	@Inject
	Exporter exporter;

	@Inject
	Logger LOG;

	/**
	 * For a given grid (and entity), extract only the columns specified in the
	 * Grid.
	 * 
	 * @param grid
	 * @return
	 */
	public List<Object[]> find(Grid grid) {

		String query = "SELECT ";
		String comma = "";
		for (GridColumn c : grid.getColumns()) {
			if (c.getOrdering() == null)
				continue; // not needed
			query += comma;
			query += c.getColumnDefinition();
			comma = ",";
		}
		query += " FROM " + grid.getEntity();

		// TODO order by? filters?

		@SuppressWarnings("unchecked")
		List<Object[]> items = em.createQuery(query).getResultList();

		return items;
	}

	public List<Grid> findGridsForEntity(String entity) {
		List<Grid> grids = genericManager.findByProperty(Grid.class, "entity", entity);
		return grids;
	}

	/**
	 * Return a Grid for given entity, and create it if neededd.
	 * 
	 * @param entity
	 * @return null if entity does not exist
	 */
	public Grid getGrid(String entity) {

		Class<?> clazz = genericManager.getEntityClass(entity);
		if (clazz == null)
			return null;

		List<Grid> grids = findGridsForEntity(entity);

		Grid grid = null;

		if (grids.isEmpty())
			grid = createDefaultGrid(entity);
		else {
			if (grids.size() > 1)
				LOG.warn("More grids found for entity " + entity
						+ ". This is not supported yet. We take the first one.");
			grid = grids.get(0);
		}

		return grid;
	}

	/**
	 * Create default Grid. There is no check if Grid already existed or not.
	 * 
	 * @param entity
	 * @return
	 */
	protected Grid createDefaultGrid(String entity) {

		Grid grid = new Grid();
		grid.setEntity(entity);
		grid.setDescription(entity);

		// guess there is a better solution
		EntityType<?> et = getEntity(entity);
		Set<?> attrs = et.getAttributes();
		int colnum = 0;
		for (Object x : attrs) {
			if (x instanceof Attribute) {
				Attribute<?, ?> a = (Attribute<?, ?>) x;
				grid.getColumns().add(new GridColumn(grid, a.getName(), a.getName(), colnum++, BooleanYN.N));
			}
		}
		grid = genericManager.merge(grid); // FIXME check if columns are saved
		return grid;
	}

	private EntityType<?> getEntity(String entity) {
		for (EntityType<?> entityType : em.getMetamodel().getEntities()) {
			if (entityType.getName().equals(entity)) {
				return entityType;
			}
		}
		return null;
	}

	/**
	 * Export grid in XLSX format.
	 * 
	 * @param grid @return @throws IOException @throws
	 */
	public File exportXLSX(Grid grid) throws IOException {

		List<Object[]> items = find(grid);

		// FIXME lambda?
		String[] headers = new String[grid.getColumns().size()];
		int colnum = 0;
		for (GridColumn gc : grid.getColumns()) {
			if (gc.getOrdering() == null)
				continue; // this means not needed in view
			headers[colnum++] = gc.getDescription(); // TODO i18n
		}

		return exporter.exportXLSX(headers, items);
	}

	/**
	 * Export grid in CSV format.
	 * 
	 * @param grid
	 * @return
	 * @throws IOException
	 */
	public File exportCSV(Grid grid) throws IOException {

		List<Object[]> items = find(grid);

		// FIXME lambda?
		String[] headers = new String[grid.getColumns().size()];
		int colnum = 0;
		for (GridColumn gc : grid.getColumns()) {
			if (gc.getOrdering() == null)
				continue; // this means not needed in view
			headers[colnum++] = gc.getDescription(); // TODO i18n
		}

		return exporter.exportCSV(headers, items);
	}

}
