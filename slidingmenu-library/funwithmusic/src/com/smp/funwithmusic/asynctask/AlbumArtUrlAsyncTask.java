package com.smp.funwithmusic.asynctask;

import android.os.AsyncTask;
import android.widget.ImageView;
//unused
public class AlbumArtUrlAsyncTask extends AsyncTask<ImageView, Void, String>
{
	
	ImageView icon;
	 protected String doInBackground(ImageView... icon) {
		 this.icon = icon[0];
		return null;
		
       
     }



     protected void onPostExecute(String result) {
         
     }
}
