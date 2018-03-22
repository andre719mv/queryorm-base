package com.stayfit.queryorm.lib;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class QueryParms {

	private List<CriteriaParam> _selectCriterias = new ArrayList<CriteriaParam>();
	private List<OrderParam> _orderParams = new ArrayList<OrderParam>();
	private List<InParam> _inParams = new ArrayList<InParam>();
	private List<InParam> _notInParams = new ArrayList<InParam>();
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

	// Properties
	// / <summary>
	// / Параметры in(...).
	// / </summary>
	public List<InParam> getInParams() {
		return _inParams;
	}

	public List<InParam> getNotInParams() {
		return _notInParams;
	}


	// / <summary>
	// / Список поисковых критериев.
	// / </summary>
	public List<CriteriaParam> getSelectCriterias() {
		if(hasIsDeletedColumn()){
			boolean canAddCriteria = true;
			for (CriteriaParam criteria: _selectCriterias) {
				if(criteria.PropertyName.equals(CommonFields.IsDeleted)) {
					canAddCriteria = false;
					break;
				}
			}

			for (InParam inParam: _inParams) {
				if(CommonFields.IsDeleted.equals(inParam.PropertyName)) {
					canAddCriteria = false;
					break;
				}
			}

			for (InParam inParam: _notInParams) {
				if(CommonFields.IsDeleted.equals(inParam.PropertyName)) {
					canAddCriteria = false;
					break;
				}
			}
			if(canAddCriteria)
				addCriteria(CommonFields.IsDeleted, 0);
		}

		return _selectCriterias;
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
	public QueryParms addCriteria(String member, Object value,
								  MemberOperatorType operatorType) {
		if (operatorType == null)
			operatorType = MemberOperatorType.IsEqualTo;
		if (StrUtils.isEmpty(member)) {
			throw new InvalidParameterException(
					"Argument 'member' is null or empty.");
		}

		CriteriaParam criteriaParam = new CriteriaParam();
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

		_selectCriterias.add(criteriaParam);

		return this;
	}

	public QueryParms addCriteria(String member, String value) {
		if (StrUtils.isEmpty(member)) {
			throw new InvalidParameterException(
					"Argument 'member' is null or empty.");
		}

		CriteriaParam criteriaParam = new CriteriaParam();
		criteriaParam.PropertyName = member;
		criteriaParam.Operator = MemberOperatorType.IsEqualTo;
		criteriaParam.CriteriaValue = value;

		_selectCriterias.add(criteriaParam);

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

	public QueryParms in(String member, List<String> values) {
		if (StrUtils.isEmpty(member)) {
			throw new InvalidParameterException(
					"Argument 'member' is null or empty.");
		}

		InParam criteriaParam = new InParam();
		criteriaParam.PropertyName = member;
		criteriaParam.Values = values;

		_inParams.add(criteriaParam);

		return this;
	}

	public QueryParms notIn(String member, List<String> values) {
		if (StrUtils.isEmpty(member)) {
			throw new InvalidParameterException(
					"Argument 'member' is null or empty.");
		}

		InParam criteriaParam = new InParam();
		criteriaParam.PropertyName = member;
		criteriaParam.Values = values;

		_notInParams.add(criteriaParam);

		return this;
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

		if (!_selectCriterias.isEmpty()) {
			sb.append("SelectCriterias:");
			for (CriteriaParam c : _selectCriterias) {
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

		if (!_inParams.isEmpty()) {
			sb.append(". InParams:");
			for (InParam p : _inParams) {
				sb.append(p.toString());
			}
		} else {
			sb.append(". InParams: empty");
		}

		if (!_notInParams.isEmpty()) {
			sb.append(". NotInParams:");
			for (InParam p : _notInParams) {
				sb.append(p.toString());
			}
		} else {
			sb.append(". NotInParams: empty");
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
