/*************************************************************************
 * Copyright 2010 Jules White
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/
 * LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions
 * and limitations under the License.
 **************************************************************************/

package org.vt.smssec;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

public class TauntCommand extends Command  {

	private static final String TAG = "TauntCommand";
	private static final int REQUEST_CODE = 12341234;

	public TauntCommand(Context ctx, String m, int i){
		super(ctx, m, i);
		
	}

	public void sendTaunt() {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		int mult = Integer.parseInt(prefs.getString("NuisanceMultiplier", "1"));

		Toast t = Toast.makeText(context, msg, super.toastLength);
		t.show();

		Log.v(TAG, "Nuisance Multiplier:" + mult);

		mult--;
		if (mult > 0) {
			// Get the AlarmManager service
			AlarmManager am = (AlarmManager) context
					.getSystemService(Activity.ALARM_SERVICE);

			while (mult > 0) {
				// get a Calendar object with current time
				Calendar cal = Calendar.getInstance();

				int timeInMinUntilTaunt = 5; // change me
				cal.add(Calendar.SECOND, timeInMinUntilTaunt);

				Intent intent = new Intent(context,
						ScheduledTauntReceiver.class);
				intent.putExtra("msg", msg);

				PendingIntent sender = PendingIntent
						.getBroadcast(context, REQUEST_CODE, intent,
								PendingIntent.FLAG_UPDATE_CURRENT);

				am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

				Log.v(TAG, "Scheduled a taunt.");

				mult--;

			}
		}
	}

	@Override
	public void launchCommand() {
		sendTaunt();
	}

}
