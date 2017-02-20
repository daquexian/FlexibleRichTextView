# FlexibleRichTextView

[中文版](README.zh-cn.md)

## Description
This library is used for showing various rich text, including LaTeX, images, codes, tables, and normal styles such as center, bold, italic and so on.

A interesting feature is, you can customize most tags as whatever you want.

It uses [CodeView](https://github.com/Softwee/codeview-android) to support code highlight, and [JLaTeXMath](https://github.com/mksmbrtsh/jlatexmath-android) (as well as [this fork](https://github.com/sixgodIT/JLaTexMath-andriod)) to support LaTeX.

The method to parse rich text is something like recursively descending. I'm open to PR that could improve performance, fix bugs and anything make this library better.

Screenshot:

![Screenshot](screencap.png)

## Download

add in your root `build.gradle` :
````
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
````


then add
````
compile 'com.github.daquexian:FlexibleRichTextView:0.8.1'
````
in your app's `build.gradle`

## Usage

You need initialize JLaTeXMath for LaTeX rendering by command:
```
AjLatexMath.init(context); // init library: load fonts, create paint, etc.
```

If you want to use code classifier to auto language recognizing just add to your Application
```
// train classifier on app start
CodeProcessor.init(this);
```

To show rich text, just invoke `flexibleRichTextView.setText`, such as
```
String richText = "[h][center]hi![/center][/h]" +
                "[quote]This is quote[/quote]" +
                "[code]print(\"Hello FlexibleRichTextView!\")[/code]" +
                "Hello FlexibleRichTextView!\n" +
                "This is LaTeX:\n" +
                "$e^{\\pi i} - 1 = 0$";
flexibleRichTextView.setText(richText);
```

## Tags

You can see default tags in [this](https://github.com/daquexian/FlexibleRichTextView/blob/master/library/src/main/java/com/daquexian/flexiblerichtextview/Parser4.java#L711). Try to customize your own tags by the series of  `set***Labels(String... labels)` methods.

* For image labels, `\w` represents width ,`\h` represents height and `\u` represents the url of image.

* For attachment labels, `\s` repersents attachment id.

* For color labels, `\s` represents color (e.g. red).

* For url labels, `\s` represents url.

* For quote labels, `\p` represents the id of the quoted content, `\m` represents the name of the person quoted.

For example, if you customize image label as `<img height=\h width=\w>\u</img>`, the text `<img height=200 width=100>https://example.img</img>` will be treated as a image, its height is 200, width is 100 and its url is https://example.img . And of course you can just customize image label as `<img>\u</img>`, then the original size of image will be respected.

## Attachment

This library support attachment. If there are attachments to show, create a class and inherit abstract class `Attachment`, then invoke `flexibleRichTextView.setText(String richText, List<Attachment> attachments)`.


If `attachment.isImage()` is true, the attachment will be shown as image, or it will be shown as a link (please implement `getText()` method whose return value is the link text, and `getUrl()` method whose return value is the url of image, see [Callbacks](#callbacks) for more information). Both images and links produced from attachments will be shown at the bottom of view.

If you need the attachment embedded in text, please implement `getAttachmentId()` method and provide the id in attachment tag.

For example, the default attachment tag is `[attachment:\s]`, where `\s` represents attachment id. So you should invoke
```
String textWithAttachment = "This is an attachment\n" +
                            "[attachment:3918dbe1ac]\n" +
                            "The attachment is above";
flexibleRichTextView.setText(textWithAttachment, attachmentList);
```

If the list `attachmentList` contains an attachment whose id equals `3918dbe1ac`, it will be shown embedded in text.

## Quote

You can not only customize the tags of `quote`, but also the layout of `QuoteView`.

The default layout is [this](https://github.com/daquexian/FlexibleRichTextView/blob/master/library/src/main/res/layout/default_quote_view.xml).

The root element of layout must be a `QuoteView` and the first child of it must be a `FrameLayout`. You can specify a button that takes the role of expanding or collapsing the QuoteView on click by adding `app:buttonId=your_button_id` in QuoteView. When the button is clicked, the `onButtonClick(View view, boolean collapsed)` method of `OnViewListener` will be invoked, you could modify the text of button or do other things according to the status of `QuoteView`.

## LaTeX
See [this](https://github.com/daquexian/FlexibleRichTextView/blob/master/library/src/main/java/com/daquexian/flexiblerichtextview/Tokenizer.java#L339) for LaTeX tags. They cannot be customized temporarily.

## Table
The grammer for table is the same as [GitHub](https://help.github.com/articles/organizing-information-with-tables/), except for the pipes on either end of the table cannot be omitted.

## Callbacks
In interface `FlexibleRichTextView.OnViewClickListener`, three callbacks are declared, including `OnImgClick(ImageView imageView)`, `OnAttClick(Attachment attachment)` and `OnQuoteButtonClick(View view, boolean collapsed)`.

For attachments, if an attachment is shown as image, `OnImgClick` will be called when clicking the attachment, `OnAttClick` is only called when an attachment shown as link is clicked.

## Lisence
This library is licensed under Apache 2.0. The full license text can be found in the [LICENSE](https://github.com/daquexian/FlexibleRichTextView/blob/master/LICENSE) file.
