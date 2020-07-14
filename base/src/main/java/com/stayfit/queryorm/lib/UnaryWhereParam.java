package com.stayfit.queryorm.lib;

public class UnaryWhereParam implements IWhereParam {
	 public String PropertyName;
     public UnaryMemberOperatorType Operator;

     @Override
     public String toString(){
         return
             String.format(
                 "%s %s",
                     PropertyName, Operator);
     }

    @Override
    public String getPropertyName() {
        return PropertyName;
    }
}
