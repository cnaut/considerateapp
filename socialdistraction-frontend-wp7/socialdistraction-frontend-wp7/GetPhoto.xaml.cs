using Microsoft.Phone.Controls;
using System;
using System.Windows.Controls;
using Microsoft.Phone.Tasks;
using System.Net;
using System.IO;
using System.IO.IsolatedStorage;
using System.Windows;
using System.Diagnostics;
using System.Windows.Media.Imaging;
using System.Collections.Generic;

namespace socialdistraction_frontend_wp7
{
    public partial class GetPhoto : PhoneApplicationPage
    {
        private byte[] formData;

        private PhotoChooserTask photoChooser;

        private byte[] picData;

        private bool picReady;

        public GetPhoto()
        {
            picReady = false;
            InitializeComponent();
            saveButton.IsEnabled = false;
            photoChooser = new PhotoChooserTask();
            photoChooser.PixelHeight = 180;
            photoChooser.PixelWidth = 180;
            photoChooser.ShowCamera = true;
            photoChooser.Completed += photoChooserTask_Completed;
        }

        private void GotRequestStream(IAsyncResult asyncResult)
        {
            HttpWebRequest asyncState = asyncResult.AsyncState as HttpWebRequest;
            Stream stream = asyncState.EndGetRequestStream(asyncResult);
            stream.Write(formData, 0, (int)formData.Length);
            stream.Flush();
            stream.Close();
            asyncState.BeginGetResponse(GotResponseStream, asyncState);
        }

        private void GotResponseStream(IAsyncResult asyncResult)
        {
            HttpWebRequest asyncState = asyncResult.AsyncState as HttpWebRequest;
            HttpWebResponse httpWebResponse = asyncState.EndGetResponse(asyncResult) as HttpWebResponse;
            Stream responseStream = httpWebResponse.GetResponseStream();
            StreamReader streamReader = new StreamReader(responseStream);
            string end = streamReader.ReadToEnd();
            IsolatedStorageSettings applicationSettings = IsolatedStorageSettings.ApplicationSettings;
            applicationSettings["id"] = end;
            Deployment.Current.Dispatcher.BeginInvoke(() => { (Application.Current.RootVisual as PhoneApplicationFrame).Navigate(new Uri("/MCMain.xaml", UriKind.Relative)); });
            
            
        }

        private void nameBox_TextChanged(object sender, TextChangedEventArgs e)
        {
            bool flag;
            bool text = !(nameBox.Text == string.Empty);
            if (!text)
            {
                saveButton.IsEnabled = false;
            }
            if (nameBox.Text == string.Empty)
            {
                flag = true;
            }
            else
            {
                flag = !picReady;
            }
            text = flag;
            if (!text)
            {
                saveButton.IsEnabled = true;
            }
        }

        private void photoChooserTask_Completed(object sender, PhotoResult e)
        {
            bool taskResult = e.TaskResult != TaskResult.OK;
            if (!taskResult)
            {
                BitmapImage bitmapImage = new BitmapImage();
                bitmapImage.SetSource(e.ChosenPhoto);
                userImg.Source = bitmapImage;
                WriteableBitmap writeableBitmap = new WriteableBitmap(bitmapImage);
                MemoryStream memoryStream = new MemoryStream();
                try
                {
                    Extensions.SaveJpeg(writeableBitmap, memoryStream, bitmapImage.PixelHeight, bitmapImage.PixelWidth, 0, 100);
                    picData = memoryStream.ToArray();
                }
                finally
                {
                    taskResult = memoryStream == null;
                    if (!taskResult)
                    {
                        memoryStream.Dispose();
                    }
                }
                picReady = true;
                taskResult = !(nameBox.Text != string.Empty);
                if (!taskResult)
                {
                    saveButton.IsEnabled = true;
                }
            }
        }

        private void saveButton_Click(object sender, RoutedEventArgs e)
        {
            Dictionary<string, object> strs = new Dictionary<string, object>();
            strs.Add("name", nameBox.Text);
            strs.Add("photo", new FormUpload.FileParameter(picData, "file.jpg", "application/octet-stream"));
            formData = FormUpload.GetMultipartFormData(strs);
            HttpWebRequest request = FormUpload.GetRequest("http://184.169.136.30:8002/adduser", "");
            request.BeginGetRequestStream(GotRequestStream, request);
        }

        private void selectPicButton_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                photoChooser.Show();
            }
            catch (InvalidOperationException invalidOperationException1)
            {
                InvalidOperationException invalidOperationException = invalidOperationException1;
                MessageBox.Show("An error occurred.");
            }
        }
    }
}