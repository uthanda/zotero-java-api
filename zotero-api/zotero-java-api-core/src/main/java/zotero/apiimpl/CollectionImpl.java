package zotero.apiimpl;

import zotero.api.Collection;
import zotero.api.constants.ZoteroKeys;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.ZoteroRest.URLParameter;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;

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
		collection.getProperties().putValue(ZoteroKeys.Collection.PARENT_COLLECTION, parent.getKey());
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
		ZoteroRestItem.ItemBuilder ib = new ZoteroRestItem.ItemBuilder(delta);

		ZoteroRestData.DataBuilder db = ib.dataBuilder();

		db.addProperty(getProperties().getProperty(ZoteroKeys.Collection.PARENT_COLLECTION));
		db.addProperty(getProperties().getProperty(ZoteroKeys.Collection.NAME));

		if (getVersion() != null)
		{
			ib.version(getVersion());
		}

		return ib.build();
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
}