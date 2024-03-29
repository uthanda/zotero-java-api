package zotero.api.properties;

public interface PropertyEnum<T extends Enum<T>> extends Property<T>
{
	Class<T> getType();
}
