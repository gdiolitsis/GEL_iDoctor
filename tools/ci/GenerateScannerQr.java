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
        BitMatrix qr = new MultiFormatWriter().encode(
                "GEL_RELEASE_SCANNER_SMOKE", BarcodeFormat.QR_CODE, 320, 320);
        for (int y = 0; y < 320; y++) for (int x = 0; x < 320; x++)
            image.setRGB(160 + x, 80 + y, qr.get(x, y) ? 0 : 0xFFFFFF);
        if (!ImageIO.write(image, "PNG", new File(args[0]))) throw new IllegalStateException("PNG writer unavailable");
    }
}

