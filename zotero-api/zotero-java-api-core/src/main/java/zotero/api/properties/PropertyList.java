package zotero.api.properties;

import java.util.List;

public interface PropertyList<T,L extends List<T>> extends Property<L>
{
	Class<T> getType();
}
