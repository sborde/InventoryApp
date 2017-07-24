package hu.borde.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;

import hu.borde.inventoryapp.R;
import hu.borde.inventoryapp.data.InventoryContract.ItemTable;

/**
 * This class extends the contentprovider, provides acces to the item database.
 */

public class InventoryProvider extends ContentProvider {

    /**
     * Name of the class for logging.
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /**
     * Constant for Uri referring to whole table.
     */
    private static final int ITEMS = 100;

    /**
     * Constant for Uri referring to a single entry.
     */
    private static final int ITEM_ID = 101;

    /**
     * Uri matcher to resolve URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * URI - Constant mapping
     */
    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS + "/#", ITEM_ID);
    }


    /**
     * Object to manipulate the database.
     */
    private InventoryDbHelper mDbHelper;


    @Override
    public boolean onCreate() {

        this.mDbHelper = new InventoryDbHelper(getContext());

        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                cursor = database.query(ItemTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = ItemTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ItemTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.exception_unknown_uri));
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return ItemTable.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        int match = sUriMatcher.match(uri);

        switch (match) {
            case ITEMS:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("Invalid content URI.");
        }


    }

    private Uri insertItem(Uri uri, ContentValues values) {

        String name = values.getAsString(ItemTable.COLUMN_ITEM_NAME);

        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        Integer price = values.getAsInteger(ItemTable.COLUMN_ITEM_PRICE);

        if (price != null && price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        Integer quantity = values.getAsInteger(ItemTable.COLUMN_ITEM_QUANTITY);

        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        if (values.containsKey(ItemTable.COLUMN_ITEM_IMAGEURI)) {
            String imageUriString = values.getAsString(ItemTable.COLUMN_ITEM_IMAGEURI);

            //If something was put here
            if (imageUriString != null) {
                //but it is empty string, remove it
                if (TextUtils.isEmpty(imageUriString)) {
                    values.remove(ItemTable.COLUMN_ITEM_IMAGEURI);
                } else {
                    Uri imUri = Uri.parse(imageUriString);

                    String mimeType = getContext().getContentResolver().getType(imUri);

                    //If it is not for an image, remove
                    if (mimeType != null && !mimeType.startsWith("image")) {
                        values.remove(ItemTable.COLUMN_ITEM_IMAGEURI);
                    }

                }
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long lastId = database.insert(ItemTable.TABLE_NAME, null, values);

        if (lastId == -1) {
            Log.e(LOG_TAG, "Cannot insert item to database.");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, lastId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUriMatcher.match(uri);

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int deletedRows;
        switch (match) {
            case ITEMS:
                deletedRows = database.delete(ItemTable.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = ItemTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = database.delete(ItemTable.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("This content URI is not supported by delete.");
        }

        if (deletedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUriMatcher.match(uri);

        switch (match) {
            case ITEMS:
                return updateItem(uri, values, selection, selectionArgs);
            case ITEM_ID:
                selection = ItemTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported with this uri.");
        }

    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        if (values.containsKey(ItemTable.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(ItemTable.COLUMN_ITEM_NAME);

            if (name == null || name.equals("")) {
                throw new IllegalArgumentException("Name cannot be empty.");
            }
        }

        if (values.containsKey(ItemTable.COLUMN_ITEM_PRICE)) {
            Integer price = values.getAsInteger(ItemTable.COLUMN_ITEM_PRICE);

            if (price != null && price < 0) {
                throw new IllegalArgumentException("Price cannot be negative.");
            }
        }

        if (values.containsKey(ItemTable.COLUMN_ITEM_QUANTITY)) {

            Integer quantity = values.getAsInteger(ItemTable.COLUMN_ITEM_QUANTITY);

            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative.");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        int affectedRows = database.update(ItemTable.TABLE_NAME, values, selection, selectionArgs);

        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;

    }
}
