package com.hospital.appointment.controller;

import com.hospital.appointment.model.AppointmentEntity;
import com.hospital.appointment.repository.AppointmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository repo;

    public AppointmentController(AppointmentRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<AppointmentEntity> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentEntity> get(@PathVariable Integer id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public AppointmentEntity create(@RequestBody AppointmentEntity a) {
        return repo.save(a);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentEntity> update(@PathVariable Integer id, @RequestBody AppointmentEntity a) {
        return repo.findById(id).map(existing -> {
            a.setId(existing.getId());
            return ResponseEntity.ok(repo.save(a));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!repo.existsById(id))
            return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
