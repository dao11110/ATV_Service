package com.foxconn.fii.service;

import com.foxconn.fii.data.primary.model.agile.AgileBomPn;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RWoRequestService {

    List<Object> checkRWoRequestNew(String mFactory);

    List<AgileBomPn> readBomFileData(MultipartFile file) throws IOException;
}
