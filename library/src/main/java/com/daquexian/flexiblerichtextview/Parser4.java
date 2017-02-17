package com.daquexian.flexiblerichtextview;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by daquexian on 17-2-9.
 */

public class Parser4 {
    private static List<String> colorStartLabels = new ArrayList<>();
    private static List<String> colorEndLabels = new ArrayList<>();
    private static List<String> urlStartLabels = new ArrayList<>();
    private static List<String> urlEndLabels = new ArrayList<>();
    private static List<String> curtainStartLabels = new ArrayList<>();
    private static List<String> curtainEndLabels = new ArrayList<>();
    private static List<String> underlineStartLabels = new ArrayList<>();
    private static List<String> underlineEndLabels = new ArrayList<>();
    private static List<String> boldStartLabels = new ArrayList<>();
    private static List<String> boldEndLabels = new ArrayList<>();
    private static List<String> italicStartLabels = new ArrayList<>();
    private static List<String> italicEndLabels = new ArrayList<>();
    private static List<String> deleteStartLabels = new ArrayList<>();
    private static List<String> deleteEndLabels = new ArrayList<>();
    private static List<String> centerStartLabels = new ArrayList<>();
    private static List<String> centerEndLabels = new ArrayList<>();
    private static List<String> titleStartLabels = new ArrayList<>();
    private static List<String> titleEndLabels = new ArrayList<>();
    private static List<String> attachmentLabels = new ArrayList<>();
    private static List<String> imageLabels = new ArrayList<>();
    private static List<String> codeStartLabels = new ArrayList<>();
    private static List<String> codeEndLabels = new ArrayList<>();
    private static List<String> quoteStartLabels = new ArrayList<>();
    private static List<String> quoteEndLabels = new ArrayList<>();

    private static List<ImgPos> imgPosList = new ArrayList<>();
    private static List<QuotePos> quotePosList = new ArrayList<>();

    private static final String TAG = "SFXParser3";

    private static List<String> iconStrs = new ArrayList<>();
    private static List<Integer> icons = new ArrayList<>();

    static {
        initLabels();
    }

    static abstract class TOKEN implements Comparable<TOKEN> {
        int position;
        int length;
        CharSequence value;

        public TOKEN(int position, int length, CharSequence value) {
            this.position = position;
            this.length = length;
            this.value = value;
        }

        public static String getString(List<TOKEN> tokens) {
            StringBuilder builder = new StringBuilder();
            for (TOKEN token : tokens) {
                builder.append(token.value);
            }
            return builder.toString();
        }

        @Override
        public int compareTo(@NotNull TOKEN token) {
            if (position < token.position) {
                return -1;
            } if (position == token.position) {
                /**
                 * tokenA < tokenB when tokenA.position == tokenB.position and
                 * tokenA.length > tokenB.length,
                 * used to remove overlapping tokens
                 */
                if (length < token.length) {
                    return 1;
                } else if (length > token.length) {
                    return -1;
                } else {
                    return 0;
                }
            } else {
                return 1;
            }
        }
    }

    static class PLAIN extends TOKEN {
        PLAIN(int position, CharSequence value) {
            super(position, value.length(), value);
        }
    }

    static class COLOR_START extends TOKEN {
        String color;

        COLOR_START(int position, String value, String color) {
            super(position, value.length(), value);
            this.color = color;
        }
    }

    static class COLOR_END extends TOKEN {
        COLOR_END(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class URL_START extends TOKEN {
        String url;
        URL_START(int position, String url, String value) {
            super(position, value.length(), value);
            this.url = url;
        }
    }

    static class URL_END extends TOKEN {
        URL_END(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class CURTAIN_START extends TOKEN {
        CURTAIN_START(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class CURTAIN_END extends TOKEN {
        CURTAIN_END(int position, String value) {
            super(position, value.length(), value);
        }

    }

    static class BOLD_START extends TOKEN {
        BOLD_START(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class BOLD_END extends TOKEN {

        BOLD_END(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class ITALIC_START extends TOKEN {
        ITALIC_START(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class ITALIC_END extends TOKEN {
        ITALIC_END(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class UNDERLINE_START extends TOKEN {
        UNDERLINE_START(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class UNDERLINE_END extends TOKEN {
        UNDERLINE_END(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class DELETE_START extends TOKEN {
        DELETE_START(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class DELETE_END extends TOKEN {
        DELETE_END(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class CENTER_START extends TOKEN {
        CENTER_START(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class CENTER_END extends TOKEN {
        CENTER_END(int position, String value) {
            super(position, value.length(), value);

        }
    }

    static class TITLE_START extends TOKEN {
        TITLE_START(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class TITLE_END extends TOKEN {
        TITLE_END(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class ATTACHMENT extends TOKEN {
        Attachment attachment;
        ATTACHMENT(int position, Attachment attachment, String value) {
            super(position, value.length(), value);
            this.attachment = attachment;
        }
    }

    static class ICON extends TOKEN {
        int iconId;
        ICON(int position, String iconStr, int iconId) {
            super(position, iconStr.length(), iconStr);
            this.iconId = iconId;
        }
    }

    static class FORMULA extends TOKEN {
        String content;
        int contentStart;
        FORMULA(int position, String content, int contentStart, String value) {
            /*
             * remove all newline character to avoid the ImageSpan shows multiple times when
             * formula content stretches over multiple lines.
             */
            super(position, value.length(), value.replaceAll("[\n\r]", ""));
            this.content = content.replaceAll("[\n\r]", "");
            this.contentStart = contentStart;
        }
    }

    static class CODE_START extends TOKEN {
        CODE_START(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class CODE_END extends TOKEN {
        CODE_END(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class QUOTE_START extends TOKEN {
        String quotedUsername;
        String postId;
        QUOTE_START(int position, String value, String quotedUsername, String postId) {
            super(position, value.length(), value);
            this.quotedUsername = quotedUsername;
            this.postId = postId;
        }
    }

    static class QUOTE_END extends TOKEN {
        QUOTE_END(int position, String value) {
            super(position, value.length(), value);
        }
    }

    static class IMAGE extends TOKEN {
        String url;
        int width, height;

        IMAGE(int position, String url, String value) {
            this(position, url, value, -1);
        }

        IMAGE(int position, String url, String value, int size) {
            this(position, url, value, -1, -1);
        }

        IMAGE(int position, String url, String value, int width, int height) {
            super(position, value.length(), value);
            this.url = url;
            this.width = width;
            this.height = height;
        }
    }

    static class TABLE extends TOKEN {
        TABLE(int position, String content) {
            super(position, content.length(), content);
        }
    }

    static class END extends TOKEN {
        END(int position) {
            super(position, 0, "");
        }
    }


    private static class ImgPos {
        byte widthPos = -1;
        byte heightPos = -1;
        byte urlPos = -1;
        byte sizePos = -1;

        public ImgPos(byte widthPos, byte heightPos, byte urlPos) {
            this.widthPos = widthPos;
            this.heightPos = heightPos;
            this.urlPos = urlPos;
        }
        public ImgPos(byte sizePos, byte urlPos) {
            this.sizePos = sizePos;
            this.urlPos = urlPos;
        }
    }

    private static class QuotePos {
        byte postIdPos = -1;
        byte memberPos = -1;

        public QuotePos(byte postIdPos, byte memberPos) {
            this.postIdPos = postIdPos;
            this.memberPos = memberPos;
        }
    }

    private static final Pattern FORMULA_REG1 = Pattern.compile("(?i)\\$\\$?((.|\\n)+?)\\$\\$?");
    private static final Pattern FORMULA_REG2 = Pattern.compile("(?i)\\\\[(\\[]((.|\\n)*?)\\\\[\\])]");
    private static final Pattern FORMULA_REG3 = Pattern.compile("(?i)\\[tex]((.|\\n)*?)\\[/tex]");
    private static final Pattern FORMULA_REG4 = Pattern.compile("(?i)\\\\begin\\{.*?\\}(.|\\n)*?\\\\end\\{.*?\\}");
    // private static final Pattern FORMULA_REG5 = Pattern.compile("(?i)\\$\\$(.+?)\\$\\$");
    private static final Pattern[] PATTERNS = {FORMULA_REG1, FORMULA_REG2, FORMULA_REG3, FORMULA_REG4};

    private static final Pattern IMG_REG = Pattern.compile("(?i)\\[img(=\\d+)?](.*?)\\[/img]");

    private static final Pattern TABLE_REG = Pattern.compile("(?:\\n|^)( *\\|.+\\| *\\n)??( *\\|(?: *:?----*:? *\\|)+ *\\n)((?: *\\|.+\\| *(?:\\n|$))+)");

    private static List<TOKEN> mTokenList = new ArrayList<>();

    public static int setUrlStartLabel(String... labels) {
        int ret = labels.length;

        urlStartLabels = new ArrayList<>();
        for (String label : labels) {
            if (label.contains("\\s")) {
                urlStartLabels.add(formatLabel(label)
                        .replaceAll("\\\\s", "(.+?)"));
                ret--;
            }
        }

        return ret;
    }

    public static int setColorStartLabel(String... labels) {
        int ret = labels.length;

        colorStartLabels = new ArrayList<>();
        for (String label : labels) {
            if (label.contains("\\s")) {
                colorStartLabels.add(formatLabel(label)
                        .replaceAll("\\\\s", "(.+?)"));
                ret--;
            }
        }

        return ret;
    }

    public static int setCurtainStartLabels(String... labels) {
        int ret = labels.length;

        curtainStartLabels = new ArrayList<>();
        for (String label : labels) {
            curtainStartLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setBoldStartLabels(String... labels) {
        int ret = labels.length;

        boldStartLabels = new ArrayList<>();
        for (String label : labels) {
            boldStartLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setCodeStartLabels(String... labels) {
        int ret = labels.length;

        codeStartLabels = new ArrayList<>();
        for (String label : labels) {
            codeStartLabels.add(formatLabel(label));

            ret--;
        }

        return ret;
    }

    public static int setQuoteStartLabels(String... labels) {
        int ret = labels.length;

        quoteStartLabels = new ArrayList<>();
        quotePosList = new ArrayList<>();

        for (String label : labels) {
            byte tmp = 1, postIdPos = -1, memberPos = -1;

            for (int i = 0; i < label.length() - 1; i++) {
                if (label.substring(i).startsWith("\\m")) {
                    memberPos = tmp++;
                } else if (label.substring(i).startsWith("\\p")) {
                    postIdPos = tmp++;
                }
            }
            quoteStartLabels.add(formatLabel(label)
                    .replaceAll("\\\\m", "(.+?)")
                    .replaceAll("\\\\p", "(.+?)"));
            quotePosList.add(new QuotePos(postIdPos, memberPos));
            ret--;
        }

        return ret;
    }

    public static int setItalicStartLabels(String... labels) {
        int ret = labels.length;

        italicStartLabels = new ArrayList<>();
        for (String label : labels) {
            italicStartLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setCenterStartLabels(String... labels) {
        int ret = labels.length;

        centerStartLabels = new ArrayList<>();
        for (String label : labels) {
            centerStartLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setDeleteStartLabels(String... labels) {
        int ret = labels.length;

        deleteStartLabels = new ArrayList<>();
        for (String label : labels) {
            deleteStartLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setTitleStartLabels(String... labels) {
        int ret = labels.length;

        titleStartLabels = new ArrayList<>();
        for (String label : labels) {
            titleStartLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setTitleEndLabels(String... labels) {
        int ret = labels.length;

        titleEndLabels = new ArrayList<>();
        for (String label : labels) {
            titleEndLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setColorEndLabels(String... labels) {
        int ret = labels.length;

        colorEndLabels = new ArrayList<>();
        for (String label : labels) {
            colorEndLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setCurtainEndLabels(String... labels) {
        int ret = labels.length;

        curtainEndLabels = new ArrayList<>();
        for (String label : labels) {
            curtainEndLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setCodeEndLabels(String... labels) {
        int ret = labels.length;

        codeEndLabels = new ArrayList<>();
        for (String label : labels) {
            codeEndLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setQuoteEndLabels(String... labels) {
        int ret = labels.length;

        quoteEndLabels = new ArrayList<>();
        for (String label : labels) {
            quoteEndLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setCenterEndLabels(String... labels) {
        int ret = labels.length;

        centerEndLabels = new ArrayList<>();
        for (String label : labels) {
            centerEndLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setBoldEndLabels(String... labels) {
        int ret = labels.length;

        boldEndLabels = new ArrayList<>();
        for (String label : labels) {
            boldEndLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setItalicEndLabels(String... labels) {
        int ret = labels.length;

        italicEndLabels = new ArrayList<>();
        for (String label : labels) {
            italicEndLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setUrlEndLabels(String... labels) {
        int ret = labels.length;

        urlEndLabels = new ArrayList<>();
        for (String label : labels) {
            urlEndLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setDeleteEndLabels(String... labels) {
        int ret = labels.length;

        deleteEndLabels = new ArrayList<>();
        for (String label : labels) {
            deleteEndLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setAttachmentLabels(String... labels) {
        int ret = labels.length;

        attachmentLabels = new ArrayList<>();

        for (String label : labels) {
            if (label.contains("\\s")) {
                attachmentLabels.add(formatLabel(label)
                        .replaceAll("\\\\s", "(.+?)"));
                ret--;
            }
        }

        return ret;
    }

    public static int setUnderlineStartLabels(String... labels) {
        int ret = labels.length;

        underlineStartLabels = new ArrayList<>();
        for (String label : labels) {
            underlineStartLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setUnderlineEndLabels(String... labels) {
        int ret = labels.length;

        underlineEndLabels = new ArrayList<>();
        for (String label : labels) {
            underlineEndLabels.add(formatLabel(label));
            ret--;
        }

        return ret;
    }

    public static int setImageLabels(String... labels) {
        int ret = labels.length;

        imageLabels = new ArrayList<>();
        imgPosList = new ArrayList<>();

        for (String label : labels) {
            boolean hasUrl = false;
            byte tmp = 1, widthPos = -1, heightPos = -1, urlPos = -1, sizePos = -1;

            for (int j = 0; j < label.length() - 1; j++) {
                if (label.substring(j).startsWith("\\w")) {
                    widthPos = tmp++;
                } else if (label.substring(j).startsWith("\\h")) {
                    heightPos = tmp++;
                } else if (label.substring(j).startsWith("\\s")) {
                    sizePos = tmp++;
                } else if (label.substring(j).startsWith("\\u")) {
                    urlPos = tmp++;
                    hasUrl = true;
                }
            }

            if (!hasUrl) {
                continue;
            }

            imageLabels.add(formatLabel(label)
                    .replaceAll("\\\\w", "(\\\\d+?)")
                    .replaceAll("\\\\h", "(\\\\d+?)")
                    .replaceAll("\\\\s", "(\\\\d+?)")
                    .replaceAll("\\\\u", "(.+?)"));
            if (sizePos == -1) {
                imgPosList.add(new ImgPos(widthPos, heightPos, urlPos));
            } else {
                imgPosList.add(new ImgPos(sizePos, urlPos));
            }

            ret--;
        }

        return ret;
    }

    public static void setIconStrs(String... iconStrs) {
        Parser4.iconStrs = new ArrayList<>();
        Collections.addAll(Parser4.iconStrs, iconStrs);
    }

    public static void setIcons(Integer... icons) {
        Parser4.icons = new ArrayList<>();
        Collections.addAll(Parser4.icons, icons);
    }



    private static String formatLabel(String label) {
        return "(?i)" + label.replaceAll("\\[", "\\\\[").replaceAll("\\(", "\\\\(");
    }

    private static void initLabels() {
        setUrlStartLabel("[url=\\s]");
        setUrlEndLabels("[/url]");
        setAttachmentLabels("[attachment:\\s]");
        setBoldStartLabels("[b]");
        setBoldEndLabels("[/b]");
        setItalicStartLabels("[i]");
        setItalicEndLabels("[/i]");
        setCurtainStartLabels("[curtain]");
        setCurtainEndLabels("[/curtain]");
        setCenterStartLabels("[center]");
        setCenterEndLabels("[/center]");
        setCodeStartLabels("[code]");
        setCodeEndLabels("[/code]");
        setTitleStartLabels("[h]");
        setTitleEndLabels("[/h]");
        setColorStartLabel("[c=\\s]", "[color=\\s]");
        setColorEndLabels("[/c]", "[/color]");
        setQuoteStartLabels("[quote]", "[quote=\\p:@\\m]");
        setQuoteEndLabels("[/quote]");
        setImageLabels("[img]\\u[/img]", "[img=\\s]\\u[/img]");
        setDeleteStartLabels("[s]");
        setDeleteEndLabels("[/s]");
        setUnderlineStartLabels("[u]");
        setUnderlineEndLabels("[/u]");
        /* setIcons(R.drawable.emoticons__0050_1, R.drawable.emoticons__0049_2, R.drawable.emoticons__0048_3, R.drawable.emoticons__0047_4,
            R.drawable.emoticons__0046_5, R.drawable.emoticons__0045_6, R.drawable.emoticons__0044_7, R.drawable.emoticons__0043_8, R.drawable.emoticons__0042_9,
            R.drawable.emoticons__0041_10, R.drawable.emoticons__0040_11, R.drawable.emoticons__0039_12, R.drawable.emoticons__0038_13, R.drawable.emoticons__0037_14,
            R.drawable.emoticons__0036_15, R.drawable.emoticons__0035_16, R.drawable.emoticons__0034_17, R.drawable.emoticons__0033_18, R.drawable.emoticons__0032_19,
            R.drawable.emoticons__0031_20, R.drawable.emoticons__0030_21, R.drawable.emoticons__0029_22, R.drawable.emoticons__0028_23, R.drawable.emoticons__0027_24,
            R.drawable.emoticons__0026_25, R.drawable.emoticons__0025_26, R.drawable.emoticons__0024_27, R.drawable.emoticons__0023_28, R.drawable.emoticons__0022_29,
            R.drawable.emoticons__0021_30, R.drawable.emoticons__0020_31, R.drawable.emoticons__0019_32, R.drawable.emoticons__0018_33, R.drawable.emoticons__0017_34,
            R.drawable.emoticons__0016_35, R.drawable.emoticons__0015_36, R.drawable.emoticons__0014_37, R.drawable.emoticons__0013_38, R.drawable.emoticons__0012_39,
            R.drawable.emoticons__0011_40, R.drawable.emoticons__0010_41, R.drawable.emoticons__0009_42, R.drawable.emoticons__0008_43, R.drawable.emoticons__0007_44,
            R.drawable.emoticons__0006_45, R.drawable.emoticons__0005_46, R.drawable.emoticons__0004_47, R.drawable.emoticons__0003_48, R.drawable.emoticons__0002_49,
            R.drawable.emoticons__0001_50, R.drawable.asonwwolf_smile, R.drawable.asonwwolf_laugh, R.drawable.asonwwolf_upset, R.drawable.asonwwolf_tear,
            R.drawable.asonwwolf_worry, R.drawable.asonwwolf_shock, R.drawable.asonwwolf_amuse);
        setIconStrs("/:)", "/:D", "/^b^", "/o.o", "/xx", "/#", "/))", "/--", "/TT", "/==",
            "/.**", "/:(", "/vv", "/$$", "/??", "/:/", "/xo", "/o0", "/><", "/love",
            "/...", "/XD", "/ii", "/^^", "/<<", "/>.", "/-_-", "/0o0", "/zz", "/O!O",
            "/##", "/:O", "/<", "/heart", "/break", "/rose", "/gift", "/bow", "/moon", "/sun",
            "/coin", "/bulb", "/tea", "/cake", "/music", "/rock", "/v", "/good", "/bad", "/ok",
            "/asnowwolf-smile", "/asnowwolf-laugh", "/asnowwolf-upset", "/asnowwolf-tear",
            "/asnowwolf-worry", "/asnowwolf-shock", "/asnowwolf-amuse");*/
    }

    public static List<TOKEN> tokenizer(CharSequence text, List<Attachment> attachmentList) {

        mTokenList = new ArrayList<>();

        Pattern pattern;
        Matcher matcher;
        int start;

        for (String colorStartLabel : colorStartLabels) {
            pattern = Pattern.compile(colorStartLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new COLOR_START(matcher.start(), matcher.group(), matcher.group(1)));
            }
        }

        for (String urlStartLabel : urlStartLabels) {
            pattern = Pattern.compile(urlStartLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                String url = matcher.group(1).toLowerCase();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                mTokenList.add(new URL_START(matcher.start(), url, matcher.group()));

            }

        }

        for (String urlEndLabel : urlEndLabels) {
            pattern = Pattern.compile(urlEndLabel);
            matcher = pattern.matcher(text);


            while (matcher.find()) {
                mTokenList.add(new URL_END(matcher.start(), matcher.group()));
            }
        }

        for (String centerStartLabel : centerStartLabels) {
            pattern = Pattern.compile(centerStartLabel);
            matcher = pattern.matcher(text);


            while (matcher.find()) {
                mTokenList.add(new CENTER_START(matcher.start(), matcher.group()));

            }
        }

        for (String centerEndLabel : centerEndLabels) {
            pattern = Pattern.compile(centerEndLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new CENTER_END(matcher.start(), matcher.group()));
            }
        }

        for (String curtainStartLabel : curtainStartLabels) {
            pattern = Pattern.compile(curtainStartLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new CURTAIN_START(matcher.start(), matcher.group()));
            }
        }

        for (String curtainEndLabel : curtainEndLabels) {
            pattern = Pattern.compile(curtainEndLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new CURTAIN_END(matcher.start(), matcher.group()));
            }
        }

        for (String attachmentLabel : attachmentLabels) {
            pattern = Pattern.compile(attachmentLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                String id = matcher.group(1);
                if (attachmentList != null) {
                    for (Attachment attachment : attachmentList) {
                        if (attachment.getAttachmentId().equals(id)) {
                            mTokenList.add(new ATTACHMENT(matcher.start(), attachment, matcher.group()));
                            break;
                        }
                    }
                }
            }
        }

        for (String colorEndLabel : colorEndLabels) {
            pattern = Pattern.compile(colorEndLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new COLOR_END(matcher.start(), matcher.group()));
            }
        }

        for (String italicStartLabel : italicStartLabels) {
            pattern = Pattern.compile(italicStartLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new ITALIC_START(matcher.start(), matcher.group()));
            }
        }

        for (String italicEndLabel : italicEndLabels) {
            pattern = Pattern.compile(italicEndLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new ITALIC_END(matcher.start(), matcher.group()));
            }
        }

        for (String boldStartLabel : boldStartLabels) {
            pattern = Pattern.compile(boldStartLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new BOLD_START(matcher.start(), matcher.group()));
            }
        }

        for (String boldEndLabel : boldEndLabels) {
            pattern = Pattern.compile(boldEndLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new BOLD_END(matcher.start(), matcher.group()));
            }
        }

        for (String deleteStartLabel : deleteStartLabels) {
            pattern = Pattern.compile(deleteStartLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new DELETE_START(matcher.start(), matcher.group()));
            }
        }

        for (String deleteEndLabel : deleteEndLabels) {
            pattern = Pattern.compile(deleteEndLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new DELETE_END(matcher.start(), matcher.group()));
            }
        }

        for (String underlineStartLabel : underlineStartLabels) {
            pattern = Pattern.compile(underlineStartLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new UNDERLINE_START(matcher.start(), matcher.group()));
            }
        }

        for (String underlineEndLabel : underlineEndLabels) {
            pattern = Pattern.compile(underlineEndLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new UNDERLINE_END(matcher.start(), matcher.group()));
            }
        }

        for (String titleStartLabel : titleStartLabels) {
            pattern = Pattern.compile(titleStartLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new TITLE_START(matcher.start(), matcher.group()));
            }
        }

        for (String titleEndLabel : titleEndLabels) {
            pattern = Pattern.compile(titleEndLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new TITLE_END(matcher.start(), matcher.group()));
            }
        }

        for (String codeStartLabel : codeStartLabels) {
            pattern = Pattern.compile(codeStartLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new CODE_START(matcher.start(), matcher.group()));
            }
        }

        for (String codeEndLabel : codeEndLabels) {
            pattern = Pattern.compile(codeEndLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new CODE_END(matcher.start(), matcher.group()));
            }
        }

        for (int i = 0; i < quoteStartLabels.size(); i++) {
            String quoteStartLabel = quoteStartLabels.get(i);
            QuotePos quotePos = quotePosList.get(i);
            pattern = Pattern.compile(quoteStartLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                if (quotePos.postIdPos == -1 && quotePos.memberPos == -1) {
                    mTokenList.add(new QUOTE_START(matcher.start(), matcher.group(), "", ""));
                } else if (quotePos.postIdPos == -1) {
                    mTokenList.add(new QUOTE_START(matcher.start(), matcher.group(), matcher.group(1), ""));
                } else if (quotePos.memberPos == -1) {
                    mTokenList.add(new QUOTE_START(matcher.start(), matcher.group(), "", matcher.group(1)));
                } else {
                    mTokenList.add(new QUOTE_START(matcher.start(), matcher.group(), matcher.group(quotePos.memberPos), matcher.group(quotePos.postIdPos)));
                }
            }
        }

        for (String quoteEndLabel : quoteEndLabels) {
            pattern = Pattern.compile(quoteEndLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                mTokenList.add(new QUOTE_END(matcher.start(), matcher.group()));
            }
        }

        String str = text.toString();
        for (int i = 0; i < iconStrs.size(); i++) {
            int from = 0;
            String iconStr = iconStrs.get(i);
            while ((from = str.indexOf(iconStr, from)) >= 0) {

                /**
                 * only show icons when iconStr is surrounded by spaces
                 */
                if (iconStr.equals("/^^")) Log.d(TAG, "parse: " + str.trim().length() + ", " + iconStr.length() + ", " + (from + iconStr.length()) + ", " + str.length());
                if (str.trim().length() == iconStr.length() ||
                        ((from == 0 || ' ' == str.charAt(from - 1)) && (from + iconStr.length() == str.length() || ' ' == str.charAt(from + iconStr.length()) || '\n' == str.charAt(from + iconStr.length())))) {
                    mTokenList.add(new ICON(from, iconStr, icons.get(i)));
                }
                from += iconStr.length();
            }
        }

        for (int i = 0; i < imageLabels.size(); i++) {
            String imageLabel = imageLabels.get(i);
            ImgPos imgPos = imgPosList.get(i);
            pattern = Pattern.compile(imageLabel);
            matcher = pattern.matcher(text);

            while (matcher.find()) {
                if (imgPos.heightPos == -1 && imgPos.widthPos == -1) {
                    mTokenList.add(new IMAGE(matcher.start(), matcher.group(1), matcher.group()));
                } else if (imgPos.heightPos == -1) {
                    mTokenList.add(new IMAGE(matcher.start(), matcher.group(imgPos.urlPos), matcher.group(), Integer.valueOf(matcher.group(imgPos.widthPos)), -1));
                } else if (imgPos.widthPos == -1) {
                    mTokenList.add(new IMAGE(matcher.start(), matcher.group(imgPos.urlPos), matcher.group(), -1, Integer.valueOf(matcher.group(imgPos.heightPos))));
                } else {
                    mTokenList.add(new IMAGE(matcher.start(), matcher.group(imgPos.urlPos), matcher.group(), Integer.valueOf(matcher.group(imgPos.widthPos)), Integer.valueOf(matcher.group(imgPos.heightPos))));
                }
            }
        }

        pattern = TABLE_REG;
        matcher = pattern.matcher(text);

        while (matcher.find()) {
            mTokenList.add(new TABLE(matcher.start(), matcher.group()));
        }


        final int[] indexInRegex = {1, 1, 1, 0, 1};
        Matcher[] matchers = new Matcher[PATTERNS.length];
        for (int i = 0; i < PATTERNS.length; i++) {
            matchers[i] = PATTERNS[i].matcher(text);
        }

        for (int i = 0; i < matchers.length; i++) {
            matcher = matchers[i];
            int index = indexInRegex[i];

            String content, value;
            int contentStart;

            while (matcher.find()) {
                start = matcher.start();
                content = matcher.group(index);
                value = matcher.group();

                contentStart = matcher.start(index);

                mTokenList.add(new FORMULA(start, content, contentStart - start, value));
            }
        }

        Collections.sort(mTokenList);

        for (int i = 0; i < mTokenList.size(); i++) {
            TOKEN token = mTokenList.get(i);

            if (token instanceof TABLE) {
                for (int j = 0; j < mTokenList.size(); j++) {
                    TOKEN token1 = mTokenList.get(j);

                    if (token1.position >= token.position + token.length) {
                        break;
                    }

                    if (token1.position > token.position) {
                        mTokenList.remove(j);
                        j--;
                    }
                }
            }
        }

        mTokenList.add(new END(text.length()));
        removeOverlappingTokens(mTokenList);

        start = 0;
        for (int i = 0; i < mTokenList.size(); i++) {
            TOKEN token = mTokenList.get(i);
            if (token.position > start) {
                mTokenList.add(i, new PLAIN(start, text.subSequence(start, token.position)));
                i++;
            }
            start = token.position + token.length;
        }

        return mTokenList;
    }

    /**
     * @param tokenList a sorted list
     */
    private static void removeOverlappingTokens(List<TOKEN> tokenList) {
        for (int i = tokenList.size() - 1; i >= 1; i--) {
            TOKEN thisToken = tokenList.get(i);
            TOKEN prevToken = tokenList.get(i - 1);

            if (thisToken.position < prevToken.position + prevToken.length) {
                tokenList.remove(thisToken);
            }
        }
    }
}
