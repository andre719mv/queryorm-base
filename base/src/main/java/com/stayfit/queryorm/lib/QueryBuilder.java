package com.stayfit.queryorm.lib;
import java.util.ArrayList;
import java.util.List;


class QueryBuilder {
	static SmartSqlQuery buildSql(QueryParms queryParms) {
		List<String> args = new ArrayList<>();
		String sql = "SELECT * FROM " +
				DOBase.getTableName(queryParms.getEntityType()) + " " +
				createWhere(queryParms, args) + " " +
				createGroupBy(queryParms, args) + " " +
				createOrder(queryParms, args) + " " +
				createSkipTake(queryParms, args);
		return new SmartSqlQuery(sql, args.toArray(new String[args.size()]));
	}

	static SmartSqlQuery builDeleteSql(QueryParms queryParms) {
		List<String> args = new ArrayList<>();
		String sql = "DELETE FROM " +
				DOBase.getTableName(queryParms.getEntityType()) + " " +
				createWhere(queryParms, args) + " ";
		return new SmartSqlQuery(sql, args.toArray(new String[args.size()]));
	}



	private static String createSkipTake(QueryParms queryParms, List<String> args) {
		String expr = " LIMIT ? OFFSET ?";
		args.add(String.valueOf(queryParms.TakeCount));
		args.add(String.valueOf(queryParms.SkipCount));
		return expr;
	}

	private static String createGroupBy(QueryParms queryParms, List<String> args) {
		StringBuilder expr = new StringBuilder();
		List<String> criterias = queryParms.getGroupByParams();

		if (!criterias.isEmpty()) {
			expr.append("GROUP BY ")
			.append(StrUtils.join(",", criterias))
			.append(" ");
		}
		return expr.toString();
	}

	private static String createOrder(QueryParms queryParms, List<String> args) {
		StringBuilder expr = new StringBuilder();
		List<OrderParam> criterias = queryParms.getOrderParams();

		if (!criterias.isEmpty()) {
			expr.append("ORDER BY ");

			int counter = 0;
			for (OrderParam param : criterias) {
				if (counter != 0)
					expr.append(", ");
				
				expr.append(param.getPropertyName())
						.append(param.IsDesc ? " DESC " : " ASC ");
				counter++;
			}
		}
		return expr.toString();
	}

	private static String createWhere(QueryParms queryParms, List<String> args) {
		StringBuilder expr = new StringBuilder();
		List<IWhereParam> criterias = queryParms.getSelectCriterias();
		if (!(criterias.isEmpty() )) {
			expr.append("WHERE ");
			
			int counter = 0;
			for (IWhereParam param : criterias) {
				if (counter != 0)
					expr.append(" AND ");
				counter++;

				if(param instanceof BinaryWhereParam) {
					BinaryWhereParam cParam = ((BinaryWhereParam) param);
					switch (cParam.Operator) {
						case IsEqualTo:
							expr.append(param.getPropertyName() + " = ? ");
							args.add(cParam.CriteriaValue);
							break;
						case IsNotEqualTo:
							expr.append(param.getPropertyName() + " <> ? ");
							args.add(cParam.CriteriaValue);
							break;
						case IsLessThan:
							expr.append(param.getPropertyName() + " < ? ");
							args.add(cParam.CriteriaValue);
							break;
						case IsLessThanOrEqualTo:
							expr.append(param.getPropertyName() + " <= ? ");
							args.add(cParam.CriteriaValue);
							break;
						case IsGreaterThanOrEqualTo:
							expr.append(param.getPropertyName() + " >= ? ");
							args.add(cParam.CriteriaValue);
							break;
						case IsGreaterThan:
							expr.append(param.getPropertyName() + " > ? ");
							args.add(cParam.CriteriaValue);
							break;
						case StartsWith:
							throw new UnsupportedOperationException();
						case EndsWith:
							throw new UnsupportedOperationException();
						case Contains:
							throw new UnsupportedOperationException();
						case BitwiseOneOf:
							expr.append(param.getPropertyName() + " & ? <> 0 ");
							args.add(cParam.CriteriaValue);
							break;
						case BitwiseAll:
							throw new UnsupportedOperationException();
							//break;
						default:
							throw new UnsupportedOperationException();
					}
				} else if (param instanceof UnaryWhereParam) {
					UnaryWhereParam uParam = (UnaryWhereParam) param;
					switch (uParam.Operator) {
						case IsNull:
							expr.append(param.getPropertyName() + " IS NULL ");
							break;
						case IsNotNull:
							expr.append(param.getPropertyName() + " NOT NULL ");
							break;
						default:
							throw new UnsupportedOperationException();
					}
				} else if (param instanceof CollectionWhereParam) {
					CollectionWhereParam clParam = (CollectionWhereParam) param;
					switch (clParam.Operator) {
						case In:
							expr.append(param.getPropertyName() + " in (");
							break;
						case NotIn:
							expr.append(param.getPropertyName() + " NOT in (");
							break;
						default:
							throw new UnsupportedOperationException();
					}

					for (int i = 0; i< clParam.Values.size(); i++) {
						expr.append("?");
						args.add( clParam.Values.get(i));

						if(i< clParam.Values.size() - 1)
							expr.append(", ");
					}
					expr.append(") ");
				}
			}
		}
		return expr.toString();
	}
}
