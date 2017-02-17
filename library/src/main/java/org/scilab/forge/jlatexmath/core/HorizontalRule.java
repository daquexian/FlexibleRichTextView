/* HorizontalRule.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 * 
 * Copyright (C) 2004-2007 Universiteit Gent
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
 * A box representing a horizontal line.
 */
public class HorizontalRule extends Box {
    
    private Integer color = null;
    private float speShift = 0;;
    
    public HorizontalRule(float thickness, float width, float s) {
	height = thickness;
	this.width = width;
	shift = s;
    }

    public HorizontalRule(float thickness, float width, float s, boolean trueShift) {
	height = thickness;
	this.width = width;
	if (trueShift) {
	    shift = s;
	} else {
	    shift = 0;
	    speShift = s;
	}	
    }

    public HorizontalRule(float thickness, float width, float s, Integer c) {
	height = thickness;
	this.width = width;
	color = c;
	shift = s;
    }

    public void draw(Canvas g2, float x, float y) {
	Paint st = AjLatexMath.getPaint();
	Style s = st.getStyle();
	float w = st.getStrokeWidth();
	st.setStyle(Style.FILL);
	st.setStrokeWidth(0);
	int c = st.getColor();
	if (color != null)
	    st.setColor(color);
	if (speShift == 0) {
		g2.drawRect(x, y - height, x + width, y, st);
	} else {
	    g2.drawRect(x, y - height + speShift, x + width, y + speShift, st);
	}
		st.setColor(c);
		st.setStyle(s);
	st.setStrokeWidth(w);
    }
    
    public int getLastFontId() {
	return TeXFont.NO_FONT;
    }
}
