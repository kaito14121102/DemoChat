package com.anime.rezero.demochat;

/**
 * Created by zing on 10/6/2017.
 */

public class NoiDungChat {
    String noidung;
    byte[] hinh;
    byte[] amthanh;

    public NoiDungChat() {
    }

    public NoiDungChat(String noidung, byte[] hinh,byte[] amthanh) {
        this.noidung = noidung;
        this.hinh = hinh;
        this.amthanh=amthanh;
    }

    public byte[] getAmthanh() {
        return amthanh;
    }

    public void setAmthanh(byte[] amthanh) {
        this.amthanh = amthanh;
    }

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }

    public byte[] getHinh() {
        return hinh;
    }

    public void setHinh(byte[] hinh) {
        this.hinh = hinh;
    }
}
