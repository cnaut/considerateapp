package com.pinokia.considerateapp;

import android.app.IntentService;
import android.content.Intent;

public class TopAppsIntentService extends IntentService {

  /** 
   * A constructor is required, and must call the super IntentService(String)
   * constructor with a name for the worker thread.
   */
  public TopAppsIntentService() {
      super("TopAppsIntentService");
  }

  /**
   * The IntentService calls this method from the default worker thread with
   * the intent that started the service. When this method returns, IntentService
   * stops the service, as appropriate.
   */
  @Override
  protected void onHandleIntent(Intent intent) {
  	  StatsService.updateTopApps();
  }
}
