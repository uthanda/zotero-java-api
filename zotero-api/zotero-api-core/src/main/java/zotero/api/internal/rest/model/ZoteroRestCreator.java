package zotero.api.internal.rest.model;

public class ZoteroRestCreator
{
	private String creatorType = null;

	private String firstName = null;

	private String lastName = null;

	public String getCreatorType()
	{
		return creatorType;
	}

	public void setCreatorType(String creatorType)
	{
		this.creatorType = creatorType;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
}
