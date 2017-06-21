package com.example.myapp.crud.resources;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * Class ListType(T) is a runtime placeholder for List&lt;T&gt;.
 * 
 * @see https://stackoverflow.com/questions/2989475
 */
public class ListType implements ParameterizedType {

	private Type rawType;
	private Type[] actualTypeArguments;

	public ListType(Class<?> type) {
		if (type == null)
			throw new IllegalArgumentException("null type given");
		this.rawType = List.class;
		this.actualTypeArguments = new Type[] { type };
	}

	@Override
	public Type[] getActualTypeArguments() {
		return actualTypeArguments;
	}

	@Override
	public Type getRawType() {
		return rawType;
	}

	@Override
	public Type getOwnerType() {
		return null;
	}

	@Override
	public String toString() {
		return "" + getRawType() + "<" + Arrays.asList(getActualTypeArguments()) + ">";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ParameterizedType))
			return false;
		ParameterizedType t2 = (ParameterizedType) obj;
		return toString().equals(t2.toString());
	}

}