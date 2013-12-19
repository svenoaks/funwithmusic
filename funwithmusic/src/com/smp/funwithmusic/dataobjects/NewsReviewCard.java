package com.smp.funwithmusic.dataobjects;

import com.afollestad.cardsui.Card;

public class NewsReviewCard extends Card
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1097621978984542869L;
	
	private NewsReview review;

	public NewsReview getReview()
	{
		return review;
	}

	public NewsReviewCard(final NewsReview review)
	{
		super(review.getName(), review.getSummary());
		this.review = review;
	}

}
