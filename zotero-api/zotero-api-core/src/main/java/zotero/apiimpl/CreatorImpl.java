package zotero.apiimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zotero.api.Creator;
import zotero.api.properties.*;
import zotero.api.constants.CreatorType;
import zotero.api.internal.rest.model.ZoteroRestCreator;
import zotero.apiimpl.properties.*;

public final class CreatorImpl extends PropertiesItemImpl implements Creator
{
	private static final String LAST_NAME = "lastName";
	private static final String FIRST_NAME = "firstName";
	private static final String CREATOR_TYPE = "creatorType";

	public CreatorImpl(Map<String, Object> values)
	{
		PropertyObjectImpl<CreatorType> creatorTypeProperty = new PropertyObjectImpl<>(CREATOR_TYPE, CreatorType.class, null);

		if (values.containsKey(CREATOR_TYPE))
		{
			creatorTypeProperty.setValue(CreatorType.fromZoteroType((String) values.get(CREATOR_TYPE)));
		}

		getProperties().putValue(LAST_NAME, (String) values.get(LAST_NAME));
		getProperties().putValue(FIRST_NAME, (String) values.get(FIRST_NAME));
		((PropertiesImpl) getProperties()).addProperty(creatorTypeProperty);
	}

	public CreatorImpl()
	{
		PropertyObjectImpl<CreatorType> creatorTypeProperty = new PropertyObjectImpl<>(CREATOR_TYPE, CreatorType.class, null);
		getProperties().putValue(LAST_NAME, null);
		getProperties().putValue(FIRST_NAME, null);
		((PropertiesImpl) getProperties()).addProperty(creatorTypeProperty);
	}

	@SuppressWarnings("unchecked")
	public static CreatorImpl fromMap(Object values)
	{
		return new CreatorImpl((Map<String, Object>) values);
	}

	private static List<ZoteroRestCreator> gatherChanges(List<Creator> creators)
	{
		if (creators == null)
		{
			return null;
		}

		List<ZoteroRestCreator> list = new ArrayList<>();

		creators.forEach(creator -> {
			ZoteroRestCreator c = new ZoteroRestCreator();
			c.setCreatorType(creator.getProperties().getString(CREATOR_TYPE));
			c.setFirstName(creator.getProperties().getString(FIRST_NAME));
			c.setLastName(creator.getProperties().getString(LAST_NAME));
		});

		return list;
	}

	public static ZoteroRestCreator to(Creator creator)
	{
		String firstName = creator.getProperties().getString(FIRST_NAME);
		String lastName = creator.getProperties().getString(LAST_NAME);
		CreatorType type = (CreatorType) creator.getProperties().getProperty(CREATOR_TYPE).getValue();
		
		ZoteroRestCreator zrc = new ZoteroRestCreator();
		zrc.setCreatorType(type != null ? type.getZoteroName() : null);
		zrc.setFirstName(firstName);
		zrc.setLastName(lastName);

		return zrc;
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