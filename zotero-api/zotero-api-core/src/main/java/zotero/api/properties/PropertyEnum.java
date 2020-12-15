package zotero.api.properties;

public interface PropertyEnum<T extends Enum<T>> extends Property
{
	T getValue();

	void setValue(T value);

	Class<T> getType();
}
