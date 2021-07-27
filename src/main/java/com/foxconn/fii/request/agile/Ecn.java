package com.foxconn.fii.request.agile;

import com.foxconn.fii.DataStatic;
import lombok.Data;

@Data
public class Ecn {
    private String site;
    private String changeNumber;
    private String username;
    private String password;

    public Ecn(String mEcnNo){
        this.changeNumber = mEcnNo;
    }

    @Override
    public String toString() {
        return "{\n" +
                " \"changenumber\":\""+this.changeNumber+"\",\n" +
                " \"site\":\"vn\",\n" +
                " \"username\":\""+ DataStatic.AGILE.USERNAME +"\",\n" +
                " \"password\":\""+DataStatic.AGILE.PASSWORD+"\"\n" +
                "}";
    }
}
