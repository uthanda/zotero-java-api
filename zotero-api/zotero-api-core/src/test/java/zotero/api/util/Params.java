package zotero.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Params extends HashMap<String, List<String>>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -845725319683896051L;

	public void addParam(String param, String value)
	{
		if (!containsKey(param))
		{
			put(param, new ArrayList<>());
		}
		
		get(param).add(value);
	}
	
	public String buildQueryParams()
	{
		if (this.isEmpty())
		{
			return "<empty>";
		}

		String[] keys = this.keySet().toArray(new String[this.size()]);
		Arrays.sort(keys);

		StringBuffer sb = new StringBuffer();

		for (String param : keys)
		{
			get(param).forEach(value -> {
				sb.append("/");
				sb.append(param);
				sb.append("=");
				sb.append(value);
			});
		}

		return sb.toString();
	}
}