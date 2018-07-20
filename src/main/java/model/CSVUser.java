package model;

import org.csveed.annotations.CsvCell;
import org.csveed.annotations.CsvFile;

@CsvFile(escape='\\', separator=',')
public class CSVUser {

    @CsvCell(columnName = "the first column")
    private String firstname;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}
