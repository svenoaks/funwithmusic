package com.smp.funwithmusic.global;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.smp.funwithmusic.R;
import com.smp.funwithmusic.dataobjects.EventInfo;
import com.smp.funwithmusic.dataobjects.Song;
import com.smp.funwithmusic.services.IdentifyMusicService;
import com.smp.funwithmusic.views.ProgressWheel;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;
import static com.smp.funwithmusic.global.Constants.*;
import static com.smp.funwithmusic.global.UtilityMethods.readObjectFromFile;
import static com.smp.funwithmusic.global.UtilityMethods.viewVisible;

public class UtilityMethods
{
	@SuppressWarnings("deprecation")
	public static void removeLayoutListenerPre16(ViewTreeObserver observer, OnGlobalLayoutListener listener)
	{
		observer.removeGlobalOnLayoutListener(listener);
	}

	@SuppressLint("NewApi")
	public static void removeLayoutListenerPost16(ViewTreeObserver observer, OnGlobalLayoutListener listener)
	{
		observer.removeOnGlobalLayoutListener(listener);
	}
	 public static boolean isOnline(Context context)
     {
             ConnectivityManager cm = (ConnectivityManager) context.getSystemService
            		 (Context.CONNECTIVITY_SERVICE);
             NetworkInfo netInfoMob = cm
                             .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
             NetworkInfo netInfoWifi = cm
                             .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

             return ((netInfoMob != null && netInfoMob.isConnectedOrConnecting()) 
            		 || (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting()));
     }
	public static boolean isMyServiceRunning
		(Context context, Class<? extends Service> myService)
	{
		ActivityManager manager = (ActivityManager) context.getSystemService
				(Context.ACTIVITY_SERVICE);
		
		for (RunningServiceInfo service 
				: manager.getRunningServices(Integer.MAX_VALUE))
		{
			if (myService.getName().equals(service.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}

	public static SharedPreferences getPref(Context context)
	{
		// int mode = android.os.Build.VERSION.SDK_INT >= 11 ?
		// Context.MODE_MULTI_PROCESS : Context.MODE_PRIVATE;
		return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
	}

	public static synchronized Object readObjectFromFile(Context context, String fileName)
	{
		Object result = null;
		FileInputStream fis = null;
		try
		{
			fis = context.openFileInput(fileName);
			ObjectInputStream objectIn = new ObjectInputStream(fis);
			return objectIn.readObject();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (fis != null)
				try
				{
					fis.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
		}
		return result;
	}

	public static synchronized void writeObjectToFile(Context context, String fileName, Object obj)
	{
		FileOutputStream fos = null;
		try
		{
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			ObjectOutputStream objectOut = new ObjectOutputStream(fos);
			objectOut.writeObject(obj);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				fos.flush();
				fos.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Song> getSongList(Context context)
	{
		ArrayList<Song> songs;

		Object obj = readObjectFromFile(context, SONG_FILE_NAME);
		if (obj != null)
			songs = (ArrayList<Song>) obj;
		else
			songs = new ArrayList<Song>();

		return songs;

	}

	// Method to start an activity with a single Parceable, could be expanded
	// with variable arguments

	public static <T extends Parcelable> void startNewActivityWithObject(Context context, Class<?> cls, T info, String name)
	{
		Intent intent = new Intent(context, cls);
		intent.putExtra(name, info);
		context.startActivity(intent);
	}

	public static void viewVisible(final View view)
	{
		view.post(new Runnable()
		{
			@Override
			public void run()
			{
				view.setVisibility(View.VISIBLE);
			}
		});
	}

	public static void viewGone(final View view)
	{
		view.post(new Runnable()
		{
			@Override
			public void run()
			{
				view.setVisibility(View.GONE);
			}
		});
	}

	public static void progressSpin(final View view)
	{
		view.post(new Runnable()
		{
			@Override
			public void run()
			{
				ProgressWheel pw = (ProgressWheel) view.findViewById(R.id.pw_spinner);
				if (pw != null)
					pw.spin();
			}
		});
	}

	public static void progressStopSpin(final View view)
	{
		view.post(new Runnable()
		{
			@Override
			public void run()
			{
				ProgressWheel pw = (ProgressWheel) view.findViewById(R.id.pw_spinner);
				if (pw != null)
					pw.stopSpinning();
			}
		});
	}

	public static void doListen(Context context, final View idDialog)
	{
		viewVisible(idDialog);
		progressSpin(idDialog);
		Intent intent = new Intent(context, IdentifyMusicService.class);
		context.startService(intent);
	}

	public static void doDeleteFlow(Context context)
	{
		context.deleteFile(SONG_FILE_NAME);
		Toast.makeText(context, TOAST_FLOW_DELETED, Toast.LENGTH_SHORT).show();
	}
}
