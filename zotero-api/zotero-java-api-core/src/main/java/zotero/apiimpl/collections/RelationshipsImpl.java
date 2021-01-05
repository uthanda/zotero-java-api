package zotero.apiimpl.collections;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zotero.api.collections.RelationSet;
import zotero.api.collections.Relationships;
import zotero.api.constants.RelationshipType;
import zotero.apiimpl.ChangeTracker;
import zotero.apiimpl.LibraryImpl;

public final class RelationshipsImpl implements Relationships, ChangeTracker
{
	private final EnumMap<RelationshipType, RelationSet> relationships;
	private final LibraryImpl library;
	private boolean isDirty = false;
	private boolean cleared = false;

	public RelationshipsImpl(LibraryImpl library)
	{
		this.relationships = new EnumMap<>(RelationshipType.class);
		this.library = library;
	}

	@Override
	public RelationSet getRelatedItems(RelationshipType type)
	{
		relationships.computeIfAbsent(type, e -> new RelationSetImpl(library, e));

		return relationships.get(type);
	}

	public Set<RelationshipType> getTypes()
	{
		return relationships.keySet();
	}

	public boolean isDirty()
	{
		boolean dirty = this.isDirty;

		for (RelationSet list : relationships.values())
		{
			dirty |= list.isDirty();
		}

		return dirty;
	}

	@Override
	public String toString()
	{
		return String.format("[Relationships: map:%s, dirty:%b]", relationships.toString(), isDirty());
	}

	@Override
	public void clear()
	{
		this.isDirty = true;
		this.cleared = true;
		this.relationships.clear();
	}

	@Override
	public Iterator<RelationSet> iterator()
	{
		return relationships.values().iterator();
	}

	@SuppressWarnings("unchecked")
	public static RelationshipsImpl fromRest(LibraryImpl library, Map<String, Object> values)
	{
		RelationshipsImpl r = new RelationshipsImpl(library);

		if (values == null)
		{
			return r;
		}

		values.forEach((stype, list) -> {
			RelationshipType type = RelationshipType.fromZoteroType(stype);
			Set<String> set = new LinkedHashSet<>((List<String>) list);
			r.relationships.put(type, new RelationSetImpl(library, type, set));
		});

		return r;
	}

	public static Object toRest(RelationshipsImpl relationships)
	{
		if (relationships.cleared)
		{
			return false;
		}

		Map<String, List<String>> zrs = new HashMap<>();

		relationships.relationships.forEach((type, set) -> zrs.put(type.getZoteroName(), new ArrayList<>(((RelationSetImpl) set).getKeys())));

		return zrs;
	}
}