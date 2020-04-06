package com.example.khush.oilandgasproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * This activity is mainly to filter out the data and to delete the missing values
 * Also to plot the graph after the filtering process
 */
public class CleaningAndFilteringActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{

    ArrayList<String[]> csvFileArrayList;
    ArrayList<String[]> csvFileArrayListCopy;
    ArrayList<Headers>  selectedHeaders;
    ArrayList<String> selectedHeadersName;
    ArrayList<Double> selectedColumnValuesOne;
    ArrayList<Double> selectedColumnValuesTwo;
    ArrayList<String> selectedColumnValuesOneWithEmptyValues;
    ArrayList<String> selectedColumnValuesTwoWithEmptyValues;
    private String[] filteringOptions = {"<", ">", "<=", ">="};
    int headerIdForColumnOne;
    int headerIdForColumnTwo;
    private GraphView afterGraph, beforeGraph;
    public static final String SELECTED_HEADERS = "CSVFile_SelectedHeaders";
    public static final String CSV_FILE_Filtered = "CSVFileFiltered";
    private EditText criteriaOneTexColOne, criteriaTwoTexColOne, criteriaOneTexColTwo, criteriaTwoTexColTwo;
    private String filteringCriteriaOneColOne, filteringCriteriaTwoColOne, filteringCriteriaOneColTwo, filteringCriteriaTwoColTwo;
    private TextView filterPercentage;
    /**
     * This method will initialize the values for all the spinners\
     * and will check the condition to see which spinner is selected
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaningandfiltering);

        selectedHeaders = (ArrayList<Headers>) getIntent().getSerializableExtra(CSVFileFieldsActivity.SELECTED_HEADERS);
        csvFileArrayList = (ArrayList<String[]>) getIntent().getSerializableExtra(CSVFileFieldsActivity.CSV_FILE_READ);
        csvFileArrayListCopy = new ArrayList<>(csvFileArrayList);
        criteriaOneTexColOne = findViewById(R.id.criteriaOneTextColOne);
        criteriaOneTexColTwo = findViewById(R.id.criteriaOneTextColTwo);
        criteriaTwoTexColOne = findViewById(R.id.criteriaTwoTextColOne);
        criteriaTwoTexColTwo = findViewById(R.id.criteriaTwoTextColTwo);
        filterPercentage = findViewById(R.id.filterLabel);

        selectedHeadersName = new ArrayList<>();

        for (int i=0; i<selectedHeaders.size(); i++){
            Headers currHeader = selectedHeaders.get(i);
            String name = currHeader.getHeadersName();
            selectedHeadersName.add(name);
        }

        beforeGraph = (GraphView) findViewById(R.id.graph);
        afterGraph = (GraphView) findViewById(R.id.graphAfter);
        // Create the spinner.
        Spinner spinnerOne = findViewById(R.id.SelectDataOne);
        Spinner spinnerTwo = findViewById(R.id.criteriaOneSpinner);
        Spinner spinnerThree = findViewById(R.id.criteriaTwoSpinner);
        Spinner spinnerFour = findViewById(R.id.SelectDataTwo);
        Spinner spinnerFive = findViewById(R.id.criteriaOneSpinnerTwo);
        Spinner spinnerSix = findViewById(R.id.criteriaTwoSpinnerTwo);
        if (spinnerOne != null) {
            spinnerOne.setOnItemSelectedListener(this);
        }
        if (spinnerTwo != null) {
            spinnerTwo.setOnItemSelectedListener(this);
        }
        if (spinnerThree != null) {
            spinnerThree.setOnItemSelectedListener(this);
        }
        if (spinnerFour != null) {
            spinnerFour.setOnItemSelectedListener(this);
        }
        if (spinnerFive != null) {
            spinnerFive.setOnItemSelectedListener(this);
        }
        if (spinnerSix != null) {
            spinnerSix.setOnItemSelectedListener(this);
        }

        // Create ArrayAdapter using the string array and default spinner layout.

        ArrayAdapter<String> adapterOne = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                selectedHeadersName
        );

        ArrayAdapter<String> adapterTwo = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                filteringOptions
        );
        ArrayAdapter<String> adapterThree = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                filteringOptions
        );




        // Specify the layout to use when the list of choices appears.
        adapterOne.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        adapterTwo.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        adapterThree.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        if (spinnerOne != null) {
            spinnerOne.setAdapter(adapterOne);
        }
        if (spinnerTwo != null) {
            spinnerTwo.setAdapter(adapterTwo);
        }
        if (spinnerThree != null) {
            spinnerThree.setAdapter(adapterThree);
        }
        if (spinnerFour != null) {
            spinnerFour.setAdapter(adapterOne);
        }
        if (spinnerFive != null) {
            spinnerFive.setAdapter(adapterTwo);
        }
        if (spinnerSix != null) {
            spinnerSix.setAdapter(adapterThree);
        }
    }
    /**
     * This method is to display the menu items
     *
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
     * This method is to show the menu items when the burger menu is clicked
     * so that the user can choose which activity they would like to go
     *
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load_data:
                Intent intent = new Intent(CleaningAndFilteringActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * This method will check which item is selected
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        int headerId;
        switch (parent.getId()) {
            case R.id.SelectDataOne:
                headerIdForColumnOne = selectedHeaders.get(position).getHeaderId();
                selectedColumnValuesOne = getColumnValues(headerIdForColumnOne, csvFileArrayListCopy);
                break;
            case R.id.SelectDataTwo:
                headerIdForColumnTwo = selectedHeaders.get(position).getHeaderId();
                selectedColumnValuesTwo = getColumnValues(headerIdForColumnOne, csvFileArrayListCopy);
                break;
            case R.id.criteriaOneSpinner:
                filteringCriteriaOneColOne = filteringOptions[position];
                break;
            case R.id.criteriaTwoSpinner:
                filteringCriteriaTwoColOne = filteringOptions[position];
                break;
            case R.id.criteriaOneSpinnerTwo:
                filteringCriteriaOneColTwo = filteringOptions[position];
                break;
            case R.id.criteriaTwoSpinnerTwo:
                filteringCriteriaTwoColTwo = filteringOptions[position];
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * This method will get the values of the selected columns
     *
     * @param headerId
     * @param csvFile
     * @return arraylist of double
     */
    public ArrayList<Double> getColumnValues(int headerId, ArrayList<String[]> csvFile){
        ArrayList<Double> columnValues = new ArrayList<>();
        for (int i=1; i<csvFile.size(); i++){
            System.out.println("Current id is : " + i);
            String[] currRow = csvFile.get(i);
            if (currRow[headerIdForColumnOne].isEmpty() && currRow[headerIdForColumnTwo].isEmpty()){

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
     * This method will clear out the empty values
     *
     * @param headerId
     * @param csvFile
     * @return arraylist of string
     */

    public ArrayList<String> getColumnValuesWithEmptyValues(int headerId, ArrayList<String[]> csvFile){
        ArrayList<String> columnValues = new ArrayList<>();
        for (int i=1; i<csvFile.size(); i++){
            System.out.println("Current id is : " + i);
            String[] currRow = csvFile.get(i);
            if (currRow[headerId].isEmpty()){
                columnValues.add("");
            }
            else {
                columnValues.add(currRow[headerId]);
            }

        }
        return columnValues;
    }
    /**
     * This method will generate the scatter graph before filtering based on the selected value
     */
    public void generateScatterGraphBeforeFiltering(){
        ArrayList<Double> firstColumn = getColumnValues(headerIdForColumnOne, csvFileArrayList);
        ArrayList<Double> secondColumn = getColumnValues(headerIdForColumnTwo, csvFileArrayList);
        int maxValue = 0;
        if (firstColumn.size() > secondColumn.size()){
            maxValue = secondColumn.size();
        }
        else {
            maxValue = firstColumn.size();
        }
        ArrayIndexComparator comparator = new ArrayIndexComparator(firstColumn);
        Integer[] indexes = comparator.createIndexArray();
        Arrays.sort(indexes, comparator);
        Collections.sort(firstColumn);
        ArrayList<Double> sortedSecondColumn = new ArrayList<>();
        for (int index : indexes) {
            sortedSecondColumn.add(secondColumn.get(index));
        }
        List<DataPoint> arrDataPointt=new ArrayList<>();
        for (int i=0; i<maxValue; i++){
            DataPoint dp = new DataPoint(firstColumn.get(i), sortedSecondColumn.get(i));
            arrDataPointt.add(dp);
        }

        DataPoint[] listDp = new DataPoint[arrDataPointt.size()];
        for(int i=0;i<arrDataPointt.size();i++){
            listDp[i]=arrDataPointt.get(i);
        }


        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(listDp);
        series.resetData(listDp);
        beforeGraph.clearSecondScale();
        beforeGraph.removeAllSeries();
        beforeGraph.addSeries(series);
        beforeGraph.setTitle("Before");
        beforeGraph.getGridLabelRenderer().setHorizontalAxisTitle(selectedHeadersName.get(headerIdForColumnOne));
        beforeGraph.getGridLabelRenderer().setVerticalAxisTitle(selectedHeadersName.get(headerIdForColumnTwo));
        beforeGraph.getViewport().setXAxisBoundsManual(true);
    }
    /**
     * This method will generate the scatter graph after filtering based on the selected value
     */
    public void generateScatterGraph(ArrayList<Double> firstColumn, ArrayList<Double> secondColumn){
        int maxValue = 0;
        if (firstColumn.size() > secondColumn.size()){
            maxValue = secondColumn.size();
        }
        else {
            maxValue = firstColumn.size();
        }
        ArrayIndexComparator comparator = new ArrayIndexComparator(firstColumn);
        Integer[] indexes = comparator.createIndexArray();
        Arrays.sort(indexes, comparator);
        Collections.sort(firstColumn);
        ArrayList<Double> sortedSecondColumn = new ArrayList<>();
        for (int index : indexes) {
            sortedSecondColumn.add(secondColumn.get(index));
        }

        List<DataPoint> arrDataPointt=new ArrayList<>();
        for (int i=0; i<maxValue; i++){
            DataPoint dp = new DataPoint(firstColumn.get(i), sortedSecondColumn.get(i));
            arrDataPointt.add(dp);
        }

        DataPoint[] listDp = new DataPoint[arrDataPointt.size()];
        for(int i=0;i<arrDataPointt.size();i++){
            listDp[i]=arrDataPointt.get(i);
        }


        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(listDp);
        series.resetData(listDp);
        afterGraph.clearSecondScale();
        afterGraph.removeAllSeries();
        afterGraph.addSeries(series);
        afterGraph.setTitle("After");
        afterGraph.getGridLabelRenderer().setHorizontalAxisTitle(selectedHeadersName.get(headerIdForColumnOne));
        afterGraph.getGridLabelRenderer().setVerticalAxisTitle(selectedHeadersName.get(headerIdForColumnTwo));
        afterGraph.getViewport().setXAxisBoundsManual(true);
    }
    /**
     * This method will launch the nexy activity
     *
     * @param view
     */
    public void LaunchFifthActivity(View view) {
        Intent intent = new Intent(this, RegressionActivity.class);
        intent.putExtra(SELECTED_HEADERS, selectedHeaders);
        intent.putExtra(CSV_FILE_Filtered, csvFileArrayListCopy);
        startActivity(intent);
    }

    public void doFilteringAndPlotting(View view) {
        doFiltering();

    }
    /**
     * This method is to filter all the values from the file
     * based on the selected conditions that user has choose and it will plot the graph
     */
    public void doFiltering(){
        String criteriaOneVal = String.valueOf(criteriaOneTexColOne.getText());
        String criteriaTwoVal = String.valueOf(criteriaTwoTexColOne.getText());
        if ((!(criteriaOneVal.equals(""))) || (!(criteriaOneVal.equals("")))) {
            selectedColumnValuesOneWithEmptyValues = new ArrayList<>();
            selectedColumnValuesOneWithEmptyValues = getColumnValuesWithEmptyValues(headerIdForColumnOne, csvFileArrayListCopy);

            if (!(criteriaOneVal.equals(""))){
                Double criteriaOneValue = Double.parseDouble(criteriaOneVal);
                if (filteringCriteriaOneColOne.equals(">")) {

                    filterFileForGreaterThan("One", headerIdForColumnOne, criteriaOneValue);
                }
                else if (filteringCriteriaOneColOne.equals("<")){
                    filterFileForLessThan("One", headerIdForColumnOne, criteriaOneValue);
                }
                else if (filteringCriteriaOneColOne.equals("<=")){
                    filterFileForLessThanEqualTo("One", headerIdForColumnOne, criteriaOneValue);
                }
                else if (filteringCriteriaOneColOne.equals(">")){
                    filterFileForGreaterThanEqualTo("One", headerIdForColumnOne, criteriaOneValue);
                }
            }

            if (!(criteriaTwoVal.equals(""))){
                Double criteriaTwoValue = Double.parseDouble(criteriaTwoVal);
                if (filteringCriteriaTwoColOne.equals(">")) {

                    filterFileForGreaterThan("One", headerIdForColumnOne, criteriaTwoValue);
                }
                else if (filteringCriteriaTwoColOne.equals("<")){
                    filterFileForLessThan("One", headerIdForColumnOne, criteriaTwoValue);
                }
                else if (filteringCriteriaTwoColOne.equals("<=")){
                    filterFileForLessThanEqualTo("One", headerIdForColumnOne, criteriaTwoValue);
                }
                else if (filteringCriteriaTwoColOne.equals(">=")){
                    filterFileForGreaterThanEqualTo("One", headerIdForColumnOne, criteriaTwoValue);
                }
            }
        }

        String criteriaOneValColTwo = String.valueOf(criteriaOneTexColTwo.getText());
        String criteriaTwoValColTwo = String.valueOf(criteriaTwoTexColTwo.getText());
        if ((!(criteriaOneValColTwo.equals(""))) || (!(criteriaTwoValColTwo.equals("")))) {
            selectedColumnValuesTwoWithEmptyValues = new ArrayList<>();
            selectedColumnValuesTwoWithEmptyValues = getColumnValuesWithEmptyValues(headerIdForColumnTwo, csvFileArrayListCopy);

            if (!(criteriaOneValColTwo.equals(""))){
                Double criteriaOneValue = Double.parseDouble(criteriaOneValColTwo);
                if (filteringCriteriaOneColTwo.equals(">")) {

                    filterFileForGreaterThan("Two", headerIdForColumnTwo, criteriaOneValue);
                }
                else if (filteringCriteriaOneColTwo.equals("<")){
                    filterFileForLessThan("Two", headerIdForColumnTwo, criteriaOneValue);
                }
                else if (filteringCriteriaOneColTwo.equals("<=")){
                    filterFileForLessThanEqualTo("Two", headerIdForColumnTwo, criteriaOneValue);
                }
                else if (filteringCriteriaOneColTwo.equals(">=")){
                    filterFileForGreaterThanEqualTo("Two", headerIdForColumnTwo, criteriaOneValue);
                }
            }

            if (!(criteriaTwoValColTwo.equals(""))){
                Double criteriaTwoValue = Double.parseDouble(criteriaTwoValColTwo);
                if (filteringCriteriaTwoColTwo.equals(">")) {

                    filterFileForGreaterThan("Two", headerIdForColumnTwo, criteriaTwoValue);
                }
                else if (filteringCriteriaTwoColTwo.equals("<")){
                    filterFileForLessThan("Two", headerIdForColumnTwo, criteriaTwoValue);
                }
                else if (filteringCriteriaTwoColTwo.equals("<=")){
                    filterFileForLessThanEqualTo("Two", headerIdForColumnTwo, criteriaTwoValue);
                }
                else if (filteringCriteriaTwoColTwo.equals(">=")){
                    filterFileForGreaterThanEqualTo("Two", headerIdForColumnTwo, criteriaTwoValue);
                }
            }
        }

        selectedColumnValuesOne = new ArrayList<>();
        selectedColumnValuesOne = getColumnValues(headerIdForColumnOne, csvFileArrayListCopy);

        selectedColumnValuesTwo = new ArrayList<>();
        selectedColumnValuesTwo = getColumnValues(headerIdForColumnTwo, csvFileArrayListCopy);

        calculatePercentage();

        generateScatterGraph(selectedColumnValuesOne, selectedColumnValuesTwo);
        generateScatterGraphBeforeFiltering();

    }
    /**
     * This method value calculate how many percentage the data has filtered
     */
    public void calculatePercentage(){
        int numberOfRowsInActualFile = csvFileArrayList.size();
        int numberOfRowsInFilteredFile = csvFileArrayListCopy.size();
        int filteredRows = numberOfRowsInActualFile - numberOfRowsInFilteredFile;
        Double percentage = ((double) filteredRows / (double) numberOfRowsInActualFile) * 100;
        DecimalFormat df2 = new DecimalFormat("#.##");
        String labelValue = "Filtered Out: " + df2.format(percentage) + "%";
        filterPercentage.setText(labelValue);
    }
    /**
     * This method will filter the file based on the condition which is " > "
     *
     * @param selectedColumn
     * @param headerId
     * @param value
     */
    public void filterFileForGreaterThan(String selectedColumn, int headerId, Double value) {
        ArrayList<String>selectedColumnValues = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        if (selectedColumn.equals("One")){
            selectedColumnValues = selectedColumnValuesOneWithEmptyValues;
        }
        else {
            selectedColumnValues = selectedColumnValuesTwoWithEmptyValues;
        }
        for (int i=0; i<selectedColumnValues.size(); i++) {
            String currValue = selectedColumnValues.get(i);
            if (currValue.equals("")){
                indices.add(i);
            }
            else{
                Double currValueDouble = Double.parseDouble(currValue);
                if (!(currValueDouble > value)){
                    indices.add(i);
                }
            }
        }
        Collections.sort(indices, Collections.reverseOrder());
        for (int i : indices){
            csvFileArrayListCopy.remove(i+1);
        }
        if (selectedColumn.equals("One")){
            selectedColumnValuesOneWithEmptyValues = getColumnValuesWithEmptyValues(headerId, csvFileArrayListCopy);
        }
        else {
            selectedColumnValuesTwoWithEmptyValues = getColumnValuesWithEmptyValues(headerId, csvFileArrayListCopy);
        }
    }
    /**
     * This method will filter the file based on the condition which is " < "
     *
     * @param selectedColumn
     * @param headerId
     * @param value
     */
    public void filterFileForLessThan(String selectedColumn, int headerId, Double value) {
        ArrayList<String>selectedColumnValues = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        if (selectedColumn.equals("One")){
            selectedColumnValues = selectedColumnValuesOneWithEmptyValues;
        }
        else {
            selectedColumnValues = selectedColumnValuesTwoWithEmptyValues;
        }
        for (int i=0; i<selectedColumnValues.size(); i++) {
            String currValue = selectedColumnValues.get(i);
            if (currValue.equals("")){
                indices.add(i);
            }
            else{
                Double currValueDouble = Double.parseDouble(currValue);
                if (!(currValueDouble < value)){
                    indices.add(i);
                }
            }
        }

        Collections.sort(indices, Collections.reverseOrder());
        for (int i : indices){
            csvFileArrayListCopy.remove(i+1);
        }

        if (selectedColumn.equals("One")){
            selectedColumnValuesOneWithEmptyValues = getColumnValuesWithEmptyValues(headerId, csvFileArrayListCopy);
        }
        else {
            selectedColumnValuesTwoWithEmptyValues = getColumnValuesWithEmptyValues(headerId, csvFileArrayListCopy);
        }
    }
    /**
     * This method will filter the file based on the condition which is " <="
     *
     * @param selectedColumn
     * @param headerId
     * @param value
     */
    public void filterFileForLessThanEqualTo(String selectedColumn, int headerId, Double value) {
        ArrayList<String>selectedColumnValues = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        if (selectedColumn.equals("One")){
            selectedColumnValues = selectedColumnValuesOneWithEmptyValues;
        }
        else {
            selectedColumnValues = selectedColumnValuesTwoWithEmptyValues;
        }
        for (int i=0; i<selectedColumnValues.size(); i++) {
            String currValue = selectedColumnValues.get(i);
            if (currValue.equals("")){
                indices.add(i);
            }
            else{
                Double currValueDouble = Double.parseDouble(currValue);
                if (!(currValueDouble <= value)){
                    indices.add(i);
                }
            }
        }

        Collections.sort(indices, Collections.reverseOrder());
        for (int i : indices){
            csvFileArrayListCopy.remove(i+1);
        }

        if (selectedColumn.equals("One")){
            selectedColumnValuesOneWithEmptyValues = getColumnValuesWithEmptyValues(headerId, csvFileArrayListCopy);
        }
        else {
            selectedColumnValuesTwoWithEmptyValues = getColumnValuesWithEmptyValues(headerId, csvFileArrayListCopy);
        }
    }
    /**
     * This method will filter the file based on the condition which is " >= "
     *
     * @param selectedColumn
     * @param headerId
     * @param value
     */
    public void filterFileForGreaterThanEqualTo(String selectedColumn, int headerId, Double value) {
        ArrayList<String>selectedColumnValues = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        if (selectedColumn.equals("One")){
            selectedColumnValues = selectedColumnValuesOneWithEmptyValues;
        }
        else {
            selectedColumnValues = selectedColumnValuesTwoWithEmptyValues;
        }
        for (int i=0; i<selectedColumnValues.size(); i++) {
            String currValue = selectedColumnValues.get(i);
            if (currValue.equals("")){
                indices.add(i);
            }
            else{
                Double currValueDouble = Double.parseDouble(currValue);
                if (!(currValueDouble >= value)){
                    indices.add(i);
                }
            }
        }

        Collections.sort(indices, Collections.reverseOrder());
        for (int i : indices){
            csvFileArrayListCopy.remove(i+1);
        }

        if (selectedColumn.equals("One")){
            selectedColumnValuesOneWithEmptyValues = getColumnValuesWithEmptyValues(headerId, csvFileArrayListCopy);
        }
        else {
            selectedColumnValuesTwoWithEmptyValues = getColumnValuesWithEmptyValues(headerId, csvFileArrayListCopy);
        }
    }
    /**
     * This method will delete the missing rows based on missing data from column 1
     *
     * @param view
     */
    public void deleteMissingConstraintsColumnOne(View view) {
        List<Integer> indices = new ArrayList<>();
        for (int i=1; i<csvFileArrayListCopy.size(); i++){
            System.out.println("Current id is : " + i);
            String[] currRow = csvFileArrayListCopy.get(i);
            if (currRow[headerIdForColumnOne].isEmpty()){
                indices.add(i);
            }
            else {

            }
        }
        Collections.sort(indices, Collections.reverseOrder());
        for (int i : indices){
            csvFileArrayListCopy.remove(i);
        }
        calculatePercentage();
    }
    /**
     * This method will delete the missing rows based on missing data from column 2
     *
     * @param view
     */
    public void deleteMissingConstraintsColumnTwo(View view) {
        List<Integer> indices = new ArrayList<>();
        for (int i=1; i<csvFileArrayListCopy.size(); i++){
            System.out.println("Current id is : " + i);
            String[] currRow = csvFileArrayListCopy.get(i);
            if (currRow[headerIdForColumnTwo].isEmpty()){
                indices.add(i);
            }
            else {

            }
        }
        Collections.sort(indices, Collections.reverseOrder());
        for (int i : indices){
            csvFileArrayListCopy.remove(i);
        }
        calculatePercentage();
    }
}
