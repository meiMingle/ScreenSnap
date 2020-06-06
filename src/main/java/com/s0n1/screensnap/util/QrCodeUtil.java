package com.s0n1.screensnap.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.s0n1.screensnap.ui.Toast;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Edsuns@qq.com on 2020-05-29
 */
public final class QrCodeUtil {

    public static Result parseQrCode(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

            return new MultiFormatReader().decode(binaryBitmap, hints);
        } catch (NotFoundException e) {
            Toast.getInstance().show("Can't find any QRCode.", Toast.DELAY_DEFAULT);
        }
        return null;
    }
}
