package com.jamille.android.thestore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jamille.android.thestore.data.StoreContract;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int STORE_LOADER = 0;
    StoreCursorAdapter mCursorAdapter;
    ContentValues values;
    Uri currentItemUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView listView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        mCursorAdapter = new StoreCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                currentItemUri = ContentUris.withAppendedId(StoreContract.StoreEntry.CONTENT_URI, id);
                intent.setData(currentItemUri);
                startActivity(intent);

            }
        });
        getLoaderManager().initLoader(STORE_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        values = new ContentValues();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummy();
                return true;

            case R.id.action_delete_all_entries:
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummy() {

        values.put(StoreContract.StoreEntry.COLUMN_ITEM_NAME, getString(R.string.dell_laptop));
        values.put(StoreContract.StoreEntry.COLUMN_ITEM_PRICE, getString(R.string.UDS_600));
        values.put(StoreContract.StoreEntry.COLUMN_ITEM_Quantity, 4);
        values.put(StoreContract.StoreEntry.COLUMN_ITEM_SUPPLIER, getString(R.string.abc_email));
        values.put(StoreContract.StoreEntry.COLUMN_ITEM_PICTURE, "");
        Uri newUri = getContentResolver().insert(StoreContract.StoreEntry.CONTENT_URI, values);
    }

    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(StoreContract.StoreEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from  database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StoreContract.StoreEntry._ID,       //always include ID
                StoreContract.StoreEntry.COLUMN_ITEM_NAME,
                StoreContract.StoreEntry.COLUMN_ITEM_PRICE,
                StoreContract.StoreEntry.COLUMN_ITEM_Quantity
        };

        //this loader will excucte the ContentProvider query method on a background thread
        return new CursorLoader(this, StoreContract.StoreEntry.CONTENT_URI,
                projection, //coloumns to include in the resulting cursor
                null,       //no selection clause
                null,       //no selection argument
                null);      //default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mCursorAdapter.swapCursor(null);
    }
}
