/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * aapt tool from the resource data it found.  It
 * should not be modified by hand.
 */

package com.handmark.pulltorefresh.library;

public final class R {
    public static final class anim {
        public static int slide_in_from_bottom=0x7f040000;
        public static int slide_in_from_top=0x7f040001;
        public static int slide_out_to_bottom=0x7f040002;
        public static int slide_out_to_top=0x7f040003;
    }
    public static final class attr {
        /** <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static int ptrAdapterViewBackground=0x7f010000;
        /** <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
         */
        public static int ptrDrawable=0x7f010006;
        /** <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static int ptrHeaderBackground=0x7f010001;
        /** <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static int ptrHeaderSubTextColor=0x7f010003;
        /** <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static int ptrHeaderTextColor=0x7f010002;
        /** <p>Must be one or more (separated by '|') of the following constant values.</p>
<table>
<colgroup align="left" />
<colgroup align="left" />
<colgroup align="left" />
<tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td><code>pullDownFromTop</code></td><td>0x1</td><td></td></tr>
<tr><td><code>pullUpFromBottom</code></td><td>0x2</td><td></td></tr>
<tr><td><code>both</code></td><td>0x3</td><td></td></tr>
</table>
         */
        public static int ptrMode=0x7f010004;
        /** <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a boolean value, either "<code>true</code>" or "<code>false</code>".
         */
        public static int ptrShowIndicator=0x7f010005;
    }
    public static final class dimen {
        public static int indicator_corner_radius=0x7f060001;
        public static int indicator_internal_padding=0x7f060002;
        public static int indicator_right_padding=0x7f060000;
    }
    public static final class drawable {
        public static int arrow_down=0x7f020000;
        public static int arrow_up=0x7f020001;
        public static int default_ptr_drawable=0x7f020002;
        public static int indicator_bg_bottom=0x7f020003;
        public static int indicator_bg_top=0x7f020004;
    }
    public static final class id {
        public static int both=0x7f050002;
        public static int gridview=0x7f050003;
        public static int pullDownFromTop=0x7f050000;
        public static int pullUpFromBottom=0x7f050001;
        public static int pull_to_refresh_image=0x7f050007;
        public static int pull_to_refresh_sub_text=0x7f050006;
        public static int pull_to_refresh_text=0x7f050005;
        public static int webview=0x7f050004;
    }
    public static final class layout {
        public static int pull_to_refresh_header=0x7f030000;
    }
    public static final class string {
        public static int pull_to_refresh_pull_label=0x7f070000;
        public static int pull_to_refresh_refreshing_label=0x7f070002;
        public static int pull_to_refresh_release_label=0x7f070001;
    }
    public static final class styleable {
        /** Attributes that can be used with a PullToRefresh.
           <p>Includes the following attributes:</p>
           <table>
           <colgroup align="left" />
           <colgroup align="left" />
           <tr><th>Attribute</th><th>Description</th></tr>
           <tr><td><code>{@link #PullToRefresh_ptrAdapterViewBackground com.handmark.pulltorefresh.library:ptrAdapterViewBackground}</code></td><td></td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrDrawable com.handmark.pulltorefresh.library:ptrDrawable}</code></td><td></td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrHeaderBackground com.handmark.pulltorefresh.library:ptrHeaderBackground}</code></td><td></td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrHeaderSubTextColor com.handmark.pulltorefresh.library:ptrHeaderSubTextColor}</code></td><td></td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrHeaderTextColor com.handmark.pulltorefresh.library:ptrHeaderTextColor}</code></td><td></td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrMode com.handmark.pulltorefresh.library:ptrMode}</code></td><td></td></tr>
           <tr><td><code>{@link #PullToRefresh_ptrShowIndicator com.handmark.pulltorefresh.library:ptrShowIndicator}</code></td><td></td></tr>
           </table>
           @see #PullToRefresh_ptrAdapterViewBackground
           @see #PullToRefresh_ptrDrawable
           @see #PullToRefresh_ptrHeaderBackground
           @see #PullToRefresh_ptrHeaderSubTextColor
           @see #PullToRefresh_ptrHeaderTextColor
           @see #PullToRefresh_ptrMode
           @see #PullToRefresh_ptrShowIndicator
         */
        public static final int[] PullToRefresh = {
            0x7f010000, 0x7f010001, 0x7f010002, 0x7f010003,
            0x7f010004, 0x7f010005, 0x7f010006
        };
        /**
          <p>This symbol is the offset where the {@link com.handmark.pulltorefresh.library.R.attr#ptrAdapterViewBackground}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          @attr name android:ptrAdapterViewBackground
        */
        public static final int PullToRefresh_ptrAdapterViewBackground = 0;
        /**
          <p>This symbol is the offset where the {@link com.handmark.pulltorefresh.library.R.attr#ptrDrawable}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>Must be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
          @attr name android:ptrDrawable
        */
        public static final int PullToRefresh_ptrDrawable = 6;
        /**
          <p>This symbol is the offset where the {@link com.handmark.pulltorefresh.library.R.attr#ptrHeaderBackground}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          @attr name android:ptrHeaderBackground
        */
        public static final int PullToRefresh_ptrHeaderBackground = 1;
        /**
          <p>This symbol is the offset where the {@link com.handmark.pulltorefresh.library.R.attr#ptrHeaderSubTextColor}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          @attr name android:ptrHeaderSubTextColor
        */
        public static final int PullToRefresh_ptrHeaderSubTextColor = 3;
        /**
          <p>This symbol is the offset where the {@link com.handmark.pulltorefresh.library.R.attr#ptrHeaderTextColor}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          @attr name android:ptrHeaderTextColor
        */
        public static final int PullToRefresh_ptrHeaderTextColor = 2;
        /**
          <p>This symbol is the offset where the {@link com.handmark.pulltorefresh.library.R.attr#ptrMode}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>Must be one or more (separated by '|') of the following constant values.</p>
<table>
<colgroup align="left" />
<colgroup align="left" />
<colgroup align="left" />
<tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td><code>pullDownFromTop</code></td><td>0x1</td><td></td></tr>
<tr><td><code>pullUpFromBottom</code></td><td>0x2</td><td></td></tr>
<tr><td><code>both</code></td><td>0x3</td><td></td></tr>
</table>
          @attr name android:ptrMode
        */
        public static final int PullToRefresh_ptrMode = 4;
        /**
          <p>This symbol is the offset where the {@link com.handmark.pulltorefresh.library.R.attr#ptrShowIndicator}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a boolean value, either "<code>true</code>" or "<code>false</code>".
          @attr name android:ptrShowIndicator
        */
        public static final int PullToRefresh_ptrShowIndicator = 5;
    };
}
