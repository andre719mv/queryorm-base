package com.stayfit.queryorm.lib;

public class CriteriaParam implements IMemberCriteria{
	 public String PropertyName;
     public String CriteriaValue;
     public MemberOperatorType Operator;
     public boolean IsOperatorAndForSamePropertyName;

     @Override
     public String toString(){
         return
             String.format(
                 "%s %s %s, IsOperatorAndForSamePropertyName: %s.",
                     PropertyName, Operator, CriteriaValue, IsOperatorAndForSamePropertyName);
     }
}
