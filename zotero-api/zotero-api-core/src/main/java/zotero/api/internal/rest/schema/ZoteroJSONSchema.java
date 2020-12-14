package zotero.api.internal.rest.schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ZoteroJSONSchema
{
	private Double version;
	private List<ItemType> itemTypes;
	private ZoteroJSONSchema.Meta meta;
	private Map<String, Localization> locales;
	private CitationStyleLanguage csl;

	
	
	public static class CitationStyleLanguage
	{
		CitationStyleLanguage.CSLTypes types;
		CitationStyleLanguage.CSLFields fields;
		CitationStyleLanguage.CSLNames names;

		private static class CSLNames extends HashMap<String, String>
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -3983115913147035164L;
		}

		private static class CSLTypes extends HashMap<String, List<String>>
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -5078502307687325335L;
		}

		private static class CSLFields
		{
			Map<String, List<String>> text;
			Map<String, String> date;
		}
	}

	public static class Localization
	{
		Map<String, String> itemTypes;
		Map<String, String> fields;
		Map<String, String> creatorTypes;
	}

	public static class Meta
	{
		Map<String, ZoteroJSONSchema.FieldType> fields;
	}

	public static class FieldType
	{
		String type;

		public final String getType()
		{
			return type;
		}
	}

	public static class ItemType
	{
		String itemType;
		List<ZoteroJSONSchema.Field> fields;
		List<ZoteroJSONSchema.CreatorType> creatorTypes;

		public String getItemType()
		{
			return itemType;
		}
	}

	public static class Field
	{
		String field;
		String baseField;

		public String getField()
		{
			return field;
		}

		public String getBaseField()
		{
			return baseField;
		}

	}

	public static class CreatorType
	{
		String creatorType;
		Boolean primary;

		public String getCreatorType()
		{
			return creatorType;
		}
	}

	public final Double getVersion()
	{
		return version;
	}

	public final List<ItemType> getItemTypes()
	{
		return itemTypes;
	}

	public final ZoteroJSONSchema.Meta getMeta()
	{
		return meta;
	}

	public final Map<String, Localization> getLocales()
	{
		return locales;
	}

	public final CitationStyleLanguage getCsl()
	{
		return csl;
	}
}