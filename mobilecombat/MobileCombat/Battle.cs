using Microsoft.Phone.Controls;
using System;
using System.Windows.Controls;
using System.Diagnostics;
using System.Windows;

namespace MobileCombat
{
public class Battle : PhoneApplicationPage
{
	private bool _contentLoaded;

	internal TextBlock ApplicationTitle;

	internal Grid ContentPanel;

	internal Grid LayoutRoot;

	internal TextBlock PageTitle;

	internal TextBlock textBlock1;

	internal StackPanel TitlePanel;

	public Battle()
	{
		this.InitializeComponent();
	}

	[DebuggerNonUserCode]
	public void InitializeComponent()
	{
		bool flag = !this._contentLoaded;
		if (flag)
		{
			this._contentLoaded = true;
			Application.LoadComponent(this, new Uri("/MobileCombat;component/Battle.xaml", UriKind.Relative));
			this.LayoutRoot = (Grid)base.FindName("LayoutRoot");
			this.TitlePanel = (StackPanel)base.FindName("TitlePanel");
			this.ApplicationTitle = (TextBlock)base.FindName("ApplicationTitle");
			this.PageTitle = (TextBlock)base.FindName("PageTitle");
			this.ContentPanel = (Grid)base.FindName("ContentPanel");
			this.textBlock1 = (TextBlock)base.FindName("textBlock1");
		}
	}
}
}