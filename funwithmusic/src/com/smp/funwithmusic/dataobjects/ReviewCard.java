package com.smp.funwithmusic.dataobjects;

import com.afollestad.cardsui.Card;

public class ReviewCard extends Card
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1097621978984542869L;
	
	private Review review;

	public Review getReview()
	{
		return review;
	}

	public ReviewCard(final Review review)
	{
		super(review.getName(), review.getSummary());
		this.review = review;
	}

}
