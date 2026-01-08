package com.example.questmaster.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.questmaster.R;
import com.example.questmaster.model.Country;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

    // Constants for NewsAPI.org
    public static final String FRANCE = "fr";
    public static final String ITALY = "it";
    public static final String GERMANY = "de";
    public static final String UNITED_KINGDOM = "gb";
    public static final String SPAIN = "es";

    public static final String CATEGORY_MAPTRACK = "map track";
    public static final String CATEGORY_ENTERTAINMENT = "entertainment";
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_HEALTH = "health";
    public static final String CATEGORY_SCIENCE = "science";
    public static final String CATEGORY_SPORTS = "sports";
    public static final String CATEGORY_TECHNOLOGY= "technology" ;

    public static final String[] LIST_GENRES = {
            CATEGORY_MAPTRACK,
            CATEGORY_ENTERTAINMENT,
            CATEGORY_GENERAL,
            CATEGORY_HEALTH,
            CATEGORY_SCIENCE,
            CATEGORY_SPORTS,
            CATEGORY_TECHNOLOGY
    };

    public static String[] getListCategoriesNames(Context context) {
        return new String[]{

        };
    }

    public static Drawable[] getListCategoriesDrawables(Context context) {
        return new Drawable[]{

        };
    }




    public static final String SHARED_PREFERENCES_FILENAME = "com.example.questmaster.preferences";
    public static final String SHARED_PREFERENCES_COUNTRY_OF_INTEREST = "country_of_interest";
    public static final String SHARED_PREFERENCES_CATEGORIES_OF_INTEREST = "categories_of_interest";
    public static final String SHARED_PREFERNECES_LAST_UPDATE = "last_update";

    public static final String SAMPLE_JSON_API_RESPONSE = "sample_api_response.json";
    public static final String SAVED_ARTICLES_DATABASE = "db-articles";

    public static final String NEWS_API_BASE_URL = "https://newsapi.org/v2/";
    public static final String TOP_HEADLINES_ENDPOINT = "top-headlines";
    public static final String TOP_HEADLINES_COUNTRY_PARAMETER = "country";
    public static final String TOP_HEADLINES_PAGE_SIZE_PARAMETER = "pageSize";
    public static final int TOP_HEADLINES_PAGE_SIZE_VALUE = 100;

    public static final String REMOVED_ARTICLE_TITLE = "[Removed]";

    public static final int FRESH_TIMEOUT = 1000 * 60; // 1 minute in milliseconds

    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";

    public static final String BUNDLE_KEY_CURRENT_ARTICLE = "current_article";



}
