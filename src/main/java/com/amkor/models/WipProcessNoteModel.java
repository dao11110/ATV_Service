package com.amkor.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WipProcessNoteModel {
    private int factoryId;
    private int siteId;
    private long amkorId;
    private int amkorSubId;
    private String classify;
    private String engNote;
    private int serial;
    private long createDateTime;
    private int createBadge;
}
