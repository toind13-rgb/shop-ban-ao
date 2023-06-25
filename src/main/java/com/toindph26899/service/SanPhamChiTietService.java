package com.toindph26899.service;

import com.toindph26899.response.KichCoResponse;
import com.toindph26899.response.SanPhamChiTietResponse;
import com.toindph26899.response.SanPhamView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SanPhamChiTietService {

    Page<SanPhamChiTietResponse> findAll(Pageable pageable);

    List<SanPhamView> newProducts();

    List<KichCoResponse> kichCoList(Long id);

    SanPhamChiTietResponse getOne(Long id);

    List<SanPhamChiTietResponse> sanPhamCheckout(List<Long> id);

    Double tongTien(List<SanPhamChiTietResponse> list);

}
