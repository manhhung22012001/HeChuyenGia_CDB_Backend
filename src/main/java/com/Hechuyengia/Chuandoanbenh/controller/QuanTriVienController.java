/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Hechuyengia.Chuandoanbenh.controller;

import com.Hechuyengia.Chuandoanbenh.entity.BenhMoiEntity;
import com.Hechuyengia.Chuandoanbenh.entity.TrieuChungEntity;
import com.Hechuyengia.Chuandoanbenh.entity.TrieuChungMoiEntity;
import com.Hechuyengia.Chuandoanbenh.entity.UserDetailEntity;
import com.Hechuyengia.Chuandoanbenh.entity.UserEntity;
import com.Hechuyengia.Chuandoanbenh.repository.BenhMoiRepository;
import com.Hechuyengia.Chuandoanbenh.repository.TrieuChungMoiRepository;
import com.Hechuyengia.Chuandoanbenh.repository.TrieuChungRepository;
import com.Hechuyengia.Chuandoanbenh.repository.UserDetailRepository;
import com.Hechuyengia.Chuandoanbenh.repository.UserRepository;
import com.Hechuyengia.Chuandoanbenh.service.BenhMoiService;
import com.Hechuyengia.Chuandoanbenh.service.FileService;
import com.Hechuyengia.Chuandoanbenh.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author tranm
 */
@RestController
@RequestMapping("/taskbar-qtv")
public class QuanTriVienController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TrieuChungRepository trieuChungRepository;
    @Autowired
    BenhMoiRepository benhMoiRepository;

    @Autowired
    TrieuChungMoiRepository trieuChungMoiRepository;
    private final FileService fileService;

    public QuanTriVienController(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    UserService userService;

    @CrossOrigin
    @GetMapping("/getall")
    public List<UserEntity> list() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findAll();
    }

    @GetMapping("/getallTrieuChungCu")
    public List<TrieuChungEntity> listTC() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return trieuChungRepository.findAll();
    }

    @GetMapping("/getallBenhMoi")
    public List<BenhMoiEntity> listBenh() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return benhMoiRepository.findAll();
    }

    @GetMapping("/trieuchungmoi/{userId}")
    public ResponseEntity<List<Object[]>> getTrieuChungMoiByMaBenhMoi(@PathVariable Long userId,
            @RequestParam(value = "ma_benh_moi") Long ma_benh_moi) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            List<Object[]> trieuchungmoi = benhMoiRepository.findTrieuChungMoiByMaBenhMoi(ma_benh_moi);
            if (trieuchungmoi != null && !trieuchungmoi.isEmpty()) {
                return ResponseEntity.ok(trieuchungmoi);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // API để xóa một người dùng theo ID
    @CrossOrigin
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //System.out.println("Received DELETE request for user ID: " + userId);
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.deleteById(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @PutMapping("/edit/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable("id") Long userId, @RequestBody UserEntity updatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //System.out.println("Received Update request for user ID: " + userId);
        Optional<UserEntity> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            UserEntity userToUpdate = existingUser.get();
            userToUpdate.setFullname(updatedUser.getFullname());
            userToUpdate.setPhonenumber(updatedUser.getPhonenumber());
            userToUpdate.setStatus(updatedUser.getStatus());
            userToUpdate.setEmail(updatedUser.getEmail());
            // Cập nhật các trường thông tin khác tương ứng

            UserEntity savedUser = userRepository.save(userToUpdate);
            return ResponseEntity.ok(savedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/getFile/{userId}")
    public ResponseEntity<?> getUserFiles(
            @PathVariable Long userId,
            @RequestParam(value = "user_Id") Long user_Id
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<UserEntity> existingUser = userRepository.findById(userId);
            if (existingUser.isPresent()) {
                UserDetailEntity userDetail = userService.getUserDetail(user_Id);
                UserEntity userEntity = userService.getUserEntity(user_Id);
                //System.out.println("id: "+user_Id);
                if (userDetail != null) {
                    Map<String, String> responseBody = new HashMap<>();

                    // Sử dụng FileService để thực hiện việc thêm file vào response body
                    fileService.addFileToResponse("anhdaidien", userDetail.getImage(), responseBody);
                    fileService.addFileToResponse("bangTotNghiepYKhoa", userDetail.getBangTotNghiepYKhoa(), responseBody);
                    fileService.addFileToResponse("chungChiHanhNghe", userDetail.getChungChiHanhNghe(), responseBody);
                    fileService.addFileToResponse("chungNhanChuyenKhoa", userDetail.getChungNhanChuyenKhoa(), responseBody);
                    // Include hocham and hoc_vi fields in the response
                    responseBody.put("hoc_ham", userDetail.getHoc_ham());
                    responseBody.put("hoc_vi", userDetail.getHoc_vi());
                    responseBody.put("status", userEntity.getStatus());
                    //System.out.println("status" + userEntity.getStatus());
                    // Trả về response thành công nếu có file trong response body
                    if (!responseBody.isEmpty()) {
                        return ResponseEntity.ok().body(responseBody);
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/edit-benh-moi-va-trieu-chung-moi/{userId}")
    public Map<String, Object> editBenhVaTrieuChung(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, Object> editInfo) {
        Map<String, Object> responseBody = new HashMap<>();
        //System.out.println("Nhan duoc " + editInfo);
        Long ma_trieu_chung_moi = Long.valueOf(editInfo.get("ma_trieu_chung_moi").toString());
        Long ma_benh_moi = Long.valueOf(editInfo.get("ma_benh_moi").toString());
        String ten_trieu_chung_moi = (String) editInfo.get("ten_trieu_chung_moi");
        String trang_thai = (String) editInfo.get("trang_thai");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            Optional<TrieuChungMoiEntity> existingTrieuChungMoi = trieuChungMoiRepository.findById(ma_trieu_chung_moi);
            if (existingTrieuChungMoi.isPresent()) {
                TrieuChungMoiEntity trieuChungToUpDate = existingTrieuChungMoi.get();
                trieuChungToUpDate.setTen_trieu_chung_moi(ten_trieu_chung_moi);

                TrieuChungMoiEntity saveTrieuChungMoi = trieuChungMoiRepository.save(trieuChungToUpDate);

                Optional<BenhMoiEntity> existingBenhMoi = benhMoiRepository.findById(ma_benh_moi);
                if (existingBenhMoi.isPresent()) {
                    BenhMoiEntity benhMoiToUpDate = existingBenhMoi.get();
                    benhMoiToUpDate.setTrang_thai(trang_thai);

                    BenhMoiEntity saveTrangThaiMoi = benhMoiRepository.save(benhMoiToUpDate);

                    responseBody.put("message", "Success"); // Thêm thông điệp thành công vào body
                    return responseBody;
                } else {
                    responseBody.put("message", "Error: Ma_benh not found "); // Thêm thông điệp lỗi vào body
                    return responseBody;
                }
            } else {
                responseBody.put("message", "Error: TrieuChungMoi not found"); // Thêm thông điệp lỗi vào body
                return responseBody;
            }
        } else {
            responseBody.put("message", "Error: User not found"); // Thêm thông điệp lỗi vào body
            return responseBody;
        }
    }

    @PutMapping("/updateSatusUser/{userId}")
    public Map<String, Object> updateStatusUser(@PathVariable("userId") Long userId,
            @RequestBody Map<String, String> requestBody) {
        String status = requestBody.get("status");
        //System.out.println("Status: " + status);
        //System.out.println("ID: " + userId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> responseBody = new HashMap<>();
        Optional<UserEntity> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            UserEntity userToUpdate = existingUser.get();
            userToUpdate.setStatus(status);
            UserEntity savedUser = userRepository.save(userToUpdate);
            responseBody.put("message", "Success"); // Thêm thông điệp thành công vào body
            return responseBody;
        } else {
            responseBody.put("message", "Error: User not found"); // Thêm thông điệp lỗi vào body
            return responseBody;
        }
    }
    
    @GetMapping("/getCountStatus")
    public Map<String, Object> updateStatusUser(){
        Map<String, Object> responseBody = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long count = userRepository.countByStatusEqualsZero();
        //System.out.println("count "+ count);
        responseBody.put("countByStatusEqualsZero", count);
        return responseBody;
    }
    
    
}
