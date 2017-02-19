package com.example.daquexian.flexiblerichtextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.daquexian.flexiblerichtextview.Attachment;
import com.daquexian.flexiblerichtextview.FlexibleRichTextView;
import com.daquexian.flexiblerichtextview.Tokenizer;

import org.scilab.forge.jlatexmath.core.AjLatexMath;

import java.util.ArrayList;
import java.util.List;

import io.github.kbiakov.codeview.classifier.CodeProcessor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // train classifier on app start
        CodeProcessor.init(this);
        AjLatexMath.init(this); // init library: load fonts, create paint, etc.

        List<Attachment> attachments = new ArrayList<>();
        attachments.add(new ExampleAttachment("Android Image", "53ce1", true, "http://tse1.mm.bing.net/th?id=OIP.M24baa78c1fb80a71891ce775d11e038ao0&w=166&h=166&c=7&qlt=90&o=4&pid=1.7"));
        attachments.add(new ExampleAttachment("Here is a link", "bc41a", false, "https://google.com"));

        FlexibleRichTextView flexibleRichTextView = (FlexibleRichTextView) findViewById(R.id.frtv);
        Tokenizer.setCenterStartLabels("<center>");
        Tokenizer.setCenterEndLabels("</center>");
        Tokenizer.setTitleStartLabels("<h>");
        Tokenizer.setTitleEndLabels("</h>");

        flexibleRichTextView.setText("<h><center>hi!</center></h>" +
                        "[quote]This is quote\n" +
                        "second line\n" +
                        "third line\n" +
                        "fourth line[/quote]" +
                        "Here is an attachment:[attachment:53ce1]" +
                        "[code]print(\"Hello FlexibleRichTextView!\")[/code]" +
                        "Hello FlexibleRichTextView!\n" +
                        "This is LaTeX:\n" +
                        "$e^{\\pi i} - 1 = 0$\n" +
                        "This is table:\n" +
                        "| First Header  | Second Header |\n" +
                        "| --- | --- |\n" +
                        "| Content Cell  | Content Cell  |\n" +
                        "| Content Cell  | Content Cell  |\n" +
                        "An attachment is shown at the bottom: \n",
                attachments);
    }
}