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
				
				expr.append(param.PropertyName)
						.append(param.IsDesc ? " DESC " : " ASC ");
				counter++;
			}
		}
		return expr.toString();
	}

	private static String createWhere(QueryParms queryParms, List<String> args) {
		StringBuilder expr = new StringBuilder();
		List<CriteriaParam> criterias = queryParms.getSelectCriterias();
		List<InParam> inParams = queryParms.getInParams();
		List<InParam> notInParams = queryParms.getNotInParams();
		if (!(criterias.isEmpty() && inParams.isEmpty())) {
			expr.append("WHERE ");
			
			int counter = 0;
			for (CriteriaParam param : criterias) {
				if (counter != 0)
					expr.append(" AND ");
				counter++;
				switch (param.Operator) {
				case IsEqualTo:
					expr.append(param.PropertyName + " = ? ");
					args.add(param.CriteriaValue);
					break;
				case IsNotEqualTo:
					expr.append(param.PropertyName + " <> ? ");
					args.add(param.CriteriaValue);
					break;
				case IsLessThan:
					expr.append(param.PropertyName + " < ? ");
					args.add(param.CriteriaValue);
					break;
				case IsLessThanOrEqualTo:
					expr.append(param.PropertyName + " <= ? ");
					args.add(param.CriteriaValue);
					break;
				case IsGreaterThanOrEqualTo:
					expr.append(param.PropertyName + " >= ? ");
					args.add(param.CriteriaValue);
					break;
				case IsGreaterThan:
					expr.append(param.PropertyName + " > ? ");
					args.add(param.CriteriaValue);
					break;
				case StartsWith:
					throw new UnsupportedOperationException();
				case EndsWith:
					throw new UnsupportedOperationException();
				case Contains:
					throw new UnsupportedOperationException();
				case BitwiseOneOf:
					expr.append(param.PropertyName + " & ? <> 0 ");
					args.add(param.CriteriaValue);
					break;
				case BitwiseAll:
					throw new UnsupportedOperationException();
					//break;
				case IsNull:
					expr.append(param.PropertyName + " IS NULL ");
					break;
				case IsNotNull:
					expr.append(param.PropertyName + " NOT NULL ");
					break;
				default:
					throw new UnsupportedOperationException();
				}
			}
			for (InParam param : inParams) {
				if (counter != 0)
					expr.append(" AND ");
				counter++;

				expr.append(param.PropertyName + " in (");
				for (int i = 0; i< param.Values.size(); i++) {
					expr.append("?");
					args.add( param.Values.get(i));

					if(i< param.Values.size() - 1)
						expr.append(", ");
				}
				expr.append(") ");
			}

			for (InParam param : notInParams) {
				if (counter != 0)
					expr.append(" AND ");
				counter++;

				expr.append(param.PropertyName + " NOT in (");
				for (int i = 0; i< param.Values.size(); i++) {
					expr.append("?");
					args.add( param.Values.get(i));

					if(i< param.Values.size() - 1)
						expr.append(", ");
				}
				expr.append(") ");
			}
		}
		return expr.toString();
	}
}
