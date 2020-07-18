package com.yanan.utils.beans;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * decode bean
 * @author yanan
 *
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
public @interface DecodeBean {
	String[] key();
	String value() default "object";
}
