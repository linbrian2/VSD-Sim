package com.iai.traffic.repository.traffic

import io.micronaut.data.annotation.*
import io.micronaut.data.model.*
import io.micronaut.data.repository.CrudRepository
import java.util.Optional
import groovy.transform.CompileStatic
import com.iai.traffic.domain.traffic.WazeType

@CompileStatic
@Repository
interface WazeTypeRepository extends CrudRepository<WazeType, Long> {
    List<WazeType> findByIdGreaterThan(Long id)
}