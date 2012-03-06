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

namespace MobileCombat
{
public class GetPhoto : PhoneApplicationPage
{
	private bool _contentLoaded;

	internal TextBlock ApplicationTitle;

	internal Grid ContentPanel;

	private byte[] formData;

	internal Grid LayoutRoot;

	internal TextBox nameBox;

	internal TextBlock PageTitle;

	private PhotoChooserTask photoChooser;

	private byte[] picData;

	private bool picReady;

	internal Button saveButton;

	internal Button selectPicButton;

	internal TextBlock textBlock1;

	internal StackPanel TitlePanel;

	internal Image userImg;

	public GetPhoto()
	{
		this.picReady = false;
		this.InitializeComponent();
		this.saveButton.IsEnabled = false;
		this.photoChooser = new PhotoChooserTask();
		this.photoChooser.PixelHeight = 180;
		this.photoChooser.PixelWidth = 180;
		this.photoChooser.ShowCamera = true;
		this.photoChooser.add_Completed(photoChooserTask_Completed);
	}

	private void GotRequestStream(IAsyncResult asyncResult)
	{
		HttpWebRequest asyncState = asyncResult.AsyncState as HttpWebRequest;
		Stream stream = asyncState.EndGetRequestStream(asyncResult);
		stream.Write(this.formData, 0, (int)this.formData.Length);
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
		Deployment.Current.Dispatcher.BeginInvoke(new Action(this, () => base.NavigationService.GoBack()));
	}

	[DebuggerNonUserCode]
	public void InitializeComponent()
	{
		bool flag = !this._contentLoaded;
		if (flag)
		{
			this._contentLoaded = true;
			Application.LoadComponent(this, new Uri("/MobileCombat;component/GetPhoto.xaml", UriKind.Relative));
			this.LayoutRoot = (Grid)base.FindName("LayoutRoot");
			this.TitlePanel = (StackPanel)base.FindName("TitlePanel");
			this.ApplicationTitle = (TextBlock)base.FindName("ApplicationTitle");
			this.PageTitle = (TextBlock)base.FindName("PageTitle");
			this.ContentPanel = (Grid)base.FindName("ContentPanel");
			this.nameBox = (TextBox)base.FindName("nameBox");
			this.userImg = (Image)base.FindName("userImg");
			this.selectPicButton = (Button)base.FindName("selectPicButton");
			this.saveButton = (Button)base.FindName("saveButton");
			this.textBlock1 = (TextBlock)base.FindName("textBlock1");
		}
	}

	private void nameBox_TextChanged(object sender, TextChangedEventArgs e)
	{
		bool flag;
		bool text = !(this.nameBox.Text == string.Empty);
		if (!text)
		{
			this.saveButton.IsEnabled = false;
		}
		if (this.nameBox.Text == string.Empty)
		{
			flag = true;
		}
		else
		{
			flag = !this.picReady;
		}
		text = flag;
		if (!text)
		{
			this.saveButton.IsEnabled = true;
		}
	}

	private void photoChooserTask_Completed(object sender, PhotoResult e)
	{
		bool taskResult = e.TaskResult != TaskResult.OK;
		if (!taskResult)
		{
			BitmapImage bitmapImage = new BitmapImage();
			bitmapImage.SetSource(e.ChosenPhoto);
			this.userImg.Source = bitmapImage;
			WriteableBitmap writeableBitmap = new WriteableBitmap(bitmapImage);
			MemoryStream memoryStream = new MemoryStream();
			try
			{
				Extensions.SaveJpeg(writeableBitmap, memoryStream, bitmapImage.PixelHeight, bitmapImage.PixelWidth, 0, 100);
				this.picData = memoryStream.ToArray();
			}
			finally
			{
				taskResult = memoryStream == null;
				if (!taskResult)
				{
					memoryStream.Dispose();
				}
			}
			this.picReady = true;
			taskResult = !(this.nameBox.Text != string.Empty);
			if (!taskResult)
			{
				this.saveButton.IsEnabled = true;
			}
		}
	}

	private void saveButton_Click(object sender, RoutedEventArgs e)
	{
		Dictionary<string, object> strs = new Dictionary<string, object>();
		strs.Add("name", this.nameBox.Text);
		strs.Add("photo", new FileParameter(this.picData, "file.jpg", "application/octet-stream"));
		this.formData = FormUpload.GetMultipartFormData(strs);
		HttpWebRequest request = FormUpload.GetRequest("http://184.169.136.30:8000/adduser", "");
		request.BeginGetRequestStream(GotRequestStream, request);
	}

	private void selectPicButton_Click(object sender, RoutedEventArgs e)
	{
		try
		{
			this.photoChooser.Show();
		}
		catch (InvalidOperationException invalidOperationException1)
		{
			InvalidOperationException invalidOperationException = invalidOperationException1;
			MessageBox.Show("An error occurred.");
		}
	}
}
}