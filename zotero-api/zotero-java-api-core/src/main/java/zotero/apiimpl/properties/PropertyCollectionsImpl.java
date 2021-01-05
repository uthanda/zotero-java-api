package zotero.apiimpl.properties;

import java.util.List;

import zotero.api.collections.Collections;
import zotero.api.constants.ZoteroKeys;
import zotero.api.properties.Property;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.collections.CollectionsImpl;

public class PropertyCollectionsImpl extends PropertyObjectImpl<Collections>
{
	public PropertyCollectionsImpl(Collections value)
	{
		super(ZoteroKeys.ItemKeys.COLLECTIONS, Collections.class, value);
	}

	@SuppressWarnings({"squid:S1452","unchecked"})
	public static Property<?> fromRest(LibraryImpl library, Object value)
	{
		CollectionsImpl collections = CollectionsImpl.fromRest(library, (List<String>) value);

		return new PropertyCollectionsImpl(collections);
	}
	
	@Override
	public Object toRestValue()
	{
		return CollectionsImpl.toRest((CollectionsImpl) getValue());
	}
	
	@Override
	public void clearValue()
	{
		super.clearValue();
		getValue().clear();
	}
}
