using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using System.Diagnostics;

namespace WP7GapClassLib.PhoneGap.Commands
{
    public class MobileCombat:BaseCommand
    {
        public void checkin(string options)
        {
            Deployment.Current.Dispatcher.BeginInvoke(() => { (Application.Current.RootVisual as PhoneApplicationFrame).Navigate(new Uri("/MCMain.xaml", UriKind.Relative)); });
            
            //Application.LoadComponent(this, new Uri("/socialdistraction-fronend-wp7;component/Battle.xaml", UriKind.Relative));
        }
    }
}
