package zotero.api.properties;

public interface PropertyObject<T> extends Property
{
	T getValue();
	
	void setValue(T value);

	Class<T> getType();
}
