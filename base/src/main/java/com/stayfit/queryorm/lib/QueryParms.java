package com.stayfit.queryorm.lib;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class QueryParms {

	private List<IWhereParam> _whereParams = new ArrayList<IWhereParam>();
	private List<OrderParam> _orderParams = new ArrayList<OrderParam>();
	private List<String> _groupByParams = new ArrayList<String>();
	private Class _entityType;

	// Constructors
	private QueryParms() {
	}

	public QueryParms(Class cl) {
		if (cl == null) {
			throw new InvalidParameterException("type");
		}

		TakeCount = Integer.MAX_VALUE;
		TypeName = cl.getSimpleName();
		_entityType = cl;
	}

	// Properties
	// / <summary>
	// / Параметры сортировки.
	// / </summary>
	public List<OrderParam> getOrderParams() {
		return _orderParams;
	}

	// Properties
	// / <summary>
	// / Параметры групирования.
	// / </summary>
	public List<String> getGroupByParams() {
		return _groupByParams;
	}




	// / <summary>
	// / Список поисковых критериев.
	// / </summary>
	public List<IWhereParam> getSelectCriterias() {
		if(hasIsDeletedColumn()){
			boolean canAddCriteria = true;
			for (IWhereParam criteria: _whereParams) {
				if(criteria.getPropertyName().equals(CommonFields.IsDeleted)) {
					canAddCriteria = false;
					break;
				}
			}

			if(canAddCriteria)
				addCriteria(CommonFields.IsDeleted, 0);
		}

		return _whereParams;
	}

	private boolean hasIsDeletedColumn() {
		boolean hasIsDeletedColumn = false;
		for(Field f : _entityType.getFields()){
			if(f.isAnnotationPresent(MapColumn.class)
				&& f.getAnnotation(MapColumn.class).value().equals(CommonFields.IsDeleted)){
				hasIsDeletedColumn = true;
			}
		}
		return hasIsDeletedColumn;
	}

	// / <summary>
	// / Возвращает и задает пропускаемое количество элементов.
	// / Используется для пейджинга
	// / </summary>
	public int SkipCount;

	// / <summary>
	// / Возвращает или задает указанное число элементов.
	// / </summary>
	public int TakeCount;

	// / <summary>
	// / Возвращает имя типа, для которого будут создаваться поисковые критерии.
	// / </summary>
	public String TypeName;

	// Methods
	// / <summary>
	// / Создает экземпляр параметров запроса для заданного типа.
	// / </summary>
	// / <typeparam name="T">Тип, для которого создается экземпляр параметров
	// запроса.</typeparam>
	// / <returns>Экземпляр параметров запроса для заданного типа.</returns>
	/*
	 * public static QueryParms<T> Create<T>() { return new QueryParms<T>(); }
	 */

	// [Obsolete("Use generic method.", false)]
	public QueryParms addCriteria(String member, Object value, MemberOperatorType operatorType) {
		if (operatorType == null)
			operatorType = MemberOperatorType.IsEqualTo;

		if (StrUtils.isEmpty(member)) {
			throw new InvalidParameterException(
					"Argument 'member' is null or empty.");
		}

		if (value == null) {
			throw new InvalidParameterException("Argument 'value' is null.");
		}

		BinaryWhereParam criteriaParam = new BinaryWhereParam();
		criteriaParam.PropertyName = member;
		criteriaParam.Operator = operatorType;

		String strValue = null;
		if(value.getClass() ==  String.class | value.getClass() ==  Integer.class | value.getClass() ==  Long.class)
			strValue = value.toString();
		else if(value.getClass() == Boolean.class)
			strValue = ((Boolean)value) ? "1" : "0";
		else if(value.getClass() == Date.class)
			strValue = new TypeConverter().writeDateTime((Date)value);
		else throw new IllegalArgumentException("Criteria " +  member + " was of unexpected type " + value.getClass().toString());

		criteriaParam.CriteriaValue = strValue;

		_whereParams.add(criteriaParam);

		return this;
	}

	public QueryParms addCriteria(String member, String value) {
		if (StrUtils.isEmpty(member)) {
			throw new InvalidParameterException(
					"Argument 'member' is null or empty.");
		}

		IWhereParam param;
		if(value == null) {
			UnaryWhereParam criteriaParam = new UnaryWhereParam();
			criteriaParam.PropertyName = member;
			criteriaParam.Operator = UnaryMemberOperatorType.IsNull;
			param = criteriaParam;
		}else {
			BinaryWhereParam criteriaParam = new BinaryWhereParam();
			criteriaParam.PropertyName = member;
			criteriaParam.Operator = MemberOperatorType.IsEqualTo;
			criteriaParam.CriteriaValue = value;
			param = criteriaParam;
		}

		_whereParams.add(param);

		return this;
	}
	
	public QueryParms addCriteria(String member, Integer value) {
		return addCriteria(member, value.toString());
	}
	public QueryParms addCriteria(String member, Long value) {
		return addCriteria(member, value.toString());
	}
	public QueryParms addCriteria(String member, Boolean value) {
		return addCriteria(member, value? "1": "0");
	}

	public QueryParms addCriteria(String member, UnaryMemberOperatorType operatorType) {
		if (StrUtils.isEmpty(member)) {
			throw new InvalidParameterException(
					"Argument 'member' is null or empty.");
		}

		if (operatorType == null) {
			throw new InvalidParameterException(
					"Argument 'operatorType' is null.");
		}

		UnaryWhereParam criteriaParam = new UnaryWhereParam();
		criteriaParam.PropertyName = member;
		criteriaParam.Operator = operatorType;

		_whereParams.add(criteriaParam);

		return this;
	}

	public QueryParms addCriteria(String member, List<String> values, CollectionMemberOperatorType operatorType) {
		if (StrUtils.isEmpty(member)) {
			throw new InvalidParameterException(
					"Argument 'member' is null or empty.");
		}

		if (operatorType == null) {
			throw new InvalidParameterException(
					"Argument 'operatorType' is null.");
		}

		CollectionWhereParam criteriaParam = new CollectionWhereParam();
		criteriaParam.PropertyName = member;
		criteriaParam.Operator = operatorType;
		criteriaParam.Values = values;

		_whereParams.add(criteriaParam);

		return this;
	}

	public QueryParms isNull(String member) {
		return addCriteria(member, UnaryMemberOperatorType.IsNull);
	}

	public QueryParms isNotNull(String member) {
		return addCriteria(member, UnaryMemberOperatorType.IsNotNull);
	}

	public QueryParms in(String member, List<String> values) {
		return addCriteria(member, values, CollectionMemberOperatorType.In);
	}

	public QueryParms notIn(String member, List<String> values) {
		return addCriteria(member, values, CollectionMemberOperatorType.NotIn);
	}


	public QueryParms orderBy(String member) {
		OrderParam orderParam = new OrderParam();
		orderParam.PropertyName = member;
		_orderParams.add(orderParam);

		return this;
	}

	public QueryParms orderByDescending(String member) {
		OrderParam orderParam = new OrderParam();
		orderParam.PropertyName = member;
		orderParam.IsDesc = true;
		_orderParams.add(orderParam);

		return this;
	}

	public QueryParms skip(int itemsCount) {
		if (itemsCount < 0) {
			throw new InvalidParameterException("itemsCount");
		}

		SkipCount = itemsCount;

		return this;
	}

	public QueryParms take(int itemsCount) {
		if(itemsCount < 0)
			throw new InvalidParameterException("Argument 'itemsCount' is less than zero.");

		TakeCount = itemsCount;

		return this;
	}

	public QueryParms groupBy(String member) {
		if(!_groupByParams.contains(member))
			_groupByParams.add(member);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append("QueryParms:");
		sb.append(String.format("TypeName: %s", TypeName));

		if (!_whereParams.isEmpty()) {
			sb.append("SelectCriterias:");
			for (IWhereParam c : _whereParams) {
				sb.append(c.toString());
			}
		} else {
			sb.append("SelectCriterias: empty");
		}

		if (!_orderParams.isEmpty()) {
			sb.append(". OredrParams:");
			for (OrderParam p : _orderParams) {
				sb.append(p.toString());
			}
		} else {
			sb.append(". SelectCriterias: empty");
		}

		if (!_groupByParams.isEmpty()) {
			sb.append(". GroupByParams:");
			for (String p : _groupByParams) {
				sb.append(p);
			}
		} else {
			sb.append(". InParams: empty");
		}

		sb.append(String.format(". SkipCount: %s", SkipCount));

		sb.append(String.format(". TakeCount: %s", TakeCount));

		return sb.toString();
	}

	public QueryParms withDeleted() {
		if(hasIsDeletedColumn())
			return in(CommonFields.IsDeleted, new ArrayList<>(Arrays.asList("0", "1")));
		else
			return  this;
	}

	// / <summary>
	// / Возвращает тип для которого созданы параметры запроса.
	// / </summary>
	// / <returns></returns>
	public Class getEntityType() {
		return _entityType;
	}

}
