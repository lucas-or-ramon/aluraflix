package br.com.alura.aluraflix.controllers;

import br.com.alura.aluraflix.controllers.request.LoginRequest;
import br.com.alura.aluraflix.controllers.request.SignupRequest;
import br.com.alura.aluraflix.controllers.response.JwtResponse;
import br.com.alura.aluraflix.controllers.response.UserResponse;
import br.com.alura.aluraflix.controllers.response.VideoResponse;
import br.com.alura.aluraflix.models.ERole;
import br.com.alura.aluraflix.services.UserService;
import br.com.alura.aluraflix.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/start")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PublicController {

    private final VideoService videoService;
    private final UserService userService;

    @Autowired
    public PublicController(VideoService videoService, UserService userService) {
        this.videoService = videoService;
        this.userService = userService;
    }

    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(userService.authenticateUser(loginRequest));
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {

        return ResponseEntity.ok(userService.saveUser(signupRequest, ERole.ROLE_USER));
    }

    @GetMapping(value = "/free", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VideoResponse>> getFreeVideos(@RequestParam int pageNumber) {

        return ResponseEntity.ok(videoService.findFreeVideos(pageNumber));
    }
}

