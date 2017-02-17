/* JLaTeXMathCache.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/p/jlatexmath
 *
 * Copyright (C) 2010 DENIZET Calixte
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

package org.scilab.forge.jlatexmath.cache;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;

import org.scilab.forge.jlatexmath.core.Insets;
import org.scilab.forge.jlatexmath.core.ParseException;
import org.scilab.forge.jlatexmath.core.TeXFormula;
import org.scilab.forge.jlatexmath.core.TeXIcon;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Class to cache generated image from formulas
 * 
 * @author Calixte DENIZET
 */
public final class JLaTeXMathCache {
	private static Matrix matrix = new Matrix();
	private static ConcurrentMap<CachedTeXFormula, SoftReference<CachedImage>> cache = new ConcurrentHashMap<CachedTeXFormula, SoftReference<CachedImage>>(
			128);
	private static int max = Integer.MAX_VALUE;
	private static ReferenceQueue queue = new ReferenceQueue();

	private JLaTeXMathCache() {
	}

	/**
	 * Set max size. Take care the cache will be reinitialized
	 * 
	 * @param max
	 *            the max size
	 */
	public static void setMaxCachedObjects(int max) {
		JLaTeXMathCache.max = Math.max(max, 1);
		cache.clear();
		cache = new ConcurrentHashMap<CachedTeXFormula, SoftReference<CachedImage>>(
				JLaTeXMathCache.max);
	}

	/**
	 * @param f
	 *            a formula
	 * @param style
	 *            a style like TeXConstants.STYLE_DISPLAY
	 * @param size
	 *            the size of font
	 * @param inset
	 *            the inset to add on the top, bottom, left and right
	 * @return an array of length 3 containing width, height and depth
	 */
	public static int[] getCachedTeXFormulaDimensions(String f, int style,
			int type, int size, int inset, Integer fgcolor)
			throws ParseException {
		return getCachedTeXFormulaDimensions(new CachedTeXFormula(f, style,
				type, size, inset, fgcolor));
	}

	public static int[] getCachedTeXFormulaDimensions(String f, int style,
			int size, int inset) throws ParseException {
		return getCachedTeXFormulaDimensions(f, style, 0, size, inset, null);
	}

	/**
	 * @param o
	 *            an Object to identify the image in the cache
	 * @return an array of length 3 containing width, height and depth
	 */
	public static int[] getCachedTeXFormulaDimensions(Object o)
			throws ParseException {
		if (o == null || !(o instanceof CachedTeXFormula)) {
			return new int[] { 0, 0, 0 };
		}
		CachedTeXFormula cached = (CachedTeXFormula) o;
		SoftReference<CachedImage> img = cache.get(cached);
		if (img == null || img.get() == null) {
			img = makeImage(cached);
		}

		return new int[] { cached.width, cached.height, cached.depth };
	}

	/**
	 * Get a cached formula
	 * 
	 * @param f
	 *            a formula
	 * @param style
	 *            a style like TeXConstants.STYLE_DISPLAY
	 * @param size
	 *            the size of font
	 * @param inset
	 *            the inset to add on the top, bottom, left and right
	 * @return the key in the map
	 */
	public static Object getCachedTeXFormula(String f, int style, int type,
			int size, int inset, Integer fgcolor) throws ParseException {
		CachedTeXFormula cached = new CachedTeXFormula(f, style, type, size,
				inset, fgcolor);
		SoftReference<CachedImage> img = cache.get(cached);
		if (img == null || img.get() == null) {
			img = makeImage(cached);
		}

		return cached;
	}

	public static Object getCachedTeXFormula(String f, int style, int size,
			int inset) throws ParseException {
		return getCachedTeXFormula(f, style, 0, size, inset, null);
	}

	/**
	 * Clear the cache
	 */
	public static void clearCache() {
		cache.clear();
	}

	/**
	 * Remove a formula from the cache
	 * 
	 * @param f
	 *            a formula
	 * @param style
	 *            a style like TeXConstants.STYLE_DISPLAY
	 * @param size
	 *            the size of font
	 * @param inset
	 *            the inset to add on the top, bottom, left and right
	 */
	public static void removeCachedTeXFormula(String f, int style, int type,
			int size, int inset, Integer fgcolor) throws ParseException {
		cache.remove(new CachedTeXFormula(f, style, type, size, inset, fgcolor));
	}

	public static void removeCachedTeXFormula(String f, int style, int size,
			int inset) throws ParseException {
		removeCachedTeXFormula(f, style, 0, size, inset, null);
	}

	/**
	 * Remove a formula from the cache. Take care, remove the Object o,
	 * invalidate it !
	 * 
	 * @param o
	 *            an Object to identify the image in the cache
	 */
	public static void removeCachedTeXFormula(Object o) throws ParseException {
		if (o != null && o instanceof CachedTeXFormula) {
			cache.remove((CachedTeXFormula) o);
		}
	}

	/**
	 * Paint a cached formula
	 * 
	 * @param f
	 *            a formula
	 * @param style
	 *            a style like TeXConstants.STYLE_DISPLAY
	 * @param size
	 *            the size of font
	 * @param inset
	 *            the inset to add on the top, bottom, left and right
	 * @return the key in the map
	 */
	public static Object paintCachedTeXFormula(String f, int style, int type,
			int size, int inset, Integer fgcolor, Canvas g)
			throws ParseException {
		return paintCachedTeXFormula(new CachedTeXFormula(f, style, type, size,
				inset, fgcolor), g);
	}

	public static Object paintCachedTeXFormula(String f, int style, int size,
			int inset, Canvas g) throws ParseException {
		return paintCachedTeXFormula(f, style, 0, size, inset, null, g);
	}

	/**
	 * Paint a cached formula
	 * 
	 * @param o
	 *            an Object to identify the image in the cache
	 * @param g
	 *            the graphics where to paint the image
	 * @return the key in the map
	 */
	public static Object paintCachedTeXFormula(Object o, Canvas g)
			throws ParseException {
		if (o == null || !(o instanceof CachedTeXFormula)) {
			return null;
		}
		CachedTeXFormula cached = (CachedTeXFormula) o;
		SoftReference<CachedImage> img = cache.get(cached);
		if (img == null || img.get() == null) {
			img = makeImage(cached);
		}

		g.drawBitmap(img.get().image, matrix, null);

		return cached;
	}

	/**
	 * Get a cached formula
	 * 
	 * @param f
	 *            a formula
	 * @param style
	 *            a style like TeXConstants.STYLE_DISPLAY
	 * @param size
	 *            the size of font
	 * @param inset
	 *            the inset to add on the top, bottom, left and right
	 * @return the cached image
	 */
	public static Bitmap getCachedTeXFormulaImage(String f, int style,
			int type, int size, int inset, Integer fgcolor)
			throws ParseException {
		return getCachedTeXFormulaImage(new CachedTeXFormula(f, style, type,
				size, inset, fgcolor));
	}

	public static Bitmap getCachedTeXFormulaImage(String f, int style,
			int size, int inset) throws ParseException {
		return getCachedTeXFormulaImage(f, style, 0, size, inset, null);
	}

	/**
	 * Get a cached formula
	 * 
	 * @param o
	 *            an Object to identify the image in the cache
	 * @return the cached image
	 */
	public static Bitmap getCachedTeXFormulaImage(Object o)
			throws ParseException {
		if (o == null || !(o instanceof CachedTeXFormula)) {
			return null;
		}
		CachedTeXFormula cached = (CachedTeXFormula) o;
		SoftReference<CachedImage> img = cache.get(cached);
		if (img == null || img.get() == null) {
			img = makeImage(cached);
		}

		return img.get().image;
	}

	private static SoftReference<CachedImage> makeImage(CachedTeXFormula cached)
			throws ParseException {
		TeXFormula formula = new TeXFormula(cached.f);
		TeXIcon icon = formula.createTeXIcon(cached.style, cached.size,
				cached.type, cached.fgcolor);
		icon.setInsets(new Insets(cached.inset, cached.inset, cached.inset,
				cached.inset));
		Bitmap image = Bitmap.createBitmap(icon.getIconWidth(),
				icon.getIconHeight(), Config.ARGB_8888);
		Canvas g2 = new Canvas(image);
		icon.paintIcon(g2, 0, 0);
		cached.setDimensions(icon.getIconWidth(), icon.getIconHeight(),
				icon.getIconDepth());
		SoftReference<CachedImage> img = new SoftReference<CachedImage>(
				new CachedImage(image, cached), queue);

		if (cache.size() >= max) {
			Reference soft;
			while ((soft = queue.poll()) != null) {
				CachedImage ci = (CachedImage) soft.get();
				if (ci != null) {
					cache.remove(ci.cachedTf);
				}
			}
			Iterator<CachedTeXFormula> iter = cache.keySet().iterator();
			if (iter.hasNext()) {
				CachedTeXFormula c = iter.next();
				SoftReference<CachedImage> cachedImage = cache.get(c);
				if (cachedImage != null) {
					cachedImage.clear();
				}
				cache.remove(c);
			}
		}
		cache.put(cached, img);

		return img;
	}

	private static class CachedImage {

		Bitmap image;
		CachedTeXFormula cachedTf;

		CachedImage(Bitmap image, CachedTeXFormula cachedTf) {
			this.image = image;
			this.cachedTf = cachedTf;
		}
	}

	private static class CachedTeXFormula {

		String f;
		int style;
		int type;
		int size;
		int inset;
		int width = -1;
		int height;
		int depth;
		Integer fgcolor;

		CachedTeXFormula(String f, int style, int type, int size, int inset,
				Integer fgcolor) {
			this.f = f;
			this.style = style;
			this.type = type;
			this.size = size;
			this.inset = inset;
			this.fgcolor = fgcolor;
		}

		void setDimensions(int width, int height, int depth) {
			this.width = width;
			this.height = height;
			this.depth = depth;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean equals(Object o) {
			if (o != null && o instanceof CachedTeXFormula) {
				CachedTeXFormula c = (CachedTeXFormula) o;
				boolean b = (c.f.equals(f) && c.style == style
						&& c.type == type && c.size == size && c.inset == inset && c.fgcolor
						.equals(fgcolor));
				if (b) {
					if (c.width == -1) {
						c.width = width;
						c.height = height;
						c.depth = depth;
					} else if (width == -1) {
						width = c.width;
						height = c.height;
						depth = c.depth;
					}
				}

				return b;
			}

			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public int hashCode() {
			return f.hashCode();
		}
	}
}
