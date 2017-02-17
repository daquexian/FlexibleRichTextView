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
public class FramedBox extends Box {

	protected Box box;
	protected float thickness;
	protected float space;
	private Integer line;
	private Integer bg;

	public FramedBox(Box box, float thickness, float space) {
		this.box = box;
		this.width = box.width + 2 * thickness + 2 * space;
		this.height = box.height + thickness + space;
		this.depth = box.depth + thickness + space;
		this.shift = box.shift;
		this.thickness = thickness;
		this.space = space;
	}

	public FramedBox(Box box, float thickness, float space, Integer line,
			Integer bg) {
		this(box, thickness, space);
		this.line = line;
		this.bg = bg;
	}

	public void draw(Canvas g2, float x, float y) {
		Paint st = AjLatexMath.getPaint();
		float w = st.getStrokeWidth();
		Style s = st.getStyle();
		int c = st.getColor();
		st.setStrokeWidth(thickness);
		st.setStyle(Style.FILL_AND_STROKE);
		float th = thickness / 2;
		if (bg != null) {
			st.setColor(bg);
			g2.drawRect(x + th, y - height + th, x + th + width - thickness, y
					+ th + depth - thickness, st);
		}
		st.setStyle(Style.STROKE);
		if (line != null) {
			st.setColor(line);
			g2.drawRect(x + th, y - height + th, x + th + width - thickness, y
					+ th + depth - thickness, st);
		} else {
			g2.drawRect(x + th, y - height + th, x + th + width - thickness, y
					+ th + depth - thickness, st);
		}
		// drawDebug(g2, x, y);
		st.setStrokeWidth(w);
		st.setStyle(s);
		box.draw(g2, x + space + thickness, y);
		// st.setStyle(s);
		st.setColor(c);
	}

	public int getLastFontId() {
		return box.getLastFontId();
	}
}
