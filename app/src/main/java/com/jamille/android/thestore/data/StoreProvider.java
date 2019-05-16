package com.jamille.android.thestore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.jamille.android.thestore.data.StoreContract.CONTENT_AUTHORITY;
import static com.jamille.android.thestore.data.StoreContract.PATH_THESTORE;

/**
 * Created by Jamille on 02/11/2017.
 */

public class StoreProvider extends ContentProvider {

    private static final int STORE = 100;
    private static final int STORE_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_THESTORE, STORE);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_THESTORE + "/#", STORE_ID);
    }

    StoreDbHelper mDbHelper;
    public static final String LOG_TAG = StoreProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        mDbHelper = new StoreDbHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORE:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(StoreContract.StoreEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        //notify all listeners that the data has changed for the STORE content URI
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORE:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case STORE_ID:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(StoreContract.StoreEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(StoreContract.StoreEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Requires a name");
            }
        }
        if (values.containsKey(StoreContract.StoreEntry.COLUMN_ITEM_PRICE)) {
            String price = values.getAsString(StoreContract.StoreEntry.COLUMN_ITEM_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Requires Price");
            }
        }

        if (values.containsKey(StoreContract.StoreEntry.COLUMN_ITEM_Quantity)) {
            Integer qty = values.getAsInteger(StoreContract.StoreEntry.COLUMN_ITEM_Quantity);
            if (qty == null) {
                throw new IllegalArgumentException("Requires qty");
            }
        }

        if (values.containsKey(StoreContract.StoreEntry.COLUMN_ITEM_SUPPLIER)) {
            String supplier = values.getAsString(StoreContract.StoreEntry.COLUMN_ITEM_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Requires supplier");
            }
        }

        if (values.containsKey(StoreContract.StoreEntry.COLUMN_ITEM_PICTURE)) {
            String picture = values.getAsString(StoreContract.StoreEntry.COLUMN_ITEM_PICTURE);
            if (picture == null) {
                throw new IllegalArgumentException("Requires supplier");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(StoreContract.StoreEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORE:
                rowsDeleted = database.delete(StoreContract.StoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STORE_ID:
                // Delete a single row given by the ID in the URI
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(StoreContract.StoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORE:
                return StoreContract.StoreEntry.CONTENT_LIST_TYPE;
            case STORE_ID:
                return StoreContract.StoreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STORE:
                cursor = database.query(StoreContract.StoreEntry.TABLE_NAME, projection, selection, selectionArgs, null,
                        null, sortOrder);
                break;
            case STORE_ID:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(StoreContract.StoreEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
}
