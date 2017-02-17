package com.example.daquexian.flexiblerichtextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.daquexian.flexiblerichtextview.Attachment;
import com.daquexian.flexiblerichtextview.FlexibleRichTextView;
import com.daquexian.flexiblerichtextview.Parser4;

import org.scilab.forge.jlatexmath.core.AjLatexMath;

import java.util.ArrayList;

import io.github.kbiakov.codeview.classifier.CodeProcessor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // train classifier on app start
        CodeProcessor.init(this);
        AjLatexMath.init(this); // init library: load fonts, create paint, etc.

        FlexibleRichTextView flexibleRichTextView = (FlexibleRichTextView) findViewById(R.id.frtv);
        Parser4.setCenterStartLabels("<center>");
        Parser4.setCenterEndLabels("</center>");
        Parser4.setTitleStartLabels("<h>");
        Parser4.setTitleEndLabels("</h>");
        flexibleRichTextView.setText("<h><center>hi!</center></h>" +
                "[quote]This is quote[/quote]" +
                "[code]print(\"Hello FlexibleRichTextView!\")[/code]Hello FlexibleRichTextView!\n" +
                "$e^{\\pi i} - 1 = 0$");
    }
}