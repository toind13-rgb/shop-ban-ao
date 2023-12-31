package com.toindph26899.repository;

import com.toindph26899.entity.GioHang;
import com.toindph26899.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Long> {

    GioHang findGioHangByNguoiSoHuu(KhachHang khachHang);

}
