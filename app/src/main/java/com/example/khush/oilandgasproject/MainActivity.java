package com.example.khush.oilandgasproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
 * This activity will have the main logic of the first page
 * which will show the welcome display and a button to load the file
 */
public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();

    private static final int READ_REQUEST_CODE = 42;
    public static final String EXTRA_MESSAGE = "CSVFile_Headers";
    public static final String CSV_FILE_READ = "CSVFileRead";
    /**
     * In the oncreate method there will be a logic for the onclick button
     * and to read the CSV file.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * On press of + button, it will allow the user to select file from disk
     * @param view
     */
    public void launchSecondActivity(View view) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);

    }
    /**
     * Once the user press on the button this code will take place to open the CSV file (Intent)
     *
     * @param requestCode
     * @param resultCode
     * @param resultData
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == MainActivity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                try {
                    readCsvFile(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * This method is to open the CSV file
     *
     * @param uri
     * @throws IOException
     */
    private void readCsvFile(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader BufferReader = new BufferedReader(new InputStreamReader(inputStream));
        CSVReader reader = new CSVReader(BufferReader);

        List<String[]> allRows = reader.readAll();
        String[] headers = allRows.get(0);
        System.out.println(headers);
        LinkedList<Headers> csvheaders = new LinkedList<>();
        for (int i = 0; i<headers.length; i++) {
            String headerName = headers[i];
            csvheaders.add(new Headers(i, headerName));
        }

        ArrayList<String[]> csvFileArrayList = new ArrayList<String[]>(allRows);
        Intent intent = new Intent(this, CSVFileFieldsActivity.class);
        intent.putExtra(EXTRA_MESSAGE, csvheaders);
        intent.putExtra(CSV_FILE_READ, csvFileArrayList);
        startActivity(intent);

    }
}
