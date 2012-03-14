using System.Runtime.Serialization;
using System;

namespace socialdistraction_frontend_wp7
{
[DataContract]
public class Fields
{
	[DataMember]
	public string joined_on;

	[DataMember]
	public string name;

	[DataMember]
	public string photo;

	public Fields()
	{
	}
}
}