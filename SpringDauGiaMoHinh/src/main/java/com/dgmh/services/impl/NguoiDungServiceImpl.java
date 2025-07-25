/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dgmh.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dgmh.pojo.NguoiDung;
import com.dgmh.repositories.NguoiDungRepository;
import com.dgmh.services.NguoiDungService;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tran Quoc Phong
 */
@Service
@Primary
@Transactional
public class NguoiDungServiceImpl implements NguoiDungService, UserDetailsService{
    @Autowired
    private NguoiDungRepository nguoiDungRepo;

    @Autowired
    private BCryptPasswordEncoder passEncoder;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public NguoiDung getByUsername(String username) {
        return nguoiDungRepo.getByUsername(username);
    }

    @Override
    public NguoiDung getById(int id) {
        return nguoiDungRepo.getById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NguoiDung u = nguoiDungRepo.getByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Không tìm thấy user");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(u.getVaiTro()));

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities);
    }

    @Override
    public NguoiDung addUser(NguoiDung user) {
        user.setPassword(passEncoder.encode(user.getPassword())); // mã hóa
        return nguoiDungRepo.addUser(user);
    }

    @Override
    public NguoiDung mergeUser(NguoiDung user) {
        // Nếu đã có user (tức là đã có ID), gọi merge để cập nhật
        return nguoiDungRepo.merge(user);  // Dùng merge để cập nhật thay vì persist
    }

    @Override
    public NguoiDung addUser(Map<String, String> params, MultipartFile avatar) {
        NguoiDung u = new NguoiDung();
        u.setUsername(params.get("username"));
        u.setPassword(params.get("password"));
        u.setEmail(params.get("email"));
        u.setHoTen(params.get("hoTen"));
        u.setSoDienThoai(params.get("soDienThoai"));
        u.setDiaChi(params.get("diaChi"));
        u.setVaiTro(params.get("vaiTro"));

        u.setPassword(passEncoder.encode(u.getPassword()));  // Mã hóa mật khẩu

        // Gọi repository để lưu người dùng vào DB
        return nguoiDungRepo.addUser(u);  // Đảm bảo phương thức này hỗ trợ lưu user
    }

    @Override
    public boolean deleteUser(int id) {
        return nguoiDungRepo.deleteUser(id);
    }

    @Override
    public boolean authenticate(String username, String rawPassword) {
        NguoiDung u = this.getByUsername(username);
        if (u != null) {
            System.out.println(" Testing login:");
            System.out.println(" - Username: " + u.getUsername());
            System.out.println(" - Raw password: " + rawPassword);
            System.out.println(" - Stored hash: " + u.getPassword());
            System.out.println(" - Password match: " + passEncoder.matches(rawPassword, u.getPassword()));

            System.out.println("Raw: " + rawPassword);
            System.out.println("Encoded: " + u.getPassword());
            System.out.println("Match: " + passEncoder.matches(rawPassword, u.getPassword()));
        }
        return u != null && passEncoder.matches(rawPassword, u.getPassword());
    }

    @Override
    public List<NguoiDung> getAllUsers() {
        return nguoiDungRepo.getAllUsers();
    }
}
