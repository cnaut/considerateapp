using System.Runtime.Serialization;
using System;

namespace MobileCombat
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