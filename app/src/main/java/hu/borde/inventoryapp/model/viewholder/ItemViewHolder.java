package hu.borde.inventoryapp.model.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import hu.borde.inventoryapp.R;
import hu.borde.inventoryapp.eventhandler.DecrementOnStockEvent;

/**
 * Class which implements the view holder pattern for items list element.
 */
public class ItemViewHolder {

    private TextView mNameTV;
    private TextView mPieceTV;
    private TextView mPriceTV;
    private Button mSaleButton;

    private Context context;

    public ItemViewHolder(Context context, View parent) {
        mNameTV = (TextView) parent.findViewById(R.id.item_name);
        mPieceTV = (TextView) parent.findViewById(R.id.item_count);
        mPriceTV = (TextView) parent.findViewById(R.id.item_price);
        mSaleButton = (Button) parent.findViewById(R.id.sale_item_btn);

        this.context = context;
    }

    public void setName(String name) {
        this.mNameTV.setText(name);
    }

    public void setPiece(int piece) {
        this.mPieceTV.setText(String.valueOf(piece));
    }

    public void setPrice(int price) {
        this.mPriceTV.setText(context.getString(R.string.price_placeholder, String.valueOf(price)));
    }

    public void setButton(long itemId) {
        this.mSaleButton.setOnClickListener(new DecrementOnStockEvent(context, itemId, this.mPieceTV));
    }

}
