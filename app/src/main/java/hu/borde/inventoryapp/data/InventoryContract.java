package hu.borde.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class to define the item database.
 */

public final class InventoryContract {

    /**
     * Prevent instantiation.
     */
    private InventoryContract() {}

    /**
     * Content authority.
     */
    public static final String CONTENT_AUTHORITY = "hu.borde.inventoryapp";

    /**
     * Base URI for this content.
     *
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Content path for this object.
     */
    public static final String PATH_ITEMS = "items";


    /**
     * Inner class which defines the items database table.
     */
    public static class ItemTable implements BaseColumns {

        /**
         * Name of the table containing the items.
         */
        public static final String TABLE_NAME = "item";

        /**
         * Content URI for this table.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        /**
         * ID of the item table.
         *
         * INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Name of the item.
         *
         * TEXT
         */
        public static final String COLUMN_ITEM_NAME = "name";

        /**
         * Current quantity in stock. It can be only non negative integer.
         *
         * INTEGER
         */
        public static final String COLUMN_ITEM_QUANTITY = "quantity";

        /**
         * Unit price of this item. To retain precision, stored in cents (so it have to be divided by 100 before display).
         *
         * INTEGER
         */
        public static final String COLUMN_ITEM_PRICE = "price";

        /**
         * Content URI of the representing image. It can be null.
         *
         * TEXT
         */
        public static final String COLUMN_ITEM_IMAGEURI = "image";

    }

}
