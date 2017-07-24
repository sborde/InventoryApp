package hu.borde.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import hu.borde.inventoryapp.data.InventoryContract.ItemTable;

public class AddItem extends AppCompatActivity {

    private EditText mItemNameTV;
    private EditText mItemPriceTV;
    private EditText mItemQuantityTV;
    private EditText mImageUriTV;
    private ImageView mSelectedImageIV;

    private boolean mItemChanged;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mItemNameTV = (EditText) findViewById(R.id.name_field);
        mItemPriceTV = (EditText) findViewById(R.id.price_field);
        mItemQuantityTV = (EditText) findViewById(R.id.quant_field);
        mImageUriTV = (EditText) findViewById(R.id.image_uri);
        mSelectedImageIV = (ImageView) findViewById(R.id.selected_image);

        mItemChanged = false;

        mItemNameTV.setOnTouchListener(mTouchListener);
        mItemPriceTV.setOnTouchListener(mTouchListener);
        mItemQuantityTV.setOnTouchListener(mTouchListener);
        mImageUriTV.setOnTouchListener(mTouchListener);

        mImageUriTV.setText(null);

        findViewById(R.id.select_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClicked();
            }
        });
    }

    private void saveClicked() {

        if (isEverythingEmpty() && !mItemChanged) {
            finish();
            return;
        }

        try {
            ContentValues values = new ContentValues();

            String nameString = mItemNameTV.getText().toString();
            String quantityString = mItemQuantityTV.getText().toString();
            String priceString = mItemPriceTV.getText().toString();
            String imageUriString = mImageUriTV.getText().toString();

            values.put(ItemTable.COLUMN_ITEM_NAME, nameString);

            int quantity = 0;
            if (!TextUtils.isEmpty(quantityString)) {
                quantity = Integer.parseInt(quantityString);
            }

            int price = 0;

            if (!TextUtils.isEmpty(priceString)) {
                price = Integer.parseInt(priceString);
            }

            if (TextUtils.isEmpty(imageUriString)) {
                imageUriString = null;
            } else {
                Uri imageUri = Uri.parse(imageUriString);
                String mimeType = getContentResolver().getType(imageUri);
                if (mimeType != null && !mimeType.startsWith("image")) {
                    imageUriString = null;
                }
            }

            values.put(ItemTable.COLUMN_ITEM_QUANTITY, quantity);
            values.put(ItemTable.COLUMN_ITEM_PRICE, price);
            values.put(ItemTable.COLUMN_ITEM_IMAGEURI, imageUriString);

            getContentResolver().insert(
                    ItemTable.CONTENT_URI,
                    values);

            Toast.makeText(this, getString(R.string.item_saved), Toast.LENGTH_LONG).show();
            finish();

        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private boolean isEverythingEmpty() {
        return TextUtils.isEmpty(mItemNameTV.getText()) &&
                TextUtils.isEmpty(mItemPriceTV.getText()) &&
                TextUtils.isEmpty(mItemQuantityTV.getText()) &&
                TextUtils.isEmpty(mImageUriTV.getText());
    }

    static final int REQUEST_IMAGE_OPEN = 1;

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_IMAGE_OPEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == RESULT_OK) {
            Uri fullPhotoUri = data.getData();
            mSelectedImageIV.setImageURI(fullPhotoUri);
            mImageUriTV.setText(fullPhotoUri.toString());
        }
    }

}
