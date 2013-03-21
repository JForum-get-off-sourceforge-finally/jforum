package net.jforum.util;

import java.util.LinkedHashMap;

public class BoundedLinkedHashMap<K, V> extends LinkedHashMap<K, V>
{
    private int size;
	public BoundedLinkedHashMap(int size)
	{
	    this.size = size;
	}

	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest)
	{
		return this.size() > size;
	}
}