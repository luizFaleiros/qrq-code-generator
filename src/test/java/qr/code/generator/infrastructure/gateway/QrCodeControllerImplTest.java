package qr.code.generator.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.zxing.WriterException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import qr.code.generator.adapters.qrcode.dto.QrCodeGenerateRequest;
import qr.code.generator.adapters.qrcode.dto.QrCodeGenerateResponse;
import qr.code.generator.core.cases.QrCodeGeneratorUseCase;

@ExtendWith(MockitoExtension.class)
class QrCodeControllerImplTest {

  @Mock
  private QrCodeGeneratorUseCase qrCodeGeneratorUseCase;

  @InjectMocks
  private QrCodeControllerImpl qrCodeController;

  private static final String TEST_URL = "https://example.com/qr/test123";
  private static final String TEST_TEXT = "Test QR Code";

  @Test
  void generateShouldReturnOkResponseWhenGenerationIsSuccessful() throws Exception {
    // Arrange
    QrCodeGenerateRequest request = new QrCodeGenerateRequest(TEST_TEXT);
    QrCodeGenerateResponse expectedResponse = new QrCodeGenerateResponse(TEST_URL);

    when(qrCodeGeneratorUseCase.generateAndUploadQrCode(TEST_TEXT))
        .thenReturn(expectedResponse);

    // Act
    ResponseEntity<QrCodeGenerateResponse> response = qrCodeController.generate(request);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(TEST_URL, response.getBody().url());

    verify(qrCodeGeneratorUseCase, times(1)).generateAndUploadQrCode(TEST_TEXT);
  }

  @Test
  void generateShouldPropagateIoExceptionWhenGenerationFails() throws Exception {
    // Arrange
    QrCodeGenerateRequest request = new QrCodeGenerateRequest(TEST_TEXT);

    when(qrCodeGeneratorUseCase.generateAndUploadQrCode(TEST_TEXT))
        .thenThrow(new IOException("IO Error"));

    // Act & Assert
    assertThrows(IOException.class, () -> qrCodeController.generate(request));
    verify(qrCodeGeneratorUseCase, times(1)).generateAndUploadQrCode(TEST_TEXT);
  }

  @Test
  void generateShouldPropagateWriterExceptionWhenGenerationFails() throws Exception {
    // Arrange
    QrCodeGenerateRequest request = new QrCodeGenerateRequest(TEST_TEXT);

    when(qrCodeGeneratorUseCase.generateAndUploadQrCode(TEST_TEXT))
        .thenThrow(new WriterException("QR Code generation failed"));

    // Act & Assert
    assertThrows(WriterException.class, () -> qrCodeController.generate(request));
    verify(qrCodeGeneratorUseCase, times(1)).generateAndUploadQrCode(TEST_TEXT);
  }

  @Test
  void generateShouldHandleEmptyText() throws Exception {
    // Arrange
    String emptyText = "";
    QrCodeGenerateRequest request = new QrCodeGenerateRequest(emptyText);
    QrCodeGenerateResponse expectedResponse = new QrCodeGenerateResponse(TEST_URL);

    when(qrCodeGeneratorUseCase.generateAndUploadQrCode(emptyText))
        .thenReturn(expectedResponse);

    // Act
    ResponseEntity<QrCodeGenerateResponse> response = qrCodeController.generate(request);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(TEST_URL, response.getBody().url());

    verify(qrCodeGeneratorUseCase, times(1)).generateAndUploadQrCode(emptyText);
  }
}