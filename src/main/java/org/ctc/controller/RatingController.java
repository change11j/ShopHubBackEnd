package org.ctc.controller;

import org.ctc.dto.RatingDTO;
import org.ctc.entity.Rating;
import org.ctc.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/create")
    public ResponseEntity<Rating> createRating(@RequestBody RatingDTO ratingDTO) {
        Rating savedRating = ratingService.createRating(ratingDTO);
        return ResponseEntity.ok(savedRating);
    }
}
