package com.daquexian.flexiblerichtextview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;

public class CenteredImageSpan extends ImageSpan
{
	public CenteredImageSpan(Bitmap b)
	{
		super(b);
	}

	public CenteredImageSpan(Bitmap b, int verticalAlignment)
	{
		super(b, verticalAlignment);
	}

	public CenteredImageSpan(Context context, Bitmap b)
	{
		super(context, b);
	}

	public CenteredImageSpan(Context context, Bitmap b, int verticalAlignment)
	{
		super(context, b, verticalAlignment);
	}

	public CenteredImageSpan(Drawable d)
	{
		super(d);
	}

	public CenteredImageSpan(Drawable d, int verticalAlignment)
	{
		super(d, verticalAlignment);
	}

	public CenteredImageSpan(Drawable d, String source)
	{
		super(d, source);
	}

	public CenteredImageSpan(Drawable d, String source, int verticalAlignment)
	{
		super(d, source, verticalAlignment);
	}

	public CenteredImageSpan(Context context, Uri uri)
	{
		super(context, uri);
	}

	public CenteredImageSpan(Context context, Uri uri, int verticalAlignment)
	{
		super(context, uri, verticalAlignment);
	}

	public CenteredImageSpan(Context context, int resourceId)
	{
		super(context, resourceId);
	}

	public CenteredImageSpan(Context context, int resourceId, int verticalAlignment)
	{
		super(context, resourceId, verticalAlignment);
	}

	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint)
	{
		Drawable d = getDrawable();
		Paint.FontMetricsInt fm = paint.getFontMetricsInt();
		int transY = y + (fm.descent + fm.ascent - d.getBounds().bottom) / 2;
		canvas.save();
		canvas.translate(x, transY);
		d.draw(canvas);
		canvas.restore();
	}

	public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
		Drawable d = getDrawable();
		Rect rect = d.getBounds();
		if (fm != null) {
			Paint.FontMetricsInt fmPaint=paint.getFontMetricsInt();
			int fontHeight = fmPaint.bottom - fmPaint.top;
			int drHeight=rect.bottom-rect.top;
			int top= drHeight/2 - fontHeight/4;
			int bottom=drHeight/2 + fontHeight/4;

			fm.ascent=-bottom;
			fm.top=-bottom;
			fm.bottom=top;
			fm.descent=top;
		}

		return rect.right;
	}
}
