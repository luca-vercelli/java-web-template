package odata.jpa.antlr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import odata.antlr.ODataParserLexer;
import odata.antlr.ODataParserParser;

@Stateless
public class OdataJPAHelper {

	/**
	 * Convert $orderby clause to JPA ORDER BY clause (without any semantical
	 * check).
	 * 
	 * @param orderby
	 * @param bindVariable
	 * @param aliases 
	 * @return
	 */
	public String parseOrderByClause(String orderby, String bindVariable, Map<String, String> aliases) {
		if (orderby == null)
			return null;

		// TODO use ExpressionVisitor instead
		// FIXME consider aliases

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
				orderbyCondition.append(comma).append(" ").append(bindVariable).append(".").append(attr).append(" ").append(asc);
				comma = ",";
			}
		}
		return orderbyCondition.toString();
	}

	/**
	 * Convert $filter clause to JPA WHERE clause (without any semantical check).
	 * 
	 * @param filter
	 * @param bindVariable
	 * @param aliases
	 * @return
	 */
	public String parseFilterClause(String filter, String bindVariable, Map<String, String> aliases) {
		if (filter == null)
			return null;

		// Init lexer
		ODataParserLexer lexer = new ODataParserLexer(new ANTLRInputStream(filter));
		lexer.removeErrorListeners();
		lexer.addErrorListener(ThrowingErrorListener.INSTANCE);

		// Get a list of matched tokens
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// Init parser with got tokens
		ODataParserParser parser = new ODataParserParser(tokens);
		// parser.removeErrorListeners();
		// parser.addErrorListener(ThrowingErrorListener.INSTANCE);

		// Get the context
		ParseTree tree = parser.clause();
		// here, the input has already been read and parsed

		// Run the VisitorStringLiteral
		OData2JpqlExpressionVisitor visitor = new OData2JpqlExpressionVisitor(bindVariable, aliases);
		String jpql = visitor.visit(tree);

		System.out.println("DEBUG parseFilterClause() jpql=" + jpql);

		return jpql;
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

		// TODO use ExpressionVisitor instead

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
	 * attribute must not be "*"
	 * 
	 * @param attribute
	 * @return
	 */
	public List<String> parseAttributes(String attribute) {
		List<String> list = new ArrayList<String>();

		if (attribute == null || "".equals(attribute))
			return list;

		// TODO use ExpressionVisitor instead

		String[] pieces = attribute.split(",");
		for (String piece : pieces) {
			list.add(parseAttribute(piece));
		}
		return list;
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

	/**
	 * Remove first and last characters (probably they are apex's)
	 * 
	 * @param s
	 * @return
	 */
	public String removeFirstAndLast(String s) {
		if (s == null)
			return s;
		if (s.length() <= 2)
			return "";
		return s.substring(1, s.length() - 1);
	}
}
