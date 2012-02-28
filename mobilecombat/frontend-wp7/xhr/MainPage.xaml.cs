using System;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Microsoft.Phone.Controls;

namespace xhr
{
    public partial class MainPage : PhoneApplicationPage
    {

        // Constructor
        public MainPage()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, RoutedEventArgs e)
        {
            string url = "http://184.169.136.30:80/caps";
            string message = "HeresaMessage";
            WebClient client = new WebClient();
            client.Headers["User-Agent"] = "Test Service 1.0";
            client.Headers["Cache-Control"] = "no-cache";
            //5req.Credentials = new NetworkCredential("test", "test");
            //client.Headers["user-agent"] = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)";
            client.UploadStringCompleted += (sent, a) =>
                {
                    System.Diagnostics.Debug.WriteLine(a.Result);
                };
            client.UploadStringAsync(new Uri(url), "message=" + message);
            //client.UploadStringAsync(new Uri(url), message);
 
            /*client.DownloadStringCompleted += (sent, a) =>
                {
                    System.Diagnostics.Debug.WriteLine(a.Result);
                };
            client.DownloadStringAsync(new Uri(url));*/
        }
    }
}