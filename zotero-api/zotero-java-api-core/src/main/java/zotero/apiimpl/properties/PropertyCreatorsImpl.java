package zotero.apiimpl.properties;

import zotero.api.collections.Creators;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.collections.CreatorsImpl;

public class PropertyCreatorsImpl extends PropertyObjectImpl<Creators>
{
	private CreatorsImpl list;
	
	public PropertyCreatorsImpl(CreatorsImpl values)
	{
		super(ZoteroKeys.Document.CREATORS, Creators.class, values);
		this.list = values;
	}
	
	@Override
	public Object toRestValue()
	{
		return CreatorsImpl.toRest(list);
	}
	
	public static PropertyCreatorsImpl fromRest(LibraryImpl library, Object value)
	{
		return new PropertyCreatorsImpl(CreatorsImpl.fromRest(library, value));
	}
	  
	@Override
	public void clearValue()
	{
		list.clear();
	}
}
