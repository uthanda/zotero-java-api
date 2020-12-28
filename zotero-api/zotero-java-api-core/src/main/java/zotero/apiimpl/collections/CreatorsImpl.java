package zotero.apiimpl.collections;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import zotero.api.Creator;
import zotero.api.collections.Creators;
import zotero.api.constants.CreatorType;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.CreatorImpl;
import zotero.apiimpl.properties.PropertyListImpl;

public final class CreatorsImpl extends PropertyListImpl.ObservableList<Creator> implements Creators
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2763232832891672780L;
	
	public CreatorsImpl()
	{
		super(ZoteroKeys.CREATORS, null, false);
	}
	
	public CreatorsImpl(List<Creator> list)
	{
		super(ZoteroKeys.CREATORS, list, false);
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

	public static Creators fromRest(List<?> listValue)
	{
		List<Creator> list = listValue.stream().map(CreatorImpl::fromRest).collect(Collectors.toList());
		return new CreatorsImpl(list);
	}

	public static List<Map<String, String>> toRest(Creators creators)
	{
		return creators.stream().map(CreatorImpl::toRest).collect(Collectors.toList());
	}
}