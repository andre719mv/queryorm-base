package com.stayfit.queryorm.lib;

public class BinaryWhereParam implements IWhereParam {
	 public String PropertyName;
     public String CriteriaValue;
     public MemberOperatorType Operator;

     @Override
     public String toString(){
         return
             String.format("%s %s %s", PropertyName, Operator, CriteriaValue);
     }

     @Override
     public String getPropertyName() {
          return PropertyName;
     }
}
