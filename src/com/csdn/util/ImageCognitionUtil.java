/**
 * 
 */
package com.csdn.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.csdn.bean.ImageXyBean;
import com.csdn.bean.RgbImageComparerBean;


public class ImageCognitionUtil {
	public static final int SIM_ACCURATE_VERY = 0;
	public static final int SIM_ACCURATE = 31;
	public static final int SIM_BLUR = 61;
	public static final int SIM_BLUR_VERY = 81;

	public List<ImageXyBean> imageSearch(BufferedImage sourceImage, BufferedImage searchImage, int sim) {
		List<ImageXyBean> list = new ArrayList<ImageXyBean>();

		RgbImageComparerBean pxSource = getPX(sourceImage);
		RgbImageComparerBean pxSearch = getPX(searchImage);

		int[][] px = pxSource.getColorArray();
		int[][] pxS = pxSearch.getColorArray();
		int pxSXMax = pxSearch.getImgWidth() - 1;
		int pxSYMax = pxSearch.getImgHeight() - 1;
		int xSearchEnd = pxSource.getImgWidth() - pxSearch.getImgWidth();
		int ySearchEnd = pxSource.getImgHeight() - pxSearch.getImgHeight();

		int contentSearchX = 1;
		int contentSearchY = 1;

		double pxPercent = 0.9900000095367432D;
		if (sim > 0) {
			pxPercent = sim / 255.0D / 4.0D;
		}
		for (int x = 0; x < xSearchEnd; x++) {
			for (int y = 0; y < ySearchEnd; y++) {

				boolean contrast = false;

				if (sim < 32) {

					if (colorCompare(px[x][y], pxS[0][0], sim)) {

						int pxX = x + pxSearch.getImgWidth() - 1;
						if (colorCompare(px[pxX][y], pxS[pxSXMax][0], sim)) {

							int pxY = y + pxSearch.getImgHeight() - 1;
							if (colorCompare(px[x][pxY], pxS[0][pxSYMax], sim)) {
								if (colorCompare(px[pxX][pxY], pxS[pxSXMax][pxSYMax], sim)) {
									if (pxSXMax > 2) {
										contentSearchX = (int) Math.ceil((pxSXMax / 2));
									}
									if (pxSYMax > 2) {
										contentSearchY = (int) Math.ceil((pxSYMax / 2));
									}
									if (colorCompare(px[x + contentSearchX][y + contentSearchY],
											pxS[contentSearchX][contentSearchY], sim)) {
										contrast = true;
									}
								}
							}
						}
					}
				} else {

					contrast = true;
				}
				if (sim < 62) {

					if (contrast) {
						int yes = 0;
						int ySour = y + contentSearchY;
						for (int i = 0; i < pxSearch.getImgWidth(); i++) {
							if (colorCompare(px[x + i][ySour], pxS[i][contentSearchY], sim)) {
								yes++;
							}
						}
						if ((yes / pxSearch.getImgWidth()) > pxPercent) {
							contrast = true;
						} else {
							contrast = false;
						}
					}
					if (contrast) {
						int yes = 0;
						int xSour = x + contentSearchX;
						for (int i = 0; i < pxSearch.getImgHeight(); i++) {
							if (colorCompare(px[xSour][y + i], pxS[contentSearchX][i], sim)) {
								yes++;
							}
						}

						if ((yes / pxSearch.getImgHeight()) > pxPercent) {
							contrast = true;
						} else {
							contrast = false;
						}
					}
				} else {

					contrast = true;
				}
				if (contrast) {
					int yes = 0;
					for (int xS = 0; xS < pxSearch.getImgWidth(); xS++) {
						for (int yS = 0; yS < pxSearch.getImgHeight(); yS++) {
							if (colorCompare(px[x + xS][y + yS], pxS[xS][yS], sim)) {
								yes++;
							}
						}
					}
					if ((yes / pxSearch.getPxCount()) > pxPercent) {
						ImageXyBean img = new ImageXyBean();
						img.x = x;
						img.y = y;
						list.add(img);
					}
				}
			}
		}

		return list;
	}

	public RgbImageComparerBean getPX(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		int minx = bufferedImage.getMinX();
		int miny = bufferedImage.getMinY();

		RgbImageComparerBean rgb = new RgbImageComparerBean();
		int[][] colorArray = new int[width][height];
		for (int i = minx; i < width; i++) {
			for (int j = miny; j < height; j++) {
				colorArray[i][j] = bufferedImage.getRGB(i, j);
			}
		}
		rgb.setColorArray(colorArray);
		return rgb;
	}

	public boolean colorCompare(int pxSource, int pxSearch, int sim) {
		if (sim == 0) {
			return (pxSearch == pxSource);
		}
		Color sourceRgb = new Color(pxSource);
		Color searchRgb = new Color(pxSearch);
		return colorCompare(sourceRgb, searchRgb, sim);
	}

	public boolean colorCompare(Color color1, Color color2, int sim) {
		if (Math.abs(color1.getRed() - color2.getRed()) <= sim && Math.abs(color1.getGreen() - color2.getGreen()) <= sim
				&& Math.abs(color1.getBlue() - color2.getBlue()) <= sim) {
			return true;
		}
		return false;
	}
}