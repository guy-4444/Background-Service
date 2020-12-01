package com.classy.backgroundservice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PathImageGenerator {

    static class LatLng {
        double Latitude;
        double Longitude;

        public LatLng() { }

        public LatLng(double latitude, double longitude) {
            Latitude = latitude;
            Longitude = longitude;
        }
    }

    public static Bitmap generatePath(List<LatLng> latLngList) {
        Paint drawPaint = new Paint();
        Paint BGPaint = new Paint();
        Paint EndDotdrawPaint = new Paint();
        Paint EndDotBGPaint = new Paint();

        double MinLatitude = 0;
        double MaxLatitude = 0;
        double MinLongitude = 0;
        double MaxLongitude = 0;
        double Distance_Proportion;
        double DrawScale;
        double Lat_Offset;
        double Lon_Offset;

        boolean isFirst = true;
        for (LatLng latLng : latLngList) {
            if (isFirst) {
                isFirst = false;
                MinLatitude = latLng.Latitude;
                MaxLatitude = latLng.Latitude;
                MinLongitude = latLng.Longitude;
                MaxLongitude = latLng.Longitude;
            } else {
                MinLatitude = Math.min(MinLatitude, latLng.Latitude);
                MaxLatitude = Math.max(MaxLatitude, latLng.Latitude);
                MinLongitude = Math.min(MinLongitude, latLng.Longitude);
                MaxLongitude = Math.max(MaxLongitude, latLng.Longitude);
            }
        }

        float thumbLineWidth = 1.0f;
        int Size = 300;
        int Margin = (int) Math.ceil(thumbLineWidth * 3);
        int Size_Minus_Margins = Size - 2 * Margin;

        int color_in_line = Color.WHITE;
        int color_line_stroke = Color.GRAY;
        int color_dest_point = Color.RED;

        drawPaint.setColor(color_in_line);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(thumbLineWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        BGPaint.setColor(color_line_stroke);
        BGPaint.setAntiAlias(true);
        BGPaint.setStrokeWidth(thumbLineWidth * 3);
        BGPaint.setStyle(Paint.Style.STROKE);
        BGPaint.setStrokeJoin(Paint.Join.ROUND);
        BGPaint.setStrokeCap(Paint.Cap.ROUND);

        EndDotdrawPaint.setColor(color_in_line);
        EndDotdrawPaint.setAntiAlias(true);
        EndDotdrawPaint.setStrokeWidth(thumbLineWidth * 2.5f);
        EndDotdrawPaint.setStyle(Paint.Style.STROKE);
        EndDotdrawPaint.setStrokeJoin(Paint.Join.ROUND);
        EndDotdrawPaint.setStrokeCap(Paint.Cap.ROUND);

        EndDotBGPaint.setColor(color_dest_point);
        EndDotBGPaint.setAntiAlias(true);
        EndDotBGPaint.setStrokeWidth(thumbLineWidth * 6.5f);
        EndDotBGPaint.setStyle(Paint.Style.STROKE);
        EndDotBGPaint.setStrokeJoin(Paint.Join.ROUND);
        EndDotBGPaint.setStrokeCap(Paint.Cap.ROUND);

        // Calculate the drawing scale
        double Mid_Latitude = (MaxLatitude + MinLatitude) / 2;
        double Angle_From_Equator = Math.abs(Mid_Latitude);

        Distance_Proportion = Math.cos(Math.toRadians(Angle_From_Equator));

        DrawScale = Math.max(MaxLatitude - MinLatitude, Distance_Proportion * (MaxLongitude - MinLongitude));
        Lat_Offset = Size_Minus_Margins * (1 - (MaxLatitude - MinLatitude) / DrawScale) / 2;
        Lon_Offset = Size_Minus_Margins * (1 - (Distance_Proportion * (MaxLongitude - MinLongitude) / DrawScale)) / 2;

        Path path = new Path();


        if (!latLngList.isEmpty()) {
            Bitmap ThumbBitmap = Bitmap.createBitmap(Size, Size, Bitmap.Config.ARGB_8888);
            Canvas ThumbCanvas = new Canvas(ThumbBitmap);

            for (int i = 0; i < latLngList.size(); i++) {
                if (i == 0)
                    path.moveTo((float) (Lon_Offset + Margin + Size_Minus_Margins * ((latLngList.get(i).Longitude - MinLongitude) * Distance_Proportion / DrawScale)),
                            (float) (-Lat_Offset + Size - (Margin + Size_Minus_Margins * ((latLngList.get(i).Latitude - MinLatitude) / DrawScale))));
                else
                    path.lineTo((float) (Lon_Offset + Margin + Size_Minus_Margins * ((latLngList.get(i).Longitude - MinLongitude) * Distance_Proportion / DrawScale)),
                            (float) (-Lat_Offset + Size - (Margin + Size_Minus_Margins * ((latLngList.get(i).Latitude - MinLatitude) / DrawScale))));
            }
            ThumbCanvas.drawPath(path, BGPaint);
            ThumbCanvas.drawPoint((float) (Lon_Offset + Margin + Size_Minus_Margins * ((latLngList.get(latLngList.size()-1).Longitude - MinLongitude) * Distance_Proportion / DrawScale)),
                    (float) (-Lat_Offset + Size - (Margin + Size_Minus_Margins * ((latLngList.get(latLngList.size()-1).Latitude - MinLatitude) / DrawScale))), EndDotBGPaint);
            ThumbCanvas.drawPath(path, drawPaint);
            ThumbCanvas.drawPoint((float) (Lon_Offset + Margin + Size_Minus_Margins * ((latLngList.get(latLngList.size()-1).Longitude - MinLongitude) * Distance_Proportion / DrawScale)),
                    (float) (-Lat_Offset + Size - (Margin + Size_Minus_Margins * ((latLngList.get(latLngList.size()-1).Latitude - MinLatitude) / DrawScale))), EndDotdrawPaint);

            return ThumbBitmap;
        }

        return null;
    }

    public static void saveBitmapFile(Context appContext, String fileName, Bitmap bitmap) {
        try {
            File file = new File(appContext.getFilesDir() + "/Thumbnails/", fileName);
            file.mkdirs();
            if (file.exists()) file.delete();

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
        List<LatLng> latLngList = new ArrayList<>();
        latLngList.add(new LatLng(32.04459453327321, 34.81337634371992));
        latLngList.add(new LatLng(32.045794843072635, 34.8136508893958));
        latLngList.add(new LatLng(32.04779124187704, 34.81401695030466));
        latLngList.add(new LatLng(32.04909765516038, 34.814050666425885));
        latLngList.add(new LatLng(32.051269525980004, 34.81374240453798));
        latLngList.add(new LatLng(32.05318824112971, 34.81248527425862));
        latLngList.add(new LatLng(32.05495995514941, 34.810414139985774));
        latLngList.add(new LatLng(32.05626218390476, 34.80771684886214));
        latLngList.add(new LatLng(32.056960236202805, 34.80564571459457));
        latLngList.add(new LatLng(32.05801342784333, 34.80437413444911));
        latLngList.add(new LatLng(32.059291120780834, 34.80409958874776));
        latLngList.add(new LatLng(32.06007487250032, 34.804186287416826));
        latLngList.add(new LatLng(32.061201503783074, 34.80512070603298));
        latLngList.add(new LatLng(32.06061369788845, 34.805790212154854));
        latLngList.add(new LatLng(32.05962584890389, 34.80771684874103));
        latLngList.add(new LatLng(32.05878494425782, 34.81064533616659));
        latLngList.add(new LatLng(32.05843388468925, 34.8129958328493));
        latLngList.add(new LatLng(32.0584134742076, 34.815341512773536));
        latLngList.add(new LatLng(32.058601250464335, 34.816747957365436));
        latLngList.add(new LatLng(32.059621766861625, 34.82117922126961));
        latLngList.add(new LatLng(32.05965181887188, 34.82452289519991));
        latLngList.add(new LatLng(32.05721283448303, 34.832961287674976));
        latLngList.add(new LatLng(32.05214863372553, 34.83759508709318));
        latLngList.add(new LatLng(32.04661903326276, 34.83480261391267));
        latLngList.add(new LatLng(32.04793169758498, 34.83160773127351));

        Bitmap bitmap = PathImageGenerator.generatePath(latLngList);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
 */
