package org.ctc.dto;

import java.io.Serializable;

public class ImageDTO implements Serializable {
    private byte[] data;

    private String fileName;

    public ImageDTO() {
    }

    public ImageDTO(byte[] data, String fileName) {
        this.data = data;
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
