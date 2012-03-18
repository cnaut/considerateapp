using System.Runtime.Serialization;
using System;

namespace socialdistraction_frontend_wp7
{
[DataContract]
public class User
{
	[DataMember]
	public Fields fields;

	[DataMember]
	public string model;

	[DataMember]
	public string pk;

	public Uri imageUri
	{
		get;
		set;
	}

	public string name
	{
		get;
		set;
	}

	public User()
	{
	}

	public override bool Equals(object obj)
	{
		bool flag;
		User user = obj as User;
		bool flag1 = user != null;
		if (flag1)
		{
			flag = user.pk == this.pk;
		}
		else
		{
			flag = false;
		}
		return flag;
	}

	public override int GetHashCode()
	{
		int hashCode = this.pk.GetHashCode();
		return hashCode;
	}
}
}