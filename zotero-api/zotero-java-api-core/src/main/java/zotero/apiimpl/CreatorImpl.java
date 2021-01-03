package zotero.apiimpl;

import zotero.api.constants.ZoteroKeys;

import java.util.HashMap;
import java.util.Map;

import zotero.api.Creator;
import zotero.api.constants.CreatorType;
import zotero.api.properties.PropertyEnum;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyEnumImpl;
import zotero.apiimpl.properties.PropertyStringImpl;

public final class CreatorImpl extends PropertiesItemImpl implements Creator, ChangeTracker
{
	public CreatorImpl()
	{
		PropertyEnumImpl<CreatorType> creatorTypeProperty = new PropertyEnumImpl<>(ZoteroKeys.Creator.CREATOR_TYPE, CreatorType.class, null);
		((PropertiesImpl)getProperties()).addProperty(new PropertyStringImpl(ZoteroKeys.Creator.LAST_NAME, null));
		((PropertiesImpl)getProperties()).addProperty(new PropertyStringImpl(ZoteroKeys.Creator.FIRST_NAME, null));
		((PropertiesImpl) getProperties()).addProperty(creatorTypeProperty);
	}

	@SuppressWarnings("unchecked")
	public static CreatorImpl fromRest(Object json)
	{
		Map<String, Object> values = (Map<String, Object>) json;

		CreatorImpl creator = new CreatorImpl();

		CreatorType type = null;

		if (values.containsKey(ZoteroKeys.Creator.CREATOR_TYPE))
		{
			type = CreatorType.fromZoteroType((String) values.get(ZoteroKeys.Creator.CREATOR_TYPE));
		}
		
		PropertyEnumImpl<CreatorType> creatorTypeProperty = new PropertyEnumImpl<>(ZoteroKeys.Creator.CREATOR_TYPE, CreatorType.class, type);

		// Get the raw collection so we can add the appropriate properties to it
		PropertiesImpl properties = (PropertiesImpl) creator.getProperties();

		properties.addProperty(new PropertyStringImpl(ZoteroKeys.Creator.LAST_NAME, (String) values.get(ZoteroKeys.Creator.LAST_NAME)));
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.Creator.FIRST_NAME, (String) values.get(ZoteroKeys.Creator.FIRST_NAME)));
		properties.addProperty(creatorTypeProperty);

		return creator;
	}

	public static Map<String, String> toRest(Creator creator)
	{
		String firstName = creator.getProperties().getString(ZoteroKeys.Creator.FIRST_NAME);
		String lastName = creator.getProperties().getString(ZoteroKeys.Creator.LAST_NAME);
		CreatorType type = (CreatorType) creator.getProperties().getProperty(ZoteroKeys.Creator.CREATOR_TYPE).getValue();

		Map<String, String> map = new HashMap<>();

		map.put(ZoteroKeys.Creator.CREATOR_TYPE, type != null ? type.getZoteroName() : null);
		map.put(ZoteroKeys.Creator.FIRST_NAME, firstName);
		map.put(ZoteroKeys.Creator.LAST_NAME, lastName);

		return map;
	}

	@Override
	public CreatorType getType()
	{
		return (CreatorType) getProperties().getProperty(ZoteroKeys.Creator.CREATOR_TYPE).getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setType(CreatorType type)
	{
		((PropertyEnum<CreatorType>) getProperties().getProperty(ZoteroKeys.Creator.CREATOR_TYPE)).setValue(type);
	}

	@Override
	public String getFirstName()
	{
		return getProperties().getString(ZoteroKeys.Creator.FIRST_NAME);
	}

	@Override
	public void setFirstName(String name)
	{
		getProperties().putValue(ZoteroKeys.Creator.FIRST_NAME, name);
	}

	@Override
	public String getLastName()
	{
		return getProperties().getString(ZoteroKeys.Creator.LAST_NAME);
	}

	@Override
	public void setLastName(String name)
	{
		getProperties().putValue(ZoteroKeys.Creator.LAST_NAME, name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isDirty()
	{
		//@formatter:off
		return 
			((PropertyEnumImpl<CreatorType>) getProperties().getProperty(ZoteroKeys.Creator.CREATOR_TYPE)).isDirty() ||
			((PropertyStringImpl) getProperties().getProperty(ZoteroKeys.Creator.FIRST_NAME)).isDirty() ||
			((PropertyStringImpl) getProperties().getProperty(ZoteroKeys.Creator.LAST_NAME)).isDirty();
		//@formatter:on
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toString()
	{
		return String.format("[Creator type:%s, first:%s, last:%s, dirty:%s]", 
				((PropertyEnumImpl<CreatorType>) getProperties().getProperty(ZoteroKeys.Creator.CREATOR_TYPE)).getValue(),
				((PropertyStringImpl) getProperties().getProperty(ZoteroKeys.Creator.FIRST_NAME)).getValue(),
				((PropertyStringImpl) getProperties().getProperty(ZoteroKeys.Creator.LAST_NAME)).getValue(),
				this.isDirty()
				);
	}
}