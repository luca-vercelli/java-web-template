package com.example.myapp.login.actions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

/**
 * JAX-RS provides @NameBinding, a meta-annotation used to create name-binding
 * annotations for filters and interceptors
 * 
 * @see https://stackoverflow.com/questions/26777083
 *
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Secured {
}