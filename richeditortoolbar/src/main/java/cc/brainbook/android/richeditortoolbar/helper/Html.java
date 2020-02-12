package cc.brainbook.android.richeditortoolbar.helper;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.text.TextDirectionHeuristicsCompat;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import android.text.style.ParagraphStyle;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;
import cc.brainbook.android.richeditortoolbar.span.nest.AlignCenterSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.AlignNormalSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.AlignOppositeSpan;
import cc.brainbook.android.richeditortoolbar.span.block.AudioSpan;
import cc.brainbook.android.richeditortoolbar.span.character.BlockSpan;
import cc.brainbook.android.richeditortoolbar.span.character.BoldSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CodeSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomAbsoluteSizeSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomBackgroundColorSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomFontFamilySpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomForegroundColorSpan;
import cc.brainbook.android.richeditortoolbar.span.block.CustomImageSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.CustomLeadingMarginSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.CustomQuoteSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomRelativeSizeSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomStrikethroughSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomSubscriptSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomSuperscriptSpan;
import cc.brainbook.android.richeditortoolbar.span.block.CustomURLSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomUnderlineSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.DivSpan;
import cc.brainbook.android.richeditortoolbar.span.paragraph.HeadSpan;
import cc.brainbook.android.richeditortoolbar.span.character.ItalicSpan;
import cc.brainbook.android.richeditortoolbar.span.paragraph.LineDividerSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.ListItemSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.ListSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.PreSpan;
import cc.brainbook.android.richeditortoolbar.span.block.VideoSpan;
import cc.brainbook.android.richeditortoolbar.util.SpanUtil;

import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.LIST_TYPE_ORDERED_DECIMAL;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.LIST_TYPE_ORDERED_LOWER_LATIN;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.LIST_TYPE_ORDERED_LOWER_ROMAN;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.LIST_TYPE_ORDERED_UPPER_LATIN;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.LIST_TYPE_ORDERED_UPPER_ROMAN;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.LIST_TYPE_UNORDERED_DISC;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.LIST_TYPE_UNORDERED_CIRCLE;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.LIST_TYPE_UNORDERED_SQUARE;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.isListTypeOrdered;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.updateListSpans;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.getSpanFlags;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isBlockCharacterStyle;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isCharacterStyle;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isParagraphStyle;

/**
 * This class processes HTML strings into displayable styled text.
 * Not all HTML tags are supported.
 */
public class Html {
    ///[UPGRADE#android.text.Html]缺省的屏幕密度
    ///px in CSS is the equivalance of dip in Android
    ///注意：一般情况下，CustomAbsoluteSizeSpan的dip都为true，否则需要在使用Html之前设置本机的具体准确的屏幕密度！
    public static float sDisplayMetricsDensity = 3.0f;

    /**
     * Retrieves images for HTML &lt;img&gt; tags.
     */
    public static interface ImageGetter {
        /**
         * This method is called when the HTML parser encounters an
         * &lt;img&gt; tag.  The <code>source</code> argument is the
         * string from the "src" attribute; the return value should be
         * a Drawable representation of the image or <code>null</code>
         * for a generic replacement image.  Make sure you call
         * setBounds() on your Drawable if it doesn't already have
         * its bounds set.
         */
        public Drawable getDrawable(String source);
    }

    /**
     * Is notified when HTML tags are encountered that the parser does
     * not know how to interpret.
     */
    public static interface TagHandler {
        /**
         * This method will be called whenn the HTML parser encounters
         * a tag that it does not know how to interpret.
         */
        public void handleTag(boolean opening, String tag,
                              Editable output, XMLReader xmlReader);
    }

    /**
     * Option for {@link #toHtml(Spanned, int)}: Wrap consecutive lines of text delimited by '\n'
     * inside &lt;p&gt; elements. {@link BulletSpan}s are ignored.
     */
    public static final int TO_HTML_PARAGRAPH_LINES_CONSECUTIVE = 0x00000000;

    /**
     * Option for {@link #toHtml(Spanned, int)}: Wrap each line of text delimited by '\n' inside a
     * &lt;p&gt; or a &lt;li&gt; element. This allows {@link ParagraphStyle}s attached to be
     * encoded as CSS styles within the corresponding &lt;p&gt; or &lt;li&gt; element.
     */
    public static final int TO_HTML_PARAGRAPH_LINES_INDIVIDUAL = 0x00000001;

    /**
     * Flag indicating that texts inside &lt;p&gt; elements will be separated from other texts with
     * one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH = 0x00000001;

    /**
     * Flag indicating that texts inside &lt;h1&gt;~&lt;h6&gt; elements will be separated from
     * other texts with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_HEADING = 0x00000002;

    /**
     * Flag indicating that texts inside &lt;li&gt; elements will be separated from other texts
     * with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM = 0x00000004;

    /**
     * Flag indicating that texts inside &lt;ul&gt; elements will be separated from other texts
     * with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_LIST = 0x00000008;

    /**
     * Flag indicating that texts inside &lt;div&gt; elements will be separated from other texts
     * with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_DIV = 0x00000010;

    /**
     * Flag indicating that texts inside &lt;blockquote&gt; elements will be separated from other
     * texts with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE = 0x00000020;

    /**
     * Flag indicating that CSS color values should be used instead of those defined in
     * {@link Color}.
     */
    public static final int FROM_HTML_OPTION_USE_CSS_COLORS = 0x00000100;

    /**
     * Flags for {@link #fromHtml(String, int, Html.ImageGetter, Html.TagHandler)}: Separate block-level
     * elements with blank lines (two newline characters) in between. This is the legacy behavior
     * prior to N.
     */
    public static final int FROM_HTML_MODE_LEGACY = 0x00000000;

    /**
     * Flags for {@link #fromHtml(String, int, Html.ImageGetter, Html.TagHandler)}: Separate block-level
     * elements with line breaks (single newline character) in between. This inverts the
     * {@link Spanned} to HTML string conversion done with the option
     * {@link #TO_HTML_PARAGRAPH_LINES_INDIVIDUAL}.
     */
    public static final int FROM_HTML_MODE_COMPACT =
            FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                    | FROM_HTML_SEPARATOR_LINE_BREAK_HEADING
                    | FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
                    | FROM_HTML_SEPARATOR_LINE_BREAK_LIST
                    | FROM_HTML_SEPARATOR_LINE_BREAK_DIV
                    | FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE;

    /**
     * The bit which indicates if lines delimited by '\n' will be grouped into &lt;p&gt; elements.
     */
    private static final int TO_HTML_PARAGRAPH_FLAG = 0x00000001;

    private Html() { }

    /**
     * Returns displayable styled text from the provided HTML string with the legacy flags
     * {@link #FROM_HTML_MODE_LEGACY}.
     *
     * @deprecated use {@link #fromHtml(String, int)} instead.
     */
    @Deprecated
    public static Spanned fromHtml(String source) {
        return fromHtml(source, FROM_HTML_MODE_LEGACY, null, null);
    }

    /**
     * Returns displayable styled text from the provided HTML string. Any &lt;img&gt; tags in the
     * HTML will display as a generic replacement image which your program can then go through and
     * replace with real images.
     *
     * <p>This uses TagSoup to handle real HTML, including all of the brokenness found in the wild.
     */
    public static Spanned fromHtml(String source, int flags) {
        return fromHtml(source, flags, null, null);
    }

    /**
     * Lazy initialization holder for HTML parser. This class will
     * a) be preloaded by the zygote, or b) not loaded until absolutely
     * necessary.
     */
    private static class HtmlParser {
        private static final HTMLSchema schema = new HTMLSchema();
    }

    /**
     * Returns displayable styled text from the provided HTML string with the legacy flags
     * {@link #FROM_HTML_MODE_LEGACY}.
     *
     * @deprecated use {@link #fromHtml(String, int, Html.ImageGetter, Html.TagHandler)} instead.
     */
    @Deprecated
    public static Spanned fromHtml(String source, Html.ImageGetter imageGetter, Html.TagHandler tagHandler) {
        return fromHtml(source, FROM_HTML_MODE_LEGACY, imageGetter, tagHandler);
    }

    /**
     * Returns displayable styled text from the provided HTML string. Any &lt;img&gt; tags in the
     * HTML will use the specified ImageGetter to request a representation of the image (use null
     * if you don't want this) and the specified TagHandler to handle unknown tags (specify null if
     * you don't want this).
     *
     * <p>This uses TagSoup to handle real HTML, including all of the brokenness found in the wild.
     */
    public static Spanned fromHtml(String source, int flags, Html.ImageGetter imageGetter,
                                   Html.TagHandler tagHandler) {
        Parser parser = new Parser();
        try {
            parser.setProperty(Parser.schemaProperty, HtmlParser.schema);
        } catch (org.xml.sax.SAXNotRecognizedException e) {
            // Should not happen.
            throw new RuntimeException(e);
        } catch (org.xml.sax.SAXNotSupportedException e) {
            // Should not happen.
            throw new RuntimeException(e);
        }

        HtmlToSpannedConverter converter =
                new HtmlToSpannedConverter(source, imageGetter, tagHandler, parser, flags);
        return converter.convert();
    }

    /**
     * @deprecated use {@link #toHtml(Spanned, int)} instead.
     */
    @Deprecated
    public static String toHtml(Spanned text) {
        return toHtml(text, TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
    }

    /**
     * Returns an HTML representation of the provided Spanned text. A best effort is
     * made to add HTML tags corresponding to spans. Also note that HTML metacharacters
     * (such as "&lt;" and "&amp;") within the input text are escaped.
     *
     * @param text input text to convert
     * @param option one of {@link #TO_HTML_PARAGRAPH_LINES_CONSECUTIVE} or
     *     {@link #TO_HTML_PARAGRAPH_LINES_INDIVIDUAL}
     * @return string containing input converted to HTML
     */
    public static String toHtml(Spanned text, int option) {
        StringBuilder out = new StringBuilder();

        ///[UPGRADE#android.text.Html]
//        withinHtml(out, text, option);
        handleHtml(out, text, null);

        return out.toString();
    }

    /**
     * Returns an HTML escaped representation of the given plain text.
     */
    public static String escapeHtml(CharSequence text) {
        StringBuilder out = new StringBuilder();
        withinStyle(out, text, 0, text.length());
        return out.toString();
    }


    /* ------------------- ///[UPGRADE#android.text.Html] ------------------- */
    private static void handleHtml(StringBuilder out, Spanned text, ParagraphStyle compareSpan) {
        int start, end;
        if (compareSpan == null) {
            start = 0;
            end = text.length();
        } else {
            start = text.getSpanStart(compareSpan);
            end = text.getSpanEnd(compareSpan);
        }

        int next;
        for (int i = start; i < end; i = next) {
            final ParagraphStyle nextParagraphStyleSpan = getNextParagraphStyleSpan(text, i, compareSpan);

            if (nextParagraphStyleSpan == null) {
                next = text.nextSpanTransition(i, end, ParagraphStyle.class);

                ///[PreSpan]
                if (compareSpan instanceof PreSpan) {
                    withinParagraph(out, text, i, next);
                } else {
                    handleParagraph(out, text, i, next, compareSpan == null
                            || compareSpan instanceof INestParagraphStyle && !(compareSpan instanceof ListItemSpan));
                }
            } else {
                next = text.getSpanEnd(nextParagraphStyleSpan);

                if (nextParagraphStyleSpan instanceof DivSpan) {
                    out.append("<div>\n");
                } else
                if (nextParagraphStyleSpan instanceof CustomLeadingMarginSpan) {
                    final int leadingMarginSpanIndent = ((CustomLeadingMarginSpan) nextParagraphStyleSpan).getLeadingMargin(true);
                    out.append("<div style=\"text-indent:").append(leadingMarginSpanIndent).append("px;\">\n");
                } else
                if (nextParagraphStyleSpan instanceof AlignNormalSpan) {
                    out.append("<div style=\"text-align:start;\">\n");
                } else
                if (nextParagraphStyleSpan instanceof AlignCenterSpan) {
                    out.append("<div style=\"text-align:center;\">\n");
                } else
                if (nextParagraphStyleSpan instanceof AlignOppositeSpan) {
                    out.append("<div style=\"text-align:end;\">\n");
                } else
                if (nextParagraphStyleSpan instanceof ListSpan) {
                    final int listStart = ((ListSpan) nextParagraphStyleSpan).getStart();
                    final boolean isReversed = ((ListSpan) nextParagraphStyleSpan).isReversed();
                    final int listType = ((ListSpan) nextParagraphStyleSpan).getListType();

                    if (isListTypeOrdered(listType)) {
                        out.append("<ol start=\"").append(listStart).append("\"");

                        if (isReversed) {
                            out.append(" reversed");
                        }

                        if (listType == LIST_TYPE_ORDERED_DECIMAL) {
                            out.append(" style=\"list-style-type:decimal\"");
                        } else if (listType == LIST_TYPE_ORDERED_LOWER_LATIN) {
                            out.append(" style=\"list-style-type:lower-alpha\"");
                        } else if (listType == LIST_TYPE_ORDERED_UPPER_LATIN) {
                            out.append(" style=\"list-style-type:upper-alpha\"");
                        } else if (listType == LIST_TYPE_ORDERED_LOWER_ROMAN) {
                            out.append(" style=\"list-style-type:lower-roman\"");
                        } else if (listType == LIST_TYPE_ORDERED_UPPER_ROMAN) {
                            out.append(" style=\"list-style-type:upper-roman\"");
                        }

                        out.append(">\n");
                    } else {
                        out.append("<ul");

                        if (listType == LIST_TYPE_UNORDERED_DISC) {
                            out.append(" style=\"list-style-type:disc\"");
                        } else if (listType == LIST_TYPE_UNORDERED_CIRCLE) {
                            out.append(" style=\"list-style-type:circle\"");
                        } else if (listType == LIST_TYPE_UNORDERED_SQUARE) {
                            out.append(" style=\"list-style-type:square\"");
                        }

                        out.append(">\n");
                    }
                } else
                if (nextParagraphStyleSpan instanceof ListItemSpan) {
                    out.append("<li>\n");
                } else
                if (nextParagraphStyleSpan instanceof CustomQuoteSpan) {
                    out.append("<blockquote>\n");
                } else

                if (nextParagraphStyleSpan instanceof PreSpan) {    ///[PreSpan]
                    out.append("<pre>");
                } else

                if (nextParagraphStyleSpan instanceof HeadSpan) {
                    out.append("<h").append(((HeadSpan) nextParagraphStyleSpan).getLevel() + 1).append(">");
                } else

                if (nextParagraphStyleSpan instanceof LineDividerSpan) {
                    out.append("<hr>\n");
                }

                handleHtml(out, text, nextParagraphStyleSpan);

                if (nextParagraphStyleSpan instanceof DivSpan
                        || nextParagraphStyleSpan instanceof CustomLeadingMarginSpan
                        || nextParagraphStyleSpan instanceof AlignNormalSpan
                        || nextParagraphStyleSpan instanceof AlignCenterSpan
                        || nextParagraphStyleSpan instanceof AlignOppositeSpan) {
                    out.append("</div>\n");
                } else
                if (nextParagraphStyleSpan instanceof ListSpan) {
                    final int listType = ((ListSpan) nextParagraphStyleSpan).getListType();
                    final String listTag = isListTypeOrdered(listType) ? "ol" : "ul";

                    out.append("</").append(listTag).append(">\n");
                } else
                if (nextParagraphStyleSpan instanceof ListItemSpan) {
                    out.append("</li>\n");
                } else
                if (nextParagraphStyleSpan instanceof CustomQuoteSpan) {
                    out.append("</blockquote>\n");
                } else

                if (nextParagraphStyleSpan instanceof HeadSpan) {
                    out.append("</h").append(((HeadSpan) nextParagraphStyleSpan).getLevel() + 1).append(">\n");
                } else
                if (nextParagraphStyleSpan instanceof PreSpan) {    ///[PreSpan]
                    out.append("</pre>\n");
                }
            }
        }
    }

    private static ParagraphStyle getNextParagraphStyleSpan(Spanned text, int where, ParagraphStyle compareSpan) {
        ParagraphStyle resultSpan = null;

        final ArrayList<ParagraphStyle> paragraphStyleSpans = SpanUtil.getFilteredSpans(ParagraphStyle.class, (Editable) text, where, where, true);
        for (ParagraphStyle paragraphStyleSpan : paragraphStyleSpans) {
            if (paragraphStyleSpan == compareSpan) {
                break;
            }

            final int paragraphStyleSpanStart = text.getSpanStart(paragraphStyleSpan);
            final int paragraphStyleSpanEnd = text.getSpanEnd(paragraphStyleSpan);
            final int resultSpanEnd = text.getSpanEnd(resultSpan);
            if (paragraphStyleSpanStart == where && (resultSpan == null || resultSpanEnd <= paragraphStyleSpanEnd)) {
                resultSpan = paragraphStyleSpan;
            }
        }

        return resultSpan;
    }

    private static void handleParagraph(StringBuilder out, Spanned text, int start, int end, boolean isOutParagraph) {
        if (isOutParagraph) {
            out.append("<p").append(getTextDirection(text, start, end)).append(">");
        }

        int next;
        for (int i = start; i < end; i = next) {
            next = TextUtils.indexOf(text, '\n', i, end);
            if (next < 0) {
                next = end;
            }

            withinParagraph(out, text, i, next);

            if (++next < end) {
                if (isOutParagraph) {
                    out.append("</p>\n<p").append(getTextDirection(text, start, end)).append(">");
                } else {
                    out.append("<br>\n");
                }
            }
        }

        if (isOutParagraph) {
            out.append("</p>\n");
        } else {
            out.append('\n');
        }
    }

    private static String getTextDirection(Spanned text, int start, int end) {
        ///[UPGRADE#android.text.Html]
//        if (TextDirectionHeuristics.FIRSTSTRONG_LTR.isRtl(text, start, end - start)) {
        if (TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR.isRtl(text, start, end - start)) {
            return " dir=\"rtl\"";
        } else {
            return " dir=\"ltr\"";
        }
    }

    private static void withinParagraph(StringBuilder out, Spanned text, int start, int end) {
        int next;
        for (int i = start; i < end; i = next) {
            next = text.nextSpanTransition(i, end, CharacterStyle.class);

            ///[PreSpan]注意：PreSpan同时继承ParagraphStyle和ICharacterStyle！
            Object[] style = text.getSpans(i, next, ICharacterStyle.class);

            for (int j = 0; j < style.length; j++) {
                if (style[j] instanceof CodeSpan) {
                    out.append("<code>");
                } else
                if (style[j] instanceof BlockSpan) {
                    out.append("<block>");
                } else

                if (style[j] instanceof StyleSpan) {
                    int s = ((StyleSpan) style[j]).getStyle();

                    if ((s & Typeface.BOLD) != 0) {
                        out.append("<b>");
                    }
                    if ((s & Typeface.ITALIC) != 0) {
                        out.append("<i>");
                    }
                } else

                if (style[j] instanceof UnderlineSpan) {
                    out.append("<u>");
                } else

                if (style[j] instanceof StrikethroughSpan) {
                    ///[UPGRADE#android.text.Html]
                    out.append("<span style=\"text-decoration:line-through;\">");
//                    out.append("<strike>");
                } else

                if (style[j] instanceof SuperscriptSpan) {
                    out.append("<sup>");
                } else
                if (style[j] instanceof SubscriptSpan) {
                    out.append("<sub>");
                } else

                if (style[j] instanceof CustomForegroundColorSpan) {
                    int color = ((CustomForegroundColorSpan) style[j]).getForegroundColor();
                    out.append(String.format("<span style=\"color:#%06X;\">", 0xFFFFFF & color));
                } else
                if (style[j] instanceof CustomBackgroundColorSpan) {
                    int color = ((CustomBackgroundColorSpan) style[j]).getBackgroundColor();
                    out.append(String.format("<span style=\"background-color:#%06X;\">",
                            0xFFFFFF & color));
                } else

                if (style[j] instanceof CustomFontFamilySpan) {
                    String s = ((CustomFontFamilySpan) style[j]).getFamily();
                    ///注意：当face="monospace"时转换为tt标签
                    if ("monospace".equals(s)) {
                        out.append("<tt>");
                    } else {
                        out.append("<font face=\"").append(s).append("\">");
                    }
                } else

                if (style[j] instanceof CustomAbsoluteSizeSpan) {
                    CustomAbsoluteSizeSpan s = ((CustomAbsoluteSizeSpan) style[j]);
                    float sizeDip = s.getSize();
                    if (!s.getDip()) {
                        ///[UPGRADE#android.text.Html]px in CSS is the equivalance of dip in Android
                        ///注意：一般情况下，CustomAbsoluteSizeSpan的dip都为true，否则需要在使用Html之前设置本机的具体准确的屏幕密度！
//                        Application application = ActivityThread.currentApplication();
//                        sizeDip /= application.getResources().getDisplayMetrics().density;
                        sizeDip /= sDisplayMetricsDensity;
                    }

                    // px in CSS is the equivalance of dip in Android
                    out.append(String.format("<span style=\"font-size:%.0fpx\";>", sizeDip));
                } else
                if (style[j] instanceof CustomRelativeSizeSpan) {
                    float sizeEm = ((CustomRelativeSizeSpan) style[j]).getSizeChange();
                    if (sizeEm == 1.25f) {
                        out.append("<big>");
                    } else if (sizeEm == 0.8f) {
                        out.append("<small>");
                    } else {
                        out.append(String.format("<span style=\"font-size:%.2fem;\">", sizeEm));
                    }
                } else

                if (style[j] instanceof URLSpan) {
                    out.append("<a href=\"");
                    out.append(((URLSpan) style[j]).getURL());
                    out.append("\">");
                } else

                if (style[j] instanceof CustomImageSpan) {
                    if (style[j] instanceof VideoSpan) {
                        out.append("<video src=\"").append(((VideoSpan) style[j]).getUri()).append("\"");
                        out.append(" img=\"").append(((VideoSpan) style[j]).getSource()).append("\"");
                    } else if (style[j] instanceof AudioSpan) {
                        out.append("<audio src=\"").append(((AudioSpan) style[j]).getUri()).append("\"");
                        out.append(" img=\"").append(((AudioSpan) style[j]).getSource()).append("\"");
                    } else {
                        out.append("<img src=\"").append(((CustomImageSpan) style[j]).getSource()).append("\"");
                    }
                    out.append(" width=\"").append(((CustomImageSpan) style[j]).getDrawableWidth()).append("\"");
                    out.append(" height=\"").append(((CustomImageSpan) style[j]).getDrawableHeight()).append("\"");
                    out.append(" align=\"").append(((CustomImageSpan) style[j]).getVerticalAlignment()).append("\"");
                    out.append(" />");

                    // Don't output the dummy character underlying the image.
                    i = next;
                }
            }

            ///[PreSpan]注意：PreSpan同时继承ParagraphStyle和ICharacterStyle！
            if (style.length == 0 || !(style[0] instanceof PreSpan)) {
                withinStyle(out, text, i, next);
            } else {
                out.append(text.subSequence(i, next));
            }

            for (int j = style.length - 1; j >= 0; j--) {
                if (style[j] instanceof CodeSpan) {
                    out.append("</code>");
                } else
                if (style[j] instanceof BlockSpan) {
                    out.append("</block>");
                } else

                if (style[j] instanceof StyleSpan) {
                    int s = ((StyleSpan) style[j]).getStyle();

                    if ((s & Typeface.BOLD) != 0) {
                        out.append("</b>");
                    }
                    if ((s & Typeface.ITALIC) != 0) {
                        out.append("</i>");
                    }
                } else

                if (style[j] instanceof UnderlineSpan) {
                    out.append("</u>");
                } else

                if (style[j] instanceof StrikethroughSpan) {
                    out.append("</span>");
//                    out.append("</strike>");
                } else

                if (style[j] instanceof SubscriptSpan) {
                    out.append("</sub>");
                } else
                if (style[j] instanceof SuperscriptSpan) {
                    out.append("</sup>");
                } else

                if (style[j] instanceof CustomBackgroundColorSpan || style[j] instanceof CustomForegroundColorSpan) {
                    out.append("</span>");
                } else

                if (style[j] instanceof CustomFontFamilySpan) {
                    String s = ((CustomFontFamilySpan) style[j]).getFamily();
                    ///注意：当face="monospace"时转换为tt标签
                    if ("monospace".equals(s)) {
                        out.append("</tt>");
                    } else {
                        out.append("</font>");
                    }
                } else

                if (style[j] instanceof CustomAbsoluteSizeSpan) {
                    out.append("</span>");
                } else
                if (style[j] instanceof CustomRelativeSizeSpan) {
                    float sizeEm = ((CustomRelativeSizeSpan) style[j]).getSizeChange();
                    if (sizeEm == 1.25f) {
                        out.append("</big>");
                    } else if (sizeEm == 0.8f) {
                        out.append("</small>");
                    } else {
                        out.append("</span>");
                    }
                } else

                if (style[j] instanceof URLSpan) {
                    out.append("</a>");
                }
            }
        }
    }

    private static void withinStyle(StringBuilder out, CharSequence text,
                                    int start, int end) {
        for (int i = start; i < end; i++) {
            char c = text.charAt(i);

            if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else if (c >= 0xD800 && c <= 0xDFFF) {
                if (c < 0xDC00 && i + 1 < end) {
                    char d = text.charAt(i + 1);
                    if (d >= 0xDC00 && d <= 0xDFFF) {
                        i++;
                        int codepoint = 0x010000 | (int) c - 0xD800 << 10 | (int) d - 0xDC00;
                        out.append("&#").append(codepoint).append(";");
                    }
                }
            } else if (c > 0x7E || c < ' ') {
                out.append("&#").append((int) c).append(";");
            } else if (c == ' ') {
                while (i + 1 < end && text.charAt(i + 1) == ' ') {
                    out.append("&nbsp;");
                    i++;
                }

                out.append(' ');
            } else {
                out.append(c);
            }
        }
    }
}



class HtmlToSpannedConverter implements ContentHandler {

//    private static final float[] HEADING_SIZES = {
//            1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f,
//    };

    private String mSource;
    private XMLReader mReader;
    private SpannableStringBuilder mSpannableStringBuilder;
    private Html.ImageGetter mImageGetter;
    private Html.TagHandler mTagHandler;
    private int mFlags;

    private static Pattern sListType;
    private static Pattern sLeadingMargin;
    private static Pattern sTextAlignPattern;
    private static Pattern sForegroundColorPattern;
    private static Pattern sBackgroundColorPattern;
    private static Pattern sTextDecorationPattern;
    private static Pattern sFontSizePattern;

    /**
     * Name-value mapping of HTML/CSS colors which have different values in {@link Color}.
     */
    private static final Map<String, Integer> sColorMap;

    static {
        sColorMap = new HashMap<>();
        sColorMap.put("darkgray", 0xFFA9A9A9);
        sColorMap.put("gray", 0xFF808080);
        sColorMap.put("lightgray", 0xFFD3D3D3);
        sColorMap.put("darkgrey", 0xFFA9A9A9);
        sColorMap.put("grey", 0xFF808080);
        sColorMap.put("lightgrey", 0xFFD3D3D3);
        sColorMap.put("green", 0xFF008000);
//        //////??????[UPGRADE#增加颜色]参考Color#sColorNameMap
//        sColorMap.put("red", 0xFFFF0000);
//        sColorMap.put("yellow", 0xFFFFFF00);
//        sColorMap.put("blue", 0xFF0000FF);
    }

    private static Pattern getListTypePattern() {
        if (sListType == null) {
            sListType = Pattern.compile("(?:\\s+|\\A)list-style-type\\s*:\\s*(\\S*)\\b");
        }
        return sListType;
    }

    private static Pattern getLeadingMarginPattern() {
        if (sLeadingMargin == null) {
            sLeadingMargin = Pattern.compile("(?:\\s+|\\A)text-indent\\s*:\\s*(\\S*)\\b");
        }
        return sLeadingMargin;
    }

    private static Pattern getTextAlignPattern() {
        if (sTextAlignPattern == null) {
            sTextAlignPattern = Pattern.compile("(?:\\s+|\\A)text-align\\s*:\\s*(\\S*)\\b");
        }
        return sTextAlignPattern;
    }

    private static Pattern getForegroundColorPattern() {
        if (sForegroundColorPattern == null) {
            sForegroundColorPattern = Pattern.compile(
                    "(?:\\s+|\\A)color\\s*:\\s*(\\S*)\\b");
        }
        return sForegroundColorPattern;
    }

    private static Pattern getBackgroundColorPattern() {
        if (sBackgroundColorPattern == null) {
            sBackgroundColorPattern = Pattern.compile(
                    "(?:\\s+|\\A)background(?:-color)?\\s*:\\s*(\\S*)\\b");
        }
        return sBackgroundColorPattern;
    }

    private static Pattern getTextDecorationPattern() {
        if (sTextDecorationPattern == null) {
            sTextDecorationPattern = Pattern.compile(
                    "(?:\\s+|\\A)text-decoration\\s*:\\s*(\\S*)\\b");
        }
        return sTextDecorationPattern;
    }

    private static Pattern getFontSizePattern() {
        if (sFontSizePattern == null) {
            sFontSizePattern = Pattern.compile(
                    "(?:\\s+|\\A)font-size\\s*:\\s*(\\S*)\\b");
        }
        return sFontSizePattern;
    }

    private static boolean isInteger(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    private int getHtmlColor(String color) {
        if ((mFlags & Html.FROM_HTML_OPTION_USE_CSS_COLORS)
                == Html.FROM_HTML_OPTION_USE_CSS_COLORS) {
            Integer i = sColorMap.get(color.toLowerCase(Locale.US));
            if (i != null) {
                return i;
            }
        }
        ///[UPGRADE#android.text.Html]
//        return Color.getHtmlColor(color);
        return Color.parseColor(color);
    }


    public HtmlToSpannedConverter(String source, Html.ImageGetter imageGetter,
                                  Html.TagHandler tagHandler, Parser parser, int flags) {
        mSource = source;
        mSpannableStringBuilder = new SpannableStringBuilder();
        mImageGetter = imageGetter;
        mTagHandler = tagHandler;
        mReader = parser;
        mFlags = flags;
    }


    public void setDocumentLocator(Locator locator) {}

    public void startDocument() throws SAXException {}

    public void endDocument() throws SAXException {}

    public void startPrefixMapping(String prefix, String uri) throws SAXException {}

    public void endPrefixMapping(String prefix) throws SAXException {}

    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        handleStartTag(localName, attributes);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        handleEndTag(localName);
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        ///[PreSpan]
        if (getLast(null, Pre.class) == null) {
            handleCharacters(ch, start, length);
        } else {
            mSpannableStringBuilder.append(String.valueOf(ch, start, length));
        }
    }

    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {}

    public void processingInstruction(String target, String data) throws SAXException {}

    public void skippedEntity(String name) throws SAXException {}


    /* ---------------------------------------------------------------------------------- */
    ///[更新ListSpan]
    private ArrayList<ListSpan> updateListSpans = new ArrayList<>();

    public Spanned convert() {
        mReader.setContentHandler(this);
        try {
            mReader.parse(new InputSource(new StringReader(mSource)));
        } catch (IOException e) {
            // We are reading from a string. There should not be IO problems.
            throw new RuntimeException(e);
        } catch (SAXException e) {
            // TagSoup doesn't throw parse exceptions.
            throw new RuntimeException(e);
        }

        ///[更新ListSpan]
        updateListSpans(mSpannableStringBuilder, updateListSpans);

        ///[FIX#如果SPAN_INCLUSIVE_INCLUSIVE或SPAN_EXCLUSIVE_INCLUSIVE，则需要暂时改为SPAN_EXCLUSIVE_EXCLUSIVE，将来再改回来
        ///否则在添加内容后自动延长span的范围]//////??????CharacterStyle也用SPAN_INCLUSIVE_EXCLUSIVE，就无需FIX了
        final CharacterStyle[] characterStyles = mSpannableStringBuilder.getSpans(0, mSpannableStringBuilder.length(), CharacterStyle.class);
        for (CharacterStyle characterStyle : characterStyles) {
            final Class clazz = characterStyle.getClass();
            if (isCharacterStyle(clazz)
                    && !isBlockCharacterStyle(clazz)
                    && !isParagraphStyle(clazz)) {
                final int flags = mSpannableStringBuilder.getSpanFlags(characterStyle);
                if (flags != getSpanFlags(clazz)) {
                    final int spanStart = mSpannableStringBuilder.getSpanStart(characterStyle);
                    final int spanEnd = mSpannableStringBuilder.getSpanEnd(characterStyle);
                    mSpannableStringBuilder.setSpan(characterStyle, spanStart, spanEnd, getSpanFlags(clazz));
                }
            }
        }

        return mSpannableStringBuilder;
    }

    private void handleStartTag(String tag, Attributes attributes) {
        mLinkedList.add(new LL(mSpannableStringBuilder.length()));

        if (tag.equalsIgnoreCase("br")) {
            // We don't need to handle this. TagSoup will ensure that there's a </br> for each <br>
            // so we can safely emit the linebreaks when we handle the close tag.

        } else if (tag.equalsIgnoreCase("p")) {
            startP(mSpannableStringBuilder, attributes);

        } else if (tag.equalsIgnoreCase("div")) {
            startDiv(mSpannableStringBuilder, attributes);

        } else if (tag.equalsIgnoreCase("ul")) {
            startUl(mSpannableStringBuilder, attributes);
        } else if (tag.equalsIgnoreCase("ol")) {
            startOl(mSpannableStringBuilder, attributes);
        } else if (tag.equalsIgnoreCase("li")) {
            startLi(mSpannableStringBuilder, attributes);

        } else if (tag.equalsIgnoreCase("blockquote")) {
            startBlockquote(mSpannableStringBuilder, attributes);

        } else if (tag.equalsIgnoreCase("pre")) {   ///[PreSpan]
            startPre(mSpannableStringBuilder, attributes);

        } else if (tag.length() == 2 &&
                Character.toLowerCase(tag.charAt(0)) == 'h' &&
                tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
            startHeading(mSpannableStringBuilder, attributes, tag.charAt(1) - '1');

        } else if (tag.equalsIgnoreCase("hr")) {
            // We don't need to handle this.

        } else if (tag.equalsIgnoreCase("span")) {
            startCssStyle(mSpannableStringBuilder, attributes);

        } else if (tag.equalsIgnoreCase("strong")
                || tag.equalsIgnoreCase("b")) {
            start(mSpannableStringBuilder, new Bold());
        } else if (tag.equalsIgnoreCase("em")
                || tag.equalsIgnoreCase("cite")
                || tag.equalsIgnoreCase("dfn")
                || tag.equalsIgnoreCase("i")) {
            start(mSpannableStringBuilder, new Italic());
        } else if (tag.equalsIgnoreCase("u")) {
            start(mSpannableStringBuilder, new Underline());
        } else if (tag.equalsIgnoreCase("del")
                || tag.equalsIgnoreCase("s")
                || tag.equalsIgnoreCase("strike")) {
            start(mSpannableStringBuilder, new Strikethrough());
        } else if (tag.equalsIgnoreCase("sup")) {
            start(mSpannableStringBuilder, new Super());
        } else if (tag.equalsIgnoreCase("sub")) {
            start(mSpannableStringBuilder, new Sub());

        } else if (tag.equalsIgnoreCase("code")) {
            start(mSpannableStringBuilder, new Code());
        } else if (tag.equalsIgnoreCase("block")) {
            start(mSpannableStringBuilder, new Block());

        } else if (tag.equalsIgnoreCase("tt")) {
            start(mSpannableStringBuilder, new Monospace());
        } else if (tag.equalsIgnoreCase("font")) {
            startFont(mSpannableStringBuilder, attributes);

        } else if (tag.equalsIgnoreCase("big")) {
            start(mSpannableStringBuilder, new Big());
        } else if (tag.equalsIgnoreCase("small")) {
            start(mSpannableStringBuilder, new Small());

        } else if (tag.equalsIgnoreCase("a")) {
            startA(mSpannableStringBuilder, attributes);

        } else if (tag.equalsIgnoreCase("img")) {
            startMedia(mSpannableStringBuilder, attributes, mImageGetter, "img");

        } else if (tag.equalsIgnoreCase("video")) {
            startMedia(mSpannableStringBuilder, attributes, mImageGetter, "video");

        } else if (tag.equalsIgnoreCase("audio")) {
            startMedia(mSpannableStringBuilder, attributes, mImageGetter, "audio");

        } else if (mTagHandler != null) {
            mTagHandler.handleTag(true, tag, mSpannableStringBuilder, mReader);
        }
    }

    private void handleCharacters(char ch[], int start, int length) {
        StringBuilder sb = new StringBuilder();

        /*
         * Ignore whitespace that immediately follows other whitespace;
         * newlines count as spaces.
         */

        for (int i = 0; i < length; i++) {
            char c = ch[i + start];

            ///[UPGRADE#android.text.Html#'\n'应该忽略！]
//            if (c == ' ' && c == '\n') {
            if (c == ' ') {
                char pred;
                int len = sb.length();

                if (len == 0) {
                    len = mSpannableStringBuilder.length();

                    if (len == 0) {
                        pred = '\n';
                    } else {
                        pred = mSpannableStringBuilder.charAt(len - 1);
                    }
                } else {
                    pred = sb.charAt(len - 1);
                }

                ///[UPGRADE#android.text.Html#'\n'应该忽略！]
//                if (pred != ' ' && pred != '\n') {
                if (pred != ' ') {

                    sb.append(' ');
                }

                ///[UPGRADE#android.text.Html#'\n'应该忽略！]
//            } else {
            } else if (c != '\n') {

                sb.append(c);
            }
        }

        mSpannableStringBuilder.append(sb);
    }

    private void handleEndTag(String tag) {
        if (tag.equalsIgnoreCase("br")) {
            handleBr(mSpannableStringBuilder);

        } else if (tag.equalsIgnoreCase("p")) {
            endP(mSpannableStringBuilder);

        } else if (tag.equalsIgnoreCase("div")) {
            endDiv(mSpannableStringBuilder);

        } else if (tag.equalsIgnoreCase("ul")) {
            endUl(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("ol")) {
            endOl(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("li")) {
            endLi(mSpannableStringBuilder);

        } else if (tag.equalsIgnoreCase("blockquote")) {
            endBlockquote(mSpannableStringBuilder);

        } else if (tag.equalsIgnoreCase("pre")) {   ///[PreSpan]
            endPre(mSpannableStringBuilder);

        } else if (tag.length() == 2 &&
                Character.toLowerCase(tag.charAt(0)) == 'h' &&
                tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
            endHeading(mSpannableStringBuilder);

        } else if (tag.equalsIgnoreCase("hr")) {
            handleHr(mSpannableStringBuilder);

        } else if (tag.equalsIgnoreCase("span")) {
            endCssStyle(mSpannableStringBuilder);

        } else if (tag.equalsIgnoreCase("strong")
                || tag.equalsIgnoreCase("b")) {
            ///[UPGRADE#android.text.Html]
//            end(mSpannableStringBuilder, Bold.class, new StyleSpan(Typeface.BOLD));
            end(mSpannableStringBuilder, Bold.class, new BoldSpan());
        } else if (tag.equalsIgnoreCase("em")
                || tag.equalsIgnoreCase("cite")
                || tag.equalsIgnoreCase("dfn")
                || tag.equalsIgnoreCase("i")) {
            ///[UPGRADE#android.text.Html]
//            end(mSpannableStringBuilder, Italic.class, new StyleSpan(Typeface.ITALIC));
            end(mSpannableStringBuilder, Italic.class, new ItalicSpan());
        } else if (tag.equalsIgnoreCase("u")) {
            ///[UPGRADE#android.text.Html]
//            end(mSpannableStringBuilder, Underline.class, new UnderlineSpan());
            end(mSpannableStringBuilder, Underline.class, new CustomUnderlineSpan());
        } else if (tag.equalsIgnoreCase("del")
                || tag.equalsIgnoreCase("s")
                || tag.equalsIgnoreCase("strike")) {
            ///[UPGRADE#android.text.Html]
//            end(mSpannableStringBuilder, Strikethrough.class, new StrikethroughSpan());
            end(mSpannableStringBuilder, Strikethrough.class, new CustomStrikethroughSpan());
        } else if (tag.equalsIgnoreCase("sup")) {
            ///[UPGRADE#android.text.Html]
//            end(mSpannableStringBuilder, Super.class, new SuperscriptSpan());
            end(mSpannableStringBuilder, Super.class, new CustomSuperscriptSpan());
        } else if (tag.equalsIgnoreCase("sub")) {
            ///[UPGRADE#android.text.Html]
//            end(mSpannableStringBuilder, Sub.class, new SubscriptSpan());
            end(mSpannableStringBuilder, Sub.class, new CustomSubscriptSpan());
        } else if (tag.equalsIgnoreCase("code")) {
            end(mSpannableStringBuilder, Code.class, new CodeSpan());
        } else if (tag.equalsIgnoreCase("block")) {
            end(mSpannableStringBuilder, Block.class, new BlockSpan());
        } else if (tag.equalsIgnoreCase("tt")) {
            ///[UPGRADE#android.text.Html]
//            end(mSpannableStringBuilder, Monospace.class, new TypefaceSpan("monospace"));
            end(mSpannableStringBuilder, Monospace.class, new CustomFontFamilySpan("monospace"));
        } else if (tag.equalsIgnoreCase("font")) {
            endFont(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("big")) {
            ///[UPGRADE#android.text.Html]
//            end(mSpannableStringBuilder, Big.class, new RelativeSizeSpan(1.25f));
            end(mSpannableStringBuilder, Big.class, new CustomRelativeSizeSpan(1.25f));
        } else if (tag.equalsIgnoreCase("small")) {
            ///[UPGRADE#android.text.Html]
//            end(mSpannableStringBuilder, Small.class, new RelativeSizeSpan(0.8f));
            end(mSpannableStringBuilder, Small.class, new CustomRelativeSizeSpan(0.8f));

        } else if (tag.equalsIgnoreCase("a")) {
            endA(mSpannableStringBuilder);

        } else if (mTagHandler != null) {
            mTagHandler.handleTag(false, tag, mSpannableStringBuilder, mReader);
        }

        mLinkedList.removeLast();
    }


    /* ----------------------------------------------------------------------------------------- */
    private void handleBr(Editable text) {
        text.append('\n');
    }

    private void startP(Editable text, Attributes attributes) {
        startBlockCssStyle(text, attributes);
        startBlockElement(text, attributes);
        startCssStyle(text, attributes);
    }

    private void endP(Editable text) {
        endCssStyle(text);
        endBlockElement(text, null);

        final BlockCssStyle blockCssStyle = (BlockCssStyle) getLast(text, BlockCssStyle.class);
        endBlockCssStyle(text, blockCssStyle);
    }

    private void startDiv(Editable text, Attributes attributes) {
        if (startBlockCssStyle(text, attributes).styles.isEmpty()) {
            start(text, new Div());
        }

        startBlockElement(text, attributes);
        startCssStyle(text, attributes);
    }

    private void endDiv(Editable text) {
        endCssStyle(text);

        final BlockCssStyle blockCssStyle = (BlockCssStyle) getLast(text, BlockCssStyle.class);
        if (blockCssStyle == null || blockCssStyle.styles.isEmpty()) {
            endBlockElement(text, Div.class);

            final int nestingLevel = getNestingLevel(text, Div.class);
            end(text, Div.class, new DivSpan(nestingLevel));
        } else {
            for (Object obj : blockCssStyle.styles) {
                if (obj instanceof Alignment) {
                    endBlockElement(text, Alignment.class);
                } else if (obj instanceof LeadingMargin) {
                    endBlockElement(text, LeadingMargin.class);
                }
            }
        }

        endBlockCssStyle(text, blockCssStyle);
    }

    private void startUl(Editable text, Attributes attributes) {
        int listType = LIST_TYPE_UNORDERED_DISC;
        final String styles = attributes.getValue("", "style");
        if (styles != null) {
            for (String style : styles.split(";")) {
                final Matcher m = getListTypePattern().matcher(style);
                if (m.find()) {
                    String listTypeString = m.group(1).toLowerCase();
                    if ("circle".equals(listTypeString)) {
                        listType = LIST_TYPE_UNORDERED_CIRCLE;
                    } else if ("square".equals(listTypeString)) {
                        listType = LIST_TYPE_UNORDERED_SQUARE;
                    }
                }
            }
        }

        startBlockCssStyle(text, attributes);
        start(text, new UnOrderList(listType));
        startBlockElement(text, attributes);
        startCssStyle(text, attributes);
    }

    private void endUl(Editable text) {
        endCssStyle(text);
        endBlockElement(text, UnOrderList.class);

        final int nestingLevel = getNestingLevel(text, OrderUnOrderList.class);
        final UnOrderList unOrderList = (UnOrderList) getLast(text, UnOrderList.class);
        final ListSpan listSpan = new ListSpan(nestingLevel, unOrderList.getListType());
        end(text, UnOrderList.class, listSpan);

        final BlockCssStyle blockCssStyle = (BlockCssStyle) getLast(text, BlockCssStyle.class);
        endBlockCssStyle(text, blockCssStyle);

        ///[更新ListSpan]
        updateListSpans.add(listSpan);
    }

    private void startOl(Editable text, Attributes attributes) {
        int listType = LIST_TYPE_ORDERED_DECIMAL;
        final String styles = attributes.getValue("", "style");
        if (styles != null) {
            for (String style : styles.split(";")) {
                final Matcher m = getListTypePattern().matcher(style);
                if (m.find()) {
                    String listTypeString = m.group(1).toLowerCase();
                    if ("lower-alpha".equals(listTypeString)) {
                        listType = LIST_TYPE_ORDERED_LOWER_LATIN;
                    } else if ("upper-alpha".equals(listTypeString)) {
                        listType = LIST_TYPE_ORDERED_UPPER_LATIN;
                    } else if ("lower-roman".equals(listTypeString)) {
                        listType = LIST_TYPE_ORDERED_LOWER_ROMAN;
                    } else if ("upper-roman".equals(listTypeString)) {
                        listType = LIST_TYPE_ORDERED_UPPER_ROMAN;
                    }
                }
            }
        }

        int start = 1;
        final String startString = attributes.getValue("", "start");
        if (isInteger(startString)) {
            start = Integer.parseInt(startString);
        }

        final boolean isReversed = !TextUtils.isEmpty(attributes.getValue("", "reversed"));

        startBlockCssStyle(text, attributes);
        start(text, new OrderList(listType, start, isReversed));
        startBlockElement(text, attributes);
        startCssStyle(text, attributes);
    }

    private void endOl(Editable text) {
        endCssStyle(text);
        endBlockElement(text, OrderList.class);

        final int nestingLevel = getNestingLevel(text, OrderUnOrderList.class);
        final OrderList orderList = (OrderList) getLast(text, OrderList.class);
        final ListSpan listSpan = new ListSpan(nestingLevel, orderList.getListType(), orderList.mStart, orderList.isReversed);
        end(text, OrderList.class, listSpan);

        final BlockCssStyle blockCssStyle = (BlockCssStyle) getLast(text, BlockCssStyle.class);
        endBlockCssStyle(text, blockCssStyle);

        ///[更新ListSpan]
        updateListSpans.add(listSpan);
    }

    private void startLi(Editable text, Attributes attributes) {
        startBlockCssStyle(text, attributes);
        start(text, new ListItem());
        startBlockElement(text, attributes);
        startCssStyle(text, attributes);
    }

    private void endLi(Editable text) {
        endCssStyle(text);
        endBlockElement(text, ListItem.class);

        final int nestingLevel = getNestingLevel(text, OrderUnOrderList.class);
        final ListItemSpan listItemSpan = new ListItemSpan(null, 0);   ///注意：需要最后更新！
        listItemSpan.setNestingLevel(nestingLevel);
        end(text, ListItem.class, listItemSpan);

        final BlockCssStyle blockCssStyle = (BlockCssStyle) getLast(text, BlockCssStyle.class);
        endBlockCssStyle(text, blockCssStyle);
    }

    private void startBlockquote(Editable text, Attributes attributes) {
        startBlockCssStyle(text, attributes);
        start(text, new Blockquote());
        startBlockElement(text, attributes);
        startCssStyle(text, attributes);
    }

    private void endBlockquote(Editable text) {
        endCssStyle(text);
        endBlockElement(text, Blockquote.class);

        final int nestingLevel = getNestingLevel(text, Blockquote.class);
        end(text, Blockquote.class, new CustomQuoteSpan(nestingLevel));

        final BlockCssStyle blockCssStyle = (BlockCssStyle) getLast(text, BlockCssStyle.class);
        endBlockCssStyle(text, blockCssStyle);
    }

    private void startHeading(Editable text, Attributes attributes, int level) {
        startBlockElement(text, attributes);
        start(text, new Heading(level));
        startBlockCssStyle(text, attributes);
        startCssStyle(text, attributes);
    }

    private void endHeading(Editable text) {
        endCssStyle(text);
        endBlockElement(text, Heading.class);

        final Heading h = (Heading) getLast(text, Heading.class);
        if (h != null) {
            end(text, Heading.class, new HeadSpan(h.mLevel));
        }

        final BlockCssStyle blockCssStyle = (BlockCssStyle) getLast(text, BlockCssStyle.class);
        endBlockCssStyle(text, blockCssStyle);
    }

    ///[PreSpan]
    private void startPre(Editable text, Attributes attributes) {
        start(text, new Pre());
        startBlockElement(text, attributes);
    }
    ///[PreSpan]
    private void endPre(Editable text) {
        endBlockElement(text, Pre.class);

        final int nestingLevel = getNestingLevel(text, Pre.class);
        end(text, Pre.class, new PreSpan(nestingLevel));
    }

    private void handleHr(Editable text) {
        final int where = text.length();
        text.append('\n');
        setSpanFromMark(text, where, new LineDividerSpan());
    }


    private void startFont(Editable text, Attributes attributes) {
        String color = attributes.getValue("", "color");
        String face = attributes.getValue("", "face");

        ///[UPGRADE#android.text.Html#Font增加尺寸size（px、%）]
        ///https://blog.csdn.net/qq_36009027/article/details/84371825
        String size = attributes.getValue("", "size");
        if (!TextUtils.isEmpty(size)) {
            if (size.endsWith("px")) {
                size = size.split("px")[0];
                ///[UPGRADE#android.text.Html]px in CSS is the equivalance of dip in Android
                ///注意：一般情况下，CustomAbsoluteSizeSpan的dip都为true，否则需要在使用Html之前设置本机的具体准确的屏幕密度！
                start(text, new AbsoluteSize(Integer.parseInt(size), true));
            } else if (size.endsWith("%")) {
                size = size.split("%")[0];
                start(text, new RelativeSize(Float.parseFloat(size) / 100));
            } else if (size.endsWith("em")) {
                size = size.split("em")[0];
                start(text, new RelativeSize(Float.parseFloat(size)));
            } else {
                // todo ...
            }
        }

        if (!TextUtils.isEmpty(color)) {
            int c = getHtmlColor(color);
            if (c != -1) {
                start(text, new Foreground(c | 0xFF000000));
            }
        }

        if (!TextUtils.isEmpty(face)) {
            start(text, new Font(face));
        }
    }

    private void endFont(Editable text) {
        final Font font = (Font) getLast(text, Font.class);
        if (font != null) {
            end(text, Font.class, new CustomFontFamilySpan(font.mFace));
        }

        final Foreground foreground = (Foreground) getLast(text, Foreground.class);
        if (foreground != null) {
            end(text, Foreground.class,
                    new CustomForegroundColorSpan(foreground.mForegroundColor));
        }

        ///[UPGRADE#android.text.Html#Font增加尺寸size（px、%）]
        ///https://blog.csdn.net/qq_36009027/article/details/84371825
        final AbsoluteSize absoluteSize = (AbsoluteSize) getLast(text, AbsoluteSize.class);
        if (absoluteSize != null) {
            end(text, AbsoluteSize.class,
                    new CustomAbsoluteSizeSpan(absoluteSize.mSize, absoluteSize.mDip));
        } else {
            final RelativeSize relativeSize = (RelativeSize) getLast(text, RelativeSize.class);
            if (relativeSize != null) {
                end(text, RelativeSize.class,
                        new CustomRelativeSizeSpan(relativeSize.mProportion));
            } else {
                // todo ...
            }
        }
    }

    private void startA(Editable text, Attributes attributes) {
        String href = attributes.getValue("", "href");
        start(text, new Href(href));
    }

    private void endA(Editable text) {
        Href h = (Href) getLast(text, Href.class);
        if (h != null) {
            if (h.mHref != null) {
                final int where = mLinkedList.getLast().where;
                setSpanFromMark(text, where, new CustomURLSpan(h.mHref));
            }
        }
    }

    private void startMedia(Editable text, Attributes attributes, Html.ImageGetter imageGetter, String type) {
        String source = attributes.getValue("", "img".equals(type) ? "src" : "img");
        if (TextUtils.isEmpty(source)) {
            source = "";
        }

        String uri = "img".equals(type) ? "" : attributes.getValue("",  "src");

        Drawable d = null;

        if (imageGetter != null) {
            d = imageGetter.getDrawable(source);
        }

        if (d == null) {
            d = Resources.getSystem().
                    ///[UPGRADE#android.text.Html]Resources.getSystem() can only support system resources!
//                    getDrawable(com.android.internal.R.drawable.unknown_image);
//                    getDrawable(android.R.drawable.gallery_thumb);
                    getDrawable(android.R.drawable.picture_frame);

            int width;
            String widthString = attributes.getValue("", "width");
            if (isInteger(widthString)) {
                width = Integer.parseInt(widthString);
            } else {
                width = d.getIntrinsicWidth();
            }

            int height;
            String heightString = attributes.getValue("", "height");
            if (isInteger(heightString)) {
                height = Integer.parseInt(heightString);
            } else {
                height = d.getIntrinsicHeight();
            }

//            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            d.setBounds(0, 0, width, height);
        }

        int len = text.length();
        CustomImageSpan span;
        if ("video".equals(type)) {
            span = new VideoSpan(d, uri, source);
        } else if ("audio".equals(type)) {
            span = new AudioSpan(d, uri, source);
        } else {
            span = new CustomImageSpan(d, source);
        }

        ///[UPGRADE#android.text.Html]把width\height\align保存到text中
//        text.append("\uFFFC");
//        text.append(String.format(Resources.getSystem().getString(R.string.image_span_text), src, ///Note: Resources.getSystem() can only support system resources!
        if ("img".equals(type)) {
            text.append(String.format("[img src=\"%1$s\" width=%2$d height=%3$d align=%4$d]", source,
                    span.getDrawableWidth(), span.getDrawableHeight(), span.getVerticalAlignment()));
        } else {
            text.append(String.format("[media uri=\"%1$s\" src=\"%2$s\" width=%3$d height=%4$d align=%5$d]", uri, source,
                    span.getDrawableWidth(), span.getDrawableHeight(), span.getVerticalAlignment()));
        }

        text.setSpan(span, len, text.length(), getSpanFlags(span.getClass()));
    }


    /* ---------------------------------------------------------------------------------- */
    private void startBlockElement(Editable text, Attributes attributes) {

    }

    private void endBlockElement(Editable text, Class kind) {
        int len = text.length();
        if (kind == null || len == 0 || text.charAt(len - 1) != '\n') {
            text.append('\n');
        } else {
            ///注意：当kind不为null、且是嵌套而不是并列时（即其start大于等于text.length()），忽略添加'\n'
            Object obj = getLast(text, kind);
//            if (obj == null || text.getSpanStart(obj) >= len) {
            if (obj == null || mLinkedList.getLast().where >= len) {
                text.append('\n');
            }
        }
    }

    private BlockCssStyle startBlockCssStyle(Editable text, Attributes attributes) {
        final BlockCssStyle blockCssStyle = new BlockCssStyle();
        final String styles = attributes.getValue("", "style");
        if (styles != null) {
            start(text, blockCssStyle);
            for (String style : styles.split(";")) {
                Matcher m = getLeadingMarginPattern().matcher(style);
                if (m.find()) {
                    int indent = 0;
                    String indentString = m.group(1).toLowerCase();
                    if (indentString.endsWith("px")) {
                        String i = indentString.substring(0, indentString.length() - 2);
                        if (isInteger(i)) {
                            indent = Integer.parseInt(i);
                        }
                    } else {
                        // todo ...
                    }
                    blockCssStyle.styles.add(new LeadingMargin(indent));

                    continue;
                }

                m = getTextAlignPattern().matcher(style);
                if (m.find()) {
                    String alignment = m.group(1);
                    if (alignment.equalsIgnoreCase("center")) {
                        blockCssStyle.styles.add(new Alignment(Layout.Alignment.ALIGN_CENTER));
                    } else if (alignment.equalsIgnoreCase("end")) {
                        blockCssStyle.styles.add(new Alignment(Layout.Alignment.ALIGN_OPPOSITE));
                    } else {
                        blockCssStyle.styles.add(new Alignment(Layout.Alignment.ALIGN_NORMAL));
                    }

                    continue;
                }
            }
        }
        return blockCssStyle;
    }

    private void endBlockCssStyle(Editable text, BlockCssStyle blockCssStyle) {
        if (blockCssStyle != null) {
            final int where = mLinkedList.getLast().where;
            for (Object obj : blockCssStyle.styles) {
                if (obj instanceof Alignment) {
                    final int nestingLevel = getNestingLevel(text, Alignment.class);
                    if (((Alignment) obj).mAlignment == Layout.Alignment.ALIGN_CENTER) {
                        setSpanFromMark(text, where, new AlignCenterSpan(nestingLevel));
                    } else if (((Alignment) obj).mAlignment == Layout.Alignment.ALIGN_OPPOSITE) {
                        setSpanFromMark(text, where, new AlignOppositeSpan(nestingLevel));
                    } else {
                        setSpanFromMark(text, where, new AlignNormalSpan(nestingLevel));
                    }
                } else if (obj instanceof LeadingMargin) {
                    final int nestingLevel = getNestingLevel(text, LeadingMargin.class);
                    setSpanFromMark(text, where, new CustomLeadingMarginSpan(nestingLevel, ((LeadingMargin) obj).mIndent));
                }
            }
        }
    }

    private CssStyle startCssStyle(Editable text, Attributes attributes) {
        final CssStyle cssStyle = new CssStyle();
        final String styles = attributes.getValue("", "style");
        if (styles != null) {
            start(text, cssStyle);
            for (String style : styles.split(";")) {
                Matcher m = getForegroundColorPattern().matcher(style);
                if (m.find()) {
                    int c = getHtmlColor(m.group(1));
                    if (c != -1) {
                        cssStyle.styles.add(new Foreground(c | 0xFF000000));
                    }

                    continue;
                }

                m = getBackgroundColorPattern().matcher(style);
                if (m.find()) {
                    int c = getHtmlColor(m.group(1));
                    if (c != -1) {
                        cssStyle.styles.add(new Background(c | 0xFF000000));
                    }

                    continue;
                }

                m = getTextDecorationPattern().matcher(style);
                if (m.find()) {
                    String textDecoration = m.group(1);
                    if (textDecoration.equalsIgnoreCase("line-through")) {
                        cssStyle.styles.add(new Strikethrough());
                    }

                    continue;
                }

                m = getFontSizePattern().matcher(style);
                if (m.find()) {
                    String size = m.group(1);
                    if (size.endsWith("px")) {
                        size = size.split("px")[0];
                        cssStyle.styles.add(new AbsoluteSize(Integer.parseInt(size), true));
                    } else if (size.endsWith("%")) {
                        size = size.split("%")[0];
                        cssStyle.styles.add(new RelativeSize(Float.parseFloat(size) / 100));
                    } else if (size.endsWith("em")) {
                        size = size.split("em")[0];
                        cssStyle.styles.add(new RelativeSize(Float.parseFloat(size)));
                    } else {
                        // todo ...
                    }

                    continue;
                }
            }
        }
        return cssStyle;
    }

    private CssStyle endCssStyle(Editable text) {
        final CssStyle cssStyle = (CssStyle) getLast(text, CssStyle.class);
        if (cssStyle != null) {
//            final int where = text.getSpanStart(cssStyle);
            final int where = mLinkedList.getLast().where;
//            text.removeSpan(cssStyle);
            for (Object obj : cssStyle.styles) {
                if (obj instanceof Foreground) {
                    setSpanFromMark(text, where, new CustomForegroundColorSpan(((Foreground) obj).mForegroundColor));
                } else if (obj instanceof Background) {
                    setSpanFromMark(text, where, new CustomBackgroundColorSpan(((Background) obj).mBackgroundColor));
                } else if (obj instanceof Strikethrough) {
                    setSpanFromMark(text, where, new CustomStrikethroughSpan());
                } else if (obj instanceof AbsoluteSize) {
                    setSpanFromMark(text, where, new CustomAbsoluteSizeSpan(((AbsoluteSize) obj).mSize, ((AbsoluteSize) obj).mDip));
                } else if (obj instanceof RelativeSize) {
                    setSpanFromMark(text, where, new CustomRelativeSizeSpan(((RelativeSize) obj).mProportion));
                }
            }
        }
        return cssStyle;
    }


    /* ---------------------------------------------------------------------------------- */
    private LinkedList<LL> mLinkedList = new LinkedList<>();
    class LL {
        int where;
        ArrayList<Object> objList = new ArrayList<>();

        public LL(int where) {
            this.where = where;
        }
    }

    private void start(Editable text, Object mark) {
        mLinkedList.getLast().objList.add(mark);
    }

    private void end(Editable text, Class kind, Object repl) {
        Object obj = getLast(text, kind);
        if (obj != null) {
            mLinkedList.getLast().objList.remove(obj);

            final int where = mLinkedList.getLast().where;
            setSpanFromMark(text, where, repl);
        }
    }

    private void setSpanFromMark(Spannable text, int where, Object... spans) {
        int len = text.length();
        if (where != len) {
            for (Object span : spans) {
                ///[FIX#如果SPAN_INCLUSIVE_INCLUSIVE或SPAN_EXCLUSIVE_INCLUSIVE，
                ///则需要暂时改为SPAN_EXCLUSIVE_EXCLUSIVE，将来再改回来，否则在添加内容后自动延长span的范围]
                if (getSpanFlags(span.getClass()) == Spanned.SPAN_INCLUSIVE_INCLUSIVE
                        || getSpanFlags(span.getClass()) == Spanned.SPAN_EXCLUSIVE_INCLUSIVE) {
                    text.setSpan(span, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    text.setSpan(span, where, len, getSpanFlags(span.getClass()));
                }
            }
        }
    }

    private Object getLast(Spanned text, Class kind) {
        for (Object obj : mLinkedList.getLast().objList) {
            if (kind == OrderUnOrderList.class) {
                if (obj.getClass() == UnOrderList.class || obj.getClass() == OrderList.class) {
                    return obj;
                }
            } else if (kind == BlockCssStyle.class || kind == CssStyle.class) {
                if (kind == obj.getClass()) {
                    return obj;
                }
            } else if (obj instanceof BlockCssStyle) {
                for (Object styleObject : ((BlockCssStyle) obj).styles) {
                    if (kind == styleObject.getClass()) {
                        return obj;
                    }
                }
            } else if (obj instanceof CssStyle) {
                for (Object styleObject : ((CssStyle) obj).styles) {
                    if (kind == styleObject.getClass()) {
                        return obj;
                    }
                }
            } else if (kind == obj.getClass()) {
                return obj;
            }
        }
        return null;
    }

    private int getNestingLevel(Spanned text, Class kind) {
        int result = 0;
        for (LL ll : mLinkedList) {
            for (Object obj : ll.objList) {
                if (kind == OrderUnOrderList.class) {
                    if (obj.getClass() == UnOrderList.class || obj.getClass() == OrderList.class) {
                        result++;
                    }
                } else if (kind == BlockCssStyle.class || kind == CssStyle.class) {
                    if (kind == obj.getClass()) {
                        result++;
                    }
                } else if (obj instanceof BlockCssStyle) {
                    for (Object styleObject : ((BlockCssStyle) obj).styles) {
                        if (kind == styleObject.getClass()) {
                            result++;
                        }
                    }
                } else if (obj instanceof CssStyle) {
                    for (Object styleObject : ((CssStyle) obj).styles) {
                        if (kind == styleObject.getClass()) {
                            result++;
                        }
                    }
                } else if (kind == obj.getClass()) {
                    result++;
                }
            }
        }
        return result;
    }

    /* ---------------------------------------------------------------------------------- */
    ///[UPGRADE#android.text.Html#CssStyle]
    private class BlockCssStyle {
        ArrayList<Object> styles = new ArrayList<>();
    }
    private class CssStyle {
        ArrayList<Object> styles = new ArrayList<>();
    }

    private class LeadingMargin {
        public int mIndent;

        public LeadingMargin(int indent) {
            mIndent = indent;
        }
    }
    private class Alignment {
        private Layout.Alignment mAlignment;

        public Alignment(Layout.Alignment alignment) {
            mAlignment = alignment;
        }
    }
    private class Foreground {
        private int mForegroundColor;

        public Foreground(int foregroundColor) {
            mForegroundColor = foregroundColor;
        }
    }
    private class Background {
        private int mBackgroundColor;

        public Background(int backgroundColor) {
            mBackgroundColor = backgroundColor;
        }
    }

    private class Div { }
    private class OrderUnOrderList {
        private int mListType;

        public int getListType() {
            return mListType;
        }

        public void setListType(int listType) {
            mListType = listType;
        }

        public OrderUnOrderList(int listType) {
            mListType = listType;
        }
    }
    private class UnOrderList extends OrderUnOrderList {
        public UnOrderList(int listType) {
            super(listType);
        }
    }
    private class OrderList extends OrderUnOrderList {
        private int mStart;
        private boolean isReversed;

        public OrderList(int listType, int start, boolean isReversed) {
            super(listType);
            this.mStart = start;
            this.isReversed = isReversed;
        }
    }
    private class ListItem { }
    private class Blockquote { }

    private class Heading {
        private int mLevel;

        public Heading(int level) {
            mLevel = level;
        }
    }
    private class Pre { }   ///[PreSpan]

    private class Bold { }
    private class Italic { }
    private class Underline { }
    private class Strikethrough { }
    private class Super { }
    private class Sub { }
    private class Code { }
    private class Block { }
    private class Href {
        public String mHref;

        public Href(String href) {
            mHref = href;
        }
    }
    private class Font {
        public String mFace;

        public Font(String face) {
            mFace = face;
        }
    }
    private class Monospace { }
    private class Big { }
    private class Small { }
    ///[UPGRADE#android.text.Html#Font增加尺寸size（px、%）]
    ///https://blog.csdn.net/qq_36009027/article/details/84371825
    private class AbsoluteSize {
        private int mSize;
        private boolean mDip;

        public AbsoluteSize(int size) {
            this(size, false);
        }
        public AbsoluteSize(int size, boolean dip) {
            mSize = size;
            mDip = dip;
        }
    }
    private class RelativeSize {
        private float mProportion;

        public RelativeSize(float proportion) {
            mProportion = proportion;
        }
    }

}
