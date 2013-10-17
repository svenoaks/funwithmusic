package com.smp.funwithmusic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import static com.smp.funwithmusic.Constants.*;
import static com.smp.funwithmusic.UtilityMethods.readObjectFromFile;

public class UtilityMethods
{
	public static SharedPreferences getPref(Context context)
	{
		//int mode = android.os.Build.VERSION.SDK_INT >= 11 ? Context.MODE_MULTI_PROCESS : Context.MODE_PRIVATE;
		return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
	}
	public static Object readObjectFromFile(Context context, String fileName)
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

	public static void writeObjectToFile(Context context, String fileName, Object obj)
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
}
