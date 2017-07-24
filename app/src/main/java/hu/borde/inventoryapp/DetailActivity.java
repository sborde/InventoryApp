package hu.borde.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import hu.borde.inventoryapp.data.InventoryContract.ItemTable;
import hu.borde.inventoryapp.eventhandler.DecrementOnStockEvent;
import hu.borde.inventoryapp.eventhandler.IncrementOnStockEvent;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mItemUri;

    private TextView mNameTV;
    private TextView mQuantityTV;
    private TextView mPriceTV;
    private ImageView mImageView;

    private final static int DEFAULT_ORDER_QUANTITY = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mItemUri = intent.getData();

        Log.i("DetailActivity", mItemUri.toString());

        setTitle(getString(R.string.activity_details));

        this.mNameTV = (TextView) findViewById(R.id.name_field);
        this.mQuantityTV = (TextView) findViewById(R.id.quant_field);
        this.mPriceTV = (TextView) findViewById(R.id.price_field);
        this.mImageView = (ImageView) findViewById(R.id.product_image);
        long mItemID = ContentUris.parseId(mItemUri);

        findViewById(R.id.decrement_button).setOnClickListener(new DecrementOnStockEvent(getApplicationContext(), mItemID, this.mQuantityTV));
        findViewById(R.id.increment_button).setOnClickListener(new IncrementOnStockEvent(getApplicationContext(), mItemID, this.mQuantityTV));
        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClicked();
            }
        });
        findViewById(R.id.order_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrder();
            }
        });

        getLoaderManager().initLoader(0, null, this);

    }

    private void sendOrder() {

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.dummy_email));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.ordering_email_subject) + this.mNameTV.getText().toString());

        String emailBody = "";

        emailBody += getString(R.string.item_name) + ": " + this.mNameTV.getText().toString() + "\n";
        emailBody += getString(R.string.quantity) + ": " + String.valueOf(DEFAULT_ORDER_QUANTITY) + "\n";
        emailBody += getString(R.string.price) + ": " + this.mPriceTV.getText().toString() + "\n";

        intent.putExtra(Intent.EXTRA_TEXT, emailBody);

        startActivity(Intent.createChooser(intent, getString(R.string.intent_title)));
    }

    private void onDeleteClicked() {

        DialogInterface.OnClickListener confirmButtonListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ContentResolver cr = getContentResolver();

                        int deletedRow = cr.delete(mItemUri, null, null);

                        switch (deletedRow) {
                            case 0:
                                Toast.makeText(getApplicationContext(), R.string.delete_went_wrong, Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(getApplicationContext(), R.string.delete_successful, Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                        }

                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_confirm_title);
        builder.setMessage(R.string.delete_confirm_text);
        builder.setNegativeButton(getString(R.string.delete_neg_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.setPositiveButton(getString(R.string.delete_pos_button), confirmButtonListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                mItemUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToNext()) {

            int nameIdx = data.getColumnIndex(ItemTable.COLUMN_ITEM_NAME);
            int priceIdx = data.getColumnIndex(ItemTable.COLUMN_ITEM_PRICE);
            int quantityIdx = data.getColumnIndex(ItemTable.COLUMN_ITEM_QUANTITY);
            int imageIdx = data.getColumnIndex(ItemTable.COLUMN_ITEM_IMAGEURI);

            String name = data.getString(nameIdx);
            String quantity = String.valueOf(data.getInt(quantityIdx));
            String price = String.valueOf(data.getInt(priceIdx));

            this.mNameTV.setText(name);
            this.mQuantityTV.setText(quantity);
            this.mPriceTV.setText(getString(R.string.price_placeholder,  price));

            String imageUri = data.getString(imageIdx);


            if (imageUri != null && !TextUtils.isEmpty(imageUri)) {
                this.mImageView.setImageURI(Uri.parse(imageUri));
            } else {
                this.mImageView.setImageResource(R.drawable.ic_image);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.mNameTV.setText(null);
        this.mPriceTV.setText(null);
        this.mQuantityTV.setText(null);
        this.mImageView.setVisibility(View.INVISIBLE);
    }
}
