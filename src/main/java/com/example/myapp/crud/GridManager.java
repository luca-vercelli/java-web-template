package com.example.myapp.crud;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

import com.example.myapp.crud.entity.Grid;
import com.example.myapp.crud.entity.GridColumn;
import com.example.myapp.main.util.Exporter;

@Stateless
public class GridManager {

	@PersistenceContext(unitName = "MyPersistenceUnit")
	private EntityManager em;

	@Inject
	GenericManager genericManager;

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
			if (c.getOrder() == null)
				continue; // not needed
			query += comma;
			query += c.getAttributeName();
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
	 * @return
	 */
	public Grid getGrid(String entity) {

		Class<?> clazz = genericManager.getEntityClass(entity);
		if (clazz == null)
			return null;

		List<Grid> grids = genericManager.findByProperty(Grid.class, "entity", entity);

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

		// TODO load metadata...

		grid = genericManager.save(grid);
		return grid;
	}

	/**
	 * Export grid in XLSX format.
	 * 
	 * Similar routines could be implemented for XLS and CSV.
	 * 
	 * @param grid
	 * @return
	 */
	public XSSFWorkbook excel(Grid grid) {

		List<Object[]> items = find(grid);

		// FIXME lambda?
		String[] headers = new String[grid.getColumns().size()];
		int colnum = 0;
		for (GridColumn gc : grid.getColumns()) {
			if (gc.getOrder() == null)
				continue; // this means not needed in view
			headers[colnum++] = gc.getDescription(); // TODO i18n
		}

		return exporter.exportXLSX(headers, items);
	}

}
