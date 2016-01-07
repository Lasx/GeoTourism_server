package com.mmontes.rest.controller;

import com.mmontes.model.service.TIPService;
import com.mmontes.rest.request.TIPPatchRequest;
import com.mmontes.rest.request.TIPRequest;
import com.mmontes.service.FacebookService;
import com.mmontes.util.GeometryUtils;
import com.mmontes.util.dto.TIPDetailsDto;
import com.mmontes.util.exception.*;
import com.vividsolutions.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TIPController {

    @Autowired
    private TIPService tipService;

    @RequestMapping(value = "/admin/tip", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TIPDetailsDto>
    create(@RequestBody TIPRequest tipRequest) {

        if (tipRequest.getName() == null || tipRequest.getName().isEmpty() ||
                tipRequest.getGeometry() == null || tipRequest.getGeometry().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Geometry geometry;
        try {
            geometry = GeometryUtils.geometryFromWKT(tipRequest.getGeometry());
        } catch (GeometryParsingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TIPDetailsDto tipDetailsDto;
        try {
            tipDetailsDto = tipService.create(tipRequest.getType(), tipRequest.getName(), tipRequest.getDescription(),
                    tipRequest.getPhotoUrl(), tipRequest.getInfoUrl(), geometry);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(tipDetailsDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/tip/{TIPId}", method = RequestMethod.GET)
    public ResponseEntity<TIPDetailsDto>
    findById(@PathVariable Long TIPId,
         @RequestHeader(value="AuthorizationFB", required = false) String accessToken,
         @RequestParam(value = "facebookUserId", required = false) Long facebookUserId) {

        if (!FacebookService.validFBparams(accessToken,facebookUserId)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        TIPDetailsDto tipDetailsDto;
        try {
            tipDetailsDto = tipService.findById(TIPId,facebookUserId);
        } catch (InstanceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tipDetailsDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/tip/{TIPId}", method = RequestMethod.DELETE)
    public ResponseEntity
    delete(@PathVariable Long TIPId) {
        try {
            tipService.remove(TIPId);
        } catch (InstanceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidRouteException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/tip/{TIPId}", method = RequestMethod.PATCH)
    public ResponseEntity
    patch(@PathVariable Long TIPId,
          @RequestBody TIPPatchRequest tipPatchRequest,
          @RequestHeader(value="AuthorizationFB", required = false) String accessToken,
          @RequestParam(value = "facebookUserId", required = false) Long facebookUserId) {

        if (!FacebookService.validFBparams(accessToken,facebookUserId)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        TIPDetailsDto tipDetailsDto;
        try {
            tipDetailsDto = tipService.edit(TIPId, facebookUserId,
                    tipPatchRequest.getType(), tipPatchRequest.getName(), tipPatchRequest.getDescription(), tipPatchRequest.getInfoUrl(), tipPatchRequest.getAddress(), tipPatchRequest.getPhotoUrl());
        } catch (InstanceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidTIPUrlException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(tipDetailsDto, HttpStatus.OK);
    }

}