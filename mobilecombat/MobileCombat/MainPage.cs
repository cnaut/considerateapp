using Microsoft.Phone.Controls;
using System;
using Microsoft.Devices.Sensors;
using System.Collections.ObjectModel;
using System.Windows.Controls;
using System.Net;
using System.Windows.Threading;
using System.Windows;
using System.Runtime.Serialization.Json;
using System.Collections.Generic;
using System.IO.IsolatedStorage;
using System.Collections;
using System.Diagnostics;
using System.Windows.Navigation;
using System.IO;
using System.Text;

namespace MobileCombat
{
public class MainPage : PhoneApplicationPage
{
	private bool _contentLoaded;

	private Accelerometer accelerometer;

	private ObservableCollection<User> allUsers;

	internal TextBlock ApplicationTitle;

	private string baseAddress;

	private string battleID;

	private bool battling;

	private WebClient client;

	internal Grid ContentPanel;

	internal TextBlock countdownText;

	internal Button fightButton;

	internal Grid LayoutRoot;

	internal ListBox listBox1;

	internal TextBlock PageTitle;

	private DispatcherTimer refreshTimer;

	private DispatcherTimer tickerTimer;

	private DateTime timeStarted;

	internal StackPanel TitlePanel;

	public MainPage()
	{
		this.baseAddress = "http://184.169.136.30:8000";
		this.InitializeComponent();
		this.client = new WebClient();
		this.client.BaseAddress = this.baseAddress;
		this.allUsers = new ObservableCollection<User>();
		this.accelerometer = new Accelerometer();
	}

	public void AccelerometerReadingChanged(object sender, AccelerometerReadingEventArgs e)
	{
		bool z = e.Z <= 0;
		if (!z)
		{
			Deployment.Current.Dispatcher.BeginInvoke(new Action(this, () => this.GameOver()));
		}
	}

	private void DefeatDownloaded(object s, DownloadStringCompletedEventArgs e)
	{
		bool result;
		DataContractJsonSerializer dataContractJsonSerializer = new DataContractJsonSerializer(typeof(string));
		IEnumerator<User> enumerator = this.allUsers.GetEnumerator();
		try
		{
			while (true)
			{
				result = enumerator.MoveNext();
				if (!result)
				{
					break;
				}
				User current = enumerator.Current;
				result = !(current.pk == e.Result);
				if (result)
				{
					continue;
				}
				this.allUsers.Remove(current);
			}
		}
		finally
		{
			result = enumerator == null;
			if (!result)
			{
				enumerator.Dispose();
			}
		}
	}

	private void fightButton_Click(object sender, RoutedEventArgs e)
	{
		bool flag;
		UserIDList userIDList = new UserIDList();
		ObservableCollection<User> observableCollection = new ObservableCollection<User>();
		userIDList.users.Add((string)IsolatedStorageSettings.ApplicationSettings["id"]);
		IEnumerator enumerator = this.listBox1.SelectedItems.GetEnumerator();
		try
		{
			while (true)
			{
				flag = enumerator.MoveNext();
				if (!flag)
				{
					break;
				}
				User current = (User)enumerator.Current;
				userIDList.users.Add(current.pk);
				observableCollection.Add(current);
			}
		}
		finally
		{
			IDisposable disposable = enumerator as IDisposable;
			flag = disposable == null;
			if (!flag)
			{
				disposable.Dispose();
			}
		}
		string str = this.Serialize(userIDList);
		Debug.WriteLine(str);
		this.client.UploadStringAsync(new Uri("/startbattle", UriKind.Relative), str);
		this.StartBattle();
		this.allUsers = observableCollection;
		this.listBox1.DataContext = this.allUsers;
	}

	private void GameOver()
	{
		this.accelerometer.Stop();
		this.countdownText.Text = "You Lose!";
		WebClient webClient = new WebClient();
		webClient.BaseAddress = this.baseAddress;
		webClient.DownloadStringCompleted += DefeatDownloaded;
		this.tickerTimer.Stop();
		string str = string.Concat("declaredefeat?userid=", (string)IsolatedStorageSettings.ApplicationSettings["id"], "&battleid=", this.battleID);
		Debug.WriteLine(str);
		webClient.DownloadStringAsync(new Uri(str, UriKind.Relative));
	}

	[DebuggerNonUserCode]
	public void InitializeComponent()
	{
		bool flag = !this._contentLoaded;
		if (flag)
		{
			this._contentLoaded = true;
			Application.LoadComponent(this, new Uri("/MobileCombat;component/MainPage.xaml", UriKind.Relative));
			this.LayoutRoot = (Grid)base.FindName("LayoutRoot");
			this.TitlePanel = (StackPanel)base.FindName("TitlePanel");
			this.ApplicationTitle = (TextBlock)base.FindName("ApplicationTitle");
			this.PageTitle = (TextBlock)base.FindName("PageTitle");
			this.ContentPanel = (Grid)base.FindName("ContentPanel");
			this.listBox1 = (ListBox)base.FindName("listBox1");
			this.fightButton = (Button)base.FindName("fightButton");
			this.countdownText = (TextBlock)base.FindName("countdownText");
		}
	}

	private void listBox1_SelectionChanged(object sender, SelectionChangedEventArgs e)
	{
		bool count = this.listBox1.SelectedItems.Count != 0;
		if (count)
		{
			this.fightButton.IsEnabled = true;
		}
		else
		{
			this.fightButton.IsEnabled = false;
		}
	}

	protected override void OnNavigatedTo(NavigationEventArgs e)
	{
		base.OnNavigatedTo(e);
		bool flag = IsolatedStorageSettings.ApplicationSettings.Contains("id");
		if (flag)
		{
			this.client.DownloadStringCompleted += StringDownloaded;
			this.client.UploadStringCompleted += StringUploaded;
			this.fightButton.IsEnabled = false;
			this.battling = false;
			this.RefreshUsers(null, null);
			this.refreshTimer = new DispatcherTimer();
			this.refreshTimer.Interval = new TimeSpan(0, 0, 0, 5);
			this.refreshTimer.Tick += RefreshUsers;
			this.refreshTimer.Start();
			this.listBox1.DataContext = this.allUsers;
		}
		else
		{
			base.NavigationService.Navigate(new Uri("/GetPhoto.xaml", UriKind.Relative));
		}
	}

	private void ProfileMenu_Click(object sender, EventArgs e)
	{
		base.NavigationService.Navigate(new Uri("/GetPhoto.xaml", UriKind.Relative));
	}

	private void RefreshUsers(object s, EventArgs e)
	{
		bool isBusy = this.client.IsBusy;
		if (!isBusy)
		{
			this.client.DownloadStringAsync(new Uri(string.Concat("/allusers?", Environment.TickCount), UriKind.Relative));
		}
	}

	public string Serialize(object o)
	{
		DataContractJsonSerializer dataContractJsonSerializer = new DataContractJsonSerializer(o.GetType());
		MemoryStream memoryStream = new MemoryStream();
		dataContractJsonSerializer.WriteObject(memoryStream, o);
		memoryStream.Position = (long)0;
		StreamReader streamReader = new StreamReader(memoryStream);
		string end = streamReader.ReadToEnd();
		return end;
	}

	private void StartBattle()
	{
		this.fightButton.Visibility = 1;
		this.countdownText.Visibility = 0;
		this.tickerTimer = new DispatcherTimer();
		this.timeStarted = DateTime.Now;
		this.tickerTimer.Interval = new TimeSpan(0, 0, 1);
		this.tickerTimer.Tick += UpdateTime;
		this.UpdateTime(null, null);
		this.tickerTimer.Start();
		this.refreshTimer.Stop();
		this.accelerometer.ReadingChanged += AccelerometerReadingChanged;
		try
		{
			this.accelerometer.Start();
		}
		catch
		{
			Debug.WriteLine("OOPS!");
		}
	}

	private void StringDownloaded(object sender, DownloadStringCompletedEventArgs e)
	{
		User[] userArray;
		bool flag2;
		DataContractJsonSerializer dataContractJsonSerializer = new DataContractJsonSerializer(typeof(User[]));
		MemoryStream memoryStream = new MemoryStream(Encoding.UTF8.GetBytes(e.Result));
		try
		{
			userArray = (User[])dataContractJsonSerializer.ReadObject(memoryStream);
		}
		finally
		{
			flag2 = memoryStream == null;
			if (!flag2)
			{
				memoryStream.Dispose();
			}
		}
		IEnumerator<User> enumerator = userArray.Where<User>((User u) => {
			bool flag;
			if (u.pk == (string)IsolatedStorageSettings.ApplicationSettings["id"])
			{
				bool flag = false;
			}
			else
			{
				flag = !this.allUsers.Contains(u);
			}
			bool flag1 = flag;
			return flag1;
		}
		).GetEnumerator();
		try
		{
			while (true)
			{
				flag2 = enumerator.MoveNext();
				if (!flag2)
				{
					break;
				}
				User current = enumerator.Current;
				current.imageUri = new Uri(string.Concat(this.client.BaseAddress, "user_photos/", current.fields.photo), UriKind.Absolute);
				Debug.WriteLine(current.imageUri.ToString());
				current.name = current.fields.name;
				this.allUsers.Add(current);
			}
		}
		finally
		{
			flag2 = enumerator == null;
			if (!flag2)
			{
				enumerator.Dispose();
			}
		}
		Debug.WriteLine(e.Result);
	}

	private void StringUploaded(object sender, UploadStringCompletedEventArgs e)
	{
		this.battleID = e.Result;
	}

	private void UpdateTime(object sender, EventArgs e)
	{
		TimeSpan now = DateTime.Now - this.timeStarted;
		now = new TimeSpan(1, 0, 0) - now;
		this.countdownText.Text = string.Concat(now.Minutes, ":", now.Seconds);
	}
}
}