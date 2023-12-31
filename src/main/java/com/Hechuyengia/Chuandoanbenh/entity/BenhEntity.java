/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Hechuyengia.Chuandoanbenh.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author tranm
 */
@Entity
@Table(name = "benh")
public class BenhEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_benh")
    private Long ma_benh;

    @Column(name = "ten_benh")
    private String ten_benh;

    @Column(name = "loai_he")
    private String loai_he;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserEntity userEntity; // Định nghĩa mối quan hệ với bảng người dùng

    public BenhEntity() {
    }

    public BenhEntity(Long ma_benh, String ten_benh, String loai_he) {
        this.ma_benh = ma_benh;
        this.ten_benh = ten_benh;
        this.loai_he = loai_he;
    }

    public BenhEntity(Long ma_benh, String ten_benh, String loai_he, UserEntity userEntity) {
        this.ma_benh = ma_benh;
        this.ten_benh = ten_benh;
        this.loai_he = loai_he;
        this.userEntity = userEntity;
    }

    

   

    

    public BenhEntity(Long ma_benh, String ten_benh) {
        this.ma_benh = ma_benh;
        this.ten_benh = ten_benh;
    }

    

    public Long getMa_benh() {
        return ma_benh;
    }

    public void setMa_benh(Long ma_benh) {
        this.ma_benh = ma_benh;
    }

    

    public String getTen_benh() {
        return ten_benh;
    }

    public void setTen_benh(String ten_benh) {
        this.ten_benh = ten_benh;
    }

    public String getLoai_he() {
        return loai_he;
    }

    public void setLoai_he(String loai_he) {
        this.loai_he = loai_he;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    

}
