package com.jamille.android.thestore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.jamille.android.thestore.data.StoreContract;

/**
 * Created by Jamille on 04/11/2017.
 */

public class StoreCursorAdapter extends CursorAdapter {

    public StoreCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView qtyTextView = (TextView) view.findViewById(R.id.qty);
        Button saleButton = (Button) view.findViewById(R.id.buttonSale);
        int nameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_ITEM_PRICE);
        int qtyColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_ITEM_Quantity);
        final int idIndex = cursor.getColumnIndex(StoreContract.StoreEntry._ID);
        String name = cursor.getString(nameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        final int qty = cursor.getInt(qtyColumnIndex);
        final int productId = cursor.getInt(idIndex);
        nameTextView.setText(name);
        priceTextView.setText(price);
        qtyTextView.setText(String.valueOf(qty));

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri mProductUri = ContentUris.withAppendedId(StoreContract.StoreEntry.CONTENT_URI, productId);
                doSale(context, qty, mProductUri);
            }
        });
    }

    private void doSale(Context context, int qty, Uri mProductUri) {
        if (qty == 0) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(StoreContract.StoreEntry.COLUMN_ITEM_Quantity, --qty);
        int result = context.getContentResolver().update(mProductUri, contentValues, null, null);
        Log.d("doSale", "doSale: " + result);
    }
}