package zotero.api.properties;

import java.util.List;

public interface PropertyList<T> extends Property
{
	Class<T> getType();

	List<T> getValue();
}
