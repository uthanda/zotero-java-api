package zotero.apiimpl.collections;

import static zotero.api.constants.ZoteroKeys.Document.CREATORS;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import zotero.api.Creator;
import zotero.api.collections.Creators;
import zotero.api.constants.CreatorType;
import zotero.apiimpl.ChangeTracker;
import zotero.apiimpl.CreatorImpl;
import zotero.apiimpl.properties.PropertyListImpl;

public final class CreatorsImpl extends PropertyListImpl.ObservableList<Creator> implements Creators, ChangeTracker
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2763232832891672780L;

	public CreatorsImpl()
	{
		super(CREATORS, null, false);
	}

	public CreatorsImpl(List<Creator> list)
	{
		super(CREATORS, list, false);
	}

	@Override
	public void add(CreatorType type, String firstName, String lastName)
	{
		CreatorImpl creator = new CreatorImpl();
		creator.setFirstName(firstName);
		creator.setLastName(lastName);
		creator.setType(type);

		this.add(creator);
	}

	public static CreatorsImpl fromRest(Object value)
	{
		if (value instanceof Boolean && Boolean.FALSE.equals(value))
		{
			return new CreatorsImpl();
		}

		List<Creator> list = ((List<?>) value).stream().map(CreatorImpl::fromRest).collect(Collectors.toList());
		return new CreatorsImpl(list);
	}

	public static List<Map<String, String>> toRest(Creators creators)
	{
		return creators.stream().map(CreatorImpl::toRest).collect(Collectors.toList());
	}

	@Override
	public boolean isDirty()
	{
		return super.isDirty() || this.parallelStream().anyMatch(e -> ((CreatorImpl)e).isDirty());
	}

	@Override
	public String toString()
	{
		return String.format("[Creators list:%s, dirty:%b]", super.toString(), isDirty());
	}
}