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
using System.Data.Linq;
using System.ServiceModel;

namespace socialdistraction_frontend_wp7
{
    public partial class MCMain : PhoneApplicationPage
    {
        private Accelerometer accelerometer;

        private ObservableCollection<User> allUsers;

        private string baseAddress;

        private string battleID;

        private bool battling;

        private WebClient client;

        private DispatcherTimer refreshTimer;

        private DispatcherTimer tickerTimer;

        private DateTime timeStarted;

        public MCMain()
        {
            baseAddress = "http://184.169.136.30:8002";
            InitializeComponent();
            client = new WebClient();
            client.BaseAddress = baseAddress;
            allUsers = new ObservableCollection<User>();
            accelerometer = new Accelerometer();
        }

        public void AccelerometerReadingChanged(object sender, SensorReadingEventArgs<AccelerometerReading> e)
        {
            if (e.SensorReading.Acceleration.Z > 0)
            {
                Deployment.Current.Dispatcher.BeginInvoke(new Action(GameOver));
            }
        }

        private void DefeatDownloaded(object s, DownloadStringCompletedEventArgs e)
        {
            bool result;
            DataContractJsonSerializer dataContractJsonSerializer = new DataContractJsonSerializer(typeof(string));
            IEnumerator
                <User> enumerator = allUsers.GetEnumerator();
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
                    allUsers.Remove(current);
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
            UserIDList userIDList = new UserIDList();
            ObservableCollection<User> observableCollection = new ObservableCollection<User>();
            userIDList.users.Add((string)IsolatedStorageSettings.ApplicationSettings["id"]);
            foreach (User u in listBox1.SelectedItems)
            {
                userIDList.users.Add(u.pk);
                observableCollection.Add(u);
            }
            string str = Serialize(userIDList);
            Debug.WriteLine(str);
            client.UploadStringAsync(new Uri("/startbattle", UriKind.Relative), str);
            StartBattle();
            allUsers = observableCollection;
            listBox1.DataContext = allUsers;
        }

        private void GameOver()
        {
            accelerometer.Stop();
            countdownText.Text = "You Lose!";
            WebClient webClient = new WebClient();
            webClient.BaseAddress = baseAddress;
            webClient.DownloadStringCompleted += DefeatDownloaded;
            tickerTimer.Stop();
            string str = string.Concat("declaredefeat?userid=", (string)IsolatedStorageSettings.ApplicationSettings["id"], "&battleid=", battleID);
            Debug.WriteLine(str);
            webClient.DownloadStringAsync(new Uri(str, UriKind.Relative));
        }


        private void listBox1_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            bool count = listBox1.SelectedItems.Count != 0;
            if (count)
            {
                fightButton.IsEnabled = true;
            }
            else
            {
                fightButton.IsEnabled = false;
            }
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);
            if (IsolatedStorageSettings.ApplicationSettings.Contains("id"))
            {
                client.DownloadStringCompleted += StringDownloaded;
                client.UploadStringCompleted += StringUploaded;
                fightButton.IsEnabled = false;
                battling = false;
                RefreshUsers(null, null);
                refreshTimer = new DispatcherTimer();
                refreshTimer.Interval = new TimeSpan(0, 0, 0, 5);
                refreshTimer.Tick += RefreshUsers;
                refreshTimer.Start();
                listBox1.DataContext = allUsers;
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
            bool isBusy = client.IsBusy;
            if (!isBusy)
            {
                client.DownloadStringAsync(new Uri(string.Concat("/allusers?", Environment.TickCount), UriKind.Relative));
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
            fightButton.Visibility = System.Windows.Visibility.Collapsed;
            countdownText.Visibility = System.Windows.Visibility.Visible;
            tickerTimer = new DispatcherTimer();
            timeStarted = DateTime.Now;
            tickerTimer.Interval = new TimeSpan(0, 0, 1);
            tickerTimer.Tick += UpdateTime;
            UpdateTime(null, null);
            tickerTimer.Start();
            refreshTimer.Stop();
            accelerometer.CurrentValueChanged += AccelerometerReadingChanged;
            accelerometer.Start();
        }

        private void StringDownloaded(object sender, DownloadStringCompletedEventArgs e)
        {
            User[] userArray;
            DataContractJsonSerializer dataContractJsonSerializer = new DataContractJsonSerializer(typeof(User[]));
            MemoryStream memoryStream = new MemoryStream(Encoding.UTF8.GetBytes(e.Result));
            userArray = (User[])dataContractJsonSerializer.ReadObject(memoryStream);
            foreach (User u in userArray)
            {
                if (allUsers.Contains(u) ||
                    (string)IsolatedStorageSettings.ApplicationSettings["id"] == u.pk)
                    continue;

                u.imageUri = new Uri(string.Concat(client.BaseAddress, "user_photos/", u.fields.photo), UriKind.Absolute);
                Debug.WriteLine(u.imageUri.ToString());
                u.name = u.fields.name;
                allUsers.Add(u);
            }
            Debug.WriteLine(e.Result);
        }

        private void StringUploaded(object sender, UploadStringCompletedEventArgs e)
        {
            battleID = e.Result;
        }

        private void UpdateTime(object sender, EventArgs e)
        {
            TimeSpan now = DateTime.Now - timeStarted;
            now = new TimeSpan(1, 0, 0) - now;
            countdownText.Text = string.Concat(now.Minutes, ":", now.Seconds);
        }
    }
}
