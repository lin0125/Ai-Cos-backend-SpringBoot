package com.example.aicosbackendspringboot.JWT.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

//    private final AdminRepository adminRepository;
//    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
//        // 檢查 "admin" 帳號是否已經存在
//        if (adminRepository.findByAccount("admin").isEmpty()) {
//            // 如果不存在，就建立一個新的 AdminEntity
//            AdminEntity adminEntity = AdminEntity.builder()
//                    .account("admin")
//                    .password_hash(passwordEncoder.encode("NUTCadmin")) // 您可以在這裡設定預設密碼
//                    .role(Role.admin)
//                    .build();
//
//            // 存入資料庫
//            adminRepository.save(adminEntity);
//
//            log.info("預設管理員帳號 'admin' 已成功建立。");
//        } else {
//            log.info("管理員帳號 'admin' 已存在，無需建立。");
//        }
    }
}