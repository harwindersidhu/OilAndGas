package com.example.khush.oilandgasproject;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * This activity is to plot the regression
 */
public class RegressionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    ArrayList<String[]> csvFileArrayListFiltered;
    ArrayList<Headers>  selectedHeaders;
    ArrayList<String> selectedHeadersName;
    ArrayList<Double> selectedColumnValuesXAxis;
    ArrayList<Double> selectedColumnValuesYAxis;
    private GraphView regressionGraph;
    private int columnOneHeaderId, columnTwoHeaderId;
    /**
     * This method will initialize the values for the spinner for on X axis and Y axis
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regression);

        selectedHeaders = (ArrayList<Headers>) getIntent().getSerializableExtra(CleaningAndFilteringActivity.SELECTED_HEADERS);
        csvFileArrayListFiltered = (ArrayList<String[]>) getIntent().getSerializableExtra(CleaningAndFilteringActivity.CSV_FILE_Filtered);
        selectedHeadersName = new ArrayList<>();

        for (int i=0; i<selectedHeaders.size(); i++){
            Headers currHeader = selectedHeaders.get(i);
            String name = currHeader.getHeadersName();
            selectedHeadersName.add(name);
        }

        regressionGraph = (GraphView) findViewById(R.id.regressionGraph);
        // Create the spinner.
        Spinner spinnerX_Axis = findViewById(R.id.xAxis);
        Spinner spinnerY_Axis = findViewById(R.id.yAxis);
        if (spinnerX_Axis != null) {
            spinnerX_Axis.setOnItemSelectedListener(this);
        }
        if (spinnerY_Axis != null) {
            spinnerY_Axis.setOnItemSelectedListener(this);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                selectedHeadersName
        );

        adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        if (spinnerX_Axis != null) {
            spinnerX_Axis.setAdapter(adapter);
        }
        if (spinnerY_Axis != null) {
            spinnerY_Axis.setAdapter(adapter);
        }
    }
    /**
     * This method is to display the menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    /**
     * This method is to choose the select items
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load_data:
                Intent intent = new Intent(RegressionActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * This item will check which item is selected
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int headerId;
        switch (parent.getId()) {
            case R.id.xAxis:
                columnOneHeaderId = selectedHeaders.get(position).getHeaderId();

                break;
            case R.id.yAxis:
                columnTwoHeaderId = selectedHeaders.get(position).getHeaderId();

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
/**
 * This method will get the values of the selected column from a CSV file
 *
 * @param headerId
 * @return
*/
    public ArrayList<Double> getColumnValues(int headerId){
        ArrayList<Double> columnValues = new ArrayList<>();
        for (int i=1; i<csvFileArrayListFiltered.size(); i++){
            System.out.println("Current id is : " + i);
            String[] currRow = csvFileArrayListFiltered.get(i);
            if (currRow[columnOneHeaderId].isEmpty() && currRow[columnTwoHeaderId].isEmpty()){

            }
            else if (currRow[headerId].isEmpty()){
                columnValues.add(0.0);
            }
            else {
                columnValues.add(Double.parseDouble(currRow[headerId]));
            }

        }
        return columnValues;
    }
    /**
     * This method is to perform the power regression
     *
     * @return Y values we generated from Power regression
     */
    public ArrayList<Double> performPowerRegression(){
        int maxValue = 0;
        if (selectedColumnValuesXAxis.size() > selectedColumnValuesYAxis.size()){
            maxValue = selectedColumnValuesYAxis.size();
        }
        else {
            maxValue = selectedColumnValuesXAxis.size();
        }
        ArrayList<Double> logxValues = new ArrayList<>();
        ArrayList<Double> logyValues = new ArrayList<>();
        for (int i=0; i<maxValue; i++) {
            Double logx = Math.log(selectedColumnValuesXAxis.get(i));
            Double logy = Math.log(selectedColumnValuesYAxis.get(i));
            logxValues.add(logx);
            logyValues.add(logy);
        }
        SimpleRegression simpleRegression = new SimpleRegression();
        for (int i=0; i<maxValue; i++) {
            simpleRegression.addData(logxValues.get(i), logyValues.get(i));
        }

        Double interceptValue = simpleRegression.getIntercept();
        Double slopeValue = simpleRegression.getSlope();

        ArrayList<Double> powerRegression_yValues = new ArrayList<>();
        for (int i=0; i<logxValues.size(); i++){
            Double log_xValue = logxValues.get(i);
            Double value = interceptValue + (slopeValue * log_xValue);
            Double regression_yValue = Math.exp(value);
            powerRegression_yValues.add(regression_yValue);
        }
        return powerRegression_yValues;
    }
    /**
     * This methos will plot the graph
     *
     * @param view
     */
    public void plotGraph(View view) {
        selectedColumnValuesXAxis = getColumnValues(columnOneHeaderId);
        selectedColumnValuesYAxis = getColumnValues(columnTwoHeaderId);
        ArrayList<Double> regression_yValues = performPowerRegression();
        generateLineGraph(regression_yValues);

    }
    /**
     * This method will generate the graph for regression
     *
     * @param reg_yValues
     */
    public void generateLineGraph(ArrayList<Double> reg_yValues){
        int maxValue = 0;
        if (selectedColumnValuesXAxis.size() > reg_yValues.size()){
            maxValue = reg_yValues.size();
        }
        else {
            maxValue = selectedColumnValuesXAxis.size();
        }
        ArrayIndexComparator comparator = new ArrayIndexComparator(selectedColumnValuesXAxis);
        Integer[] indexes = comparator.createIndexArray();
        Arrays.sort(indexes, comparator);
        Collections.sort(selectedColumnValuesXAxis);
        ArrayList<Double> sortedSecondColumnRegression = new ArrayList<>();
        ArrayList<Double> sortedSecondColumn = new ArrayList<>();
        for (int index : indexes) {
            sortedSecondColumnRegression.add(reg_yValues.get(index));
            sortedSecondColumn.add(selectedColumnValuesYAxis.get(index));
        }

        List<DataPoint> arrDataPointtForRegression=new ArrayList<>();
        for (int i=0; i<maxValue; i++){
            DataPoint dp = new DataPoint(selectedColumnValuesXAxis.get(i), sortedSecondColumnRegression.get(i));
            arrDataPointtForRegression.add(dp);
        }

        DataPoint[] listDpRegression = new DataPoint[arrDataPointtForRegression.size()];
        for(int i=0;i<arrDataPointtForRegression.size();i++){
            listDpRegression[i]=arrDataPointtForRegression.get(i);
        }

        List<DataPoint> arrDataPointtForScatter=new ArrayList<>();
        for (int i=0; i<selectedColumnValuesXAxis.size(); i++){
            DataPoint dp = new DataPoint(selectedColumnValuesXAxis.get(i), sortedSecondColumn.get(i));
            arrDataPointtForScatter.add(dp);
        }

        DataPoint[] listDpScatter = new DataPoint[arrDataPointtForScatter.size()];
        for(int i=0;i<arrDataPointtForScatter.size();i++){
            listDpScatter[i]=arrDataPointtForScatter.get(i);
        }


        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(listDpRegression);
        series.resetData(listDpRegression);
        series.setColor(Color.RED);
        regressionGraph.clearSecondScale();
        regressionGraph.removeAllSeries();
        regressionGraph.addSeries(series);
        regressionGraph.getGridLabelRenderer().setHorizontalAxisTitle(selectedHeadersName.get(columnOneHeaderId));
        regressionGraph.getGridLabelRenderer().setVerticalAxisTitle(selectedHeadersName.get(columnTwoHeaderId));

        PointsGraphSeries<DataPoint> scatterSeries = new PointsGraphSeries<>(listDpScatter);
        scatterSeries.resetData(listDpScatter);
        regressionGraph.addSeries(scatterSeries);
        regressionGraph.getViewport().setXAxisBoundsManual(true);

    }
}
