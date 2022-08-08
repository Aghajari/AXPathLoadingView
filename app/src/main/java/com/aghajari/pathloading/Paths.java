package com.aghajari.pathloading;

import android.graphics.Path;

import com.aghajari.axpathloading.AXPathLoadingView;

@SuppressWarnings("unused")
public class Paths {

    public static Path createHR(AXPathLoadingView view) {
        float h = view.getResources().getDisplayMetrics().density * 40 * 2;
        float w = view.getResources().getDisplayMetrics().density * 56 * 2;

        float part = w / 4;
        float center = h / 2f;

        Path path = new Path();
        path.moveTo(0, center);
        path.lineTo(part, center);
        path.lineTo(part + part / 2f, h);
        path.lineTo(2 * part, center);
        path.lineTo(2 * part + part / 2f, 0);
        path.lineTo(3 * part, center);
        path.lineTo(4 * part, center);
        return path;
    }

    // https://stackoverflow.com/a/41251829/9187189
    public static Path createHeart(AXPathLoadingView view) {
        Path path = new Path();
        float size = view.getResources().getDisplayMetrics().density * 168;

        // Starting point
        path.moveTo(size / 2, size / 5);

        // Upper left path
        path.cubicTo(5 * size / 14, 0,
                0, size / 15,
                size / 28, 2 * size / 5);

        // Lower left path
        path.cubicTo(size / 14, 2 * size / 3,
                3 * size / 7, 5 * size / 6,
                size / 2, size);

        // Lower right path
        path.cubicTo(4 * size / 7, 5 * size / 6,
                13 * size / 14, 2 * size / 3,
                27 * size / 28, 2 * size / 5);

        // Upper right path
        path.cubicTo(size, size / 15,
                9 * size / 14, 0,
                size / 2, size / 5);
        return path;
    }
}
