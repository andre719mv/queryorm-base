package com.stayfit.queryorm.lib;

/**
 * Created by Администратор on 10/20/2016.
 */

public class SmartSqlQuery{
    SmartSqlQuery(String sql, String[] args){
        mSql = sql;
        mArgs = args;
    }
    private String mSql;
    private String[] mArgs;

    public String getSql(){return mSql;}
    public String[] getArgs(){return mArgs;}
}