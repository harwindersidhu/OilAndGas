package com.example.khush.oilandgasproject;
/**
 * This class is to store the attributes for frequency
 */
public class FrequencyOfValues {
    private Double value;
    private Double frequencyOfValue;

    public FrequencyOfValues(Double value, Double frequencyOfValue) {
        this.value = value;
        this.frequencyOfValue = frequencyOfValue;
    }

    public Double getValue() {
        return value;
    }

    public Double getFrequencyOfValue() {
        return frequencyOfValue;
    }
}
