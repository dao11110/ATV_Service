package com.amkor.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
public class SAPService {

    public List<CSVRecord> checkFTPFile(){
        List<CSVRecord> listResult=new ArrayList<>();
        String result="OK";
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect("10.201.11.45", 2121);
            ftpClient.login("ExchangeRate", "Amkor123!@#");
            ftpClient.enterLocalPassiveMode();
            final FTPFile[] ftpFiles = ftpClient.listFiles("/ExchangeRate");
            final FTPFile latestFile = Stream.of(ftpFiles).max(Comparator.comparing(FTPFile::getTimestamp))
                    .orElse(null);
            assert latestFile != null;
            result=latestFile.getName();


            String filePath="/ExchangeRate/"+result;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ftpClient.retrieveFile(filePath, outputStream);



            InputStream inputStream=new ByteArrayInputStream(outputStream.toByteArray());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
//            List<CSVRecord> records = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                System.out.println("aa---"+record);
                listResult.add(record);
            }

            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return listResult;
    }
}
