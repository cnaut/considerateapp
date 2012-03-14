using System.Runtime.Serialization;
using System.Collections.Generic;

namespace socialdistraction_frontend_wp7
{
[DataContract]
public class UserIDList
{
	[DataMember]
	public List<string> users;

	public UserIDList()
	{
		this.users = new List<string>();
	}
}
}