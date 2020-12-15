package zotero.apiimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zotero.api.Creator;
import zotero.api.constants.CreatorType;
import zotero.api.internal.rest.model.ZoteroRestCreator;

public final class CreatorImpl extends PropertiesItemImpl implements Creator
{
	private static final String LAST_NAME = "lastName";
	private static final String FIRST_NAME = "firstName";
	private static final String CREATOR_TYPE = "creatorType";

	public CreatorImpl(Map<String, Object> values)
	{
		super(values);
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
		ZoteroRestCreator zrc = new ZoteroRestCreator();
		zrc.setCreatorType(creator.getProperties().getString(CREATOR_TYPE));
		zrc.setFirstName(creator.getProperties().getString(FIRST_NAME));
		zrc.setLastName(creator.getProperties().getString(LAST_NAME));

		return zrc;
	}

	@Override
	public CreatorType getType()
	{
		String type = getProperties().getString(CREATOR_TYPE);
		return CreatorType.fromZoteroType(type);
	}

	@Override
	public void setType(CreatorType type)
	{
		getProperties().putProperty(CREATOR_TYPE, type.name());
	}

	@Override
	public String getFirstName()
	{
		return getProperties().getString(FIRST_NAME);
	}

	@Override
	public void setFirstName(String name)
	{
		getProperties().putProperty(FIRST_NAME, name);
	}

	@Override
	public String getLastName()
	{
		return getProperties().getString(LAST_NAME);
	}

	@Override
	public void setLastName(String name)
	{
		getProperties().putProperty(LAST_NAME, name);
	}
}