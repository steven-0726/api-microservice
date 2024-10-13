package com.example.repository;

import com.example.entity.ZtEdgeCl03Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZtEdgeCl03DataRepository extends JpaRepository<ZtEdgeCl03Data, Long> {
}
