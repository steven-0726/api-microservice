package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "ZT_EDGE_CL03_DATA")
@Data
public class ZtEdgeCl03Data {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oid_generator")
    @SequenceGenerator(name = "oid_generator", sequenceName = "ZT_EDGE_CL03_DATA_SEQ", allocationSize = 1)
    @Column(name = "OID")
    private Long oid;

    @Column(name = "TX_AMT")
    private Double txAmt;

    @Column(name = "TX_DATE")
    private LocalDateTime txDate;

    @Column(name = "SING")
    private String sing;

    @Column(name = "VERSION", nullable = false)
    private String version = "0";

    @Column(name = "STS", nullable = false)
    private String sts = "1";

    @Column(name = "CREATOR")
    private String creator;

    @Column(name = "CREATETIME", nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();
}
