package com.jamille.android.thestore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jamille.android.thestore.data.StoreContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_ITEM_LOADER = 0;
    private Uri mCurrentItemUri;
    int qty;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mqtyEditText;
    public Button addImageButton;
    private EditText mSupplierEditText;
    private ContentValues values;
    private boolean mItemHasChanged = false;
    private int REQUEST_CODE = 85;
    private ImageView imageViewProduct;
    private Uri imageUri;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    public void onClickAdd(View v) {

        String qtyString = mqtyEditText.getText().toString().trim();
        qty = 0;
        if (!TextUtils.isEmpty(qtyString)) {
            qty = Integer.parseInt(qtyString);
        }
        ++qty;
        values.put(StoreContract.StoreEntry.COLUMN_ITEM_Quantity, qty);
        getContentResolver().update(mCurrentItemUri, values, null, null);
        Toast.makeText(this, R.string.click_to_update, Toast.LENGTH_SHORT).show();
    }

    public void onClickMinus(View v) {

        String qtyString = mqtyEditText.getText().toString().trim();
        qty = 0;
        if (!TextUtils.isEmpty(qtyString)) {
            qty = Integer.parseInt(qtyString);
        }
        --qty;
        if (qty < 0) {
            Toast.makeText(this, R.string.not_less_than, Toast.LENGTH_SHORT).show();
            qty = 0;
            return;
        }
        values.put(StoreContract.StoreEntry.COLUMN_ITEM_Quantity, qty);
        getContentResolver().update(mCurrentItemUri, values, null, null);
        Toast.makeText(this, R.string.click_to_update, Toast.LENGTH_SHORT).show();
    }

    public void order(View v) {

        String nameString = mNameEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", supplierString, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, nameString);
        startActivity(Intent.createChooser(emailIntent, "Send email.."));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Button addButton = (Button) findViewById(R.id.add);
        Button minusButton = (Button) findViewById(R.id.minus);
        Button orderButton = (Button) findViewById(R.id.order);
        imageViewProduct = (ImageView) findViewById(R.id.imageViewProduct);
        addImageButton = (Button) findViewById(R.id.buttonAddImage);
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= 20) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                } else {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Choose image.."), REQUEST_CODE);
            }
        });

        values = new ContentValues();
        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.new_item));
            addButton.setVisibility(View.INVISIBLE);
            minusButton.setVisibility(View.INVISIBLE);
            invalidateOptionsMenu();
            orderButton.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.edit_item));
            addButton.setVisibility(View.VISIBLE);
            minusButton.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mqtyEditText = (EditText) findViewById(R.id.edit_qty);
        mSupplierEditText = (EditText) findViewById(R.id.edit_supplier);
        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mqtyEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            if (mCurrentItemUri != null)
                updateProductImage(imageUri);
            imageViewProduct.setImageURI(imageUri);
        }
    }

    private void updateProductImage(Uri uri) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(StoreContract.StoreEntry.COLUMN_ITEM_PICTURE, uri.toString());
        int result = getContentResolver().update(mCurrentItemUri, contentValues, null, null);
        Log.d("Image", "saveProductImage: " + result);
    }

    private boolean saveItem() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String qtyString = mqtyEditText.getText().toString().trim();
        String supplyString = mSupplierEditText.getText().toString().trim();

        if (nameString.isEmpty() || priceString.isEmpty() || qtyString.isEmpty()
                || supplyString.isEmpty()) {
            Toast.makeText(this, R.string.enter_all_info,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        // Check if this is supposed to be a new item
        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(qtyString) && TextUtils.isEmpty(supplyString)) {
            // Since no fields were modified, we can return early without creating a new item.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return true;
        }
        values.put(StoreContract.StoreEntry.COLUMN_ITEM_NAME, nameString);
        values.put(StoreContract.StoreEntry.COLUMN_ITEM_PRICE, priceString);

        if (!TextUtils.isEmpty(qtyString)) {
            qty = Integer.parseInt(qtyString);
        }
        values.put(StoreContract.StoreEntry.COLUMN_ITEM_Quantity, qty);
        values.put(StoreContract.StoreEntry.COLUMN_ITEM_SUPPLIER, supplyString);
        if (imageUri != null)
            values.put(StoreContract.StoreEntry.COLUMN_ITEM_PICTURE, imageUri.toString());

        // Determine if this is a new or existing item by checking if mCurrentItemUri is null or not
        if (mCurrentItemUri == null) {
            // This is a NEW item, so insert a new item into the provider,
            // returning the content URI for the new item.
            Uri newUri = getContentResolver().insert(StoreContract.StoreEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "insert failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "insert Successfull",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING item, so update the item with content URI: mCurrentItemUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentItemUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "update failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "update successfull",
                        Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                boolean x = saveItem();
                if (x) {
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                ;
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("discard changes and quit editing?");
        builder.setPositiveButton("discard", discardButtonClickListener);
        builder.setNegativeButton("keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("delete this item?");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteItem();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {

                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentItemUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the item that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "error with deleting item",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "item deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all item attributes, define a projection that contains
        // all columns from the store table
        String[] projection = {
                StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_ITEM_NAME,
                StoreContract.StoreEntry.COLUMN_ITEM_PRICE,
                StoreContract.StoreEntry.COLUMN_ITEM_Quantity,
                StoreContract.StoreEntry.COLUMN_ITEM_SUPPLIER,
                StoreContract.StoreEntry.COLUMN_ITEM_PICTURE
        };
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {

            // Find the columns of item attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_ITEM_PRICE);
            int qtyColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_ITEM_Quantity);
            int supplyColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_ITEM_SUPPLIER);
            int imageColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_ITEM_PICTURE);
            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String supplier = cursor.getString(supplyColumnIndex);
            int qty = cursor.getInt(qtyColumnIndex);
            imageUri = Uri.parse(cursor.getString(imageColumnIndex));
            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mqtyEditText.setText(Integer.toString(qty));
            mSupplierEditText.setText(supplier);
            imageViewProduct.setImageURI(imageUri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mqtyEditText.setText("");
        mSupplierEditText.setText("");
    }
}
