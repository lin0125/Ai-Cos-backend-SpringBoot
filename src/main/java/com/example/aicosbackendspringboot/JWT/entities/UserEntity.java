package com.example.aicosbackendspringboot.JWT.entities;

import com.example.aicosbackendspringboot.JWT.base.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "_user")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("流水ID")
    private int id;

    @Comment("用戶 Google Token")
    private String userGoogleId;

    @Comment("用戶 Email")
    @Column(unique = true)
    private String userEmail;

    @Comment("用戶名稱")
    private String userName;

    @CreatedDate
    @Comment("建立時間")
    private LocalDateTime created_at;

    @CreatedDate
    @Comment("更新時間")
    private LocalDateTime update_at;

    @Enumerated(EnumType.STRING)
    @Comment("角色權限")
    private Role role;

    @OneToMany(mappedBy = "buildFieldEmail", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FieldEntity> fieldEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.userEmail;
    }
}
