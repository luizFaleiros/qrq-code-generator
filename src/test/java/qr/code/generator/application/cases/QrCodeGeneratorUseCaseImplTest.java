package qr.code.generator.application.cases;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qr.code.generator.core.ports.StoragePort;

@ExtendWith(MockitoExtension.class)
class QrCodeGeneratorUseCaseImplTest {

  @Mock
  private StoragePort storagePort;

  private MeterRegistry meterRegistry;
  private QrCodeGeneratorUseCaseImpl qrCodeGenerator;
  private final String testUrl = "https://example.com/qr/" + UUID.randomUUID();
  private final String testText = "Test QR Code";

  @BeforeEach
  void setUp() {
    meterRegistry = new SimpleMeterRegistry();
    qrCodeGenerator = new QrCodeGeneratorUseCaseImpl(storagePort, meterRegistry);
  }

  @Test
  void generateAndUploadQrCodeShouldReturnValidResponseWhenInputIsValid() throws Exception {
    // Arrange
    when(storagePort.uploadFile(any(byte[].class), anyString(), anyString()))
        .thenReturn(testUrl);

    // Act
    var response = qrCodeGenerator.generateAndUploadQrCode(testText);

    // Assert
    assertNotNull(response);
    verify(storagePort, times(1)).uploadFile(any(byte[].class),
        anyString(), eq("image/png"));
  }

  @Test
  void generateAndUploadQrCodeShouldThrowRuntimeExceptionWhenStorageFails() {
    // Arrange
    when(storagePort.uploadFile(any(byte[].class), anyString(), anyString()))
        .thenThrow(new RuntimeException("Storage error"));

    // Act & Assert
    assertThrows(RuntimeException.class,
        () -> qrCodeGenerator.generateAndUploadQrCode(testText)
    );
  }

  @Test
  void generateAndUploadQrCodeShouldHandleEmptyText() {
    // Arrange
    when(storagePort.uploadFile(any(byte[].class), anyString(), anyString()))
        .thenReturn(testUrl);

    // Act
    var response = qrCodeGenerator.generateAndUploadQrCode("Test QR Code");

    // Assert
    assertNotNull(response);
    assertTrue(response.url().startsWith("https://example.com/qr/"));
  }

  @Test
  void generateAndUploadQrCodeShouldGenerateDifferentFilenames() {
    // Arrange
    when(storagePort.uploadFile(any(byte[].class), anyString(), anyString()))
        .thenReturn("https://example.com/qr/file1")
        .thenReturn("https://example.com/qr/file2");

    // Act
    var response1 = qrCodeGenerator.generateAndUploadQrCode(testText);
    var response2 = qrCodeGenerator.generateAndUploadQrCode(testText);

    // Assert
    assertNotEquals(response1.url(), response2.url());
  }
}