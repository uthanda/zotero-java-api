package zotero.apiimpl;

import zotero.api.Collection;
import zotero.api.constants.ZoteroKeys;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.iterators.TagIterator;
import zotero.apiimpl.iterators.TagIteratorImpl;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyStringImpl;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.ZoteroRest.URLParameter;
import zotero.apiimpl.rest.model.SerializationMode;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.GetBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;
import zotero.apiimpl.rest.response.RestResponse;

public final class CollectionImpl extends EntryImpl implements Collection
{
	private int numItems;
	private int numCollections;

	public CollectionImpl(LibraryImpl library, ZoteroRestItem item)
	{
		this(library, ((Double) item.getMeta().get(ZoteroKeys.MetaKeys.NUM_COLLECTIONS)).intValue(), ((Double) item.getMeta().get(ZoteroKeys.MetaKeys.NUM_ITEMS)).intValue());
	}

	public CollectionImpl(LibraryImpl library)
	{
		this(library,0,0);
	}

	private CollectionImpl(LibraryImpl library, int numCollections, int numItems)
	{
		super(library);
		this.numCollections = numCollections;
		this.numItems = numItems;
		
		PropertiesImpl properties = (PropertiesImpl) getProperties();
		
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.CollectionKeys.NAME, null));
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.CollectionKeys.PARENT_COLLECTION, null));
	}

	@Override
	public ItemIterator fetchItems()
	{
		return getLibrary().fetchCollectionItems(this.getKey());
	}

	public static CollectionImpl create(LibraryImpl library, Collection parent)
	{
		CollectionImpl collection = new CollectionImpl(library);

		if (parent != null)
		{
			collection.getProperties().putValue(ZoteroKeys.CollectionKeys.PARENT_COLLECTION, parent.getKey());
		}

		return collection;
	}

	public static CollectionImpl fromItem(LibraryImpl library, ZoteroRestItem item)
	{
		CollectionImpl collection = new CollectionImpl(library, item);
		
		collection.refresh(item);
		
		return collection;
	}

	@Override
	public void save()
	{
		if (getVersion() == null)
		{
			performCreate();
		}
		else
		{
			performPatch();
		}
	}

	private void performPatch()
	{
		// Either If-Unmodified-Since-Version or object version property must be
		// provided for key-based writes
		ZoteroRestItem item = buildContent(SerializationMode.PARTIAL);

		super.executeUpdate(ZoteroRest.Collections.SPECIFIC, URLParameter.COLLECTION_KEY, getKey(), item);
	}

	private void performCreate()
	{
		ZoteroRestItem item = buildContent(SerializationMode.FULL);

		item = super.executeCreate(ZoteroRest.Collections.ALL, item);

		this.refresh(item);
	}

	private ZoteroRestItem buildContent(SerializationMode mode)
	{
		ZoteroRestData data = new ZoteroRestData();
		
		PropertiesImpl.toRest(data, getProperties(), mode);

		ZoteroRestItem item = new ZoteroRestItem();
		item.setData(data);
		
		if (getVersion() != null)
		{
			item.setVersion(getVersion());
		}

		return item;
	}

	@Override
	public void refresh()
	{
		// TBI
	}

	@Override
	public CollectionIterator fetchSubCollections()
	{
		return ((LibraryImpl) getLibrary()).fetchCollections(ZoteroRest.Collections.SUBCOLLECTIONS, this.getKey());
	}

	@Override
	public String getName()
	{
		return getProperties().getString(ZoteroKeys.CollectionKeys.NAME);
	}

	@Override
	public void setName(String name)
	{
		getProperties().putValue(ZoteroKeys.CollectionKeys.NAME, name);
	}

	@Override
	public int getNumberOfCollections()
	{
		return numCollections;
	}

	@Override
	public int getNumberOfItems()
	{
		return numItems;
	}

	@SuppressWarnings({ "squid:S1168" })
	@Override
	public Collection fetchParentCollection()
	{
		String parentCollectionKey = getProperties().getString(ZoteroKeys.CollectionKeys.PARENT_COLLECTION);

		if (parentCollectionKey == null)
		{
			return null;
		}

		return ((LibraryImpl) getLibrary()).fetchCollection(parentCollectionKey);
	}

	@Override
	public TagIterator fetchTags(boolean top)
	{
		LibraryImpl library = (LibraryImpl) getLibrary();

		GetBuilder<ZoteroRestItem[], ?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem[].class));
		builder.url(ZoteroRest.Tags.COLLECTION_TAGS);
		builder.urlParam(URLParameter.COLLECTION_KEY, this.getKey());

		RestResponse<ZoteroRestItem[]> response = library.performRequest(builder);

		return new TagIteratorImpl(response, library);
	}
}