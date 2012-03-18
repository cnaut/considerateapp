using System.Runtime.Serialization;
using System.Collections.Generic;

namespace MobileCombat
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