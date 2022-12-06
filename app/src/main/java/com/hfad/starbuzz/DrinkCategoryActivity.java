package com.hfad.starbuzz;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class DrinkCategoryActivity extends Activity {

    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_category);
        new populateDrinksListViewTask().execute();
        setupDrinksOptionsListView();
    }

    private class populateDrinksListViewTask extends AsyncTask<Integer, Void, Boolean> {

        ListView listDrinks = (ListView) findViewById(R.id.list_drinks);

        protected Boolean doInBackground(Integer... drinks) {
            SQLiteOpenHelper starbuzzDatabaseHelper =
                    new StarbuzzDatabaseHelper(DrinkCategoryActivity.this);
            try {
                db = starbuzzDatabaseHelper.getReadableDatabase();
                cursor = db.query("DRINK",
                        new String[]{"_id", "NAME"},
                        null, null, null, null, null);
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if (success) {
                SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
                        DrinkCategoryActivity.this,
                        android.R.layout.simple_list_item_1,
                        cursor,
                        new String[]{"NAME"},
                        new int[]{android.R.id.text1},
                        0);
                listDrinks.setAdapter(listAdapter);
            } else {
                Toast toast = Toast.makeText(DrinkCategoryActivity.this,
                        "Database unavailable",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void setupDrinksOptionsListView() {
        //Create the listener
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listDrinks,
                                    View itemView,
                                    int position,
                                    long id) {
                //Pass the drink the user clicks on to DrinkActivity
                Intent intent = new Intent(DrinkCategoryActivity.this,
                        DrinkActivity.class);
                intent.putExtra(DrinkActivity.EXTRA_DRINKID, (int) id);
                startActivity(intent);
            }
        };

        //Assign the listener to the list view
        ListView listDrinks = (ListView) findViewById(R.id.list_drinks);
        listDrinks.setOnItemClickListener(itemClickListener);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }
}