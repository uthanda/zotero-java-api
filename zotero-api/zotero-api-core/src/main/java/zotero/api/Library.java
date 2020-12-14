package zotero.api;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zotero.api.constants.ZoteroKeys;
import zotero.api.internal.rest.RestGetRequest;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.ZoteroGetUserAPIRequest;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.internal.rest.model.ZoteroRestLink;
import zotero.api.internal.rest.model.ZoteroRestLinks;
import zotero.api.internal.rest.model.ZoteroRestMeta;
import zotero.api.internal.rest.schema.ZoteroSchema;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.iterators.ZoteroIterator;

@SuppressWarnings({ "squid:S1610" })
public abstract class Library
{
	private static final class LibraryImpl extends Library
	{
		private final ZoteroAPIKey apiKey;
		private final String userId;

		private LibraryImpl(ZoteroAPIKey apiKey, String userId)
		{
			this.apiKey = apiKey;
			this.userId = userId;
		}

		@Override
		public String getUserId()
		{
			return userId;
		}

		@Override
		public Collection fetchCollection(String key)
		{
			RestGetRequest<ZoteroRestItem> req = createGetRequest(ZoteroRestItem.class);

			req.apiUrl(CollectionImpl.URI_COLLECTION).addUrlParam("key", key);

			RestResponse<ZoteroRestItem> response = performGet(req);

			return CollectionImpl.fromItem(response.getResponse(), this);
		}

		private <T> RestResponse<T> performGet(RestGetRequest<T> req)
		{
			RestResponse<T> resp = req.get();

			if (resp.wasSuccessful())
			{
				return resp;
			}
			
			throw new RuntimeException(resp.getErrorMessage());
		}

		@Override
		public ItemIterator fetchCollectionItems(String key)
		{
			return fetchItems(CollectionImpl.URI_COLLECTION_ITEMS, key);
		}

		@Override
		public ItemIterator fetchCollectionItemsTop(String key)
		{
			return fetchItems(CollectionImpl.URI_COLLECTION_ITEMS_TOP, key);
		}

		@Override
		public CollectionIterator fetchCollectionsAll()
		{
			return fetchCollections(CollectionImpl.URI_COLLECTIONS_ALL, null);
		}

		@Override
		public CollectionIterator fetchCollectionsTop()
		{
			return fetchCollections(CollectionImpl.URI_COLLECTIONS_TOP, null);
		}

		private CollectionIterator fetchCollections(String url, String key)
		{
			RestGetRequest<ZoteroRestItem[]> req = createGetRequest(ZoteroRestItem[].class);
			req.apiUrl(url);

			if (key != null)
			{
				req.addUrlParam("key", key);
			}

			RestResponse<ZoteroRestItem[]> response = performGet(req);

			return new CollectionIteratorImpl(response, this);
		}

		@Override
		public ItemIterator fetchItemsAll()
		{
			return fetchItems(ItemImpl.URI_ITEMS_ALL, null);
		}

		@Override
		public ItemIterator fetchItemsTop()
		{
			return fetchItems(ItemImpl.URI_ITEMS_TOP, null);
		}

		@Override
		public ItemIterator fetchItemsTrash()
		{
			return fetchItems(ItemImpl.URI_ITEMS_TRASH, null);
		}

		@Override
		public Item fetchItem(String key)
		{
			RestGetRequest<ZoteroRestItem> req = createGetRequest(ZoteroRestItem.class);

			req.apiUrl(ItemImpl.URI_ITEM).addUrlParam("key", key);

			RestResponse<ZoteroRestItem> response = performGet(req);

			return ItemImpl.fromItem(response.getResponse(), this);
		}

		private ItemIterator fetchItems(String url, String key)
		{
			RestGetRequest<ZoteroRestItem[]> req = createGetRequest(ZoteroRestItem[].class);
			req.apiUrl(url);

			if (key != null)
			{
				req.addUrlParam("key", key);
			}

			RestResponse<ZoteroRestItem[]> response = performGet(req);

			return new ZoteroItemIteratorImpl(response, this);
		}

		private <T> RestGetRequest<T> createGetRequest(Class<T> type)
		{
			return ZoteroGetUserAPIRequest.create(apiKey, userId, type);
		}
	}

	private static final class LinkImpl implements Link
	{
		private String uri;
		@SuppressWarnings("unused")
		private String newUri = null;
		private String type;
		@SuppressWarnings("unused")
		private String newType = null;

		@Override
		public final String getUri()
		{
			return uri;
		}

		@Override
		public final void setUri(String uri)
		{
			this.newUri = uri;
		}

		@Override
		public final String getType()
		{
			return type;
		}

		@Override
		public final void setType(String type)
		{
			this.newType = type;
		}

		public static LinkImpl from(ZoteroRestLink jsonLink)
		{
			LinkImpl link = new LinkImpl();

			link.type = jsonLink.getType();
			link.uri = jsonLink.getHref();

			return link;
		}
	}

	@SuppressWarnings({ "squid:S2160" })
	private static final class ItemImpl extends EntryImpl implements Item
	{
		private static final String RELATIONS = "relations";

		private static final String URI_ITEMS_ALL = "/items";
		private static final String URI_ITEMS_TOP = "/items/top";
		private static final String URI_ITEMS_TRASH = "/items/trash";
		private static final String URI_ITEM = "/items/{key}";
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

		@Override
		public String getTitle()
		{
			return super.getProperties().getString(ZoteroKeys.TITLE);
		}

		@Override
		@SuppressWarnings("unchecked")
		public List<Creator> getCreators()
		{
			return (List<Creator>) super.getProperties().getRaw(ZoteroKeys.CREATORS);
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
				private Set<String> set = (Set<String>) getProperties().getRaw(ZoteroKeys.COLLECTIONS);
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

		private static Item fromItem(ZoteroRestItem jsonItem, Library library)
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
		public void save() throws Exception
		{
			// TODO
		}

		@Override
		public void delete() throws Exception
		{
			// TODO
		}

		@Override
		public Relationships getRelationships()
		{
			return (Relationships) super.getProperties().getRaw(RELATIONS);
		}
	}

	private static final class PropertiesImpl implements Properties
	{
		private static final Logger logger = LogManager.getLogger(PropertiesImpl.class);

		private Map<String, Object> currentValues = new HashMap<>();
		@SuppressWarnings("unused")
		private Map<String, Object> changedValues = new HashMap<>();

		@Override
		public String getString(String key)
		{
			return (String) currentValues.get(key);
		}

		@Override
		public Integer getInteger(String key)
		{
			return (Integer) currentValues.get(key);
		}

		@SuppressWarnings("unchecked")
		private static Properties from(ZoteroRestItem jsonItem)
		{
			PropertiesImpl properties = new PropertiesImpl();
			ZoteroSchema schema = ZoteroSchema.getCurrentSchema();

			logger.debug("Available keys");
			logger.debug(schema.getFields().keySet());

			for (Map.Entry<String, Object> e : jsonItem.getData().entrySet())
			{
				String name = e.getKey();
				Object value = e.getValue();

				logger.debug("Processing {} of type {} and value {}", name, value.getClass().getCanonicalName(), value);

				if (value instanceof List)
				{
					switch (e.getKey())
					{
						case ZoteroKeys.CREATORS:
						{
							properties.currentValues.put(ZoteroKeys.CREATORS, prepareCreators((List<?>) e.getValue()));
							break;
						}
						case ZoteroKeys.TAGS:
						{
							List<Map<String, Object>> jsonTags = (List<Map<String, Object>>) value;

							List<Tag> tags = jsonTags.stream().map(TagImpl::fromMap).collect(Collectors.toList());

							properties.currentValues.put(ZoteroKeys.TAGS, tags);
							break;
						}
						case ZoteroKeys.COLLECTIONS:
						{
							List<String> collectionsList = (List<String>) value;

							Set<String> collections = new HashSet<>();
							collections.addAll(collectionsList);

							properties.currentValues.put(ZoteroKeys.COLLECTIONS, collections);
							break;
						}
						default:
						{
							logger.error("Unknown List key {} for value {}", e.getKey(), e.getValue());
						}
					}
				}
				else if (value instanceof Map)
				{
					switch (e.getKey())
					{
						case ZoteroKeys.RELATIONS:
						{
							properties.currentValues.put(ZoteroKeys.RELATIONS, RelationsImpl.fromMap((Map<String, Object>) value));
							break;
						}
						default:
						{
							logger.error("Unknown Map key {} for value {}", e.getKey(), e.getValue());
						}
					}
				}
				else
				{
					properties.currentValues.put(name, value);
				}
			}

			return properties;
		}

		private static List<Creator> prepareCreators(List<?> value)
		{
			return value.stream().map(CreatorImpl::fromMap).collect(Collectors.toList());
		}

		@Override
		public Double getDouble(String key)
		{
			return (Double) currentValues.get(key);
		}

		public static Properties from(Map<String, Object> values)
		{
			PropertiesImpl properties = new PropertiesImpl();

			properties.currentValues.putAll(values);

			return properties;
		}

		@Override
		public Object getRaw(String key)
		{
			return this.currentValues.get(key);
		}
	}

	private static class PropertiesItemImpl implements PropertiesItem
	{
		private Properties properties = new PropertiesImpl();

		private PropertiesItemImpl(Map<String, Object> values)
		{
			this.properties = PropertiesImpl.from(values);
		}

		private PropertiesItemImpl(ZoteroRestItem item)
		{
			this.properties = PropertiesImpl.from(item);
		}

		@Override
		public final Properties getProperties()
		{
			return properties;
		}
	}

	private static final class CreatorImpl extends PropertiesItemImpl implements Creator
	{
		private CreatorImpl(Map<String, Object> values)
		{
			super(values);
		}

		@SuppressWarnings("unchecked")
		public static CreatorImpl fromMap(Object values)
		{
			return new CreatorImpl((Map<String, Object>) values);
		}
	}

	private abstract static class EntryImpl extends PropertiesItemImpl implements Entry
	{
		private Library library;

		private Links links;

		private String key;

		private int version;

		public EntryImpl(ZoteroRestItem item, Library library)
		{
			super(item);
			this.key = item.getKey();
			this.version = item.getVersion();
			this.library = library;
			this.links = LinksImpl.from(item.getLinks());
		}

		@Override
		public int hashCode()
		{
			return key.hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof EntryImpl))
			{
				return false;
			}

			if (obj.getClass() != this.getClass())
			{
				return false;
			}

			return key.equals(((EntryImpl) obj).key);
		}

		@Override
		public final String getKey()
		{
			return key;
		}

		@Override
		public final int getVersion()
		{
			return version;
		}

		@Override
		public final Library getLibrary()
		{
			return library;
		}

		@Override
		public final Links getLinks()
		{
			return links;
		}

		public static void loadLinks(EntryImpl entry, ZoteroRestLinks links)
		{
			entry.links = LinksImpl.from(links);
		}
	}

	@SuppressWarnings({ "squid:S2160" })
	private static final class CollectionImpl extends EntryImpl implements Collection
	{
		private static final String URI_COLLECTIONS_ALL = "/collections";
		private static final String URI_COLLECTIONS_SUBCOLLECTIONS = "/collections/{key}/collections";
		private static final String URI_COLLECTIONS_TOP = "/collections/top";
		private static final String URI_COLLECTION = "/collections/{key}";
		private static final String URI_COLLECTION_ITEMS = "/collections/{key}/items";
		private static final String URI_COLLECTION_ITEMS_TOP = "/collections/{key}/items/top";

		private int numItems;
		private int numCollections;

		private CollectionImpl(ZoteroRestItem item, Library library)
		{
			super(item, library);
			this.numCollections = ((Double) item.getMeta().get("numCollections")).intValue();
			this.numItems = ((Double) item.getMeta().get("numItems")).intValue();
		}

		@Override
		public ItemIterator fetchItems()
		{
			return getLibrary().fetchCollectionItems(this.getKey());
		}

		private static CollectionImpl fromItem(ZoteroRestItem item, Library library)
		{
			return new CollectionImpl(item, library);
		}

		@Override
		public void save()
		{
		}

		@Override
		public void delete()
		{
		}

		@Override
		public void refresh()
		{
		}

		@Override
		public CollectionIterator fetchSubCollections()
		{
			return ((LibraryImpl) getLibrary()).fetchCollections(URI_COLLECTIONS_SUBCOLLECTIONS, this.getKey());
		}

		@Override
		public String getName()
		{
			return getProperties().getString(ZoteroKeys.NAME);
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
	}

	private static final class TagImpl extends PropertiesItemImpl implements Tag
	{
		private TagImpl(Map<String, Object> values)
		{
			super(values);
		}

		public static Tag fromMap(Map<String, Object> values)
		{
			return new TagImpl(values);
		}
	}

	private static final class RelationsImpl extends PropertiesItemImpl implements Relationships
	{
		private RelationsImpl(Map<String, Object> values)
		{
			super(values);
		}

		@Override
		@SuppressWarnings("unchecked")
		public List<String> getRelationships(String type)
		{
			return (List<String>) getProperties().getRaw(type);
		}

		public static RelationsImpl fromMap(Map<String, Object> values)
		{
			return new RelationsImpl(values);
		}
	}

	private static final class LinksImpl implements Links
	{
		private Map<String, LinkImpl> links = new HashMap<>();

		@Override
		public boolean has(String key)
		{
			return links.containsKey(key);
		}

		@Override
		public LinkImpl get(String key)
		{
			return links.get(key);
		}

		@Override
		public LinkImpl create(String key)
		{
			// TODO key checking should go here
			if (links.containsKey(key))
			{
				throw new IllegalStateException("Key " + key + " already exists");
			}

			LinkImpl link = new LinkImpl();

			links.put(key, link);

			return link;
		}

		public static Links from(ZoteroRestLinks links)
		{
			LinksImpl zl = new LinksImpl();

			zl.links.putAll(links.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
				return LinkImpl.from(e.getValue());
			})));

			return zl;
		}

	}

	private static final class ZoteroItemIteratorImpl extends ZoteroIteratorImpl<Item> implements ItemIterator
	{
		private ZoteroItemIteratorImpl(RestResponse<ZoteroRestItem[]> response, Library library)
		{
			super(response, ItemImpl::fromItem, library);
		}
	}

	private static final class CollectionIteratorImpl extends ZoteroIteratorImpl<Collection> implements CollectionIterator
	{
		private CollectionIteratorImpl(RestResponse<ZoteroRestItem[]> response, Library library)
		{
			super(response, CollectionImpl::fromItem, library);
		}
	}

	private static class ZoteroIteratorImpl<T> implements ZoteroIterator<T>
	{
		private int totalCount;
		private ZoteroRestItem[] page;
		private RestResponse<ZoteroRestItem[]> response;
		private int index = 0;
		private BiFunction<ZoteroRestItem, Library, T> builder;
		private Library library;

		private ZoteroIteratorImpl(RestResponse<ZoteroRestItem[]> response, BiFunction<ZoteroRestItem, Library, T> builder, Library library)
		{
			this.totalCount = response.getTotalResults();
			this.page = response.getResponse();
			this.response = response;
			this.builder = builder;
			this.library = library;
		}

		@Override
		public boolean hasNext()
		{
			return index < page.length || response.hasNext();
		}

		@Override
		public T next()
		{
			if (index >= page.length)
			{
				if (!response.hasNext())
				{
					throw new NoSuchElementException();
				}

				try
				{
					response = response.next();
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}

				page = response.getResponse();
				index = 0;
			}

			return builder.apply(page[index++], library);
		}

		@Override
		public final int getTotalCount()
		{
			return totalCount;
		}
	}

	public abstract String getUserId();

	public abstract Collection fetchCollection(String key);

	public abstract CollectionIterator fetchCollectionsAll();

	public abstract CollectionIterator fetchCollectionsTop();

	public abstract ItemIterator fetchCollectionItems(String key);

	public abstract ItemIterator fetchCollectionItemsTop(String key);

	public abstract Item fetchItem(String key);

	public abstract ItemIterator fetchItemsAll();

	public abstract ItemIterator fetchItemsTop();

	public abstract ItemIterator fetchItemsTrash();

	public static final Library createLibrary(String userId, ZoteroAPIKey apiKey)
	{
		return new LibraryImpl(apiKey, userId);
	}
}