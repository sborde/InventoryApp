package hu.borde.inventoryapp.model.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import hu.borde.inventoryapp.R;
import hu.borde.inventoryapp.data.InventoryContract.ItemTable;
import hu.borde.inventoryapp.model.viewholder.ItemViewHolder;

/**
 * Custom cursor adapter to populate list item with items from database.
 */
public class ItemCursorAdapter extends CursorAdapter {

    public ItemCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ItemViewHolder ivh = new ItemViewHolder(context, view);

        ivh.setName(getName(cursor));
        ivh.setPiece(getQuantity(cursor));
        ivh.setPrice(getPrice(cursor));
        ivh.setButton(getId(cursor));

    }

    private int getId(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ItemTable._ID);
        return cursor.getInt(idIndex);
    }

    private String getName(Cursor cursor) {
        int nameIndex = cursor.getColumnIndex(ItemTable.COLUMN_ITEM_NAME);
        return cursor.getString(nameIndex);
    }

    private int getPrice(Cursor cursor) {
        int priceIndex = cursor.getColumnIndex(ItemTable.COLUMN_ITEM_PRICE);
        return cursor.getInt(priceIndex);
    }

    private int getQuantity(Cursor cursor) {
        int quantityIndex = cursor.getColumnIndex(ItemTable.COLUMN_ITEM_QUANTITY);
        return cursor.getInt(quantityIndex);
    }
}
