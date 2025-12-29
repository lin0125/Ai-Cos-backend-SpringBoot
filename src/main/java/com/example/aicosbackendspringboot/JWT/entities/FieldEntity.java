package com.example.aicosbackendspringboot.JWT.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "_field")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EntityListeners(AuditingEntityListener.class)
public class FieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Comment("場域名稱")
    private String fieldID;
    @Comment("場域名稱")
    private String fieldName;

    @Comment("建立場域合約的Email")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buildFieldEmail", referencedColumnName = "userEmail")
    private UserEntity buildFieldEmail;

    @Comment("場域的地址")
    private String buildFieldAddress;

    @Comment("場域合約結束時間")
    private LocalDateTime fieldContractEndTime;

    @CreatedDate
    @Comment("場域資料建立時間")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Comment("場域資料更新時間")
    private LocalDateTime updatedDate;
}
