package zotero.apiimpl;

import java.util.HashMap;
import java.util.Map;

import zotero.api.Creator;
import zotero.api.constants.CreatorType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.properties.PropertyObject;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyObjectImpl;
import zotero.apiimpl.properties.PropertyStringImpl;

public final class CreatorImpl extends PropertiesItemImpl implements Creator
{
	public CreatorImpl(Map<String, Object> values)
	{
		PropertyObjectImpl<CreatorType> creatorTypeProperty = new PropertyObjectImpl<>(ZoteroKeys.CREATOR_TYPE, CreatorType.class, null);

		if (values.containsKey(ZoteroKeys.CREATOR_TYPE))
		{
			creatorTypeProperty.setValue(CreatorType.fromZoteroType((String) values.get(ZoteroKeys.CREATOR_TYPE)));
		}

 		((PropertiesImpl) getProperties()).addProperty(new PropertyStringImpl(ZoteroKeys.LAST_NAME, (String) values.get(ZoteroKeys.LAST_NAME)));
 		((PropertiesImpl) getProperties()).addProperty(new PropertyStringImpl(ZoteroKeys.FIRST_NAME, (String) values.get(ZoteroKeys.FIRST_NAME)));
		((PropertiesImpl) getProperties()).addProperty(creatorTypeProperty);
	}

	public CreatorImpl()
	{
		PropertyObjectImpl<CreatorType> creatorTypeProperty = new PropertyObjectImpl<>(ZoteroKeys.CREATOR_TYPE, CreatorType.class, null);
		getProperties().putValue(ZoteroKeys.LAST_NAME, null);
		getProperties().putValue(ZoteroKeys.FIRST_NAME, null);
		((PropertiesImpl) getProperties()).addProperty(creatorTypeProperty);
	}

	@SuppressWarnings("unchecked")
	public static CreatorImpl fromRest(Object values)
	{
		return new CreatorImpl((Map<String, Object>) values);
	}

	public static Map<String,String> toRest(Creator creator)
	{
		String firstName = creator.getProperties().getString(ZoteroKeys.FIRST_NAME);
		String lastName = creator.getProperties().getString(ZoteroKeys.LAST_NAME);
		CreatorType type = (CreatorType) creator.getProperties().getProperty(ZoteroKeys.CREATOR_TYPE).getValue();

		Map<String,String> map = new HashMap<>();
		
		map.put(ZoteroKeys.CREATOR_TYPE, type != null ? type.getZoteroName() : null);
		map.put(ZoteroKeys.FIRST_NAME, firstName);
		map.put(ZoteroKeys.LAST_NAME, lastName);

		return map;
	}

	@Override
	public CreatorType getType()
	{
		return (CreatorType)getProperties().getProperty(ZoteroKeys.CREATOR_TYPE).getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setType(CreatorType type)
	{
		((PropertyObject<CreatorType>) getProperties().getProperty(ZoteroKeys.CREATOR_TYPE)).setValue(type);
	}

	@Override
	public String getFirstName()
	{
		return getProperties().getString(ZoteroKeys.FIRST_NAME);
	}

	@Override
	public void setFirstName(String name)
	{
		getProperties().putValue(ZoteroKeys.FIRST_NAME, name);
	}

	@Override
	public String getLastName()
	{
		return getProperties().getString(ZoteroKeys.LAST_NAME);
	}

	@Override
	public void setLastName(String name)
	{
		getProperties().putValue(ZoteroKeys.LAST_NAME, name);
	}
}