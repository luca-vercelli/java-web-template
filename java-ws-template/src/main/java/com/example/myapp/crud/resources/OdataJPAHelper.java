package com.example.myapp.crud.resources;

public class OdataJPAHelper {

	/**
	 * Convert $orderby clause to JPA ORDER BY clause (without any semantical
	 * check).
	 * 
	 * @param orderby
	 * @return
	 */
	public static String parseOrderByClause(String orderby) {
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
				if (!"asc".equals(asc) && !"desc".equals(asc))
					throw new IllegalArgumentException("Syntax error in $orderby condiction: expected asc or desc");
				orderbyCondition.append(comma).append(" ").append(attr).append(" ").append(asc);
				comma = ",";
			}
		}
		return orderbyCondition.toString();
	}

	/**
	 * Convert $filter clause to JPA WHERE clause (without any semantical
	 * check).
	 * 
	 * @param orderby
	 * @return
	 */
	public static String parseFilterClause(String filter) {
		if (filter == null)
			return null;

		// TODO
		return filter;
	}

}
