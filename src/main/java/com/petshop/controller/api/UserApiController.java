package com.petshop.controller.api;

import com.petshop.entity.User;
import com.petshop.entity.Pet;
import com.petshop.entity.Appointment;
import com.petshop.service.UserService;
import com.petshop.service.PetService;
import com.petshop.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private PetService petService;
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody User user, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        user.setId(currentUser.getId());
        user.setPassword(currentUser.getPassword());
        User updatedUser = userService.update(user);
        session.setAttribute("user", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/pets")
    public ResponseEntity<List<Pet>> getPets(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Pet> pets = petService.findByUserId(user.getId());
        return ResponseEntity.ok(pets);
    }

    @PostMapping("/pets")
    public ResponseEntity<Pet> addPet(@RequestBody Pet pet, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        pet.setUser(user);
        Pet createdPet = petService.create(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPet);
    }

    @PutMapping("/pets/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable Integer id, @RequestBody Pet pet, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Pet existingPet = petService.findById(id);
        if (existingPet == null || !existingPet.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        pet.setId(id);
        pet.setUser(user);
        Pet updatedPet = petService.update(pet);
        return ResponseEntity.ok(updatedPet);
    }

    @DeleteMapping("/pets/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Integer id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Pet pet = petService.findById(id);
        if (pet == null || !pet.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAppointments(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Appointment> appointments = appointmentService.findByUserId(user.getId());
        return ResponseEntity.ok(appointments);
    }

    @PostMapping("/appointments")
    public ResponseEntity<Appointment> bookAppointment(@RequestBody Appointment appointment, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        appointment.setUser(user);
        Appointment createdAppointment = appointmentService.create(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }
}
