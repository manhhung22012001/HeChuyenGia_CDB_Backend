/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Hechuyengia.Chuandoanbenh.entity;
import com.Hechuyengia.Chuandoanbenh.entity.TrieuChungBenhMoiId;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 *
 * @author tranm
 */
@Entity
@Table(name = "trieu_chung_benh_moi")
@IdClass(TrieuChungBenhMoiId.class) // Định nghĩa lớp IdClass
public class TrieuChungBenhMoiEnity implements Serializable{
    @Id
    @ManyToOne
    @JoinColumn(name = "ma_trieu_chung_moi")
    private TrieuChungMoiEntity trieuChungMoi;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "ma_benh_moi")
    private BenhEntity benhMoi;

    public TrieuChungBenhMoiEnity() {
    }

    public TrieuChungBenhMoiEnity(TrieuChungMoiEntity trieuChungMoi, BenhEntity benhMoi) {
        this.trieuChungMoi = trieuChungMoi;
        this.benhMoi = benhMoi;
    }

    public TrieuChungMoiEntity getTrieuChungMoi() {
        return trieuChungMoi;
    }

    public void setTrieuChungMoi(TrieuChungMoiEntity trieuChungMoi) {
        this.trieuChungMoi = trieuChungMoi;
    }

    public BenhEntity getBenhMoi() {
        return benhMoi;
    }

    public void setBenhMoi(BenhEntity benhMoi) {
        this.benhMoi = benhMoi;
    }
    
    
}
