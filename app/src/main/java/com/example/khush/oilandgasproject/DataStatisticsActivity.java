package com.example.khush.oilandgasproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * This activity will have the histogram and all the data statistics calculations
 */
public class DataStatisticsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private CSVHeaderAdapter adapter;
    ArrayList<String[]>  csvFileArrayList;
    ArrayList<Headers>  selectedHeaders;
    ArrayList<String> selectedHeadersName;
    GraphView histogramBar;
    Double minValue;
    public static final String SELECTED_HEADERS = "CSVFile_SelectedHeaders";
    public static final String CSV_FILE_READ = "CSVFileRead";
    /**
     * This method will have the logic of the histogram graph
     * So that when a user choose any one header from the spinner
     * it will plot the histogram based on the value
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datastatistics);

        histogramBar = (GraphView) findViewById(R.id.histogram);
        Spinner spinner = findViewById(R.id.select_data);

        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }

        selectedHeaders = (ArrayList<Headers>) getIntent().getSerializableExtra(CSVFileFieldsActivity.SELECTED_HEADERS);
        csvFileArrayList = (ArrayList<String[]>) getIntent().getSerializableExtra(CSVFileFieldsActivity.CSV_FILE_READ);
        selectedHeadersName = new ArrayList<>();

        for (int i=0; i<selectedHeaders.size(); i++){
            Headers currHeader = selectedHeaders.get(i);
            String name = currHeader.getHeadersName();
            selectedHeadersName.add(name);
        }

        ArrayAdapter<String> adapterOne = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                selectedHeadersName
        );

        adapterOne.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        if (spinner != null) {
            spinner.setAdapter(adapterOne);
        }

    }
    /**
     * This method will display the menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    /* * This method is to show the menu items when the burger menu is clicked
     * so that the user can choose which activity they would like to go
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load_data:
                Intent intent = new Intent(DataStatisticsActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method will check which item is selected from the spinner
     * and it will display the calculations for that selected item
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int headerId = selectedHeaders.get(position).getHeaderId();
        ArrayList<Double> selectedColumnValues = getColumnValues(headerId);
        generateHistogram(selectedColumnValues, headerId);
        minValue(selectedColumnValues);
        maxValue(selectedColumnValues);
        mean(selectedColumnValues);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    /**
     * This method is to launch the Cleaning and filtering page
     *
     * @param view
     */
    public void LaunchFourthActivity(View view) {
        Intent intent = new Intent(this, CleaningAndFilteringActivity.class);
        intent.putExtra(SELECTED_HEADERS, selectedHeaders);
        intent.putExtra(CSV_FILE_READ, csvFileArrayList);
        startActivity(intent);
    }
    /**
     * This method will get the selected column and with the id it will get the value
     * from the CSV file
     *
     * @param headerId
     * @return
     */
    public ArrayList<Double> getColumnValues(int headerId){
        ArrayList<Double> columnValues = new ArrayList<>();
        for (int i=1; i<csvFileArrayList.size(); i++){
            System.out.println("Current id is : " + i);
            String[] currRow = csvFileArrayList.get(i);
            if (currRow[headerId].isEmpty()){

            }
            else {
                columnValues.add(Double.parseDouble(currRow[headerId]));
            }

        }
        return columnValues;
    }
    /**
     * This method will generate the histogram based on the selected column
     * The user can select X axis and Y axis so it will plot the graph accordingly
     * The bargraph will store the value to be plot
     *
     * @param values
     */
    public void generateHistogram(ArrayList<Double> values, int headerId){
        Collections.sort(values);
        List<FrequencyOfValues> listOfDP = getFrequencyWithBins(values);
        List<DataPoint> arrDataPointt=new ArrayList<>();
        for (int i=0; i<listOfDP.size(); i++){
            FrequencyOfValues currValue = listOfDP.get(i);
            Double xVal = currValue.getValue();
            Double yVal = currValue.getFrequencyOfValue();
            DataPoint dp = new DataPoint(xVal, yVal);
            arrDataPointt.add(dp);
        }

        DataPoint[] listDp = new DataPoint[arrDataPointt.size()];
        for(int i=0;i<arrDataPointt.size();i++){
            listDp[i]=arrDataPointt.get(i);
        }


        BarGraphSeries<DataPoint> histogram = new BarGraphSeries<>(listDp);
        histogram.resetData(listDp);
        histogramBar.clearSecondScale();
        histogramBar.removeAllSeries();
        histogramBar.addSeries(histogram);
        // set manual X bounds
        histogramBar.getViewport().setXAxisBoundsManual(true);
        histogramBar.getViewport().setMinX(minValue);
        histogramBar.getViewport().setMaxX(listOfDP.get(listOfDP.size()-1).getValue());
        histogramBar.getViewport().setYAxisBoundsManual(true);
        histogramBar.getViewport().setMinY(0.0);
        histogramBar.getGridLabelRenderer().setHorizontalAxisTitle(selectedHeadersName.get(headerId));
    }
    /**
     * This method will calculate the frequency of each value of the arraylist
     *
     * @param values
     * @return
     */
    public List<FrequencyOfValues> getFrequency(ArrayList<Double> values) {
        List<FrequencyOfValues> listOfDataPoints = new ArrayList<>();

        int [] fr = new int [values.size()];
        int visited = -1;

        for(int i = 0; i < values.size(); i++){
            int count = 1;
            for(int j = i+1; j < values.size(); j++){
                if(values.get(i).equals(values.get(j))){
                    count++;
                    //To avoid counting same element again
                    fr[j] = visited;
                }
            }
            if(fr[i] != visited){
                listOfDataPoints.add(new FrequencyOfValues(Double.valueOf(values.get(i)), Double.valueOf(count)));
            }

        }

        System.out.println(listOfDataPoints);
        return listOfDataPoints;

    }
    /**
     * This method will calculate the number of the values that fits in the specific bin
     *
     * @param values
     * @return
     */
    public List<FrequencyOfValues> getFrequencyWithBins(ArrayList<Double> values) {
        List<FrequencyOfValues> listOfPoints = new ArrayList<>();
            minValue = values.get(0);
            Double maxValue = values.get(values.size() - 1);
            Double binSize = (maxValue-minValue)/10;
            Double currentBin = minValue + binSize;
            int currentIndex = 0;
            Double numberOfValues = 0.0;
            FrequencyOfValues defaultValue = (new FrequencyOfValues(currentBin, numberOfValues));
            for (int i = 0; i<10; i++){
                listOfPoints.add(defaultValue);
            }

            for (int i=0; i<values.size(); i++) {
                Double curr = values.get(i);
                if (curr > currentBin){
                    listOfPoints.set(currentIndex, new FrequencyOfValues(currentBin, numberOfValues));
                    BigDecimal bin = BigDecimal.valueOf(currentBin).add(BigDecimal.valueOf(binSize));
                    currentBin = bin.doubleValue();
                    currentIndex++;
                    numberOfValues++;
                    if (currentIndex == 9){
                        currentBin = maxValue;
                    }
                    listOfPoints.set(currentIndex, new FrequencyOfValues(currentBin, numberOfValues));
                }
                else {
                    numberOfValues++;
                    listOfPoints.set(currentIndex, new FrequencyOfValues(currentBin, numberOfValues));
                    if (((i + 1) < values.size()) && values.get(i+1) > currentBin){
                        BigDecimal bin = BigDecimal.valueOf(currentBin).add(BigDecimal.valueOf(binSize));
                        currentBin = bin.doubleValue();
                        currentIndex++;
                        numberOfValues = 0.0;
                        if (currentIndex == 9){
                            currentBin = maxValue;
                        }
                        listOfPoints.set(currentIndex, new FrequencyOfValues(currentBin, numberOfValues));
                    }
                }
            }
        System.out.println(listOfPoints);
        return listOfPoints;
    }
    /**
     * This method will calculate the minimum value of the selected column
     *
     * @param values
     * @return
     */
    public Double minValue(ArrayList<Double> values){

        double minValue = values.get(0);
        for(int i=1;i < values.size();i++){
            if(values.get(i) < minValue){
                System.out.println("Min Value is: " + i);
                minValue = values.get(i);
            }
            TextView minValueText = findViewById(R.id.min);
            minValueText.setText("Min Value: " + String.valueOf(minValue));
        }
        return minValue;


    }
    /**
     * This method will calculate the maximum value of the selected column
     *
     * @param values
     * @return
     */
    public Double maxValue(ArrayList<Double> values) {

        double maxValue = values.get(0);
        for(int i=1;i < values.size();i++){
            if(values.get(i) > maxValue){
                System.out.println("Min Value is: " + i);
                maxValue = values.get(i);
            }
            TextView minValueText = findViewById(R.id.max);
            minValueText.setText("Max Value: " + String.valueOf(maxValue));
        }
        return maxValue;
    }
    /**
     * This method will calculate the mean value of the selected column
     *
     * @param values
     * @return
     */
    public Double mean(ArrayList<Double> values) {
        double meanValue = values.get(0);
        for (int i = 1; i < values.size(); i++) {
            meanValue = meanValue + values.get(i);
            double average = meanValue / values.size();
            System.out.println("Mean value is: " + i);
            TextView meanValueText = findViewById(R.id.mean);
            meanValueText.setText("Mean Value: " + String.valueOf(average));
        }
        return meanValue;
    }

}
