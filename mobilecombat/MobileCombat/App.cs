using System.Windows;
using System;
using Microsoft.Phone.Controls;
using System.Diagnostics;
using Microsoft.Phone.Shell;
using System.Windows.Navigation;

namespace MobileCombat
{
public class App : Application
{
	private bool _contentLoaded;

	private bool phoneApplicationInitialized;

	public PhoneApplicationFrame RootFrame
	{
		get;
		private set;
	}

	public App()
	{
		this.phoneApplicationInitialized = false;
		base.UnhandledException += Application_UnhandledException;
		this.InitializeComponent();
		this.InitializePhoneApplication();
		bool isAttached = !Debugger.IsAttached;
		if (!isAttached)
		{
			Application.Current.Host.Settings.EnableFrameRateCounter = true;
			PhoneApplicationService.Current.UserIdleDetectionMode = IdleDetectionMode.Disabled;
		}
	}

	private void Application_Activated(object sender, ActivatedEventArgs e)
	{
	}

	private void Application_Closing(object sender, ClosingEventArgs e)
	{
	}

	private void Application_Deactivated(object sender, DeactivatedEventArgs e)
	{
	}

	private void Application_Launching(object sender, LaunchingEventArgs e)
	{
	}

	private void Application_UnhandledException(object sender, ApplicationUnhandledExceptionEventArgs e)
	{
		bool isAttached = !Debugger.IsAttached;
		if (!isAttached)
		{
			Debugger.Break();
		}
	}

	private void CompleteInitializePhoneApplication(object sender, NavigationEventArgs e)
	{
		bool rootVisual = base.RootVisual == this.RootFrame;
		if (!rootVisual)
		{
			base.RootVisual = this.RootFrame;
		}
		this.RootFrame.remove_Navigated(CompleteInitializePhoneApplication);
	}

	[DebuggerNonUserCode]
	public void InitializeComponent()
	{
		bool flag = !this._contentLoaded;
		if (flag)
		{
			this._contentLoaded = true;
			Application.LoadComponent(this, new Uri("/MobileCombat;component/App.xaml", UriKind.Relative));
		}
	}

	private void InitializePhoneApplication()
	{
		bool flag = !this.phoneApplicationInitialized;
		if (flag)
		{
			this.RootFrame = new PhoneApplicationFrame();
			this.RootFrame.add_Navigated(CompleteInitializePhoneApplication);
			this.RootFrame.add_NavigationFailed(RootFrame_NavigationFailed);
			this.phoneApplicationInitialized = true;
		}
	}

	private void RootFrame_NavigationFailed(object sender, NavigationFailedEventArgs e)
	{
		bool isAttached = !Debugger.IsAttached;
		if (!isAttached)
		{
			Debugger.Break();
		}
	}
}
}