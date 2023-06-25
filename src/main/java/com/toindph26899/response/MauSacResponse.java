package com.toindph26899.response;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class MauSacResponse {

    private Long idMauSac;

    private String tenMauSac;

    private Integer trangThai;

}
