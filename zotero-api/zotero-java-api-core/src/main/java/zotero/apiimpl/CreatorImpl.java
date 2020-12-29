package zotero.apiimpl;

import static zotero.api.constants.ZoteroKeys.Creator.CREATOR_TYPE;
import static zotero.api.constants.ZoteroKeys.Creator.FIRST_NAME;
import static zotero.api.constants.ZoteroKeys.Creator.LAST_NAME;

import java.util.HashMap;
import java.util.Map;

import zotero.api.Creator;
import zotero.api.constants.CreatorType;
import zotero.api.properties.PropertyObject;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyObjectImpl;
import zotero.apiimpl.properties.PropertyStringImpl;

public final class CreatorImpl extends PropertiesItemImpl implements Creator
{
	private CreatorImpl(Map<String, Object> values)
	{
		// TODO move this to the static fromRest
		PropertyObjectImpl<CreatorType> creatorTypeProperty = new PropertyObjectImpl<>(CREATOR_TYPE, CreatorType.class, null);

		if (values.containsKey(CREATOR_TYPE))
		{
			creatorTypeProperty.setValue(CreatorType.fromZoteroType((String) values.get(CREATOR_TYPE)));
		}

		// Get the raw collection so we can add the appropriate properties to it
 		PropertiesImpl properties = (PropertiesImpl) getProperties();
 		
		properties.addProperty(new PropertyStringImpl(LAST_NAME, (String) values.get(LAST_NAME)));
 		properties.addProperty(new PropertyStringImpl(FIRST_NAME, (String) values.get(FIRST_NAME)));
		properties.addProperty(creatorTypeProperty);
	}

	public CreatorImpl()
	{
		PropertyObjectImpl<CreatorType> creatorTypeProperty = new PropertyObjectImpl<>(CREATOR_TYPE, CreatorType.class, null);
		getProperties().putValue(LAST_NAME, null);
		getProperties().putValue(FIRST_NAME, null);
		((PropertiesImpl) getProperties()).addProperty(creatorTypeProperty);
	}

	@SuppressWarnings("unchecked")
	public static CreatorImpl fromRest(Object values)
	{
		return new CreatorImpl((Map<String, Object>) values);
	}

	public static Map<String,String> toRest(Creator creator)
	{
		String firstName = creator.getProperties().getString(FIRST_NAME);
		String lastName = creator.getProperties().getString(LAST_NAME);
		CreatorType type = (CreatorType) creator.getProperties().getProperty(CREATOR_TYPE).getValue();

		Map<String,String> map = new HashMap<>();
		
		map.put(CREATOR_TYPE, type != null ? type.getZoteroName() : null);
		map.put(FIRST_NAME, firstName);
		map.put(LAST_NAME, lastName);

		return map;
	}

	@Override
	public CreatorType getType()
	{
		return (CreatorType)getProperties().getProperty(CREATOR_TYPE).getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setType(CreatorType type)
	{
		((PropertyObject<CreatorType>) getProperties().getProperty(CREATOR_TYPE)).setValue(type);
	}

	@Override
	public String getFirstName()
	{
		return getProperties().getString(FIRST_NAME);
	}

	@Override
	public void setFirstName(String name)
	{
		getProperties().putValue(FIRST_NAME, name);
	}

	@Override
	public String getLastName()
	{
		return getProperties().getString(LAST_NAME);
	}

	@Override
	public void setLastName(String name)
	{
		getProperties().putValue(LAST_NAME, name);
	}
}