using System;
using System.Text;
using System.Collections.Generic;
using System.IO;
using System.Net;

namespace MobileCombat
{
public static class FormUpload
{
	private static string contentType;

	private readonly static Encoding encoding;

	private static string formDataBoundary;

	static FormUpload()
	{
		FormUpload.encoding = Encoding.UTF8;
		FormUpload.formDataBoundary = string.Format("----------{0:N}", Guid.NewGuid());
		FormUpload.contentType = string.Concat("multipart/form-data; boundary=", FormUpload.formDataBoundary);
	}

	public static byte[] GetMultipartFormData(Dictionary<string, object> postParameters)
	{
		KeyValuePair<string, object> current;
		Stream memoryStream = new MemoryStream();
		bool flag = false;
		Enumerator<string, object> enumerator = postParameters.GetEnumerator();
		try
		{
			while (true)
			{
			Label1:
				bool @value = enumerator.MoveNext();
				if (!@value)
				{
					break;
				}
				current = enumerator.Current;
				@value = !flag;
				if (!@value)
				{
					memoryStream.Write(FormUpload.encoding.GetBytes("\r\n"), 0, FormUpload.encoding.GetByteCount("\r\n"));
				}
				flag = true;
				@value = current.Value as FileParameter <= null;
				if (@value)
				{
					goto Label0;
				}
				FileParameter fileParameter = (FileParameter)current.Value;
				string str = "--{0}\r\nContent-Disposition: form-data; name=\"{1}\"; filename=\"{2}\";\r\nContent-Type: {3}\r\n\r\n";
				object[] key = new object[4];
				key[0] = FormUpload.formDataBoundary;
				key[1] = current.Key;
				object[] objArray = key;
				byte num = 2;
				string fileName = fileParameter.FileName;
				string key1 = fileName;
				if (fileName == null)
				{
					key1 = current.Key;
				}
				objArray[num] = key1;
				object[] objArray1 = key;
				byte num1 = 3;
				string contentType = fileParameter.ContentType;
				string str1 = contentType;
				if (contentType == null)
				{
					str1 = "application/octet-stream";
				}
				objArray1[num1] = str1;
				string str2 = string.Format(str, key);
				memoryStream.Write(FormUpload.encoding.GetBytes(str2), 0, FormUpload.encoding.GetByteCount(str2));
				memoryStream.Write(fileParameter.File, 0, (int)fileParameter.File.Length);
			}
		}
		finally
		{
			enumerator.Dispose();
		}
		string str3 = string.Concat("\r\n--", FormUpload.formDataBoundary, "--\r\n");
		memoryStream.Write(FormUpload.encoding.GetBytes(str3), 0, FormUpload.encoding.GetByteCount(str3));
		memoryStream.Position = (long)0;
		byte[] numArray = new byte[(IntPtr)memoryStream.Length];
		memoryStream.Read(numArray, 0, (int)numArray.Length);
		memoryStream.Close();
		byte[] numArray1 = numArray;
		return numArray1;
	Label0:
		string str4 = string.Format("--{0}\r\nContent-Disposition: form-data; name=\"{1}\"\r\n\r\n{2}", FormUpload.formDataBoundary, current.Key, current.Value);
		memoryStream.Write(FormUpload.encoding.GetBytes(str4), 0, FormUpload.encoding.GetByteCount(str4));
		goto Label1;
	}

	public static HttpWebRequest GetRequest(string postUrl, string userAgent)
	{
		HttpWebRequest cookieContainer = WebRequest.Create(postUrl) as HttpWebRequest;
		bool flag = cookieContainer != null;
		if (flag)
		{
			cookieContainer.Method = "POST";
			cookieContainer.ContentType = FormUpload.contentType;
			cookieContainer.UserAgent = userAgent;
			cookieContainer.CookieContainer = new CookieContainer();
			HttpWebRequest httpWebRequest = cookieContainer;
			return httpWebRequest;
		}
		else
		{
			throw new NullReferenceException("request is not a http request");
		}
	}

	public class FileParameter
	{
		public string ContentType;

		public byte[] File;

		public string FileName;

		public FileParameter(byte[] file);

		public FileParameter(byte[] file, string filename);

		public FileParameter(byte[] file, string filename, string contenttype);
	}
}
}