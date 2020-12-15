package zotero.apiimpl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import zotero.api.Collection;
import zotero.api.Item;
import zotero.api.Library;
import zotero.api.Relationships;
import zotero.api.Tag;
import zotero.api.collections.CreatorsList;
import zotero.api.constants.ItemType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.internal.rest.builders.PutBuilder;
import zotero.api.internal.rest.impl.ZoteroRestPutRequest;
import zotero.api.internal.rest.model.ZoteroRestData;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.internal.rest.model.ZoteroRestMeta;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.apiimpl.properties.PropertiesImpl;

@SuppressWarnings({ "squid:S2160" })
final class ItemImpl extends EntryImpl implements Item
{
	private static final String RELATIONS = "relations";

	static final String URI_ITEMS_ALL = "/items";
	static final String URI_ITEMS_TOP = "/items/top";
	static final String URI_ITEMS_TRASH = "/items/trash";
	static final String URI_ITEM = "/items/{key}";
	private static final String URI_ITEM_CHILDREN = "/items/{key}/children";
	@SuppressWarnings("unused") // Implementation coming later
	private static final String URI_ITEM_TAGS = "/items/{key}/tags";

	@SuppressWarnings("unused")
	private ZoteroRestItem jsonItem;

	private int numChildren;

	private ItemImpl(ZoteroRestItem item, Library library)
	{
		super(item, library);
	}

	ItemImpl(ItemType type, Library library)
	{
		super(type, library);
	}
	
	@Override
	public String getTitle()
	{
		return super.getProperties().getString(ZoteroKeys.TITLE);
	}

	@Override
	public CreatorsList getCreators()
	{
		return (CreatorsList) super.getProperties().getRaw(ZoteroKeys.CREATORS);
	}

	@Override
	public Date getDateAdded()
	{
		return DatatypeConverter.parseDateTime(super.getProperties().getString(ZoteroKeys.DATE_ADDED)).getTime();
	}

	@Override
	public Date getDateModified()
	{
		return DatatypeConverter.parseDateTime(super.getProperties().getString(ZoteroKeys.DATE_MODIFIED)).getTime();
	}

	@Override
	public String getItemType()
	{
		return getProperties().getString(ZoteroKeys.ITEM_TYPE);
	}

	@Override
	public String getRights()
	{
		return getProperties().getString(ZoteroKeys.RIGHTS);
	}

	@Override
	public String getURL()
	{
		return getProperties().getString(ZoteroKeys.URL);
	}

	@Override
	public String getShortTitle()
	{
		return getProperties().getString(ZoteroKeys.SHORT_TITLE);
	}

	@Override
	public Date getAccessDate()
	{
		return DatatypeConverter.parseDateTime(super.getProperties().getString(ZoteroKeys.ACCESS_DATE)).getTime();
	}

	@Override
	public String getExtra()
	{
		return getProperties().getString(ZoteroKeys.EXTRA);
	}

	@Override
	public String getAbstractNote()
	{
		return getProperties().getString(ZoteroKeys.ABSTRACT_NOTE);
	}

	@Override
	public CollectionIterator getCollections()
	{
		return new CollectionIterator()
		{
			@SuppressWarnings("unchecked")
			private List<String> set = (List<String>) getProperties().getRaw(ZoteroKeys.COLLECTIONS);
			private Iterator<String> i = set.iterator();

			@Override
			public boolean hasNext()
			{
				return i.hasNext();
			}

			@Override
			public Collection next()
			{
				return getLibrary().fetchCollection(i.next());
			}

			@Override
			public int getTotalCount()
			{
				return set.size();
			}
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Tag> getTags()
	{
		return (List<Tag>) getProperties().getRaw(ZoteroKeys.TAGS);
	}

	static Item fromItem(ZoteroRestItem jsonItem, Library library)
	{
		ItemImpl item = new ItemImpl(jsonItem, library);
		item.jsonItem = jsonItem;

		EntryImpl.loadLinks(item, jsonItem.getLinks());

		Double numChildren = (Double) jsonItem.getMeta().get(ZoteroRestMeta.NUM_CHILDREN);
		item.numChildren = numChildren == null ? 0 : numChildren.intValue();
		return item;
	}

	@Override
	public ItemIterator fetchChildren()
	{
		return ((LibraryImpl) getLibrary()).fetchItems(URI_ITEM_CHILDREN, this.getKey());
	}

	@Override
	public void refresh() throws Exception
	{
		// TODO
	}

	@Override
	public final int getNumberOfChilden()
	{
		return numChildren;
	}

	@Override
	public void save()
	{
		// This is a new item, so we do a put
		if (this.jsonItem == null)
		{
			ZoteroRestItem item = new ZoteroRestItem();
			item.setKey(this.getKey());

			ZoteroRestData data = new ZoteroRestData();
			PropertiesImpl.gatherProperties(data, this.getProperties());
			
			PutBuilder builder = ZoteroRestPutRequest.Builder.createBuilder();
			builder.content(item);
			
			((LibraryImpl)getLibrary()).performPut((builder));
		}
		else
		{

		}
	}

	@Override
	public void delete()
	{
		// TODO
	}

	@Override
	public Relationships getRelationships()
	{
		return (Relationships) super.getProperties().getRaw(RELATIONS);
	}
}