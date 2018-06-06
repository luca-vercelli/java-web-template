package com.example.myapp.crud.resources;

import javax.ejb.Stateless;

@Stateless
public class OdataJPAHelper {

	/**
	 * Convert $orderby clause to JPA ORDER BY clause (without any semantical
	 * check).
	 * 
	 * @param orderby
	 * @return
	 */
	public String parseOrderByClause(String orderby) {
		if (orderby == null)
			return null;

		StringBuilder orderbyCondition = new StringBuilder();
		if (orderby != null && !orderby.trim().isEmpty()) {
			String comma = "";
			String[] orderbyPieces = orderby.split(",");
			for (String piece : orderbyPieces) {
				piece = piece.trim();
				String[] attrAndAsc = piece.split(" ");
				if (attrAndAsc.length > 2 || attrAndAsc.length == 0 || attrAndAsc[0] == null)
					throw new IllegalArgumentException("Syntax error in $orderby condiction");
				String attr = parseAttribute(attrAndAsc[0]);
				if (attr == null)
					throw new IllegalArgumentException(
							"Syntax error in $orderby condiction: expected attribute instead of: " + attr);
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
	 * Convert $filter clause to JPA WHERE clause (without any semantical check).
	 * 
	 * @param orderby
	 * @return
	 */
	public String parseFilterClause(String filter) {
		if (filter == null)
			return null;

		// TODO
		return filter;
	}

	/**
	 * Convert an attribute from OData to JPA form. For example, Address/Street
	 * becomes address.street .
	 * 
	 * @param attribute
	 * @return
	 */
	public String parseAttribute(String attribute) {
		if (attribute == null || "".equals(attribute))
			return null;

		String[] pieces = attribute.split("/");
		StringBuilder sb = new StringBuilder();
		String dot = "";
		for (String piece : pieces) {
			piece = piece.trim();
			sb.append(dot).append(firstToLower(piece));
			dot = ".";
		}
		return sb.toString();
	}

	/**
	 * Convert first letter into lowercase.
	 * 
	 * @param s
	 * @return
	 */
	public String firstToLower(String s) {
		if (s == null || s.length() == 0)
			return s;
		return Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}
}
