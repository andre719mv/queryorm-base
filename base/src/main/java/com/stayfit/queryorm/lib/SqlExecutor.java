package com.stayfit.queryorm.lib;

import com.stayfit.queryorm.lib.sqlinterfaces.ISQLiteContentValues;
import com.stayfit.queryorm.lib.sqlinterfaces.ISQLiteCursor;
import com.stayfit.queryorm.lib.sqlinterfaces.ISQLiteDatabase;

public class SqlExecutor {
	private ISQLiteDatabase db;

	public SqlExecutor(ISQLiteDatabase db){
		this.db = db;
	}
	public SqlExecutor(){
		db = DbHelper.getHelper().getWritableDatabase();
	}
	public QueryResult executeSelect(QueryParms queryParms){
		SmartSqlQuery query = QueryBuilder.buildSql(queryParms);
		ISQLiteCursor cursor = db.rawQuery(query.getSql(), query.getArgs());
		return new QueryResult(cursor,queryParms.getEntityType());
	}

	public <T> QueryResult executeSelect(Class<T> cl, String sql){
		ISQLiteCursor cursor = db.rawQuery(sql, null);
		return new QueryResult(cursor, cl);
	}

	public <T> QueryResult executeSelect(Class<T> cl, SmartSqlQuery query){
		ISQLiteCursor cursor = db.rawQuery(query.getSql(), query.getArgs());
		return new QueryResult(cursor, cl);
	}
	
	public ISQLiteCursor rawQuery(String sql){
		return db.rawQuery(sql, null);
	}

	public int delete(QueryParms queryParms){
		SmartSqlQuery query = QueryBuilder.builDeleteSql(queryParms);
		ISQLiteCursor cursor = db.rawQuery(query.getSql(), query.getArgs());
		cursor.moveToFirst();
		int rowsAffected = 0;
		if(cursor.getCount()> 0)
			rowsAffected = cursor.getInt(0);
		cursor.close();
		return rowsAffected;
	}

	public void insert(String table, String nullColumnHack, ISQLiteContentValues values) {
		db.insert(table, nullColumnHack, values);
	}

	public ISQLiteContentValues newContentValues(){
		return db.newContentValues();
	}
}
