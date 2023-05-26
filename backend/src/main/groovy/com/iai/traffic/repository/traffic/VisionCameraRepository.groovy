package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.VisionCamera
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface VisionCameraRepository extends CrudRepository<VisionCamera, Long> {
    VisionCamera getByDeviceId(int deviceId)
    List<VisionCamera> findByEnabled(int enabled)
}