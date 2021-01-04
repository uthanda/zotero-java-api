package zotero.apiimpl.properties;

import java.util.Map;

import zotero.api.collections.Relationships;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.collections.RelationshipsImpl;

public class PropertyRelationshipsImpl extends PropertyObjectImpl<Relationships>
{
	public PropertyRelationshipsImpl(Relationships value)
	{
		super(ZoteroKeys.Item.RELATIONS, Relationships.class, value);
	}

	@SuppressWarnings("unchecked")
	public static PropertyRelationshipsImpl fromRest(LibraryImpl library, Object value)
	{
		RelationshipsImpl relationships = RelationshipsImpl.fromRest(library, (Map<String, Object>) value);
		
		return new PropertyRelationshipsImpl(relationships);
	}

	@Override
	public Object toRestValue()
	{
		return RelationshipsImpl.toRest((RelationshipsImpl) getValue());
	}
	
	@Override
	public void clearValue()
	{
		super.clearValue();
		getValue().clear();
	}
}
