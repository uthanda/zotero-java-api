package zotero.apiimpl;

import zotero.api.Collection;
import zotero.api.constants.ZoteroKeys;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.iterators.TagIterator;
import zotero.api.properties.Property;
import zotero.apiimpl.iterators.TagIteratorImpl;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyImpl;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.ZoteroRest.URLParameter;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.GetBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;
import zotero.apiimpl.rest.response.RestResponse;

public final class CollectionImpl extends EntryImpl implements Collection
{
	private int numItems;
	private int numCollections;

	private CollectionImpl(ZoteroRestItem item, LibraryImpl library)
	{
		super(item, library);
		this.numCollections = ((Double) item.getMeta().get(ZoteroKeys.Meta.NUM_COLLECTIONS)).intValue();
		this.numItems = ((Double) item.getMeta().get(ZoteroKeys.Meta.NUM_ITEMS)).intValue();
	}

	public CollectionImpl(LibraryImpl library)
	{
		super(library);
		this.numCollections = 0;
		this.numItems = 0;
	}

	@Override
	public ItemIterator fetchItems()
	{
		return getLibrary().fetchCollectionItems(this.getKey());
	}

	public static CollectionImpl create(LibraryImpl library, Collection parent)
	{
		CollectionImpl collection = new CollectionImpl(library);

		PropertiesImpl.initializeCollectionProperties((PropertiesImpl) collection.getProperties());

		if (parent != null)
		{
			collection.getProperties().putValue(ZoteroKeys.Collection.PARENT_COLLECTION, parent.getKey());
		}

		return collection;
	}

	public static CollectionImpl fromItem(ZoteroRestItem item, LibraryImpl library)
	{
		return new CollectionImpl(item, library);
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
		ZoteroRestItem item = buildContent(true);

		super.executeUpdate(ZoteroRest.Collections.SPECIFIC, URLParameter.COLLECTION_KEY, getKey(), item);
	}

	private void performCreate()
	{
		ZoteroRestItem item = buildContent(false);

		item = super.executeCreate(ZoteroRest.Collections.ALL, item);

		this.refresh(item);
	}

	private ZoteroRestItem buildContent(boolean delta)
	{
		ZoteroRestData data = new ZoteroRestData();
		
		Object parentCollection = ((PropertyImpl<?>)getProperties().getProperty(ZoteroKeys.Collection.PARENT_COLLECTION)).toRestValue();
		Object name = ((PropertyImpl<?>)getProperties().getProperty(ZoteroKeys.Collection.NAME)).toRestValue();
		
		data.put(ZoteroKeys.Collection.PARENT_COLLECTION, parentCollection);
		data.put(ZoteroKeys.Collection.PARENT_COLLECTION, name);

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
		return getProperties().getString(ZoteroKeys.Collection.NAME);
	}

	@Override
	public void setName(String name)
	{
		getProperties().putValue(ZoteroKeys.Collection.NAME, name);
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
		String parentCollectionKey = getProperties().getString(ZoteroKeys.Collection.PARENT_COLLECTION);

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