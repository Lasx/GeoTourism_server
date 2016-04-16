package com.mmontes.model.service;

import com.mmontes.model.entity.TIP.TIPtype;
import com.mmontes.util.dto.TIPtypeDto;
import com.mmontes.util.exception.DuplicateInstanceException;
import com.mmontes.util.exception.InstanceNotFoundException;

import java.util.List;

public interface TIPtypeService {

    List<TIPtype> findAllTypes();
    TIPtypeDto findById(Long TIPtypeId) throws InstanceNotFoundException;
    String findTypeName(Long TIPtypeId) throws InstanceNotFoundException;
    TIPtypeDto create(String name, String icon, String OSMKey, String OSMValue) throws InstanceNotFoundException, DuplicateInstanceException;
    TIPtypeDto update(Long TIPtypeID,String name, String icon, String OSMKey, String OSMValue) throws InstanceNotFoundException;
    void delete(Long TIPtypeID) throws InstanceNotFoundException;
}
