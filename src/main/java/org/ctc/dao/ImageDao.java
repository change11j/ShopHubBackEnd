package org.ctc.dao;

import org.ctc.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageDao extends JpaRepository<Image,Integer> {

    public boolean existsBySourceStringAndSourceTypeAndSourceId(String sourceString,String sourceType,Integer id);

    public List<Image> findBySourceStringAndSourceTypeAndSourceId(String sourceString, String sourceType, Integer id);
    public List<Image> findAllBySourceStringAndSourceTypeAndSourceIdIn(String sourceString, String sourceTypes, List<Integer> ids);

    public Image findFirstBySourceTypeAndSourceStringAndSourceId(String sourceType,String sourceString, Integer id);

    public boolean existsBySourceTypeAndSourceStringAndSourceId(String sourceType,String sourceString, Integer id);
    void deleteAllBySourceTypeAndAndSourceId(String sourceType,Integer sourceId);
}


