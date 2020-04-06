package com.example.khush.oilandgasproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
/**
 * This activity will have all the headers of the CSV file to select
 */
public class CSVFileFieldsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CSVHeaderAdapter mAdapter;
    public static final String SELECTED_HEADERS = "CSVFile_SelectedHeaders";
    public static final String CSV_FILE_READ = "CSVFileRead";
    ArrayList<Headers> csvheaders;
    /**
     * This methos will hav ethe recyclerview and arraylist of headers
     * so that it can get it by id
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csvfilefields);

        // Get the intent and its data.
        Intent intent = getIntent();
        csvheaders = (ArrayList<Headers>) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);

        // Get a handle to the RecyclerView.
        mRecyclerView = findViewById(R.id.recyclerView);
        // Create an adapter and supply the data to be displayed.
        mAdapter = new CSVHeaderAdapter(this, csvheaders);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

   }
    /**
     * This method will display the menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load_data:
                Intent intent = new Intent(CSVFileFieldsActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * This method is to launch the third activity (Data statistics)
     * Along with the file it will get the selected headers as well
     *
     * @param view
     */
    public void launchThirdActivity(View view) {
        List<Headers> selectedHeaders = mAdapter.getSelectedItems();
        Intent intent = new Intent(this, DataStatisticsActivity.class);
        ArrayList<Headers> selectedHeadersArrayList = new ArrayList<Headers>(selectedHeaders);
        ArrayList<String[]> csvFileArrayList = (ArrayList<String[]>) getIntent().getSerializableExtra(MainActivity.CSV_FILE_READ);
        intent.putExtra(SELECTED_HEADERS, selectedHeadersArrayList);
        intent.putExtra(CSV_FILE_READ, csvFileArrayList);
        startActivity(intent);

    }
    /**
     * This method is to select all the headers at the same time on the page
     * Also it it will take all the selected data to next page
     *
     * @param view
     */
    public void launchAllDataThirdActivity(View view) {
        Intent intent = new Intent(this, DataStatisticsActivity.class);
        ArrayList<String[]> csvFileArrayList = (ArrayList<String[]>) getIntent().getSerializableExtra(MainActivity.CSV_FILE_READ);
        intent.putExtra(SELECTED_HEADERS, csvheaders);
        intent.putExtra(CSV_FILE_READ, csvFileArrayList);
        startActivity(intent);
    }
}
