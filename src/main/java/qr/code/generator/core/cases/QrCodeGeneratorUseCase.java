package qr.code.generator.core.cases;

import com.google.zxing.WriterException;
import java.io.IOException;
import qr.code.generator.adapters.qrcode.dto.QrCodeGenerateResponse;

public interface QrCodeGeneratorUseCase {
  QrCodeGenerateResponse generateAndUploadQrCode(String text) throws WriterException, IOException;
}
