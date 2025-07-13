package qr.code.generator.infrastructure.gateway;

import com.google.zxing.WriterException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qr.code.generator.adapters.qrcode.QrCodeController;
import qr.code.generator.adapters.qrcode.dto.QrCodeGenerateRequest;
import qr.code.generator.adapters.qrcode.dto.QrCodeGenerateResponse;
import qr.code.generator.core.cases.QrCodeGeneratorUseCase;

@RestController
@RequestMapping("/qrcode")
public class QrCodeControllerImpl implements QrCodeController {

  private static final Logger log = LoggerFactory.getLogger(QrCodeControllerImpl.class);
  private final QrCodeGeneratorUseCase qrCodeGeneratorUseCase;

  public QrCodeControllerImpl(QrCodeGeneratorUseCase qrCodeGeneratorUseCase) {
    this.qrCodeGeneratorUseCase = qrCodeGeneratorUseCase;
  }

  @PostMapping
  @Override
  public ResponseEntity<QrCodeGenerateResponse> generate(@RequestBody QrCodeGenerateRequest qrCodeGenerateRequest)
      throws IOException, WriterException {
    try {
      QrCodeGenerateResponse response = qrCodeGeneratorUseCase.generateAndUploadQrCode(qrCodeGenerateRequest.text());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("m=generate error={}", e.getCause());
      throw e;
    }
  }
}
