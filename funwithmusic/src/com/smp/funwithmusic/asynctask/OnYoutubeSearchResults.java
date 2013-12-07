package com.smp.funwithmusic.asynctask;

import java.util.List;

import com.google.api.services.youtube.model.SearchResult;

public interface OnYoutubeSearchResults
{
	public void onSearchResultsReceived(List<SearchResult> list);
}
