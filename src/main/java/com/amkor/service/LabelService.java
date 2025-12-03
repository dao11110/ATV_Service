package com.amkor.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
@RestController
public class LabelService {


    @RequestMapping(method = RequestMethod.GET, value = "/printLabel")
    public String printLabel(){
        String result="fail";
        String ipAddress="10.201.131.26";
        String zCode="";
        zCode+="^XA\n";
        zCode+="^FWR\n";
        zCode+="^LH000,000^BY2,6,60^FS\n";
        zCode+="^LH000,000^BY2,6,60^FS\n";
        zCode+="^FO320,230^A0,25,25^FDTEST PRG/REV^FS\n";
        zCode+="^FO270,30^A0,22,22^FDATE: Prod Fuse A12^FS\n";
        zCode+="^FO210,30^A0,22,22^FDSLT0: 17ADvE_134^FS\n";
        zCode+="^FO150,30^A0,22,22^FDSLT1: S1.0.8-DiscoKuma7A71422d_1.6DVT_JPI_1.13MP_1.2m_^FS\n";
        zCode+="^FO90,30^A0,22,22^FDSLT2: 1AEnD16^FS\n";
        zCode+="^XZ\n";
//        zCode = new String(new char[5]).replace("\0", zCode);
        result=printLabelIP( ipAddress,zCode);

       return result;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/printKioxiaInternalLabel")
    public String printKioxiaInternalLabel(){
        String result="fail";
        String ipAddress="10.201.137.173";
        String zCode="";
                 zCode+="^XA\n";
                 zCode+="^MCY\n";
                 zCode+="^XZ\n";
                 zCode+="^XA\n";
                 zCode+="^LH000,000^BY2,6,60^FS\n";
                 zCode+="^FO040,20^GB610,430,2^FS\n";
                 zCode+="^FO040,70^GB610,0,2^FS\n";
                 zCode+="^FO040,130^GB610,0,2^FS\n";
                 zCode+="^FO040,195^GB610,0,2^FS\n";
                 zCode+="^FO040,260^GB610,0,2^FS\n";
                 zCode+="^FO040,320^GB610,0,2^FS\n";
                 zCode+="^FO040,380^GB610,0,2^FS\n";
                 zCode+="^FO0200,130^GB0,315,2^FS\n";
                 zCode+="^FO0540,130^GB0,315,2^FS\n";
                 zCode+="^FO110,35^A0,25,25^FDLabel for Internal use only.Do not use for shipping^FS\n";
                 zCode+="^FO250,95^A0,25,25^FDTape and Reel Process^FS\n";
                 zCode+="^FO90,155^A0,25,25^FDTypes^FS\n";
                 zCode+="^FO340,155^A0,25,25^FDLot No^FS\n";
                 zCode+="^FO560,155^A0,25,25^FDQuantity^FS\n";
                 zCode+="^FO80,220^A0,25,25^FDMain Lot^FS\n";
                 zCode+="^FO55,285^A0,25,25^FDResidue Lot 1^FS\n";
                 zCode+="^FO55,345^A0,25,25^FDResidue Lot 2^FS\n";
                 zCode+="^FO80,405^A0,25,25^FDRemark^FS\n";
                 zCode+="^XZ\n";
//        zCode = new String(new char[5]).replace("\0", zCode);
        result=printLabelIP( ipAddress,zCode);

        return result;
    }

    private String printLabelIP(String IP, String zCode) {
        String result = "";
        Socket socketClient = null;
        DataOutputStream dataOutputStream = null;

        try {
            socketClient = new Socket(IP, 9100);

            dataOutputStream = new DataOutputStream(socketClient.getOutputStream());
            dataOutputStream.writeBytes(zCode);
            result = "Success";

        } catch (IOException e) {
            result = e.getMessage();
            throw new RuntimeException(e);
        }
        return result;

    }
}
