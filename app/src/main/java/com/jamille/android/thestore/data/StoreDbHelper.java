package com.jamille.android.thestore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jamille.android.thestore.data.StoreContract.StoreEntry;

/**
 * Created by Jamille on 04/11/2017.
 */

public class StoreDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 1;

    public StoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_STORE_TABLE = "CREATE TABLE " + StoreEntry.TABLE_NAME + " ("
                + StoreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StoreEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + StoreEntry.COLUMN_ITEM_PRICE + " TEXT NOT NULL, "
                + StoreEntry.COLUMN_ITEM_Quantity + " INTEGER NOT NULL DEFAULT 0, "
                + StoreEntry.COLUMN_ITEM_SUPPLIER + " TEXT NOT NULL, "
                + StoreEntry.COLUMN_ITEM_PICTURE + " TEXT DEFAULT '');";

        db.execSQL(SQL_CREATE_STORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

