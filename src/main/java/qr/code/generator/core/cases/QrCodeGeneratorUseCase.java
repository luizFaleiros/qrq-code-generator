package qr.code.generator.core.cases;

import com.google.zxing.WriterException;
import qr.code.generator.adapters.qrcode.dto.QrCodeGenerateResponse;

import java.io.IOException;

public interface QrCodeGeneratorUseCase {
    QrCodeGenerateResponse generateAndUploadQrCode(String text) throws WriterException, IOException;
}
