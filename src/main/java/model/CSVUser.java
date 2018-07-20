package model;

import org.csveed.annotations.CsvCell;
import org.csveed.annotations.CsvFile;

@CsvFile(escape='\\', separator=',')
public class CSVUser {

    @CsvCell(columnName = "weight", required = true)
    private String lsatname;

    @CsvCell(columnName = "weight", required = true)
    private String firstname;

    public String getLsatname() {
        return lsatname;
    }

    public void setLsatname(String lsatname) {
        this.lsatname = lsatname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String toString(){
        return firstname + " " + lsatname ;
    }
}
