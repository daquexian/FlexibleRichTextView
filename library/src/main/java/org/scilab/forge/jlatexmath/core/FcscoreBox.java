/* FcscoreBox.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2013 DENIZET Calixte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 */

package org.scilab.forge.jlatexmath.core;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

/**
 * A box representing glue.
 */
public class FcscoreBox extends Box {

	private int N;
	private boolean strike;
	private float space;
	private float thickness;

	public FcscoreBox(int N, float h, float thickness, float space,
			boolean strike) {
		this.N = N;
		this.width = N * (thickness + space) + 2 * space;
		this.height = h;
		this.depth = 0;
		this.strike = strike;
		this.space = space;
		this.thickness = thickness;
	}

	public void draw(Canvas g2, float x, float y) {
		Paint st = AjLatexMath.getPaint();
		/*
		 * AffineTransform transf = g2.getTransform(); Stroke oldStroke =
		 * g2.getStroke();
		 * 
		 * final double sx = transf.getScaleX(); final double sy =
		 * transf.getScaleY();
		 */
		float s = 1;
		/*
		 * if (sx == sy) { // There are rounding problems due to scale factor:
		 * lines could have different // spacing... // So the increment
		 * (space+thickness) is done in using integer. s = sx; AffineTransform t
		 * = (AffineTransform) transf.clone(); t.scale(1 / sx, 1 / sy);
		 * g2.setTransform(t); }
		 */
		float w = st.getStrokeWidth();
		Style ss = st.getStyle();
		st.setStrokeWidth((float) (s * thickness));
		st.setStyle(Style.STROKE);
		float th = thickness / 2.f;
		float xx = x + space;
		xx = (float) (xx * s + (space / 2.f) * s);
		final int inc = (int) Math.round((space + thickness) * s);

		for (int i = 0; i < N; i++) {
			g2.drawLine(xx + th * s, (y - height) * s, xx + th * s, y * s, st);
			xx += inc;
		}

		if (strike) {
			g2.drawLine((x + space) * s, (y - height / 2.f) * s, xx - s * space
					/ 2, (y - height / 2.f) * s, st);
		}

		// g2.setTransform(transf);
		// g2.setStroke(oldStroke);
		st.setStrokeWidth(w);
		st.setStyle(ss);
	}

	public int getLastFontId() {
		return TeXFont.NO_FONT;
	}
}
