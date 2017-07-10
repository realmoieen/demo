/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Muhammad Rizwan
 */
@Repository
public interface StudentRepo extends JpaRepository<Student, Long>{
    List<Student> findByNameStartsWithIgnoreCase(String name);
}
