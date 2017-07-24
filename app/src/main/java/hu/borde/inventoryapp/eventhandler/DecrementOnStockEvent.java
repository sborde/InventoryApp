package hu.borde.inventoryapp.eventhandler;

import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import hu.borde.inventoryapp.data.InventoryContract.ItemTable;

/**
 * This class is an event handler for button(s) which decrement the
 * product quantity on stock by 1.
 */
public class DecrementOnStockEvent implements View.OnClickListener {

    private Context context;

    private long mItemId;

    private TextView mOldItemCount;

    public DecrementOnStockEvent(Context context, long itemId, TextView oldItemCount) {
        this.context = context;
        this.mItemId = itemId;
        this.mOldItemCount = oldItemCount;
    }

    @Override
    public void onClick(View v) {

        int oldItemCount = Integer.parseInt(mOldItemCount.getText().toString());

        if (oldItemCount > 0) {

            ContentValues values = new ContentValues();

            int itemCountAfterSale = oldItemCount - 1;

            values.put(ItemTable.COLUMN_ITEM_QUANTITY, itemCountAfterSale);

            String selection = ItemTable._ID + "=?";
            String[] selectionArgs = {String.valueOf(mItemId)};

            this.context.getContentResolver().update(ItemTable.CONTENT_URI, values, selection, selectionArgs);


        }
    }
}
