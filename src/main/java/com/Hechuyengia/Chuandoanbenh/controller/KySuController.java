/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Hechuyengia.Chuandoanbenh.controller;

import com.Hechuyengia.Chuandoanbenh.DTO.UserInfoDTO;
import com.Hechuyengia.Chuandoanbenh.entity.BenhEntity;
import com.Hechuyengia.Chuandoanbenh.entity.BenhSuggestEntity;
import com.Hechuyengia.Chuandoanbenh.entity.LienKetTrieuChungLuatEntity;
import com.Hechuyengia.Chuandoanbenh.entity.TrieuChungEntity;
import com.Hechuyengia.Chuandoanbenh.entity.UserEntity;
import com.Hechuyengia.Chuandoanbenh.repository.BenhRepository;
import com.Hechuyengia.Chuandoanbenh.repository.BenhSuggestRepository;
import com.Hechuyengia.Chuandoanbenh.repository.LienKetBenhLuatRepository;
import com.Hechuyengia.Chuandoanbenh.repository.LienKetTrieuChungLuatRepository;
import com.Hechuyengia.Chuandoanbenh.repository.TrieuChungBenhRepository;
import com.Hechuyengia.Chuandoanbenh.repository.TrieuChungRepository;
import com.Hechuyengia.Chuandoanbenh.repository.UserRepository;
import com.Hechuyengia.Chuandoanbenh.service.BenhService;
import com.Hechuyengia.Chuandoanbenh.service.LuatService;
import com.Hechuyengia.Chuandoanbenh.service.TrieuChungService;
import com.Hechuyengia.Chuandoanbenh.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tranm
 */
@RestController
@RequestMapping("/taskbar-ks")
public class KySuController {

    @Autowired
    UserRepository userRepository;

    private final UserService userService;
    @Autowired
    TrieuChungRepository trieuChungRepository;
    @Autowired
    TrieuChungService trieuChungService;
    @Autowired
    BenhSuggestRepository benhSuggestRepository;
    @Autowired
    BenhService benhService;
    @Autowired
    LuatService luatService;
    @Autowired
    TrieuChungBenhRepository trieuChungBenhRepository;
    @Autowired
    LienKetTrieuChungLuatRepository lienKetTrieuChungLuatRepository;

    @Autowired
    public KySuController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/checkTC/{userId}")// API nhận tên tc và check xem đã có trong csdl chưa
    public Map<String, Object> CheckTc(
            @PathVariable Long userId,
            @RequestParam(value = "ten_trieu_chung") String ten_trieu_chung
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> existingUser = userRepository.findById(userId);
        Map<String, Object> responseBody = new HashMap<>();
        //System.out.println("Ds " + ten_trieu_chung);
        if (existingUser.isPresent()) {
            List<Long> maTrieuChungList = new ArrayList<>();
            String[] ten_trieu_chung_array = ten_trieu_chung.split("(?=\\p{Lu})"); // Ngắt xâu theo chữ cái in hoa ở đầu
            for (String ten : ten_trieu_chung_array) {
                Long maTrieuChung = trieuChungRepository.findMaTrieuChungByTenTrieuChung(ten);
                if (maTrieuChung != null) {
                    maTrieuChungList.add(maTrieuChung);
                } else {
                    maTrieuChungList.add(null); // Thêm mã null vào danh sách
                }
            }
            responseBody.put("message", maTrieuChungList);
        } else {

            responseBody.put("message", "Use không tồn tại"); // Người dùng không tồn tại
        }
        return responseBody;
    }

    @PostMapping("/add-Benh-and_TC/{userId}")// API thêm bệnh mới và tc mới
    public Map<String, Object> addBenhVaTrieuChung(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();
        try {

            String tenBenh = (String) requestBody.get("ten_benh");
            String loaiHe = (String) requestBody.get("loai_he");
            String ghi_chu = (String) requestBody.get("ghi_chu");
            //Long ma_benh_moi = (Long) requestBody.get("ma_benh_moi");
            // Assuming "trieu_chung" is a list of objects with a "trieu_chung" field
            List<Map<String, String>> trieuChungList = (List<Map<String, String>>) requestBody.get("trieu_chung");
            //List<TrieuChungEntity> maTrieuChungList = (List<TrieuChungEntity>) requestBody.get("ma_trieu_chung");
            //nhận thêm 1 danh sách mã triệu chứng 

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            //System.out.println(" id la: " + userId + " ten_benh: " + tenBenh + " loaiHe: " + loaiHe + " trieuChungList: " + trieuChungList);
            //Optional<UserEntity> existingUser = userRepository.findById(userId);

            // Trích xuất tên triệu chứng từ mỗi đối tượng Map
            List<String> tenTrieuChungList = trieuChungList.stream()
                    .map(trieuChung -> trieuChung.get("trieu_chung"))
                    .collect(Collectors.toList());

            trieuChungService.saveBenhVaTrieuChung(userId, loaiHe, tenBenh, tenTrieuChungList, ghi_chu);

            responseBody.put("message", "Success"); // Thêm thông điệp thành công vào body

            return responseBody;
        } catch (Exception e) {
            responseBody.put("message", "Error"); // Thêm thông điệp lỗi vào body

            return responseBody;
        }
    }

    @PostMapping("/lay-danh-sach-benh-da-co-luat")
    public Map<String, Object> getDSBenhDaCoLuat(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            List<String> tenBenhList = (List<String>) requestBody.get("ten_benh");
            //System.out.println("Danh sach nhan duoc: " + tenBenhList);

            // Gọi phương thức trong BenhService để lấy danh sách bệnh có luật
            List<Map<String, Object>> benhChuaCoLuat = benhService.getDS(tenBenhList);
            //System.out.println("benhChuaCoLuat: " + benhChuaCoLuat);

            // Tạo một biến để lưu trữ giá trị total
            int total = 0;

            // Lặp qua danh sách benhChuaCoLuat để tìm giá trị total
            for (Map<String, Object> benhInfo : benhChuaCoLuat) {
                // Kiểm tra nếu benhInfo có key là "total"
                if (benhInfo.containsKey("total")) {
                    // Lấy giá trị total và gán cho biến total
                    total = (int) benhInfo.get("total");
                    break; // Thoát khỏi vòng lặp nếu đã tìm được giá trị total
                }
            }
            //System.out.println("total: " + total);
            // Gửi danh sách bệnh có luật và giá trị total về client
            responseBody.put("data", benhChuaCoLuat);
            responseBody.put("total", total);

        } catch (Exception e) {
            responseBody.put("success", false);
            responseBody.put("message", "Có lỗi xảy ra khi xử lý dữ liệu: " + e.getMessage());
        }

        return responseBody;
    }

    @PutMapping("save-luat-loai-1/{userId}")
    public Map<String, Object> saveLuatLoai1(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, Object> requestBody
    ) {
        Map<String, Object> responseBody = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> existingUser = userRepository.findById(userId);

        if (existingUser.isPresent()) {
            Long loai_luat = Long.valueOf(requestBody.get("loai_luat").toString());
            Long ma_benh = Long.valueOf(requestBody.get("ma_benh").toString());
            List<Integer> maTrieuChungListRaw = (List<Integer>) requestBody.get("ma_trieu_chung");
            // Chuyển đổi từ Integer sang Long
            List<Long> maTrieuChungList = new ArrayList<>();
            for (Integer value : maTrieuChungListRaw) {
                maTrieuChungList.add(value.longValue());
            }
            System.out.println("Ma Benh" + ma_benh + " Trieu chung list:" + maTrieuChungList);
            luatService.saveLuatLoai1(userId, loai_luat, ma_benh, maTrieuChungList);
            // trả về danh sách
            List<Long> nonNullMatchingBenhIdsList = new ArrayList<>();
            
            // 1.
            for (Long ma_trieu_chung : maTrieuChungList) {
                // hmaf dưới là hàm lấy các mã bệnh trong bảng triệu chứng bệnh có cùng mã triệu chứng nhưng khác mã bệnh
                List<Long> matchingBenhIds = trieuChungBenhRepository.findBenhIdsByTrieuChungList(ma_trieu_chung, ma_benh);
                //System.out.println("ABC" + matchingBenhIds);

                // Check if matchingBenhIds is not null and add 1 to nonNullMatchingBenhIdsList, else add 0
                if (matchingBenhIds != null && !matchingBenhIds.isEmpty()) {
                    nonNullMatchingBenhIdsList.add(0L);
                } else {
                    nonNullMatchingBenhIdsList.add(ma_trieu_chung);
                }
                // tìm xem có luật loại 3 nào có triệu chứng của luật loại 1 mới không
                Long loai_luat3 = 3L;
                Long maLuatLoai3 = lienKetTrieuChungLuatRepository.findLuatByMaTc(ma_trieu_chung, loai_luat3);
                //Nếu có luật loại 3 
                if (maLuatLoai3 != null) {
                    System.out.println("có luật => " + maLuatLoai3);
                    //xóa luật loại 3 nếu triệu chứng trong luật loại 3 có trong luật loại 1
                    //B1: tìm triệu chứng luật
                    Optional<LienKetTrieuChungLuatEntity> lienKetTrieuChungLuatEntity
                            = Optional.ofNullable(lienKetTrieuChungLuatRepository.findByMaLuatAndMaTrieuChung(maLuatLoai3, ma_trieu_chung));

                    if (lienKetTrieuChungLuatEntity.isPresent()) {
                        LienKetTrieuChungLuatEntity entity = lienKetTrieuChungLuatEntity.get();
                        // Xử lý entity nếu tồn tại
                        System.out.println("Tồn tại: " + "entiti là: " + entity);
                        lienKetTrieuChungLuatRepository.delete(entity);
                        System.out.println("Đã xóa: " + entity);
                    }

                } else {
                    System.out.println("K có luat " + maLuatLoai3);

                }

            }
            //System.out.println("Non-null matching BenhIds: " + nonNullMatchingBenhIdsList);
            responseBody.put("nonNullMatchingBenhIdsList", nonNullMatchingBenhIdsList);
            responseBody.put("message", "Thêm luật thành công");
        } else {
            responseBody.put("message", "Use không tồn tại");
        }
        return responseBody;
    }

    @PutMapping("save-luat-loai-3/{userId}")
    public Map<String, Object> saveLuatLoai2(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, Object> requestBody
    ) {
        Map<String, Object> responseBody = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> existingUser = userRepository.findById(userId);

        if (existingUser.isPresent()) {
            Long loai_luat = Long.valueOf(requestBody.get("loai_luat").toString());
            Long ma_benh = Long.valueOf(requestBody.get("ma_benh").toString());
            List<Integer> maTrieuChungListRaw = (List<Integer>) requestBody.get("ma_trieu_chung");
            // Chuyển đổi từ Integer sang Long
            List<Long> maTrieuChungList = new ArrayList<>();
            for (Integer value : maTrieuChungListRaw) {
                maTrieuChungList.add(value.longValue());
            }
            luatService.saveLuatLoai2(userId, loai_luat, ma_benh, maTrieuChungList);
            // trả về danh sách
            List<Long> nonNullMatchingBenhIdsList = new ArrayList<>();

            responseBody.put("message", "Thêm luật thành công");
        } else {
            responseBody.put("message", "Use không tồn tại");
        }
        return responseBody;
    }

    @GetMapping("/getallBenhOfTtrieuChungMoi")// API get all new benh
    public List<BenhSuggestEntity> listBenh() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return benhSuggestRepository.findAll();
    }

    @GetMapping("/trieuChungSuggestMoi/{userId}")// API get TC when choose benh
    public ResponseEntity<List<Object[]>> getTrieuChungSuggestMoiByMaBenhCu(@PathVariable Long userId,
            @RequestParam(value = "ma_benh_suggest") Long ma_benh_suggest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            List<Object[]> trieuChungSuggestMoi = benhSuggestRepository.findTrieuChungSuggestMoiByMaBenhMoi(ma_benh_suggest);
            if (trieuChungSuggestMoi != null && !trieuChungSuggestMoi.isEmpty()) {
                return ResponseEntity.ok(trieuChungSuggestMoi);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/saveTrieuChungSuggestIntoTrieuChungBenh")// API save new tc for benh
    public Map<String, Object> saveTrieuChungSuggest(@RequestBody Map<String, Object> requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> responseBody = new HashMap<>();
        try {
            List<Map<String, String>> trieuChungList = (List<Map<String, String>>) requestBody.get("trieu_chung");
            Integer ma_benh_int = (Integer) requestBody.get("ma_benh");
            Long ma_benh = ma_benh_int != null ? ma_benh_int.longValue() : null;
            Integer ma_benh_suggest_int = (Integer) requestBody.get("ma_benh_suggest");
            Long ma_benh_suggest = ma_benh_suggest_int != null ? ma_benh_suggest_int.longValue() : null;
            Integer trang_thai_int = (Integer) requestBody.get("trang_thai");
            Long trang_thai = trang_thai_int != null ? trang_thai_int.longValue() : null;

            System.out.println("ma benh suggest " + ma_benh_suggest);
            // xử lý
            // trích xuất tên triệu chứng từ map
            List<String> tenTrieuChungList = trieuChungList.stream()
                    .map(trieuChung -> trieuChung.get("tenTrieuChung"))
                    .collect(Collectors.toList());
            String trieuChungMap = trieuChungService.saveTrieuChungSuggest(ma_benh, tenTrieuChungList, trang_thai, ma_benh_suggest);
            // tìm luật của bệnh cũ để thêm
            String luaOfBenhAndTrieuChung = luatService.getLuatAndTrieuChungByMaBenhAsJson(ma_benh);
            //luatService.separateTrieuChungMap(trieuChungMap);
            System.out.println("lua of benh" + luaOfBenhAndTrieuChung);
            responseBody.put("message", "Thêm Triệu Chứng thành công");
            responseBody.put("trieuChungMap", trieuChungMap);
            responseBody.put("luatOfMaBenh", luaOfBenhAndTrieuChung);

        } catch (Exception e) {
            responseBody.put("error", "Thêm Triệu Chứng Thất Bại");

        }
        return responseBody;
    }

    @PutMapping("/saveLuatTrieuChungSuggestIntoTrieuChungBenh/{userId}")// API save new tc into Luat
    public Map<String, Object> saveLuatTrieuChungSuggest(@PathVariable Long userId,
            @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            Integer ma_benh_int = (Integer) requestBody.get("ma_benh");
            Long ma_benh = ma_benh_int != null ? ma_benh_int.longValue() : null;

            List<Integer> existedInDatabase = (List<Integer>) requestBody.get("existedInDatabase");// tc trong csdl
            List<Integer> notExistInDatabase = (List<Integer>) requestBody.get("notExistInDatabase");// tc k có trong csdl
            // Chuyển đổi từ Integer sang Long
            List<Long> existedInDatabaseLong = existedInDatabase.stream().map(Long::valueOf).collect(Collectors.toList());
            List<Long> notExistInDatabaseLong = notExistInDatabase.stream().map(Long::valueOf).collect(Collectors.toList());

            // Xử lý logic với hai mảng đã nhận được
            // Ví dụ: In ra để kiểm tra xem đã nhận được đúng không
            System.out.println("triệu chứng đã có để làm luật loại 1': " + existedInDatabaseLong);
            System.out.println("triệu chứng chưa có bao giờ thì làm luật loại 3': " + notExistInDatabaseLong);
            luatService.saveLuatWithTrieuChungMoi(userId, ma_benh, existedInDatabaseLong, notExistInDatabaseLong);
            
            responseBody.put("message", "Thêm Triệu Chứng thành công");
        } catch (Exception e) {
            responseBody.put("error", "Thêm Triệu Chứng Thất Bại");
        }
        return responseBody;
    }

}
