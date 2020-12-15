package zotero.api.properties;

import java.util.Date;

public interface PropertyDate extends Property
{
	Date getValue();
	
	void setValue(Date value);
}
