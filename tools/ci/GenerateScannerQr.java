import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Inert QR input for the emulator camera: it cannot claim a service session. */
public final class GenerateScannerQr {
    public static void main(String[] args) throws Exception {
        BufferedImage image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 480; y++) for (int x = 0; x < 640; x++) image.setRGB(x, y, 0xFFFFFF);
        // The emulator camera crops the input differently for each preview
        // orientation. Repeat the same inert QR so a complete code remains
        // inside the decoder's framing rectangle in either orientation.
        BitMatrix qr = new MultiFormatWriter().encode(
                "GEL_RELEASE_SCANNER_SMOKE", BarcodeFormat.QR_CODE, 96, 96);
        for (int top = 16; top + 96 <= 480; top += 128)
            for (int left = 16; left + 96 <= 640; left += 128)
                for (int y = 0; y < 96; y++) for (int x = 0; x < 96; x++)
                    image.setRGB(left + x, top + y, qr.get(x, y) ? 0 : 0xFFFFFF);
        if (!ImageIO.write(image, "PNG", new File(args[0]))) throw new IllegalStateException("PNG writer unavailable");
    }
}

