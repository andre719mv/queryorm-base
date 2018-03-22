package com.stayfit.queryorm.lib;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.stayfit.queryorm.lib.sqlinterfaces.ISQLiteCursor;

public class QueryResult {
	public QueryResult(ISQLiteCursor cursor, Class entityType) {
		this.cursor = cursor;
		this.entityType = entityType;
		cursor.moveToFirst();
	}

	ISQLiteCursor cursor;
	Class entityType;

	public int getCount() {
		return cursor.getCount();
	}

	public <T> T selectSingle(boolean closeCursor) {
		T obj = null;
		if(cursor.getCount() != 0) {
			try {
				obj = (T) entityType.getDeclaredConstructors()[1].newInstance(cursor);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(closeCursor)
			closeCursor();
		return obj;
	}

	public <T> List<T> selectAll(boolean closeCursor){
		cursor.moveToFirst();
		List<T> items = new ArrayList<T>();
		for (int i = 0; i < getCount(); i++) {
			T item = selectSingle(false);

			items.add(item);
			cursor.moveToNext();
		}
		if(closeCursor)
			closeCursor();
		return items;
	}

	public boolean moveToNext() {
		return cursor.moveToNext();
	}

	public void moveToLast() {
		cursor.moveToLast();
	}

	public void moveToPrevious() {
		cursor.moveToPrevious();
	}

	public void moveToPosition(int position) {
		cursor.moveToPosition(position);
	}

	public void moveToFirst() {
		cursor.moveToFirst();		
	}

	public void closeCursor() {
		cursor.close();
	}
}
