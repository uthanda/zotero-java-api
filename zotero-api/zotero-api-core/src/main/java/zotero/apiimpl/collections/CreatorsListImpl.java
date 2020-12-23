package zotero.apiimpl.collections;

import java.util.List;

import zotero.api.Creator;
import zotero.api.collections.Creators;
import zotero.api.constants.CreatorType;
import zotero.apiimpl.CreatorImpl;
import zotero.apiimpl.properties.PropertyListImpl;

public final class CreatorsListImpl extends PropertyListImpl.ObservableList<Creator> implements Creators
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2763232832891672780L;
	
	public CreatorsListImpl()
	{
	}
	
	public CreatorsListImpl(List<Creator> list)
	{
		super(list);
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
}