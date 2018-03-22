package com.stayfit.queryorm.lib;

import java.util.List;

public class InParam implements IMemberCriteria{
	 public String PropertyName;
     public List<String> Values;
     
     @Override
     public String toString()
     {
         return String.format("%s, In: %s.", PropertyName, Values.toString());
     }
}
