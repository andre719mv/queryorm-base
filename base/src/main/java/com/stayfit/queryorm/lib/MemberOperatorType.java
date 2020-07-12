package com.stayfit.queryorm.lib;

/// <summary>
/// Операторы поисковых критериев.
/// </summary>
public enum MemberOperatorType implements BaseMemberOperatorType {
	/// <summary>
    /// Равно
    /// </summary>
    IsEqualTo,

    /// <summary>
    /// 
    /// </summary>
    IsNotEqualTo,

    /// <summary>
    /// 
    /// </summary>
    IsLessThan,

    /// <summary>
    /// 
    /// </summary>
    IsLessThanOrEqualTo,

    /// <summary>
    /// 
    /// </summary>
    IsGreaterThanOrEqualTo,

    /// <summary>
    /// 
    /// </summary>
    IsGreaterThan,

    /// <summary>
    /// 
    /// </summary>
    StartsWith,
    
    /// <summary>
    /// 
    /// </summary>
    EndsWith,
    
    /// <summary>
    /// 
    /// </summary>
    Contains,
    BitwiseOneOf,//row containt one of passed bit values
    BitwiseAll //row contains all passed bit values and possibly some others
}
