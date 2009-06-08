package com.phonegap.demo;

import android.content.Context;
import android.webkit.WebView;

/**
 * class designed to allow phonegap to print to the console
 * using the print and println methods.
 */
public class ConsoleOutput {
	
	WebView mAppView;
	Context mCtx;
	
	public ConsoleOutput(Context ctx, WebView appView)
	{
		mCtx = ctx;
		mAppView = appView;
	}

	public void print(String str) {
		System.out.print(str);
	}

	public void println(String str) {
		System.out.println(str);
	}
}
