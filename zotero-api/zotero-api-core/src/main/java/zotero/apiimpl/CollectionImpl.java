package zotero.apiimpl;

import zotero.api.Collection;
import zotero.api.Library;
import zotero.api.constants.ZoteroKeys;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.DeleteBuilder;
import zotero.apiimpl.rest.request.builders.PatchBuilder;
import zotero.apiimpl.rest.request.builders.PostBuilder;
import zotero.apiimpl.rest.response.SuccessResponseBuilder;

@SuppressWarnings({ "squid:S2160" })
public final class CollectionImpl extends EntryImpl implements Collection
{
	private int numItems;
	private int numCollections;
	private ZoteroRestItem item;

	private CollectionImpl(ZoteroRestItem item, Library library)
	{
		super(item, library);
		this.numCollections = ((Double) item.getMeta().get("numCollections")).intValue();
		this.numItems = ((Double) item.getMeta().get("numItems")).intValue();
		this.item = item;
	}

	private CollectionImpl(Library library)
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

	public static CollectionImpl create(Library library, Collection parent)
	{
		CollectionImpl collection = new CollectionImpl(library);
		PropertiesImpl.initializeCollectionProperties((PropertiesImpl) collection.getProperties());
		collection.getProperties().putValue(ZoteroKeys.PARENT_COLLECTION, parent.getKey());
		return collection;
	}

	public static CollectionImpl fromItem(ZoteroRestItem item, Library library)
	{
		return new CollectionImpl(item, library);
	}

	@Override
	public void save()
	{
		if (item == null)
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
		PatchBuilder<Boolean,?> builder = PatchBuilder.createBuilder(new SuccessResponseBuilder());
		ZoteroRestItem jsonContent = buildContent(true);
		builder.url(ZoteroRestPaths.COLLECTION).jsonObject(jsonContent);
		((LibraryImpl)getLibrary()).performRequest(builder);
	}

	private void performCreate()
	{
		PostBuilder<Boolean,?> builder = PostBuilder.createBuilder(new SuccessResponseBuilder());
		ZoteroRestItem jsonContent = buildContent(true);
		builder.url(ZoteroRestPaths.COLLECTION).jsonObject(jsonContent);
		((LibraryImpl)getLibrary()).performRequest(builder);
	}

	private ZoteroRestItem buildContent(boolean delta)
	{
		ZoteroRestItem.ItemBuilder ib = new ZoteroRestItem.ItemBuilder(delta);
		
		ZoteroRestData.DataBuilder db = ib.dataBuilder();
		
		db.addProperty(getProperties().getProperty(ZoteroKeys.PARENT_COLLECTION));
		db.addProperty(getProperties().getProperty(ZoteroKeys.NAME));
		
		return ib.build();
	}

	@Override
	public void delete()
	{
		DeleteBuilder<Boolean,?> builder = DeleteBuilder.createBuilder(new SuccessResponseBuilder());
		builder.url(ZoteroRestPaths.COLLECTIONS).itemKey(this.getKey());
		((LibraryImpl)getLibrary()).performRequest(builder);
	}

	@Override
	public void refresh()
	{
		// TBI
	}

	@Override
	public CollectionIterator fetchSubCollections()
	{
		return ((LibraryImpl) getLibrary()).fetchCollections(ZoteroRestPaths.COLLECTIONS_SUBCOLLECTIONS, this.getKey());
	}

	@Override
	public String getName()
	{
		return getProperties().getString(ZoteroKeys.NAME);
	}

	@Override
	public void setName(String name)
	{
		getProperties().putValue(ZoteroKeys.NAME, name);
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
		String parentCollectionKey = getProperties().getString(ZoteroKeys.PARENT_COLLECTION);

		if (parentCollectionKey == null)
		{
			return null;
		}

		return ((LibraryImpl) getLibrary()).fetchCollection(parentCollectionKey);
	}

	@Override
	String getDeletePath()
	{
		return ZoteroRestPaths.COLLECTIONS;
	}
}