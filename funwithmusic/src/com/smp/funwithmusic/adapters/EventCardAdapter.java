package com.smp.funwithmusic.adapters;

import static com.smp.funwithmusic.global.Constants.*;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.smp.funwithmusic.R;
import com.smp.funwithmusic.dataobjects.Event;
import com.smp.funwithmusic.dataobjects.EventCard;
import com.smp.funwithmusic.dataobjects.Performance;

public class EventCardAdapter<T extends EventCard> extends CardAdapter<Card>
{
	Context context;
	private TextAppearanceSpan titleAppearance;
	
	public EventCardAdapter(Context context)
	{
		super(context, R.layout.card_event);
		this.context = context;
		
		ColorStateList blue = ColorStateList.valueOf
				(context.getResources().getColor(R.color.holo_blue_dark));
		
		int size = context.getResources().getDimensionPixelSize(R.dimen.card_content);
		int style = Typeface.NORMAL;
		
		titleAppearance = new TextAppearanceSpan("sans-serif", style, size, blue, null);
	}

	@Override
	public View onViewCreated(int index, View recycled, Card item,
			ViewGroup parent, ViewHolder holder)
	{
		if (holder.content2 == null)
			holder.content2 = (TextView) recycled.findViewById(R.id.location);
		if (holder.content3 == null)
			holder.content3 = (TextView) recycled.findViewById(R.id.date_time);

		if (holder.content2 != null)
			onProcessLocation(holder.content2, item, parent);
		if (holder.content3 != null)
			onProcessDateTime(holder.content3, item, parent);
		
		return super.onViewCreated(index, recycled, item, parent, holder);
	}

	private void onProcessDateTime(TextView content3, Card item, ViewGroup parent)
	{
		final Event event = ((EventCard) item).getEvent();
		String date = event.getDateTime();
		if (date == null || date.equals("null"))
			date = event.getDate();

		content3.setText(date);
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
		return super.onProcessThumbnail(icon, card, parent);
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
			blueString.append(event.getDisplayName() + NEW_LINE);
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

		
		spanned.setSpan(titleAppearance, 0, blueString.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		
		int sl = spanned.length();
		int nl = NEW_LINE.length();
		CharSequence test = spanned.subSequence(sl - nl, sl);
		if (sl >= nl)
			while (spanned.subSequence(sl - nl, sl).toString().equals(NEW_LINE))
			{
				spanned.replace(sl - nl, sl, "");
				sl = spanned.length();
			}

		content.setText(spanned);

		return true;

		// return super.onProcessContent(content, card);
	}

	private void onProcessLocation(TextView title2, Card item, ViewGroup parent)
	{
		final Event event = ((EventCard) item).getEvent();
		String location = event.getLocation();
		title2.setText(location);
	}

}
