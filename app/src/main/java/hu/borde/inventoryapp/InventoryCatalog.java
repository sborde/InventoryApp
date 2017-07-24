package hu.borde.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import hu.borde.inventoryapp.data.InventoryContract.ItemTable;
import hu.borde.inventoryapp.model.adapter.ItemCursorAdapter;

public class InventoryCatalog extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ItemCursorAdapter mItemCursorAdapter;

    private static final int ITEM_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InventoryCatalog.this, AddItem.class));
            }
        });

        ListView resultList = (ListView) findViewById(R.id.item_list_view);
        resultList.setEmptyView(findViewById(R.id.empty_view));
        mItemCursorAdapter = new ItemCursorAdapter(this, null);
        resultList.setAdapter(mItemCursorAdapter);


        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(InventoryCatalog.this, DetailActivity.class);

                Uri uri = ContentUris.withAppendedId(ItemTable.CONTENT_URI, id);

                intent.setData(uri);

                startActivity(intent);
            }
        });


        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections = {ItemTable._ID, ItemTable.COLUMN_ITEM_NAME, ItemTable.COLUMN_ITEM_PRICE, ItemTable.COLUMN_ITEM_QUANTITY};

        return new CursorLoader(this,
                ItemTable.CONTENT_URI,
                projections,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mItemCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemCursorAdapter.swapCursor(null);
    }
}
