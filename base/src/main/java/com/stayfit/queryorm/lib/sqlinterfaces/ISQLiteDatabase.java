// Copyright (c) 2015, Intel Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
// 1. Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
// 3. Neither the name of the copyright holder nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.stayfit.queryorm.lib.sqlinterfaces;

import java.sql.SQLException;

public abstract class ISQLiteDatabase {

	public abstract ISQLiteContentValues newContentValues();

	public abstract ISQLiteCursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy);

	public abstract int delete(String table, String whereClause, String[] whereArgs);

	public abstract long insert(String table, String nullColumnHack, ISQLiteContentValues values);

	public abstract void execSQL(String statement);

	public abstract void update(String tableName, ISQLiteContentValues values,
                String whereClause, String[] whereArgs);

	public abstract ISQLiteCursor rawQuery(String sql, String[] selectionArgs);

	private boolean isInTransatcion = false;
	/*
		Be careful! If transaction was added in differet way, this methods will lead to unexpeced results.
	 */
	public boolean isInTransaction(){return isInTransatcion;}

	public void beginTransaction() {
		if(isInTransatcion)
			throw new RuntimeException("Already in transaction");

		this.execSQL("BEGIN TRANSACTION;");
		isInTransatcion = true;
	}

	public void commitTransaction() {
		if(!isInTransatcion)
			throw new RuntimeException("There is no active transaction");

		this.execSQL("COMMIT TRANSACTION;");
		isInTransatcion = false;
	}

	public void rollbackTransaction() {
		if(!isInTransatcion)
			throw new RuntimeException("There is no active transaction");

		this.execSQL("ROLLBACK TRANSACTION;");
		isInTransatcion = false;
	}
}
