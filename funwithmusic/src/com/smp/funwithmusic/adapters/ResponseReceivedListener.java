package com.smp.funwithmusic.adapters;

import android.view.ViewGroup;

import com.afollestad.cardsui.Card;
import com.gracenote.mmid.MobileSDK.GNSearchResponse;
import com.smp.funwithmusic.dataobjects.Song;

public interface ResponseReceivedListener
{
	void beginGracenote(ViewGroup parent, Card card, Song song);

	void gracenoteStageOneComplete(ViewGroup parent, Card card, Song song, GNSearchResponse response);

	void updateSingleView(ViewGroup parent, Card card);

	// void doNormalize(ViewGroup parent, Card card, Song song);

	void doLyricsListen(ViewGroup parent, Card card, Song song);
}
