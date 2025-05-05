package qr.code.generator.core.ports;

public interface StoragePort {
    String uploadFile(byte[] fileData, String fileName, String contentType);
}
