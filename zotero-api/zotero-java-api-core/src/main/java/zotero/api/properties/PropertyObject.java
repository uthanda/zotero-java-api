package zotero.api.properties;

public interface PropertyObject<T> extends Property<T>
{
	Class<T> getType();
}
