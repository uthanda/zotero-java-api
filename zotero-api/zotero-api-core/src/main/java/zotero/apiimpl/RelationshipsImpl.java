package zotero.apiimpl;

import java.util.List;
import java.util.Map;

import zotero.api.Relationships;

public final class RelationshipsImpl extends PropertiesItemImpl implements Relationships
{
	private RelationshipsImpl(Map<String, Object> values)
	{
		super(values);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getRelationships(String type)
	{
		return (List<String>) getProperties().getRaw(type);
	}

	public static RelationshipsImpl fromMap(Map<String, Object> values)
	{
		return new RelationshipsImpl(values);
	}
}