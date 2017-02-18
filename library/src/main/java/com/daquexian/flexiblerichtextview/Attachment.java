package com.daquexian.flexiblerichtextview;

/**
 * Created by daquexian on 17-2-17.
 * Please inherit this class to support attachment
 */

@SuppressWarnings("WeakerAccess")
public abstract class Attachment {
    public abstract String getText();
    public abstract boolean isImage();
    public abstract String getAttachmentId();

    public String getUrl() {
        return "";
    }
}
