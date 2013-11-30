package com.smp.funwithmusic.adapters;

import static com.smp.funwithmusic.global.Constants.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.apiclient.SongKickClient;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventCard;
import com.smp.funwithmusic.dataobjects.Performance;
import com.smp.funwithmusic.fragments.EventsFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Picasso.LoadedFrom;

public class EventCardAdapter<T extends EventCard> extends CardAdapter<Card>
{
	private enum ImageDisplayedView
	{
		LOADING, ARTIST_IMAGE
	};

	private Context context;
	private final static int TYPE_HEADER = 2;
	private TextAppearanceSpan titleAppearance;
	private String imageUrl;
	private Target target;
	private float LAYOUT_HEIGHT_IN_DP;
	private int noOfEvents;
	private float maxFrameWidth;

	public EventCardAdapter(Context context, String imageUrl, int noOfEvents)
	{
		super(context, R.layout.list_item_event);
		this.context = context;
		this.imageUrl = imageUrl;
		this.noOfEvents = noOfEvents;
		ColorStateList blue = ColorStateList.valueOf
				(context.getResources().getColor(R.color.holo_blue_dark));

		int size = context.getResources().getDimensionPixelSize(R.dimen.card_content);
		int style = Typeface.NORMAL;

		titleAppearance = new TextAppearanceSpan("sans-serif", style, size, blue, null);

		LAYOUT_HEIGHT_IN_DP = context
				.getResources()
				.getDimension(R.dimen.artist_info_pic_height);
	}

	@Override
	protected void setupHeader(Card header, View view)
	{
		super.setupHeader(header, view);
		final ViewFlipper flipper = (ViewFlipper) view.findViewById(R.id.flip_image);
		target = new Target()
		{
			@Override
			public void onBitmapFailed(Drawable arg0)
			{

			}

			@Override
			public void onBitmapLoaded(final Bitmap artistBitmap, LoadedFrom arg1)
			{
				setupFrame(artistBitmap, flipper);
				flipper.setDisplayedChild(ImageDisplayedView.ARTIST_IMAGE.ordinal());
			}

			@Override
			public void onPrepareLoad(Drawable arg0)
			{
				// TODO Auto-generated method stub

			}
		};
		ViewTreeObserver vto = flipper.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				maxFrameWidth = flipper.getWidth();
				Picasso.with(context).load(imageUrl).into(target);
				if (Build.VERSION.SDK_INT < 16)
				{
					removeLayoutListenerPre16(flipper.getViewTreeObserver(), this);
				}
				else
				{
					removeLayoutListenerPost16(flipper.getViewTreeObserver(), this);
				}
			}
		});

	}

	@SuppressWarnings("deprecation")
	private void removeLayoutListenerPre16(ViewTreeObserver observer, OnGlobalLayoutListener listener)
	{
		observer.removeGlobalOnLayoutListener(listener);
	}

	@SuppressLint("NewApi")
	private void removeLayoutListenerPost16(ViewTreeObserver observer, OnGlobalLayoutListener listener)
	{
		observer.removeOnGlobalLayoutListener(listener);
	}

	protected void setupFrame(Bitmap artistBitmap, ViewFlipper flipper)
	{
		FrameLayout frame = (FrameLayout) flipper.findViewById(R.id.image_frame);

		int width = artistBitmap.getWidth();
		int height = artistBitmap.getHeight();

		final double MAX_PERCENTAGE_OF_SCREEN = 0.75;
		int maxWidth = (int) (maxFrameWidth * MAX_PERCENTAGE_OF_SCREEN);

		float scale = LAYOUT_HEIGHT_IN_DP / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		Bitmap scaledBitmap = Bitmap.createBitmap(artistBitmap, 0, 0, width, height, matrix, true);

		width = scaledBitmap.getWidth();
		height = scaledBitmap.getHeight();

		if (width > maxWidth)
			width = maxWidth;

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
				height);

		frame.setLayoutParams(params);

		ImageView eventImage = (ImageView) flipper.findViewById(R.id.event_image);
		eventImage.setImageBitmap(scaledBitmap);

		TextView eventText = (TextView) flipper.findViewById(R.id.concerts_number);
		eventText.setText(String.valueOf(noOfEvents) +
				" upcoming concerts");
	}

	private int dpToPx(int dp)
	{
		float density = context.getApplicationContext().getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}

	public void cancelPicasso()
	{
		Picasso.with(context).cancelRequest(target);
	}

	@Override
	public int getLayout(int index, int type)
	{
		if (type == TYPE_HEADER)
			return R.layout.list_item_event_header;

		return super.getLayout(index, type);
	}

	@Override
	public View onViewCreated(int index, View recycled, Card item,
			ViewGroup parent, ViewHolder holder)
	{
		if (holder.content2 == null)
			holder.content2 = (TextView) recycled.findViewById(R.id.location);
		if (holder.content3 == null)
			holder.content3 = (TextView) recycled.findViewById(R.id.date_time);

		if (holder.title2 == null)
			holder.title2 = (TextView) recycled.findViewById(R.id.festival_name);

		if (holder.content2 != null)
			onProcessLocation(holder.content2, item, parent);
		if (holder.content3 != null)
			onProcessDateTime(holder.content3, item, parent);

		if (holder.title2 != null)
			onProcessFestivalName(holder.title2, item, parent);

		return super.onViewCreated(index, recycled, item, parent, holder);
	}

	private void onProcessFestivalName(TextView title2, Card item, ViewGroup parent)
	{
		final Event event = ((EventCard) item).getEvent();

		if (event.getType().equals("Festival"))
		{
			SpannableStringBuilder spanned = new SpannableStringBuilder(event.getDisplayName());
			spanned.setSpan(titleAppearance, 0, spanned.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			title2.setVisibility(View.VISIBLE);
			title2.setText(spanned);
		}
		else
		{
			title2.setText("");
			title2.setVisibility(View.GONE);
		}
	}

	private void onProcessDateTime(TextView content3, Card item, ViewGroup parent)
	{
		final Event event = ((EventCard) item).getEvent();
		Locale locale = Locale.getDefault();
		Date date = null;
		SimpleDateFormat parse = null;
		DateFormat format = null;
		if (event.getDateTime() != null
				&& !event.getDateTime().equals("null"))
		{
			final int BEG_TZ = 19;
			String noTimeZone = event.getDateTime().substring(0, BEG_TZ);
			parse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale);
			try
			{
				date = parse.parse(noTimeZone);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
			format = DateFormat.getDateTimeInstance
					(DateFormat.MEDIUM, DateFormat.SHORT, locale);
		}
		else if (event.getDate() != null && !event.getDate().equals("null"))
		{
			parse = new SimpleDateFormat("yyyy-MM-dd", locale);
			try
			{
				date = parse.parse(event.getDate());

			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
			format = DateFormat.getDateInstance
					(DateFormat.MEDIUM, locale);
		}

		content3.setText(format.format(date));
	}

	@Override
	protected boolean onProcessTitle(TextView title, Card card, int accentColor)
	{
		final Event event = ((EventCard) card).getEvent();
		if (title == null)
			return false;
		SpannableStringBuilder spanned = new SpannableStringBuilder(event.getVenueDisplayName());
		spanned.setSpan(titleAppearance, 0, spanned.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		title.setText(spanned);
		return true;
	}

	@Override
	protected boolean onProcessThumbnail(ImageView icon, Card card, ViewGroup parent)
	{
		final Event event = ((EventCard) card).getEvent();
		if (icon == null)
			return false;
		Picasso.with(context).load(imageUrl)
				.fit()
				.centerCrop()
				.into(icon);
		return true;
	}

	// Other artists at show
	@Override
	protected boolean onProcessContent(TextView content, Card card)
	{

		if (content == null)
			return false;
		final Event event = ((EventCard)
				card).getEvent();
		List<Performance> perfs = event.getPerformances();

		StringBuilder blueString = new StringBuilder();
		StringBuilder normalColorString = new StringBuilder();

		if (event.getType().equals("Festival"))
		{
			for (Performance per : perfs)
			{
				normalColorString.append(per.getDisplayName()
						+ NEW_LINE);
			}
		}
		else
		{
			for (Performance per : perfs)
			{
				if (per.getBilling().equals("headline"))
				{
					blueString.append(per.getDisplayName()
							+ NEW_LINE);
				}
				else
				{
					normalColorString.append(per.getDisplayName()
							+ NEW_LINE);
				}
			}
		}

		SpannableStringBuilder spanned = new SpannableStringBuilder(blueString.toString()
				+ normalColorString.toString());

		if (blueString.length() > 0)
			spanned.setSpan(titleAppearance, 0, blueString.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		int sl = spanned.length();
		int nl = NEW_LINE.length();

		if (sl >= nl)
			while (spanned.subSequence(sl - nl, sl).toString().equals(NEW_LINE))
			{
				spanned.replace(sl - nl, sl, "");
				sl = spanned.length();
			}

		content.setText(spanned);

		return true;
	}

	private void onProcessLocation(TextView title2, Card item, ViewGroup parent)
	{
		final Event event = ((EventCard) item).getEvent();
		String location = event.getLocation();
		title2.setText(location);
	}

}
