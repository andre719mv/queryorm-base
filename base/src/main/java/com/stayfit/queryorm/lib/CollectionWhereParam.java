package com.stayfit.queryorm.lib;

import java.util.List;

public class CollectionWhereParam implements IWhereParam {
	 public String PropertyName;
     public CollectionMemberOperatorType Operator;
     public List<String> Values;

     @Override
     public String toString(){
         return String.format("%s, In: %s.", PropertyName, Values.toString());
     }
}
