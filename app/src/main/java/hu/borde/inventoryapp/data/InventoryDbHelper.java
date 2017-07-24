package hu.borde.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import hu.borde.inventoryapp.data.InventoryContract.ItemTable;

/**
 * Extension of SQLiteOpenHelper which creates
 * and access the database.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file.
     */
    public static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version. If schema changed, it will be incremented.
     */
    public static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String TABLE_CREATE_SQL = "CREATE TABLE " + ItemTable.TABLE_NAME + " (" +
                ItemTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemTable.COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                ItemTable.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                ItemTable.COLUMN_ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0, " +
                ItemTable.COLUMN_ITEM_IMAGEURI + " TEXT);";

        db.execSQL(TABLE_CREATE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
