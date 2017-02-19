package com.example.daquexian.flexiblerichtextview;

import com.daquexian.flexiblerichtextview.Attachment;

/**
 * Created by daquexian on 17-2-19.
 */

public class ExampleAttachment extends Attachment {
    private String mText;
    private String mId;
    private boolean mIsImage;
    private String mUrl;

    public ExampleAttachment(String text, String id, boolean isImage, String url) {
        this.mText = text;
        this.mId = id;
        this.mIsImage = isImage;
        this.mUrl = url;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public String getText() {
        return mText;
    }

    @Override
    public String getAttachmentId() {
        return mId;
    }

    @Override
    public boolean isImage() {
        return mIsImage;
    }
}
