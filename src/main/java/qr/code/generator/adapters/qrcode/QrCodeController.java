package qr.code.generator.adapters.qrcode;

import com.google.zxing.WriterException;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import qr.code.generator.adapters.qrcode.dto.QrCodeGenerateRequest;
import qr.code.generator.adapters.qrcode.dto.QrCodeGenerateResponse;

public interface QrCodeController {

  ResponseEntity<QrCodeGenerateResponse> generate(@RequestBody QrCodeGenerateRequest request)
      throws IOException, WriterException;
}
