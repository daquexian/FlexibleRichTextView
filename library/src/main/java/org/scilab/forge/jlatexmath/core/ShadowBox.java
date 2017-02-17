/* FramedBox.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 * 
 * Copyright (C) 2009 DENIZET Calixte
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
 * A box representing a rotated box.
 */
public class ShadowBox extends FramedBox {

	private float shadowRule;

	public ShadowBox(FramedBox fbox, float shadowRule) {
		super(fbox.box, fbox.thickness, fbox.space);
		this.shadowRule = shadowRule;
		depth += shadowRule;
		width += shadowRule;
	}

	public void draw(Canvas g2, float x, float y) {
		float th = thickness / 2;
		box.draw(g2, x + space + thickness, y);
		Paint st = AjLatexMath.getPaint();
		float w = st.getStrokeWidth();
		int c = st.getColor();
		Style s = st.getStyle();
		st.setStrokeWidth(thickness);
		st.setStyle(Style.STROKE);
		float penth = 0;// (float) Math.abs(1 / g2.getTransform().getScaleX());
		g2.drawRect(x + th, y - height + th, x + th + width - shadowRule
				- thickness, y + th + depth - shadowRule - thickness, st);
		st.setStyle(Style.FILL);
		g2.drawRect(x + shadowRule - penth, y + depth - shadowRule - penth, x
				- penth + width, y + depth - penth, st);
		g2.drawRect(x + width - shadowRule - penth, y - height + th
				+ shadowRule, x + width - penth, y + shadowRule + depth - 2
				* shadowRule, st);

		st.setColor(c);
		st.setStrokeWidth(w);
		st.setStyle(s);
		st.clearShadowLayer();

	}

	public int getLastFontId() {
		return box.getLastFontId();
	}
}
/*
 * 
 * public void draw(Graphics2D g2, float x, float y) { float th = thickness / 2;
 * float sh = shadowRule / 2; box.draw(g2, x + space + thickness, y); Stroke st
 * = g2.getStroke(); g2.setStroke(new BasicStroke(shadowRule,
 * BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER)); g2.draw(new Line2D.Float(x +
 * shadowRule, y + depth - sh, x + width, y + depth - sh)); g2.draw(new
 * Line2D.Float(x + width - sh, y - height + shadowRule, x + width - sh, y +
 * depth - shadowRule)); g2.setStroke(new BasicStroke(thickness,
 * BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER)); g2.draw(new
 * Rectangle2D.Float(x + th, y - height + th, width - shadowRule - thickness,
 * height + depth - shadowRule - thickness)); //drawDebug(g2, x, y);
 * g2.setStroke(st); }
 */