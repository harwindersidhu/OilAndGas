package com.example.khush.oilandgasproject;

import java.io.Serializable;
/**
 * This class is to store th attributes for the CSV file headers
 */
public class Headers  implements Serializable {
    private int headerId;
    private String headersName;

    public Headers(int headerId, String headersName) {
        this.headerId = headerId;
        this.headersName = headersName;
    }

    public int getHeaderId() {
        return headerId;
    }

    public String getHeadersName() {
        return headersName;
    }
}
