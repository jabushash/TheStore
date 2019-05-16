package com.jamille.android.thestore.data;

/**
 * Created by Jamille on 04/11/2017.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class StoreContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.thestore";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_THESTORE = "store";
    private StoreContract() {
    }

    public static final class StoreEntry implements BaseColumns {
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_THESTORE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_THESTORE;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_THESTORE);
        public final static String TABLE_NAME = "store";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ITEM_NAME = "name";
        public final static String COLUMN_ITEM_PRICE = "price";
        public final static String COLUMN_ITEM_SUPPLIER = "supplier";
        public final static String COLUMN_ITEM_Quantity = "quantity";
        public final static String COLUMN_ITEM_PICTURE = "picture";

    }
}


