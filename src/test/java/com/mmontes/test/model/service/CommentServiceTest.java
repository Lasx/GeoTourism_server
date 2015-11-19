package com.mmontes.test.model.service;

import com.mmontes.model.dao.TIPDao;
import com.mmontes.model.entity.TIP;
import com.mmontes.model.service.CommentService;
import com.mmontes.model.service.TIPService;
import com.mmontes.util.GeometryConversor;
import com.mmontes.util.dto.TIPDetailsDto;
import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static com.mmontes.test.util.Constants.*;
import static com.mmontes.util.Constants.SPRING_CONFIG_FILE;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE})
@Transactional
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private TIPDao tipDao;

    @Autowired
    private TIPService tipService;

    @Test
    public void addComments() {
        try {
            Point geom = (Point) GeometryConversor.geometryFromWKT(POINT_TORRE_HERCULES);
            TIPDetailsDto towerHercules = tipService.create(MONUMENT_DISCRIMINATOR, "Tower of Hercules", "Human Patrimony", VALID_TIP_PHOTO_URL, null, geom);
            TIP tip = tipDao.findById(towerHercules.getId());

            commentService.comment("Nice", EXISTING_FACEBOOK_USER_ID, tip.getId());
            commentService.comment("Ugly", EXISTING_FACEBOOK_USER_ID, tip.getId());

            assertEquals(2,tip.getComments().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
