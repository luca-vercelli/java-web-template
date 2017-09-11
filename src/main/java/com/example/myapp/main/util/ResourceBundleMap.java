package com.example.myapp.main.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * A Map wrapper around a ResourceBundle. Only Strings are supported. Bundle's
 * content is <b>not</b> copied inside this structure.
 * 
 * @author Luca Vercelli
 *
 */
public class ResourceBundleMap implements Map<String, String> {

	private ResourceBundle bundle;

	public ResourceBundleMap(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public int size() {
		return bundle.keySet().size();
	}

	@Override
	public boolean isEmpty() {
		return bundle.keySet().isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		if (!(key instanceof String))
			return false;
		return bundle.containsKey((String) key);
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null)
			return false;
		if (!(value instanceof String))
			return false;
		String val0 = (String) value;
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (bundle.getString(key).equals(val0))
				return true;
		}
		return false;
	}

	@Override
	public String get(Object key) {
		if (!(key instanceof String))
			return null;
		return bundle.getString((String) key);
	}

	@Override
	public String put(String key, String value) {
		throw new UnsupportedOperationException("Cannot alter bundle's content");
	}

	@Override
	public String remove(Object key) {
		throw new UnsupportedOperationException("Cannot alter bundle's content");
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		throw new UnsupportedOperationException("Cannot alter bundle's content");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Cannot alter bundle's content");
	}

	@Override
	public Set<String> keySet() {
		return bundle.keySet();
	}

	@Override
	public Collection<String> values() {
		ArrayList<String> collection = new ArrayList<String>();
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			collection.add(bundle.getString(key));
		}
		return collection;
	}

	/**
	 * Differently from the interface contract, return an immutable set.
	 */
	@Override
	public Set<Entry<String, String>> entrySet() {
		Set<Entry<String, String>> entriesSet = new HashSet<Entry<String, String>>();
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = bundle.getString(key);
			Entry<String, String> entry = new AbstractMap.SimpleEntry<String, String>(key, value);
			entriesSet.add(entry);
		}
		return Collections.unmodifiableSet(entriesSet);
	}
}
