package com.exercisamte.controller;

import com.exercisamte.dto.responseDto.UserDto;
import com.exercisamte.entity.*;
import com.exercisamte.repository.*;
import com.exercisamte.repository.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserMetricRepository userMetricRepo;

    @Autowired
    private RoutineDetailRepository routineDetailRepo;

    @Autowired
    private ExerciseDetailRepository exerciseDetailRepo;

    @Autowired
    private ExerciseRepository exerciseRepo;

    @GetMapping("/")
    public ResponseEntity<ResponseObject> getListUser() {
        List<User> users = userRepo.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Get list user successfully", users)
        );
    }

    @GetMapping("/{id}/metrics")
    public ResponseEntity<ResponseObject> getUserMetrics(@PathVariable Long id) {
        List<UserMetric> metrics = userMetricRepo.findByUserId(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Get user metrics successfully", metrics)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(){
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editUser() {
        return null;
    }

    @GetMapping("/{id}/today-exercises")
    public ResponseEntity<ResponseObject> getUserTodayExercises(@PathVariable Long id) {
        int weekDate = this.getWeekDate();

        List<RoutineDetail> routineDetails = this.routineDetailRepo.findByUserIdAndDay(id, weekDate);

        if(routineDetails.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("fail", "Not found user with id=" + id, null)
            );
        }

        Long routineId = routineDetails.get(0).getRoutineId();
        List<ExerciseDetail> exerciseDetails = this.exerciseDetailRepo.findByRoutineId(routineId);

        List<Long> exerciseDetailIds = exerciseDetails.stream().map(exerciseDetail -> exerciseDetail.getExerciseId()).collect(Collectors.toList());
        List<Exercise> exercises = this.exerciseRepo.findAllById(exerciseDetailIds);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Get today exercises successfully", exercises)
        );
    }


    /* Utils */
    private int getWeekDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.DAY_OF_WEEK);
    }
}
