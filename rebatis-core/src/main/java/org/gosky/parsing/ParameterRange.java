package org.gosky.parsing;

/**
 * @description: TODO
 * @author: Galaxy
 * @date: 2019-05-26 21:37
 **/
public class ParameterRange {

    private int start;

    private int end;

    private String value;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ParameterRange{" +
                "start=" + start +
                ", end=" + end +
                ", value='" + value + '\'' +
                '}';
    }

}
