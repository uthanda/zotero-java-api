package zotero.apiimpl.properties;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyDate;

public class PropertyDateImpl extends PropertyImpl<Date> implements PropertyDate
{
	private static Logger logger = LogManager.getLogger(PropertyDateImpl.class);

	public PropertyDateImpl(String key, Date value)
	{
		this(key, value, false);
	}

	public PropertyDateImpl(String key, Date value, boolean readOnly)
	{
		super(PropertyType.DATE, key, value, readOnly);
	}

	public static PropertyDateImpl fromRest(String name, Object value)
	{
		logger.debug("Attempting to deserialize '{}' as date", value);

		Date dateValue = ((String) value).isEmpty() ? null : DatatypeConverter.parseDateTime((String) value).getTime();

		return new PropertyDateImpl(name, dateValue);
	}

	@Override
	public Object toRestValue()
	{
		Object value = super.toRestValue();
		
		if(value == null || value == Boolean.FALSE)
		{
			return value;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getValue());

		return DatatypeConverter.printDateTime(calendar);
	}
}
