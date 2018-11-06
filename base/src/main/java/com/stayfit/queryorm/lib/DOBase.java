package com.stayfit.queryorm.lib;

import com.stayfit.queryorm.lib.sqlinterfaces.ISQLiteContentValues;
import com.stayfit.queryorm.lib.sqlinterfaces.ISQLiteCursor;
import com.stayfit.queryorm.lib.sqlinterfaces.ISQLiteDatabase;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DOBase {
	public long _id = -1;
	private static Map<String, Map<String, String>> mappedClassColumnToFiled = new HashMap<>();
	private static Map<String, Map<String, String>> mappedClassFieldToColumn = new HashMap<>();
	private static Map<String, String> mappedTableNames = new HashMap<>();

	protected DOBase() {
	}

	protected DOBase(ISQLiteCursor cursor) {

		Map<String, String> mapppedColumns = getMappedColumns();
		Class c = this.getClass();

		String[] columns = cursor.getColumnNames();
		String tableName = getTableName();
		for (String column : columns) {
			try {
				String fieldName;
				if(mapppedColumns.containsKey(column))
					fieldName = mapppedColumns.get(column);
				else
					fieldName = column.replace("_" + tableName, "");

				Field f = c.getField(fieldName);
				String fieldType = f.getType().getName();

				if (fieldType.equals("java.lang.String")) {
					if(!cursor.isNull(cursor.getColumnIndex(column)))
						f.set(this, cursor.getString(cursor.getColumnIndex(column)));
				} else if (fieldType.equals("int")) {
					f.setInt(this, cursor.getInt(cursor.getColumnIndex(column)));
				} else if (fieldType.equals("boolean")) {
					f.setBoolean(this,	cursor.getInt(cursor.getColumnIndex(column)) == 1);
				} else if (fieldType.equals("long")) {
					f.setLong(this,	cursor.getLong(cursor.getColumnIndex(column)));
				} else if (fieldType.equals("java.util.Date")) {
					f.set(this,	new TypeConverter().readDate(cursor.getString(cursor.getColumnIndex(column))));
				}else if (fieldType.equals("double") || fieldType.equals("float")) {
					f.set(this,	cursor.getDouble(cursor.getColumnIndex(column)));
				}else if (fieldType.equals("java.lang.Double") || fieldType.equals("java.lang.Float")) {
					if (!cursor.isNull(cursor.getColumnIndex(column)))
						f.set(this, cursor.getDouble(cursor.getColumnIndex(column)));
				}else{
					throw new RuntimeException("Unexpected field type " + fieldType + " mapped to " + tableName + "." + column);
				}
			} catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
				//FirebaseCrash.report(e);
			}
		}
	}

	private Map<String, String> getMappedColumns() {
		Class c = this.getClass();
		String className = c.getName();
		if(!mappedClassColumnToFiled.containsKey(className)) {
			loadAnnotationMapping(c);
		}

		return mappedClassColumnToFiled.get(className);
	}

	private Map<String, String> getMappedFields() {
		Class c = this.getClass();
		String className = c.getName();
		if(!mappedClassFieldToColumn.containsKey(className)) {
			loadAnnotationMapping(c);
		}

		return mappedClassFieldToColumn.get(className);
	}

	private void loadAnnotationMapping(Class c) {
		Map<String, String> mapppedColumns = new HashMap<>();
		Map<String, String> mapppedFields = new HashMap<>();
		for (Field f : c.getFields()) {
			if (f.isAnnotationPresent(MapColumn.class)) {
				String columnName = f.getAnnotation(MapColumn.class).value();
				mapppedColumns.put(columnName, f.getName());
				mapppedFields.put(f.getName(), columnName);
			}
		}
		mappedClassColumnToFiled.put(c.getName(), mapppedColumns);
		mappedClassFieldToColumn.put(c.getName(), mapppedFields);

	}

	//public static <T> T SelectById(T t, Long id) {
	//	return SelectById(t.getClass());
	//}
	
	public static <T> T selectById(Class<T> cl, Long id) {
		SqlExecutor executor = new SqlExecutor();

		QueryParms parms = new QueryParms(cl)
				.addCriteria("_id", id.toString())
				.withDeleted();
		QueryResult result = executor.executeSelect(parms);
		T obj = result.selectSingle(true);
		return obj;
	}

	public static <T> T selectByColumnVal(Class<T> cl, String columnName, Object value) {
		SqlExecutor executor = new SqlExecutor();

		QueryParms parms = new QueryParms(cl).addCriteria(columnName, value.toString());
		QueryResult result = executor.executeSelect(parms);
		T obj = result.selectSingle(true);
		return obj;
	}

    public static <T> List<T> selectAllByColumnVal(Class<T> cl, String columnName, Object value) {
        SqlExecutor executor = new SqlExecutor();

        QueryParms parms = new QueryParms(cl).addCriteria(columnName, value.toString());
        QueryResult result = executor.executeSelect(parms);
        List<T> obj = result.selectAll(true);
        return obj;
    }

	public static <T> T selectSingle(Class<T> cl, QueryParms parms) {
		SqlExecutor executor = new SqlExecutor();

		QueryResult result = executor.executeSelect(parms);
		T obj = result.selectSingle(true);
		return obj;
	}
	
	public static <T> List<T> selectAll(Class<T> cl, QueryParms parms) {
		SqlExecutor executor = new SqlExecutor();

		QueryResult result = executor.executeSelect(parms);
		List<T> obj = result.selectAll(true);
		return obj;
	}

	public static <T> List<T> selectAll(Class<T> cl, String sql) {
		SqlExecutor executor = new SqlExecutor();

		QueryResult result = executor.executeSelect(cl, sql);
		List<T> obj = result.selectAll(true);
		return obj;
	}

	public static String getTableName(Class cl) {
		if(!mappedTableNames.containsKey(cl.getName())){
			String name = null;
			if(cl.isAnnotationPresent(MapTable.class))
				name = ((MapTable)cl.getAnnotation(MapTable.class)).value();
			else
				name = cl.getSimpleName().toLowerCase();

			mappedTableNames.put(cl.getName(), name);
		}

		return mappedTableNames.get(cl.getName());

	}

	private String getTableName() {
		return getTableName(this.getClass());
	}

	public Long save() { return save(DbHelper.getHelper().getWritableDatabase(), true); }

	public Long save(ISQLiteDatabase db, boolean closeConnection) {
		Class c = this.getClass();
		String tableName = getTableName();
		Map<String, String> mapppedFields = getMappedFields();

		ISQLiteContentValues newValues = db.newContentValues();

		try {
			for (Field f : c.getFields()) {
				//ignore voilotaile $change  and id
				if (!f.getName().equals(CommonFields.Id)
						&& !f.getName().startsWith("$")
						&& !f.getName().toLowerCase().startsWith("serialversionuid")
						&& !f.isAnnotationPresent(ColumnDeleted.class))
				{
					String valueToSet = "";
					if (f.getType().getName().equals("boolean"))
						valueToSet = Boolean.valueOf(f.get(this).toString())? "1":"0";
					else if(f.getType().getName().equals("java.util.Date"))
                        valueToSet = new TypeConverter().writeDateTime((Date)f.get(this));
                    else
						valueToSet = f.get(this) == null ? null: f.get(this).toString();

					String columnName;
					if(mapppedFields.containsKey(f.getName()))
						columnName = mapppedFields.get(f.getName());
					else
						columnName = f.getName().toLowerCase() + "_" + tableName;

					newValues.put(columnName, valueToSet);
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
			//FirebaseCrash.report(e);
		}

		if (_id >= 0) {
			newValues.put(CommonFields.Id, _id);
			String[] whereArgs = new String[]{String.valueOf(_id)};
			db.update(tableName, newValues, CommonFields.Id + " = ?", whereArgs);
		} else {
			_id = db.insert(tableName, null, newValues);
		}
		//if(closeConnection)
		//	db.close();
		return _id;
	}
	
	public void delete() {
		if (_id < 0)
			return;

		try {
			Class c = this.getClass();
			Field isDeletedField = null;

			for (Field field : c.getFields()) {
				if (field.isAnnotationPresent(MapColumn.class)
						&& field.getAnnotation(MapColumn.class).value().equals(CommonFields.IsDeleted)) {
					isDeletedField = field;
					break;
				}
			}

			if(isDeletedField != null) {
				isDeletedField.setBoolean(this, true);
				save();
				return;
			}
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
			//FirebaseCrash.report(e);
		}

		deleteForever();
	}

	public void deleteForever() {
		if (_id < 0)
			return;

		ISQLiteDatabase db = DbHelper.getHelper().getWritableDatabase();
		String tableName = getTableName();
		String[] whereArgs = new String[]{String.valueOf(_id)};
		db.delete(tableName, CommonFields.Id + " = ?", whereArgs);
		//db.close();
	}
}
