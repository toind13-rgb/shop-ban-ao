package com.toindph26899.service.impl;

import com.toindph26899.entity.*;
import com.toindph26899.repository.*;
import com.toindph26899.request.SanPhamChiTietRequest;
import com.toindph26899.response.GioHangItemResponse;
import com.toindph26899.response.GioHangResponse;
import com.toindph26899.response.SanPhamChiTietResponse;
import com.toindph26899.service.GioHangService;
import com.toindph26899.service.SanPhamChiTietService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GioHangServiceImpl implements GioHangService {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private SanPhamKichCoMauSacRepository sanPhamKichCoMauSacRepository;

    private List<SanPhamChiTietResponse> getCart() {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("cart")) {

                    String listValue = cookie.getValue();
                    // Tách các giá trị từ giá trị của cookie và tạo các đối tượng Item
                    List<SanPhamChiTietResponse> items = new ArrayList<>();
                    String[] values = listValue.split("%2C");
                    for (String value : values) {
                        String[] parts = value.split(":");
                        if (parts.length == 15) {
                            SanPhamChiTietResponse item = new SanPhamChiTietResponse();
                            item.setIdGioHangChiTiet(Long.parseLong(parts[0]));
                            item.setIdSanPham(Long.parseLong(parts[1]));
                            item.setTenSanPham(URLDecoder.decode(parts[2], StandardCharsets.UTF_8));
                            item.setGiaGiaoBan(BigDecimal.valueOf(Double.parseDouble(parts[3])));
                            item.setDuongDanAnh(URLDecoder.decode(parts[4], StandardCharsets.UTF_8));
                            item.setMoTa(URLDecoder.decode(parts[5], StandardCharsets.UTF_8));
                            item.setTenChatLieu(URLDecoder.decode(parts[6], StandardCharsets.UTF_8));
                            item.setMauSac(URLDecoder.decode(parts[7], StandardCharsets.UTF_8));
                            item.setLoaiAo(URLDecoder.decode(parts[8], StandardCharsets.UTF_8));
                            item.setLoaiCoAo(URLDecoder.decode(parts[9], StandardCharsets.UTF_8));
                            item.setLoaiTayAo(URLDecoder.decode(parts[10], StandardCharsets.UTF_8));
                            item.setTenThuongHieu(URLDecoder.decode(parts[11], StandardCharsets.UTF_8));
                            item.setKichCo(URLDecoder.decode(parts[12], StandardCharsets.UTF_8));
                            item.setSoLuong(Integer.parseInt(parts[13]));
                            item.setSoLuongMua(Integer.parseInt(parts[14]));

                            items.add(item);
                        }
                    }

                    return items;
                }
            }
        }

        return new ArrayList<>();
    }

    private void saveCookie(List<SanPhamChiTietResponse> list, Integer time) {
        // Chuyển đổi danh sách thành một chuỗi và lưu trữ trong cookie


        String listValue = list.stream()
                .map(
                        item -> URLEncoder.encode(
                                String.valueOf(sanPhamKichCoMauSacRepository.idSanPhamChiTiet(
                                        item.getIdSanPham(), item.getMauSac(), item.getKichCo()
                                )), StandardCharsets.UTF_8)
                                + ":" +
                                URLEncoder.encode(
                                        String.valueOf(item.getIdSanPham()), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(item.getTenSanPham(), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(String.valueOf(item.getGiaGiaoBan()), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(item.getDuongDanAnh(), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(item.getMoTa(), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(item.getTenChatLieu(), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(item.getMauSac(), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(item.getLoaiAo(), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(item.getLoaiCoAo(), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(item.getLoaiTayAo(), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(item.getTenThuongHieu(), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(item.getKichCo(), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(String.valueOf(item.getSoLuong()), StandardCharsets.UTF_8)
                                + ":" + URLEncoder.encode(String.valueOf(item.getSoLuongMua()), StandardCharsets.UTF_8)
                )
                .collect(Collectors.joining("%2C"));


        // Tạo cookie mới và thiết lập thời gian sống là 1 ngày
        Cookie cookie = new Cookie("cart", listValue);
        cookie.setMaxAge(time);
        cookie.setPath("/t-shop");

        // Gửi cookie về trình duyệt của người dùng
        response.addCookie(cookie);
    }

    @Override
    public List<SanPhamChiTietResponse> findAll() {
        return getCart();
    }

    @Override
    public List<SanPhamChiTietResponse> findAllByIdKhachHang(String id) {
        return null;
    }

    @Override
    public List<SanPhamChiTietResponse> removeObjectByIdSanPham(List<Long> list) {

        List<SanPhamChiTietResponse> listSanPham = new ArrayList<>(getCart());

        if (getCart().isEmpty()) {
            return null;
        } else {
            for (int i = 0; i < getCart().size(); i++) {
                for (Long l : list) {
                    if (listSanPham.get(i).getIdGioHangChiTiet().equals(l)) {
                        listSanPham.remove(i);
                    }
                }

                if (listSanPham.isEmpty()) {
                    saveCookie(listSanPham, 0);
                    return null;
                } else {
                    saveCookie(listSanPham, 5 * 60);
                }
            }

            return null;
        }
    }

    @Override
    public List<SanPhamChiTietResponse> removeSanPhamToCart(Long idSanPham) {

        List<SanPhamChiTietResponse> list = new ArrayList<>(getCart());

        if (getCart().isEmpty()) {
            return null;
        } else {
            for (int i = 0; i < getCart().size(); i++) {
                if (getCart().get(i).getIdGioHangChiTiet().equals(idSanPham)) {
                    list.remove(i);
                }

                if (list.isEmpty()) {
                    saveCookie(list, 0);
                    return null;
                } else {
                    saveCookie(list, 5 * 60);
                }
            }

            return null;
        }
    }

    @Override
    public List<SanPhamChiTietResponse> removeSanPhamToCartByUserDone(List<Long> sanPham) {

        if (sanPham == null) {
            return null;
        } else {
            for (Long id : sanPham) {
                if (id == null) {
                    return null;
                }
                gioHangChiTietRepository.deleteById(id);
            }
        }

        return null;
    }

    @Override
    public void removeSanPhamToCartByUser(Long idSanPham) {

        System.out.println("Id: " + idSanPham);

        gioHangChiTietRepository.deleteById(idSanPham);

    }

    @Override
    public void save(SanPhamChiTietResponse sanPhamChiTietResponse) {

        System.out.println(sanPhamChiTietResponse);

        Integer soLuong = -1;

        List<SanPhamChiTietResponse> list = getCart();

        for (int i = 0; i < getCart().size(); i++) {
            if (getCart().get(i).getIdSanPham()
                    .equals(sanPhamChiTietResponse.getIdSanPham()) &&
                    getCart().get(i).getKichCo()
                            .equals(sanPhamChiTietResponse.getKichCo()) &&
                    getCart().get(i).getMauSac().equals(sanPhamChiTietResponse.getMauSac())) {

                soLuong = getCart().get(i).getSoLuongMua() + sanPhamChiTietResponse.getSoLuongMua();

                sanPhamChiTietResponse.setSoLuongMua(soLuong);
                sanPhamChiTietResponse.setSoLuong(soLuong);

                list.set(i, sanPhamChiTietResponse);
                saveCookie(list, 5 * 60);
                return;
            }
        }

        list.add(sanPhamChiTietResponse);

        saveCookie(list, 15 * 60);

    }

    @Override
    public void saveGioHangByUser(SanPhamChiTietResponse sanPhamChiTietResponse, TaiKhoan taiKhoan) {

        Long idSanPhamChiTiet = sanPhamKichCoMauSacRepository.
                idSanPhamChiTiet(sanPhamChiTietResponse.getIdSanPham(), sanPhamChiTietResponse.getMauSac(),
                        sanPhamChiTietResponse.getKichCo());

        SanPhamKichCoMauSac sanPhamDb = sanPhamKichCoMauSacRepository.findSanPhamKichCoMauSacById(idSanPhamChiTiet);

                KhachHang khachHangDb = khachHangRepository.findKhachHangById(taiKhoan.getIdKhachHang().getId());
        GioHang gioHangDb = gioHangRepository.findGioHangByNguoiSoHuu(khachHangDb);
        GioHangChiTiet gioHangChiTiet = null;

        List<SanPhamChiTietResponse> list = gioHangChiTietRepository.gioHangByIdKhachHang(taiKhoan.getId());

        if (list != null) {
            for (SanPhamChiTietResponse spct : list) {
                if (spct.getIdSanPham().equals(sanPhamChiTietResponse.getIdSanPham())
                        && spct.getMauSac().equals(sanPhamChiTietResponse.getMauSac())
                        && spct.getKichCo().equals(sanPhamChiTietResponse.getKichCo())) {
                    gioHangChiTiet = GioHangChiTiet.builder()
                            .id(spct.getIdGioHangChiTiet())
                            .idSanPham(sanPhamDb)
                            .idGioHang(gioHangDb)
                            .soLuong(sanPhamChiTietResponse.getSoLuong() + spct.getSoLuong())
                            .donGio(sanPhamChiTietResponse.getGiaGiaoBan())
                            .trangThai(1)
                            .ghiChu(null)
                            .build();

                    gioHangChiTietRepository.save(gioHangChiTiet);

                    return;
                }
            }
        }

        gioHangChiTiet = GioHangChiTiet.builder()
                .idSanPham(sanPhamDb)
                .idGioHang(gioHangDb)
                .soLuong(sanPhamChiTietResponse.getSoLuong())
                .donGio(sanPhamChiTietResponse.getGiaGiaoBan())
                .trangThai(1)
                .ghiChu(null)
                .build();

        gioHangChiTietRepository.save(gioHangChiTiet);

        System.out.println("SanPhamChiTietHIHIHI: " + list);
    }

    @Override
    public List<SanPhamChiTietResponse> gioHangByUser(UUID id) {
        return gioHangChiTietRepository.gioHangByIdKhachHang(id);
    }

    @Override
    public Double tongTien() {

        List<SanPhamChiTietResponse> list = new ArrayList<>(getCart());

        Double tongTien = 0d;

        for (SanPhamChiTietResponse sp : list) {
            tongTien += Double.parseDouble(String.valueOf(sp.getGiaGiaoBan())) * sp.getSoLuong();
            System.out.println("SanPham: " + sp);
            System.out.println("Tong tien: " + tongTien);

        }

        return tongTien;
    }

    @Override
    public String checkListCheckout(List<SanPhamChiTietResponse> list, Model model) {

        if (list == null || list.isEmpty()) {
            model.addAttribute("errors", "Chọn 1 sản phẩm trước khi thanh toán");

            return "pages/gio-hang";
        }

        return null;
    }
}
