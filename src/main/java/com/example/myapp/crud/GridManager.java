package com.example.myapp.crud;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.myapp.crud.entity.Grid;
import com.example.myapp.crud.entity.GridColumn;

@Stateless
public class GridManager {

	@PersistenceContext(unitName = "MyPersistenceUnit")
	private EntityManager em;

	@Inject
	GenericManager genericManager;

	/**
	 * For a given grid (and entity), extract only the columns specified in the
	 * Grid.
	 * 
	 * @param grid
	 * @return
	 */
	public List<?> find(Grid grid) {

		String query = "FROM " + grid.getEntity();
		String comma = ",";
		for (GridColumn c : grid.getColumns()) {
			query += comma;
			query += c.getAttributeName();
			comma = ",";
		}
		
		//TODO order by? filters?

		return em.createQuery(query).getResultList();

	}

}
