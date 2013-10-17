package org.vt.smssec;

import android.content.Context;

public abstract class Command {
	Context context;
	String msg;
	int toastLength;
	
	public Command(Context ctx, String m, int tl){
		context = ctx;
		msg =m;
		toastLength=tl;
	}
	
	public abstract void launchCommand();

}
