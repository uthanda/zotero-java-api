package zotero.apiimpl;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zotero.api.collections.Relationships;
import zotero.api.constants.RelationshipType;
import zotero.apiimpl.properties.PropertyListImpl.ObservableList;

public final class RelationshipsImpl implements Relationships
{
	private EnumMap<RelationshipType, ObservableList<String>> relationships;

	public RelationshipsImpl()
	{
		this.relationships = new EnumMap<>(RelationshipType.class);
	}

	@Override
	public List<String> getRelationships(RelationshipType type)
	{
		if (!relationships.containsKey(type))
		{
			relationships.put(type, new ObservableList<>(type.getZoteroName(), null, false));
		}

		return relationships.get(type);
	}

	public Set<RelationshipType> getTypes()
	{
		return relationships.keySet();
	}

	@SuppressWarnings("unchecked")
	public static RelationshipsImpl fromMap(Map<String, Object> values)
	{
		RelationshipsImpl r = new RelationshipsImpl();

		if (values == null)
		{
			return r;
		}
		
		values.forEach((stype, list) -> {
			RelationshipType type = RelationshipType.fromZoteroType(stype);
			r.relationships.put(type, new ObservableList<>(type.getZoteroName(), (List<String>) list, false));
		});

		return r;
	}

	public boolean isDirty()
	{
		boolean isDirty = false;

		for (ObservableList<String> list : relationships.values())
		{
			isDirty |= list.isDirty();
		}

		return isDirty;
	}

	public static Map<String, List<String>> toRest(Relationships relationships)
	{
		Map<String, List<String>> zrs = new HashMap<>();

		relationships.getTypes().forEach(type -> zrs.put(type.getZoteroName(), relationships.getRelationships(type)));

		return zrs;
	}
}