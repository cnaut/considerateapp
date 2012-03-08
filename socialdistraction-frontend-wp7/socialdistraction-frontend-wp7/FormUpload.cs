using System;
using System.Text;
using System.Collections.Generic;
using System.IO;
using System.Net;

namespace socialdistraction_frontend_wp7
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
            Stream formDataStream = new System.IO.MemoryStream();
            bool needsCLRF = false;

            foreach (var param in postParameters)
            {
                // Thanks to feedback from commenters, add a CRLF to allow multiple parameters to be added.
                // Skip it on the first parameter, add it to subsequent parameters.
                if (needsCLRF)
                    formDataStream.Write(encoding.GetBytes("\r\n"), 0, encoding.GetByteCount("\r\n"));

                needsCLRF = true;

                if (param.Value is FileParameter)
                {
                    FileParameter fileToUpload = (FileParameter)param.Value;

                    // Add just the first part of this param, since we will write the file data directly to the Stream
                    string header = string.Format("--{0}\r\nContent-Disposition: form-data; name=\"{1}\"; filename=\"{2}\";\r\nContent-Type: {3}\r\n\r\n",
                        formDataBoundary,
                        param.Key,
                        fileToUpload.FileName ?? param.Key,
                        fileToUpload.ContentType ?? "application/octet-stream");

                    formDataStream.Write(encoding.GetBytes(header), 0, encoding.GetByteCount(header));

                    // Write the file data directly to the Stream, rather than serializing it to a string.
                    formDataStream.Write(fileToUpload.File, 0, fileToUpload.File.Length);
                }
                else
                {
                    string postData = string.Format("--{0}\r\nContent-Disposition: form-data; name=\"{1}\"\r\n\r\n{2}",
                        formDataBoundary,
                        param.Key,
                        param.Value);
                    formDataStream.Write(encoding.GetBytes(postData), 0, encoding.GetByteCount(postData));
                }
            }

            // Add the end of the request.  Start with a newline
            string footer = "\r\n--" + formDataBoundary + "--\r\n";
            formDataStream.Write(encoding.GetBytes(footer), 0, encoding.GetByteCount(footer));

            // Dump the Stream into a byte[]
            formDataStream.Position = 0;
            byte[] formData = new byte[formDataStream.Length];
            formDataStream.Read(formData, 0, formData.Length);
            formDataStream.Close();

            return formData;
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

            public FileParameter(byte[] file, string filename, string contenttype)
            {
                this.File = file;
                this.FileName = filename;
                this.ContentType = contenttype;
            }
        }
    }
}
