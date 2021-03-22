package widget.cf.com.widgetlibrary.util;

import android.graphics.Color;

import androidx.annotation.ColorInt;

public class ColorUtils {

    public static int[] getRGB(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return new int[]{
                r, g, b
        };
    }

    public static String getColorHexString(int color) {
        String R, G, B;
        StringBuffer sb = new StringBuffer();
        R = Integer.toHexString(Color.red(color));
        G = Integer.toHexString(Color.green(color));
        B = Integer.toHexString(Color.blue(color));
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        sb.append("#");
        sb.append(R);
        sb.append(G);
        sb.append(B);
        return sb.toString();
    }

    public static int blend(int a, int b, float val) {
        if (val > 1) {
            val = 1;
        } else if (val < 0) {
            val = 0;
        }

        return Color.rgb((int) (Color.red(b) * val + Color.red(a) * (1 - val)),
                (int) (Color.green(b) * val + Color.green(a) * (1 - val)),
                (int) (Color.blue(b) * val + Color.blue(a) * (1 - val)));
    }

    public static int getGradientColor(@ColorInt int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        LogUtils.d("getGradientColor", color + " r:" + r + "  g:" + g + "  b:" + b);

        if (r == g && r == b) {

            if (r != 255) {
                b += 30;

            } else {

                r -= 10;
                g -= 10;
            }

            LogUtils.d("getGradientColor", "r:" + r + "  g:" + g + "  b:" + b);
            return Color.rgb(checkIntRange(r), checkIntRange(g), checkIntRange(b));
        }

        if (r >= g) {

            if (g >= b) {
                if (g > 150) {
                    g -= 30;
                } else {
                    g += 30;
                }

                if (r > 150) {
                    r -= 30;
                } else if (r < 50) {
                    r += 50;
                } else {
                    r += 30;
                }

            } else {

                if (r >= b) {
                    if (b > 150) {
                        b -= 30;
                    } else {
                        b += 30;
                    }

                    if (r > 150) {
                        r -= 30;
                    } else if (r < 50) {
                        r += 50;
                    } else {
                        r += 30;
                    }
                } else {
                    if (r > 150) {
                        r -= 30;
                    } else {
                        r += 30;
                    }

                    if (b > 150) {
                        b -= 30;
                    } else if (b < 50) {
                        b += 50;
                    } else {
                        b += 30;
                    }
                }
            }

        } else { //r<g

            if (r >= b) {

                if (g > 150) {
                    g -= 30;
                } else if (g < 50) {
                    g += 50;
                } else {
                    g += 30;
                }

                if (r > 150) {
                    r -= 30;
                } else {
                    r += 30;
                }
            } else { //r<g & r<b

                if (g >= b) {
                    if (g > 150) {
                        g -= 30;
                    } else if (g < 50) {
                        g += 50;
                    } else {
                        g += 30;
                    }

                    if (b > 150) {
                        b -= 30;
                    } else {
                        b += 30;
                    }

                } else {
                    if (b > 150) {
                        b -= 30;
                    } else if (b < 50) {
                        b += 50;
                    } else {
                        b += 30;
                    }

                    if (g > 150) {
                        g -= 30;
                    } else {
                        g += 30;
                    }
                }

            }
        }
        LogUtils.d("getGradientColor", "r:" + r + "  g:" + g + "  b:" + b);
        return Color.rgb(checkIntRange(r), checkIntRange(g), checkIntRange(b));
    }

    private static int checkIntRange(int value) {
        if (value > 255) {
            value = 255;
        } else if (value < 0) {
            value = 0;
        }

        return value;
    }

    public static int getAverageColor(int color1, int color2) {
        int r1 = Color.red(color1);
        int r2 = Color.red(color2);
        int g1 = Color.green(color1);
        int g2 = Color.green(color2);
        int b1 = Color.blue(color1);
        int b2 = Color.blue(color2);
        return Color.argb(255, (r1 / 2 + r2 / 2), (g1 / 2 + g2 / 2), (b1 / 2 + b2 / 2));
    }

    public static int getPatternColor(int color) {
        float[] hsb = RGBtoHSB(Color.red(color), Color.green(color), Color.blue(color));
        if (hsb[1] > 0.0f || (hsb[2] < 1.0f && hsb[2] > 0.0f)) {
            hsb[1] = Math.min(1.0f, hsb[1] + 0.05f + 0.1f * (1.0f - hsb[1]));
        }
        if (hsb[2] > 0.5f) {
            hsb[2] = Math.max(0.0f, hsb[2] * 0.65f);
        } else {
            hsb[2] = Math.max(0.0f, Math.min(1.0f, 1.0f - hsb[2] * 0.65f));
        }
        return HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0x66ffffff;
    }

    public static float[] RGBtoHSB(int r, int g, int b) {
        float hue, saturation, brightness;
        float[] hsbvals = new float[3];
        int cmax = (r > g) ? r : g;
        if (b > cmax) {
            cmax = b;
        }
        int cmin = (r < g) ? r : g;
        if (b < cmin) {
            cmin = b;
        }

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0) {
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        } else {
            saturation = 0;
        }
        if (saturation == 0) {
            hue = 0;
        } else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax) {
                hue = bluec - greenc;
            } else if (g == cmax) {
                hue = 2.0f + redc - bluec;
            } else {
                hue = 4.0f + greenc - redc;
            }
            hue = hue / 6.0f;
            if (hue < 0) {
                hue = hue + 1.0f;
            }
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        return 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }

}
