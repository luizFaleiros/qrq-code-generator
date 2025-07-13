package qr.code.generator.application.cases;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.MeterRegistry;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import qr.code.generator.adapters.qrcode.dto.QrCodeGenerateResponse;
import qr.code.generator.core.cases.QrCodeGeneratorUseCase;
import qr.code.generator.core.ports.StoragePort;


@Service
public class QrCodeGeneratorUseCaseImpl implements QrCodeGeneratorUseCase {
  private final StoragePort storagePort;
  private final MeterRegistry meterRegistry;
  QRCodeWriter qrCodeWriter;

  public QrCodeGeneratorUseCaseImpl(StoragePort storagePort, MeterRegistry meterRegistry) {
    this.storagePort = storagePort;
    this.meterRegistry = meterRegistry;

  }

  @Override
  @Counted(value = "qr_code_generation_count", description = "Count of qr code generation")
  public QrCodeGenerateResponse generateAndUploadQrCode(String text) {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    try {
      BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
      ByteArrayOutputStream pngOut = new ByteArrayOutputStream();

      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOut);

      String url = storagePort.uploadFile(pngOut.toByteArray(), UUID.randomUUID().toString(), "image/png");
      return new QrCodeGenerateResponse(url);
    } catch (IOException | WriterException e) {
      throw new RuntimeException(e);
    }
  }
}
