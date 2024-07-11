package org.ctc.dao;

import org.ctc.entity.ProdSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdSpecDao extends JpaRepository<ProdSpec,Integer> {


     List<ProdSpec> findByProductId(Integer productId);

     void deleteAllByProductId(Integer productId);
}
