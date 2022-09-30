package com.chenyihong.exampledemo.search

import android.content.SearchRecentSuggestionsProvider

class RecentSearchProvider : SearchRecentSuggestionsProvider() {

    companion object {
        const val AUTHORITY = "com.chenyihong.exampledemo.search.RecentSearchProvider"
        const val MODE: Int = DATABASE_MODE_QUERIES or DATABASE_MODE_2LINES
    }

    init {
        setupSuggestions(AUTHORITY, MODE)
    }
}