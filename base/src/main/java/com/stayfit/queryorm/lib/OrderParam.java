package com.stayfit.queryorm.lib;

class OrderParam implements IMemberCriteria{
	 String PropertyName;
     boolean IsDesc;
     
     @Override
     public String toString()
     {
         return String.format("%s, IsDesc: %s.", PropertyName, IsDesc);
     }

     @Override
     public String getPropertyName() {
          return PropertyName;
     }
}
