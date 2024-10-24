package com.example.skkuljh;

import java.io.Serializable;

public class HosData {
    private String SIGUN_CD;
    private String SIGUN_NM;
    private String INST_NM;

    private String TELNO_INFO;
    private String HMPG_URL;
    private String REFINE_LOTNO_ADDR;
    private String REFINE_ROADNM_ADDR;
    private String REFINE_ZIP_CD;
    private double REFINE_WGS84_LOGT;
    private double REFINE_WGS84_LAT;

    public String getSIGUN_CD() {
        return SIGUN_CD;
    }

    public void setSIGUN_CD(String SIGUN_CD) {
        this.SIGUN_CD = SIGUN_CD;
    }

    public String getSIGUN_NM() {
        return SIGUN_NM;
    }

    public void setSIGUN_NM(String SIGUN_NM) {
        this.SIGUN_NM = SIGUN_NM;
    }

    public String getINST_NM() {
        return INST_NM;
    }

    public void setINST_NM(String INST_NM) {
        this.INST_NM = INST_NM;
    }

    public String getTELNO_INFO() {
        return TELNO_INFO;
    }

    public void setTELNO_INFO(String TELNO_INFO) {
        this.TELNO_INFO = TELNO_INFO;
    }

    public String getHMPG_URL() {
        return HMPG_URL;
    }

    public void setHMPG_URL(String HMPG_URL) {
        this.HMPG_URL = HMPG_URL;
    }

    public String getREFINE_LOTNO_ADDR() {
        return REFINE_LOTNO_ADDR;
    }

    public void setREFINE_LOTNO_ADDR(String REFINE_LOTNO_ADDR) {
        this.REFINE_LOTNO_ADDR = REFINE_LOTNO_ADDR;
    }

    public String getREFINE_ROADNM_ADDR() {
        return REFINE_ROADNM_ADDR;
    }

    public void setREFINE_ROADNM_ADDR(String REFINE_ROADNM_ADDR) {
        this.REFINE_ROADNM_ADDR = REFINE_ROADNM_ADDR;
    }

    public String getREFINE_ZIP_CD() {
        return REFINE_ZIP_CD;
    }

    public void setREFINE_ZIP_CD(String REFINE_ZIP_CD) {
        this.REFINE_ZIP_CD = REFINE_ZIP_CD;
    }

    public double getREFINE_WGS84_LOGT() {
        return REFINE_WGS84_LOGT;
    }

    public void setREFINE_WGS84_LOGT(double REFINE_WGS84_LOGT) {
        this.REFINE_WGS84_LOGT = REFINE_WGS84_LOGT;
    }

    public double getREFINE_WGS84_LAT() {
        return REFINE_WGS84_LAT;
    }

    public void setREFINE_WGS84_LAT(double REFINE_WGS84_LAT) {
        this.REFINE_WGS84_LAT = REFINE_WGS84_LAT;
    }
}
