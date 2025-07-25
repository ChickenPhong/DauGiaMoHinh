/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dgmh.repositories.impl;

import com.dgmh.pojo.NguoiDung;
import com.dgmh.repositories.NguoiDungRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Tran Quoc Phong
 */
@Repository
@Transactional
public class NguoiDungRepositoryImpl implements NguoiDungRepository{
    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public NguoiDung getByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();
        try {
            Query q = s.createQuery("FROM NguoiDung WHERE lower(username) = :username", NguoiDung.class);
            q.setParameter("username", username.toLowerCase().trim());
            return (NguoiDung) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public NguoiDung getById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(NguoiDung.class, id);
    }

    @Override
    public NguoiDung addUser(NguoiDung u) {
        //u.setPassword(passwordEncoder.encode(u.getPassword()));
        Session s = this.factory.getObject().getCurrentSession();
        s.persist(u);
        return u;
    }

    @Override
    public NguoiDung merge(NguoiDung u) {
        Session s = this.factory.getObject().getCurrentSession();
        return (NguoiDung) s.merge(u);  // Dùng merge để cập nhật entity đã tồn tại
    }

    @Override
    public boolean deleteUser(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        NguoiDung u = session.get(NguoiDung.class, id);
        if (u != null) {
            session.delete(u);
            return true;
        }
        return false;
    }

    @Override
    public boolean authenticate(String username, String rawPassword) {
        NguoiDung u = this.getByUsername(username);
        return u != null && passwordEncoder.matches(rawPassword, u.getPassword());
    }

    @Override
    public List<NguoiDung> getAllUsers() {
        Session session = this.factory.getObject().getCurrentSession();
        Query query = session.createQuery("FROM NguoiDung");
        return query.getResultList();
    }
}
