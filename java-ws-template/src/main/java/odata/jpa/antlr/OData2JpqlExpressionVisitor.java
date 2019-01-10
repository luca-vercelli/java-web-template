package odata.jpa.antlr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import odata.antlr.ODataParserBaseVisitor;
import odata.antlr.ODataParserParser;
import odata.antlr.ODataParserParser.FirstMemberExprContext;

/**
 * ANTLR 4.5 visitor for Expressions. Its purpose is to parse expressions in
 * $filter, $orderby and similar parameters, and not the whole URL.
 * 
 * For security reasons, we do not accept functions we do not know.
 * 
 * @author Luca Vercelli 2018-2019
 *
 */
public class OData2JpqlExpressionVisitor extends ODataParserBaseVisitor<String> {

	static Map<String, String> operators = new HashMap<String, String>();
	static OdataJPAHelper helper = new OdataJPAHelper();

	static {

		// Logic operators
		operators.put("NOT", "NOT");
		operators.put("AND", "AND");
		operators.put("OR", "OR");
		operators.put("EQ", "=");
		operators.put("NE", "!=");
		// Arithmetic operators
		operators.put("LT", "<");
		operators.put("LE", "<=");
		operators.put("GT", ">");
		operators.put("GE", ">=");
		operators.put("ADD", "+");
		operators.put("SUB", "-");
		operators.put("MUL", "*");
		operators.put("DIV", "/");
		operators.put("MOD", "MOD");
		// Arithmetic functions - not supported by JPQL
		operators.put("ROUND", "ROUND");
		operators.put("FLOOR", "FLOOR");
		operators.put("CEILING", "CEILING");
		// String functions
		operators.put("LENGTH", "LENGTH");
		operators.put("INDEXOF", "LOCATE");
		operators.put("SUBSTRING", "SUBSTRING");
		operators.put("TOLOWER", "LOWER");
		operators.put("TOUPPER", "UPPER");
		operators.put("TRIM", "TRIM");
		operators.put("CONCAT", "CONCAT");
		// String functions - not supported by JPQL
		operators.put("CONTAINS", "CONTAINS");
		operators.put("STARTSWITH", "STARTSWITH");
		operators.put("ENDSWITH", "ENDSWITH");
		operators.put("SUBSTRINGOF", "SUBSTRINGOF");
		operators.put("REPLACE", "REPLACE");
		// Date functions
		operators.put("DATE", "CURRENT_DATE");
		operators.put("TIME", "CURRENT_TIME");
		operators.put("NOW", "CURRENT_TIMESTAMP");
		// Date functions - not supported by JPQL
		operators.put("YEAR", "YEAR");
		operators.put("YEARS", "YEAR");
		operators.put("MONTH", "MONTH");
		operators.put("MONTHS", "MONTH");
		operators.put("DAY", "DAY");
		operators.put("DAYS", "DAY");
		operators.put("HOUR", "HOUR");
		operators.put("HOURS", "HOUR");
		operators.put("MINUTE", "MINUTE");
		operators.put("MINUTES", "MINUTE");
		operators.put("SECOND", "SECOND");
		operators.put("SECONDS", "SECOND");
		operators.put("TOTALOFFSETMINUTES", "MINUTES"); // more or less
		operators.put("MAXDATETIME", "{ts '9999-21-31 23:59:59'}"); // more or less
		operators.put("MINDATETIME", "{ts '0001-01-01 00:00:00'}"); // more or less
		// Types - I don't even know what they mean. TODO
		operators.put("ISOF", "???");
		operators.put("CAST", "???");
	}

	SimpleDateFormat edmDate = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat edmTime = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat edmDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	SimpleDateFormat jpqlDate = new SimpleDateFormat("{'d' ''yyyy-MM-dd''}");
	SimpleDateFormat jpqlTime = new SimpleDateFormat("{'t' ''HH:mm:ss''}");
	SimpleDateFormat jpqlDateTime = new SimpleDateFormat("{'ts' ''yyyy-MM-dd HH:mm:ss''}");
	SimpleDateFormat hqlDate = new SimpleDateFormat("''yyyy-MM-dd''");
	SimpleDateFormat hqlTime = new SimpleDateFormat("''HH:mm:ss''");
	SimpleDateFormat hqlDateTime = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss''");

	Queue<String> bindVariables = new ArrayDeque<String>();
	Map<String, String> aliases = new HashMap<String, String>();

	public OData2JpqlExpressionVisitor(String bindVariableName) {
		bindVariables.add(bindVariableName);
	}

	public OData2JpqlExpressionVisitor(String bindVariableName, Map<String, String> aliases) {
		this(bindVariableName);
		if (aliases != null)
			this.aliases = aliases;
	}

	/**
	 * DEBUG procedure
	 * 
	 * @param tree what to format
	 * @param graphical if true, text will be formatted on more lines
	 * @param if graphical, the number of spaces on the left. Usually 0.
	 */
	public String printContext(ParseTree tree, boolean graphical, int level) {
		if (tree instanceof ErrorNode) {
			ErrorNode node = (ErrorNode) tree;
			return "ErrorNode(" + node.getSymbol() + ")";
		} else if (tree instanceof TerminalNode) {
			TerminalNode node = (TerminalNode) tree;
			return "TerminalNode(" + node.getSymbol() + ")";
		} else {
			StringBuffer padding = (graphical) ? spaces(level) : new StringBuffer();
			StringBuffer sb = new StringBuffer(tree.getClass().getSimpleName()).append("(");
			for (int i = 0; i < tree.getChildCount(); ++i) {
				ParseTree child = tree.getChild(i);
				if (i != 0) {
					sb.append(", ");
				}
				if (graphical) {
					sb.append("\r\n").append(padding);
				}
				sb.append(printContext(child, graphical, level + 1));
			}
			sb.append(")");
			return sb.toString();
		}
	}

	/**
	 * Creates a string of spaces that is 'spaces' spaces long.
	 * @see https://stackoverflow.com/questions/2804827
	 * 
	 * @param spaces The number of spaces to add to the string.
	 */
	private StringBuffer spaces(int length) {
		StringBuffer sb = new StringBuffer(length);
		for (int i = 0; i < length; i++){
		   sb.append(" ");
		}
		return sb;
	}

	/**
	 * OData to JPQL symbol conversion
	 * 
	 * @param odataSymbol
	 * @return
	 */
	public String convert(ParseTree odataSymbolContext) {
		if (odataSymbolContext == null)
			throw new IllegalArgumentException("null argument given");
		String symbol0 = odataSymbolContext.getText().toUpperCase();
		String symbol1 = operators.get(symbol0);
		if (symbol1 == null)
			throw new IllegalArgumentException("Unknown symbol: '" + symbol0 + "'");
		return symbol1;
	}

	/**
	 * Aggregates the results of visiting multiple children of a node. After either
	 * all children are visited or {@link #shouldVisitNextChild} returns
	 * {@code false}, the aggregate value is returned as the result of
	 * {@link #visitChildren}.
	 *
	 * <p>
	 * The default implementation returns {@code nextResult}, meaning
	 * {@link #visitChildren} will return the result of the last child visited (or
	 * return the initial value if the node has no children).
	 * </p>
	 *
	 * @param aggregate
	 *            The previous aggregate value. In the default implementation, the
	 *            aggregate value is initialized to {@link #defaultResult}, which is
	 *            passed as the {@code aggregate} argument to this method after the
	 *            first child node is visited.
	 * @param nextResult
	 *            The result of the immediately preceeding call to visit a child
	 *            node.
	 *
	 * @return The updated aggregate result.
	 */
	@Override
	public String aggregateResult(String aggregate, String nextResult) {
		// standard implementation returns nextResult
		return (aggregate != null ? aggregate : "") + (nextResult != null ? nextResult : "");
	}

	@Override
	public String visitClause(ODataParserParser.ClauseContext ctx) {
		// just for debug
		System.out.println("DEBUG visitClause() context = " + printContext(ctx, true, 0));
		return visitChildren(ctx);
	}

	@Override
	public String visitParenthesisExpr(ODataParserParser.ParenthesisExprContext ctx) {
		return "(" + visitChildren(ctx) + ")";
	};

	@Override
	public String visitParenthesisClause(ODataParserParser.ParenthesisClauseContext ctx) {
		return "(" + visitChildren(ctx) + ")";
	};

	@Override
	public String visitBinaryClause(ODataParserParser.BinaryClauseContext ctx) {
		String jpqlConnective = convert(ctx.binaryConnective());
		return " " + jpqlConnective + " " + visit(ctx.clause());
	};

	@Override
	public String visitUnaryClause(ODataParserParser.UnaryClauseContext ctx) {
		String jpqlConnective = convert(ctx.unaryLeftConective());
		return " " + jpqlConnective + " " + visit(ctx.clause());
	};

	@Override
	public String visitBinaryOperatorClause(ODataParserParser.BinaryOperatorClauseContext ctx) {
		String jpqlOperator = convert(ctx.binaryBoolOperator());
		return " " + jpqlOperator + " " + visit(ctx.expression());
	};

	@Override
	public String visitBinaryExpr(ODataParserParser.BinaryExprContext ctx) {
		String jpqlOperator = convert(ctx.binaryOperator());
		return " " + jpqlOperator + " " + visit(ctx.expression());
	};

	@Override
	public String visitNegateExpr(ODataParserParser.NegateExprContext ctx) {
		return " - " + visit(ctx.expression());
	};

	@Override
	public String visitZeroaryMethodCall(ODataParserParser.ZeroaryMethodCallContext ctx) {
		String jpqlFunction = convert(ctx.zeroaryMethod());
		return jpqlFunction; // are () required in jpql?
	};

	@Override
	public String visitUnaryMethodCall(ODataParserParser.UnaryMethodCallContext ctx) {
		String jpqlFunction = convert(ctx.unaryMethod());
		return jpqlFunction + "(" + visit(ctx.expression()) + ")";
	};

	@Override
	public String visitBinaryMethodCall(ODataParserParser.BinaryMethodCallContext ctx) {
		String jpqlFunction = convert(ctx.binaryMethod());
		return jpqlFunction + "(" + visit(ctx.expression().get(0)) + ", " + visit(ctx.expression().get(1)) + ")";
	};

	@Override
	public String visitBinaryBoolMethodCall(ODataParserParser.BinaryBoolMethodCallContext ctx) {
		String jpqlFunction = convert(ctx.binaryBoolMethod());
		return jpqlFunction + "(" + visit(ctx.expression().get(0)) + ", " + visit(ctx.expression().get(1)) + ")";
	};

	@Override
	public String visitContainsMethodCall(ODataParserParser.ContainsMethodCallContext ctx) {
		String rightSide = ctx.expression(1).getText();
		if (rightSide == null)
			throw new IllegalStateException("'contains' method requires two not null arguments");
		if (rightSide.startsWith("'") && rightSide.endsWith("'"))
			rightSide = "'%" + rightSide.substring(1, rightSide.length() - 1) + "%'";
		else
			rightSide = "'%'||" + rightSide + "||'%'";
		return visit(ctx.expression(0)) + " LIKE " + rightSide;
	}

	@Override
	public String visitSubstringMethodCall(ODataParserParser.SubstringMethodCallContext ctx) {
		// substring may have 2 or 3 arguments
		String jpqlFunction = convert(ctx.SubstringToken());
		String ret = jpqlFunction + "(" + visit(ctx.expression().get(0)) + ", " + visit(ctx.expression().get(1));
		if (ctx.expression().size() > 2)
			ret += ", " + visit(ctx.expression().get(2));
		ret += ")";
		return ret;
	}

	@Override
	public String visitSingleNavigation(ODataParserParser.SingleNavigationContext ctx) {
		// TODO too many possibilities
		return visitChildren(ctx);
	}

	@Override
	public String visitSingleNavigationExpr(ODataParserParser.SingleNavigationExprContext ctx) {
		return "." + helper.firstToLower(visit(ctx.memberExpr()));
	}

	@Override
	public String visitQualifiedEntityTypeName(ODataParserParser.QualifiedEntityTypeNameContext ctx) {
		return helper.firstToLower(ctx.getText()) + ".";
	}

	@Override
	public String visitEntityNavigationProperty(ODataParserParser.EntityNavigationPropertyContext ctx) {
		return helper.firstToLower(ctx.getText());
	}

	@Override
	public String visitEntityColNavigationProperty(ODataParserParser.EntityColNavigationPropertyContext ctx) {
		String prefix = "";
		if (ctx.parent.parent instanceof FirstMemberExprContext)
			prefix = bindVariables.peek() + ".";
		return  prefix + helper.firstToLower(ctx.getText());
	}

	@Override
	public String visitAnyClause(ODataParserParser.AnyClauseContext ctx) {
		String upperBindVariable = bindVariables.peek();
		String bindVariable = ctx.anyExpr().lambdaVariableExpr().getText();
		String upperProperty = visit(ctx.memberExpr());

		if (ctx.anyExpr() == null) {
			return " EXISTS (SELECT " + bindVariable + " FROM " + upperBindVariable + "." + upperProperty + " "
					+ bindVariable + ")";
		} else {
			bindVariables.add(bindVariable);
			String predicate = visit(ctx.anyExpr().lambdaPredicateExpr());
			bindVariables.remove();
			return " EXISTS (SELECT " + bindVariable + " FROM " + upperBindVariable + "." + upperProperty + " "
					+ bindVariable + " WHERE " + predicate + ")";
		}

	}

	@Override
	public String visitAllClause(ODataParserParser.AllClauseContext ctx) {
		String upperBindVariable = bindVariables.peek();
		String bindVariable = ctx.allExpr().lambdaVariableExpr().getText();
		String upperProperty = visit(ctx.memberExpr());
		bindVariables.add(bindVariable);
		String predicate = visit(ctx.allExpr().lambdaPredicateExpr());
		bindVariables.remove();
		return " NOT EXISTS (SELECT " + bindVariable + " FROM " + upperBindVariable + "." + upperProperty + " "
				+ bindVariable + " WHERE NOT(" + predicate + "))";
	}

	@Override
	public String visitPrimitiveLiteral(ODataParserParser.PrimitiveLiteralContext ctx) {
		if (ctx.getChildCount() != 1)
			throw new IllegalStateException("A primitive literal should have exactly 1 terminal node as child");
		// FIXME naive

		if (ctx.dateLiteral() != null) {
			return visit(ctx.dateLiteral());
		} else if (ctx.dateTimeOffsetLiteral() != null) {
			return visit(ctx.dateTimeOffsetLiteral());
		} else if (ctx.timeOfDayLiteral() != null) {
			return visit(ctx.timeOfDayLiteral());
		}
		return ctx.getChild(0).getText();
	}

	private String commonVisitDateOrTimeLiteral(TerminalNode stringLiteral, DateFormat edmDf, DateFormat jpqlDf) {
		String dateInput = helper.removeFirstAndLast(stringLiteral.getText());
		Date date;
		try {
			date = edmDf.parse(dateInput);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Illegal date: " + stringLiteral);
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("Illegal date: null");
		}
		String dateOutput = jpqlDf.format(date);
		return dateOutput;
	}

	@Override
	public String visitDateLiteral(ODataParserParser.DateLiteralContext ctx) {

		return commonVisitDateOrTimeLiteral(ctx.StringLiteral(), edmDate, jpqlDate);
	}

	@Override
	public String visitDateTimeOffsetLiteral(ODataParserParser.DateTimeOffsetLiteralContext ctx) {

		return commonVisitDateOrTimeLiteral(ctx.StringLiteral(), edmDateTime, jpqlDateTime);
	}

	@Override
	public String visitTimeOfDayLiteral(ODataParserParser.TimeOfDayLiteralContext ctx) {

		return commonVisitDateOrTimeLiteral(ctx.StringLiteral(), edmTime, jpqlTime);
	}

	@Override
	public String visitParameterAlias(ODataParserParser.ParameterAliasContext ctx) {
		String value = aliases.get(ctx.ParameterAlias().getText());
		return value; // as-is ???
	}
}
