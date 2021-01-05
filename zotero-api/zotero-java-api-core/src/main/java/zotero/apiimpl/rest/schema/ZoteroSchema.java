package zotero.apiimpl.rest.schema;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import zotero.api.constants.ZoteroKeys.DocumentKeys;
import zotero.apiimpl.rest.schema.ZoteroRestSchema.CreatorType;
import zotero.apiimpl.rest.schema.ZoteroRestSchema.Field;
import zotero.apiimpl.rest.schema.ZoteroRestSchema.ItemType;
import zotero.apiimpl.rest.schema.ZoteroRestSchema.Meta;

public class ZoteroSchema
{
	private static final Logger logger = LogManager.getLogger(ZoteroSchema.class);

	private static ZoteroSchema currentSchema;
	private int majorVersion;
	private int minorVersion;
	private ZoteroRestSchema jsonSchema;
	private Map<String, ZoteroField> fields = new HashMap<>();
	private List<ZoteroType> types;
	
	private List<String> dateKeys = Collections.unmodifiableList(Arrays.asList(DocumentKeys.ACCESS_DATE, DocumentKeys.DATE_ADDED, DocumentKeys.DATE_MODIFIED));

	public static ZoteroSchema fromStream(InputStream is) throws IOException
	{
		logger.debug("Loading schema from InputStream {}", is);

		ZoteroRestSchema schema = new Gson().fromJson(new InputStreamReader(is), ZoteroRestSchema.class);
		is.close();

		logger.debug("Loaded ZoteroJSONSchema");

		ZoteroSchema zoteroSchema = new ZoteroSchema();
		zoteroSchema.majorVersion = schema.getVersion().intValue();
		zoteroSchema.jsonSchema = schema;

		logger.debug("Schema version {}.{}", zoteroSchema.majorVersion, zoteroSchema.minorVersion);

		zoteroSchema.types = schema.getItemTypes().stream().map(item -> ZoteroType.typeMapper(schema.getMeta(), item)).collect(Collectors.toList());
		zoteroSchema.types = Collections.unmodifiableList(zoteroSchema.types);

		zoteroSchema.fields = schema.getMeta().fields.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
			ZoteroField field = new ZoteroField();
			field.type = e.getValue().type.equals("date") ? Date.class : String.class;
			return field;
		}));

		return zoteroSchema;
	}

	public static class ZoteroField
	{
		private Class<?> type;
		private String name;

		public Class<?> getType()
		{
			return type;
		}

		public String getName()
		{
			return name;
		}
	}

	public static class ZoteroType
	{
		private String id;
		private Map<String, ZoteroField> fields;
		private List<String> creatorTypes;
		private String primaryCreatorType;

		private static ZoteroType typeMapper(Meta meta, ItemType item)
		{
			logger.debug("Mapping a new ItemType: {}", item.itemType);

			ZoteroType type = new ZoteroType();

			type.creatorTypes = item.creatorTypes.stream().map(CreatorType::getCreatorType).collect(Collectors.toList());
			type.creatorTypes = Collections.unmodifiableList(type.creatorTypes);
			type.id = item.itemType;
			type.fields = item.fields.stream().collect(Collectors.toMap(Field::getField, field -> ZoteroType.fieldMapper(field, meta)));
			type.fields = Collections.unmodifiableMap(type.fields);

			return type;
		}

		private static ZoteroField fieldMapper(ZoteroRestSchema.Field f, ZoteroRestSchema.Meta meta)
		{
			String fieldId = f.field;
			Class<?> type = String.class;

			if (meta.fields.containsKey(fieldId))
			{
				switch (meta.fields.get(fieldId).type)
				{
					case "date":
					{
						type = Date.class;
						break;
					}
					// More may come in the future
					default:
					{
						throw new RuntimeException("Unexpected field type");
					}
				}
			}

			ZoteroField field = new ZoteroField();
			field.name = fieldId;
			field.type = type;
			return field;
		}

		public String getId()
		{
			return id;
		}

		public Map<String, ZoteroField> getFields()
		{
			return fields;
		}

		public List<String> getCreatorTypes()
		{
			return creatorTypes;
		}

		public String getPrimaryCreatorType()
		{
			return primaryCreatorType;
		}
	}

	public static void main(String[] args) throws IOException
	{
		ZoteroSchema schema = fromStream(ZoteroSchema.class.getResourceAsStream("/zoteroschema.json"));

		for (ZoteroType e : schema.types)
		{
			generateClass(e, schema.jsonSchema.getLocales().get("en-US"));
		}
	}

	private static void generateClass(ZoteroType e, ZoteroRestSchema.Localization localization) throws IOException
	{
		String name = localization.itemTypes.get(e.id);
		String className = name.replace(" ", "");
		className = className.replace("-", "");

		PrintWriter writer = new PrintWriter(new FileWriter("src/main/java/litreview/zotero/api/model/v3/" + className + ".java"));
		writer.println("package litreview.zotero.api.model.v3;");

		writer.println();
		writer.println("import java.util.Date;");
		writer.println();

		writer.println("public class " + className + " extends Item");
		writer.println("{");

		for (Entry<String, ZoteroField> field : e.fields.entrySet())
		{
			String fieldName = localization.fields.get(field.getKey());
			String javaFieldName = fieldName.replace("# of ", "NumberOf");
			javaFieldName = javaFieldName.replace(" ", "");
			javaFieldName = javaFieldName.replace("/", "Or");
			javaFieldName = javaFieldName.replace(".", "");

			writer.println();
			writer.println("\tprivate " + field.getValue().type.getSimpleName() + " " + field.getValue().name + ";");

			writer.println();
			writer.println("\tpublic final " + field.getValue().type.getSimpleName() + " get" + javaFieldName + "()");
			writer.println("\t{");
			writer.println("\t\treturn " + field.getKey() + ";");
			writer.println("\t}");

			writer.println();
			writer.println("\tpublic final void set" + javaFieldName + "(" + field.getValue().type.getSimpleName() + " value)");
			writer.println("\t{");
			writer.println("\t\t" + field.getKey() + " =  value;");
			writer.println("\t}");
		}

		writer.println("}");

		writer.close();
	}

	public static ZoteroSchema getCurrentSchema()
	{
		if (ZoteroSchema.currentSchema != null)
		{
			return ZoteroSchema.currentSchema;
		}

		logger.debug("Current schema not yet loaded, loading now");

		try
		{
			InputStream is = ZoteroSchema.class.getResourceAsStream("/zoteroschema.json");
			ZoteroSchema.currentSchema = fromStream(is);
			is.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		return getCurrentSchema();
	}

	public final int getMajorVersion()
	{
		return majorVersion;
	}

	public final int getMinorVersion()
	{
		return minorVersion;
	}

	public final Map<String, ZoteroField> getFields()
	{
		return fields;
	}

	public final List<ZoteroType> getTypes()
	{
		return types;
	}

	public final List<String> getDateKeys()
	{
		return dateKeys;
	}
}
